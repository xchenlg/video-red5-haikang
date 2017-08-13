package chen.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;

import chen.sdk.src.ClientDemo.HCNetSDK;
import chen.sdk.src.ClientDemo.HCNetSDK.NET_DVR_DEVICEINFO_V30;
import chen.sdk.src.ClientDemo.HCNetSDK.NET_DVR_TIME;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;
import net.sf.json.JSONArray;

/**
 * 测试
 * 
 */
@Controller
@RequestMapping("/video")
public class videoController {

	protected static Logger logger = LoggerFactory.getLogger(PersonController.class);

	// 文件操作句柄
	private Map<String, NativeLong> file_handler = new HashMap<>();
	// 通道对应进程
	// private Map<String, NativeLong> chanel_process = new HashMap<>();
	private Set<String> chanel_live = new HashSet<>();
	// 回放每个端口对应的数量
	private Map<String, Integer> portCountMap = new ConcurrentHashMap<>();
	// 每个文件对应的端口
	private Map<String, String> filePortMap = new ConcurrentHashMap<>();

	public static AtomicInteger countBack = new AtomicInteger();
	private Sdk s;

	public static Date date = new Date();

	@Value("${rtspVideo_url}")
	private String rtspVideo_url;
	@Value("${rtmpVideo_url}")
	private String rtmpVideo_url;
	@Value("${sdk_ip}")
	private String sdk_ip;
	@Value("${sdk_port}")
	private String sdk_port;
	@Value("${sdk_user}")
	private String sdk_user;
	@Value("${sdk_password}")
	private String sdk_password;
	@Value("${playbackport}")
	private String playbackport;

	@RequestMapping("/videoJsp")
	public String videoJsp() {
		return "videoJsp";
	}

