package chen.controller;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * This is simple HTTP local server for streaming InputStream to apps which are capable to read data from url.
 * Random access input stream is optionally supported, depending if file can be opened in this mode.
 */
public class HttpServer {
    private static final boolean debug = false;

    private ServerSocket serverSocket;
    private Thread mainThread;

//    public static void main(String[] args) throws IOException {
//        new HttpServer();
//    }

    /**
     * Some HTTP response status codes
     */
    private static final String
            HTTP_BADREQUEST = "400 Bad Request",
            HTTP_416 = "416 Range not satisfiable",
            HTTP_INTERNALERROR = "500 Internal Server Error";

    public HttpServer() {
	    	try{
	    		serverSocket = new ServerSocket(38000);
	    	} catch (IOException e) {
				System.out.println("端口 [" + Constant.port + "] 被占用.");
			}
    	System.out.println(Constant.port);
    	if(serverSocket == null){
    		return;
    	}
    	
        mainThread = new Thread(() -> {
            while (true) {
                try {
                    Socket accept = serverSocket.accept();
                    new HttpSession(accept);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mainThread.setName("Stream over HTTP");
        //mainThread.setDaemon(true);
        mainThread.start();
    }

    private class HttpSession implements Runnable {
        private boolean canSeek;
        private InputStream is;
        private final Socket socket;
        private String method;
        
        private File file;

        HttpSession(Socket s) {
            socket = s;
            System.out.println("Stream over localhost: serving request on " + s.getInetAddress());
            Thread t = new Thread(this, "Http response");
            t.setDaemon(true);
            t.start();
        }

        @Override
        public void run() {
            try {
                handleResponse(socket);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        private void openInputStream() throws IOException {
            is = new DecodeInputStream(file);
            if (is != null) {
                canSeek = true;
            }
        }

        private void handleResponse(Socket socket) {
            try {
                InputStream inS = socket.getInputStream();
                if (inS == null)
                    return;
                byte[] buf = new byte[8192];
                int rlen = inS.read(buf, 0, buf.length);
                if (rlen <= 0)
                    return;

                ByteArrayInputStream hbis = new ByteArrayInputStream(buf, 0, rlen);
                BufferedReader hin = new BufferedReader(new InputStreamReader(hbis));
                Properties pre = new Properties();

                if (!decodeHeader(socket, hin, pre))
                    return;
                String range = pre.getProperty("range");

                Properties headers = new Properties();
                if (file.length() != -1)
                    headers.put("Content-Length", String.valueOf(file.length()));
                headers.put("Accept-Ranges", canSeek ? "bytes" : "none");
                headers.put("Content-Language", "zh-CN");

                long sendCount;

                String status;
                if (range == null || !canSeek) {
                    status = "200 OK";
                    sendCount = file.length();
                } else {
                    if (!range.startsWith("bytes=")) {
                        sendError(socket, HTTP_416, null);
                        return;
                    }
                    range = range.substring(6);
                    
                    System.out.println("=========================================="+range);
                    long startFrom = 0, endAt = -1;
                    int minus = range.indexOf('-');
                    if (minus > 0) {
                        try {
                            String startR = range.substring(0, minus);
                            startFrom = Long.parseLong(startR);
                            String endR = range.substring(minus + 1);
                            endAt = Long.parseLong(endR);
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    if (startFrom >= file.length()) {
                        sendError(socket, HTTP_416, null);
                        inS.close();
                        return;
                    }
                    if (endAt < 0)
                        endAt = file.length() - 1;
                    sendCount = endAt - startFrom + 1;
                    if (sendCount < 0)
                        sendCount = 0;
                    
                    status = "206 Partial Content";
                    ((DecodeInputStream) is).seek(startFrom);

                    headers.put("Content-Length", "" + sendCount);
                    String rangeSpec = "bytes " + startFrom + "-" + endAt + "/" + file.length();
                    System.out.println(rangeSpec+"==========");
                    headers.put("Content-Range", rangeSpec);
                }
                sendResponse(socket, status, "", headers, is, sendCount, buf, null, method);
                inS.close();
                if (debug)
                    System.out.println("Http stream finished");
            } catch (IOException ioe) {
                if (debug)
                    ioe.printStackTrace();
                try {
                    sendError(socket, HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
                } catch (Throwable t) {
                }
            } catch (InterruptedException ie) {
                if (debug)
                    ie.printStackTrace();
            }
        }

        private boolean decodeHeader(Socket socket, BufferedReader in, Properties pre) throws InterruptedException {
            try {
                String inLine = in.readLine();
                if (inLine == null)
                    return false;
                StringTokenizer st = new StringTokenizer(inLine);
                if (!st.hasMoreTokens())
                    sendError(socket, HTTP_BADREQUEST, "Syntax error");

                method = st.nextToken();
                if (!method.equals("GET") && !method.equals("HEAD") )
                    return false;

                if (!st.hasMoreTokens()){
                    sendError(socket, HTTP_BADREQUEST, "Missing URI");
                }else{
                	String url = st.nextToken();
                	String filePath;
                    int idx = url.indexOf("file=");
                    if (idx > 0) {
                        filePath = url.substring(idx + 5);
                        filePath = URLDecoder.decode(filePath, "UTF-8");
                	    file = new File(filePath);
                	    openInputStream();
                    }else {
                        sendError(socket, HTTP_BADREQUEST, "Missing URI");
                    }
                }

                while (true) {
                    String line = in.readLine();
                    if (line == null)
                        break;
                    int p = line.indexOf(':');
                    if (p < 0)
                        continue;
                    final String atr = line.substring(0, p).trim().toLowerCase();
                    final String val = line.substring(p + 1).trim();
                    pre.put(atr, val);
                }
            } catch (IOException ioe) {
                sendError(socket, HTTP_INTERNALERROR, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            }
            return true;
        }
    }



    public void close() {
        System.out.println("Closing stream over http");
        try {
            serverSocket.close();
            mainThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns an error message as a HTTP response and
     * throws InterruptedException to stop further request processing.
     */
    private static void sendError(Socket socket, String status, String msg) throws InterruptedException {
        sendResponse(socket, status, "text/plain", null, null, 0, null, msg);
        throw new InterruptedException();
    }

    private static void copyStream(InputStream in, OutputStream out, byte[] tmpBuf, long maxSize) throws IOException {

        while (maxSize > 0) {
            int count = (int) Math.min(maxSize, tmpBuf.length);
            count = in.read(tmpBuf, 0, count);
            if (count < 0)
                break;
            out.write(tmpBuf, 0, count);
            maxSize -= count;
        }
    }

    private static void sendResponse(Socket socket, String status, String mimeType, Properties header, InputStream isInput, int sendCount, byte[] buf, String errMsg) {
        sendResponse(socket, status, mimeType, header, isInput, sendCount, buf, errMsg, null);
    }
    /**
     * Sends given response to the socket, and closes the socket.
     */
    private static void sendResponse(Socket socket, String status, String mimeType, Properties header, InputStream isInput, long sendCount, byte[] buf, String errMsg, String method) {
        try {
            OutputStream out = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(out);

            {
                String retLine = "HTTP/1.0 " + status + " \r\n";
                pw.print(retLine);
            }
            if (mimeType != null) {
                String mT = "Content-Type: " + mimeType + "\r\n";
                pw.print(mT);
            }
            if (header != null) {
                Enumeration<?> e = header.keys();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    String value = header.getProperty(key);
                    String l = key + ": " + value + "\r\n";
//               if(debug) BrowserUtils.LOGRUN(l);
                    pw.print(l);
                }
            }
            pw.print("\r\n");
            pw.flush();
            if (isInput != null && !"HEAD".equals(method))
                copyStream(isInput, out, buf, sendCount);
            else if (errMsg != null) {
                pw.print(errMsg);
                pw.flush();
            }
            out.flush();
            out.close();
        } catch (SocketException e) {
            System.out.println("------"+e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (Throwable t) {
            }
        }
    }
}

/**
 * Seekable InputStream.
 * Abstract, you must add implementation for your purpose.
 */
class DecodeInputStream extends FileInputStream {
    int count = 0;

    public DecodeInputStream(File file) throws FileNotFoundException {
        super(file);
    }

    public void seek(long seed){
        try {
            this.skip(seed);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int read(byte b[], int off, int len) throws IOException {
        count++;
        int c = super.read(b, off, len);

        for (int i=0; i<b.length; i++) {
			//b[i] = (byte)(b[i]-1);
            b[i] = (byte)(b[i]);
		}
        return c;
    }
}