	/**
	 * 左上操作
	 */
	@ResponseBody
	@RequestMapping("/turnLeftUp")
	public void turnLeftUp(String chanel) {
		turnOperate(HCNetSDK.UP_LEFT, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 左下操作
	 */
	@ResponseBody
	@RequestMapping("/turnDownLeft")
	public void turnDownLeft(String chanel) {
		turnOperate(HCNetSDK.DOWN_LEFT, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 右上操作
	 */
	@ResponseBody
	@RequestMapping("/turnUpRight")
	public void turnUpRight(String chanel) {
		turnOperate(HCNetSDK.UP_RIGHT, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 右下操作
	 */
	@ResponseBody
	@RequestMapping("/turnDownRight")
	public void turnDownRight(String chanel) {
		turnOperate(HCNetSDK.DOWN_RIGHT, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 上操作
	 */
	@ResponseBody
	@RequestMapping("/turnUp")
	public void turnUp(String chanel) {
		turnOperate(HCNetSDK.TILT_UP, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 下操作
	 */
	@ResponseBody
	@RequestMapping("/turnDown")
	public void turnDown(String chanel) {
		turnOperate(HCNetSDK.TILT_DOWN, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 左操作
	 */
	@ResponseBody
	@RequestMapping("/turnLeft")
	public void turnLeft(String chanel) {
		turnOperate(HCNetSDK.PAN_LEFT, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 右操作
	 */
	@ResponseBody
	@RequestMapping("/turnRight")
	public void turnRight(String chanel) {
		turnOperate(HCNetSDK.PAN_RIGHT, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 近操作
	 */
	@ResponseBody
	@RequestMapping("/turnIn")
	public void turnIn(String chanel) {
		turnOperate(HCNetSDK.ZOOM_IN, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 远操作
	 */
	@ResponseBody
	@RequestMapping("/turnOut")
	public void turnOut(String chanel) {
		turnOperate(HCNetSDK.ZOOM_OUT, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 操作云控制
	 * 
	 * @param operate
	 */
	private void turnOperate(int operate, NativeLong chanel) {

		// 用户参数
		HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
		m_strClientInfo.lChannel = chanel;

		NativeLong lPreviewHandle = s.getlPreviewHandle_turn();
		NativeLong uid = s.getUid();
		HCNetSDK sdk = s.getSdk();
		// 预览句柄
		if (lPreviewHandle == null) {
			lPreviewHandle = sdk.NET_DVR_RealPlay_V30(uid, m_strClientInfo, null, null, true);
			s.setlPreviewHandle_turn(lPreviewHandle);
		}

		sdk.NET_DVR_PTZControl(lPreviewHandle, operate, 0);
		// 控制操作，左方向停止
		try {
			Thread.sleep(280);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sdk.NET_DVR_PTZControl(lPreviewHandle, operate, 1);
	}

	/**
	 * 获取通道号
	 */
	@ResponseBody
	@RequestMapping("/getChanels")
	public List<NativeLong> getChanels() {
		if (s == null || s.getUid() == null) {
			initSdk();
		}

		return s.getChanels();
	}

	private void initSdk() {

		s = new Sdk();
		HCNetSDK sdk = (HCNetSDK) Native.loadLibrary(
				videoController.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
						+ "chen\\sdk\\HCNetSDK",
				HCNetSDK.class);

		if (!sdk.NET_DVR_Init()) {
			System.out.println("SDK初始化失败");
		}

		NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new NET_DVR_DEVICEINFO_V30();// 设备信息
		// if (uid_turn.longValue() > -1) {
		// 先注销
		// sdk.NET_DVR_Logout_V30(uid_turn); uid_turn = new NativeLong(-1); }

		NativeLong uid = sdk.NET_DVR_Login_V30(sdk_ip, Short.valueOf(sdk_port), sdk_user, sdk_password,
				m_strDeviceInfo);// 返回一个用户编号，同时将设备信息写入devinfo
		int Iuid = uid.intValue();
		if (Iuid < 0) {
			System.out.println("设备注册失败");
		}

		s.setUid(uid);

		IntByReference ibrBytesReturned = new IntByReference(0);// 获取IP接入配置参数
		boolean bRet = false;

		HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
		m_strIpparaCfg.write();
		Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
		bRet = sdk.NET_DVR_GetDVRConfig(uid, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0), lpIpParaConfig,
				m_strIpparaCfg.size(), ibrBytesReturned);
		m_strIpparaCfg.read();

		List<NativeLong> chanels = new ArrayList<>();
		if (!bRet) {
			// 设备不支持,则表示没有IP通道
			for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
				chanels.add(new NativeLong(m_strDeviceInfo.byStartChan));
			}
		} else {
			// 设备支持IP通道
			for (int iChannum = 0; iChannum < m_strDeviceInfo.byChanNum; iChannum++) {
				if (m_strIpparaCfg.byAnalogChanEnable[iChannum] == 1) {
					chanels.add(new NativeLong(m_strDeviceInfo.byStartChan));
				}
			}
			for (int iChannum = 0; iChannum < HCNetSDK.MAX_IP_CHANNEL; iChannum++)
				if (m_strIpparaCfg.struIPChanInfo[iChannum].byEnable == 1) {
					chanels.add(new NativeLong(m_strDeviceInfo.byStartChan + 32));
				}
		}
		s.setChanels(chanels);
		s.setSdk(sdk);
	}

	@ResponseBody
	@RequestMapping("/liveVideo")
	public String liveVideo(String chanel) {

		if (chanel_live.contains(chanel)) {
			return rtmpVideo_url + chanel;
		}

		new Thread() {
			@Override
			public void run() {
				try {
					live(chanel);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

		return rtmpVideo_url + chanel;
	}

	@ResponseBody
	@RequestMapping("/closeVideo")
	public String closeVideo() {

		date = new Date();
		// if (p != null)
		// p.destroy();

		return rtmpVideo_url;
	}

	public void live(String chanel) throws Exception {

		// date = new Date();
		// ScheduledExecutorService executor =
		// Executors.newSingleThreadScheduledExecutor();
		// executor.scheduleAtFixedRate(new Runnable() {
		// @Override
		// public void run() {
		// if (new Date().getTime() - date.getTime() >= 1000 * 60 * 30) {
		// p.destroy();
		// }
		// }
		// }, 0, 30, TimeUnit.MINUTES);
		logger.info("直播通道号为====={}", chanel);
		chanel_live.add(chanel);
		Process p = null;
		String path = videoController.class.getClassLoader().getResource("").getPath();
		String commend = path + "ffmpeg -i " + "\"" + rtspVideo_url.replace("chanel", "ch" + chanel)
				+ "\" -b 400k -f flv -r 8 -s 1280x720 -an " + "\"" + rtmpVideo_url.replace("live", "live" + chanel)
				+ "\"";

		Runtime rt = Runtime.getRuntime();
		p = rt.exec(commend);

		StreamGobbler sg1 = new StreamGobbler(p.getInputStream(), "Console");
		StreamGobbler sg2 = new StreamGobbler(p.getErrorStream(), "Error");
		sg1.start();
		sg2.start();
		p.waitFor();
		p.destroy();

	}

	public class StreamGobbler extends Thread {
		InputStream is;
		String type;

		public StreamGobbler(InputStream is, String type) {
			this.is = is;
			this.type = type;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					if (type.equals("Error")) {
						System.err.println(line);
					} else {
						System.out.println(line);
					}
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public String connectBack(String sDate, String eDate, String chanel, String port) {
		// 创建HttpClientBuilder
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// HttpClient
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

		HttpPost httpPost = new HttpPost("http://localhost:" + port + "/video/playBackByTime");
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpPost.setConfig(requestConfig);
		// 创建参数队列
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("sDate", sDate));
		formparams.add(new BasicNameValuePair("eDate", eDate));
		formparams.add(new BasicNameValuePair("chanel", chanel));

		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httpPost.setEntity(entity);

			HttpResponse httpResponse;
			// post请求
			httpResponse = closeableHttpClient.execute(httpPost);

			// getEntity()
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				// 打印响应内容
				String fileName = EntityUtils.toString(httpEntity, "UTF-8");
				if (!"".equals(fileName) && !"false".equals(fileName)) {
					filePortMap.put(fileName, port);
					portCountMap.put(port, 1);
				}
				return fileName;
			}
			// 释放资源
			closeableHttpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	@ResponseBody
	@RequestMapping("/playBackByTime")
	public String playBackByTime(String sDate, String eDate, String chanel) {

		// if (countBack.get() >= 1) {
		// String[] ports = playbackport.split(",");
		// for (String port : ports) {
		// if (!portCountMap.containsKey(port)) {
		// return connectBack(sDate, eDate, chanel, port);
		// }
		// }
		//
		// return "full";
		// }

		if (s == null || s.getUid() == null) {
			initSdk();
		}

		if (s.getlPreviewHandle_back() != null) {
			stopPlayBack("");
		}

		NET_DVR_TIME struStartTime = new NET_DVR_TIME();
		NET_DVR_TIME struStopTime = new NET_DVR_TIME();

		DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		DateTime dt_s = DateTime.parse(sDate, format);
		DateTime dt_e = DateTime.parse(eDate, format);

		struStartTime.dwYear = new Integer(dt_s.getYear());// 开始时间
		struStartTime.dwMonth = new Integer(dt_s.getMonthOfYear());
		struStartTime.dwDay = new Integer(dt_s.getDayOfMonth());
		struStartTime.dwHour = new Integer(dt_s.getHourOfDay());
		struStartTime.dwMinute = new Integer(dt_s.getMinuteOfHour());
		struStartTime.dwSecond = new Integer(dt_s.getSecondOfMinute());
		struStopTime.dwYear = new Integer(dt_e.getYear());// 结束时间
		struStopTime.dwMonth = new Integer(dt_e.getMonthOfYear());
		struStopTime.dwDay = new Integer(dt_e.getDayOfMonth());
		struStopTime.dwHour = new Integer(dt_e.getHourOfDay());
		struStopTime.dwMinute = new Integer(dt_e.getMinuteOfHour());
		struStopTime.dwSecond = new Integer(dt_e.getSecondOfMinute());

		HCNetSDK sdk = s.getSdk();
		NativeLong uid = s.getUid();

		NativeLong m_lPlayHandle = sdk.NET_DVR_PlayBackByTime(uid, new NativeLong(Long.valueOf(chanel)), struStartTime,
				struStopTime, null);

		File f = new File(
				videoController.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
						+ "files\\");
		if (!f.exists()) {
			f.mkdirs();
		}

		String fileName = "";

		if (m_lPlayHandle.intValue() == -1) {
			logger.info("按时间回放失败!");
			return "";
		} else {
			s.setlPreviewHandle_back(m_lPlayHandle);
			fileName = UUID.randomUUID().toString().substring(0, 5);
			file_handler.put(fileName, m_lPlayHandle);
			FRealDataCallBack fPlayDataCallBack = new FRealDataCallBack(fileName);
			sdk.NET_DVR_SetPlayDataCallBack(m_lPlayHandle, fPlayDataCallBack, uid.intValue());
			// 还要调用该接口才能开始回放
			sdk.NET_DVR_PlayBackControl(m_lPlayHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
			System.out.println("开始回放");
		}

		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!new File(f.getAbsolutePath() + "\\" + fileName + ".h264").exists()) {
			return "";
		}

		countBack.addAndGet(1);

		return fileName;
	}

	@ResponseBody
	@RequestMapping("/stopPlayBack")
	public String stopPlayBack(String fileName) {

		// if (file_handler.get(fileName) != null) {
		HCNetSDK sdk = s.getSdk();
		NativeLong handle = s.getlPreviewHandle_back();
		sdk.NET_DVR_PlayBackControl(handle, HCNetSDK.NET_DVR_PLAYSTOPAUDIO, 0, null);
		sdk.NET_DVR_StopPlayBack(handle);
		s.setlPreviewHandle_back(null);
		countBack.decrementAndGet();
		System.out.println("关闭回放。。。。");
		// } else if (filePortMap.containsKey(fileName)) {
		// connectCloseBack(fileName, filePortMap.get(fileName));
		// }

		return "success";
	}

	public String connectCloseBack(String fileName, String port) {
		// 创建HttpClientBuilder
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// HttpClient
		CloseableHttpClient closeableHttpClient = httpClientBuilder.build();

		HttpPost httpPost = new HttpPost("http://localhost:" + port + "/video/stopPlayBack");
		RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(20000).setConnectTimeout(20000).build();
		httpPost.setConfig(requestConfig);
		// 创建参数队列
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		formparams.add(new BasicNameValuePair("fileName", fileName));

		UrlEncodedFormEntity entity;
		try {
			entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httpPost.setEntity(entity);

			// post请求
			closeableHttpClient.execute(httpPost);
			// getEntity()
			// 释放资源
			closeableHttpClient.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		portCountMap.remove(port);
		filePortMap.remove(fileName);
		return "";
	}

	class FRealDataCallBack implements HCNetSDK.FPlayDataCallBack {

		private String fileName;

		public FRealDataCallBack(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void invoke(NativeLong lPlayHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, int dwUser) {

			System.out.println("回调函数操作文件:" + fileName);
			OutputStream out = null;
			try {

				out = new FileOutputStream(new File(
						videoController.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
								+ "files\\" + fileName + ".h264"),
						true);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			byte[] bytes = pBuffer.getPointer().getByteArray(0, dwBufSize);
			try {
				out.write(bytes, 0, bytes.length);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			System.gc();
		}
	}

}
