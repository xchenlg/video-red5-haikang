package chen.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

/**
 * 测试
 * 
 */
@Controller
@RequestMapping("/video")
public class videoController {

	protected static Logger logger = LoggerFactory.getLogger(PersonController.class);

	private Process p;
	public static Date date = new Date();
	private NativeLong uid_turn = new NativeLong(-1);
	private NativeLong uid_back = new NativeLong(-1);
	private static HCNetSDK sdk_turn;
	private static HCNetSDK sdk_playBack;
	private String fileName;
	private OutputStream out = null;
	private static NativeLong m_lPlayHandle = null;// 播放句柄
	public NativeLong lChannel;

	// 从 application.properties 中读取配置，如取不到默认值为Hello Shanhy
	@Value("${application.hello:Hello Angel}")
	private String hello;
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

	@RequestMapping("/videoJsp")
	public String videoJsp(Map<String, Object> map) {
		logger.info("HelloController.helloJsp().hello={}", hello);
		map.put("hello", hello);
		return "videoJsp";
	}

	/**
	 * 左上操作
	 */
	@RequestMapping("/turnLeftUp")
	public void turnLeftUp() {
		turnOperate(HCNetSDK.UP_LEFT);
	}

	/**
	 * 左下操作
	 */
	@RequestMapping("/turnDownLeft")
	public void turnDownLeft() {
		turnOperate(HCNetSDK.DOWN_LEFT);
	}

	/**
	 * 右上操作
	 */
	@RequestMapping("/turnUpRight")
	public void turnUpRight() {
		turnOperate(HCNetSDK.UP_RIGHT);
	}

	/**
	 * 右下操作
	 */
	@RequestMapping("/turnDownRight")
	public void turnDownRight() {
		turnOperate(HCNetSDK.DOWN_RIGHT);
	}

	/**
	 * 上操作
	 */
	@RequestMapping("/turnUp")
	public void turnUp() {
		turnOperate(HCNetSDK.TILT_UP);
	}

	/**
	 * 下操作
	 */
	@RequestMapping("/turnDown")
	public void turnDown() {
		turnOperate(HCNetSDK.TILT_DOWN);
	}

	/**
	 * 左操作
	 */
	@RequestMapping("/turnLeft")
	public void turnLeft() {
		turnOperate(HCNetSDK.PAN_LEFT);
	}

	/**
	 * 右操作
	 */
	@RequestMapping("/turnRight")
	public void turnRight() {
		turnOperate(HCNetSDK.PAN_RIGHT);
	}

	/**
	 * 近操作
	 */
	@RequestMapping("/turnIn")
	public void turnIn() {
		turnOperate(HCNetSDK.ZOOM_IN);
	}

	/**
	 * 远操作
	 */
	@RequestMapping("/turnOut")
	public void turnOut() {
		turnOperate(HCNetSDK.ZOOM_OUT);
	}

	/**
	 * 操作云控制
	 * 
	 * @param operate
	 */
	private void turnOperate(int operate) {

		if (sdk_turn == null) {
			sdk_turn = initSdk("turn");
		}

		// 用户参数
		HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
		m_strClientInfo.lChannel = lChannel;
		// 预览句柄
		NativeLong lPreviewHandle = sdk_turn.NET_DVR_RealPlay_V30(uid_turn, m_strClientInfo, null, null, true);

		sdk_turn.NET_DVR_PTZControl(lPreviewHandle, operate, 0);
		// 控制操作，左方向停止
		try {
			Thread.sleep(80);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		sdk_turn.NET_DVR_PTZControl(lPreviewHandle, operate, 1);
	}

	private HCNetSDK initSdk(String type) {

		HCNetSDK sdk = (HCNetSDK) Native.loadLibrary(
				Device_Play.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
						+ "chen\\sdk\\HCNetSDK",
				HCNetSDK.class);

		if (!sdk.NET_DVR_Init()) {
			System.out.println("SDK初始化失败");
		}

		NET_DVR_DEVICEINFO_V30 devinfo = new NET_DVR_DEVICEINFO_V30();// 设备信息
		boolean bRet = false;
		if ("turn".equals(type)) {
			bRet = setUid_turn(sdk, devinfo);
		} else
			bRet = setUid_back(sdk, devinfo);

		if (bRet == true) {
			lChannel = new NativeLong(devinfo.byStartChan + 32);
		} else {
			lChannel = new NativeLong(devinfo.byStartChan);
		}

		return sdk;
	}

	private boolean setUid_turn(HCNetSDK sdk, NET_DVR_DEVICEINFO_V30 devinfo) {

		if (uid_turn.longValue() > -1) {
			// 先注销
			sdk.NET_DVR_Logout_V30(uid_turn);
			uid_turn = new NativeLong(-1);
		}

		uid_turn = sdk.NET_DVR_Login_V30(sdk_ip, Short.valueOf(sdk_port), sdk_user, sdk_password, devinfo);// 返回一个用户编号，同时将设备信息写入devinfo
		int Iuid = uid_turn.intValue();
		if (Iuid < 0) {
			System.out.println("设备注册失败");
		}

		// 设备信息
		// for (int iChannum = 0; iChannum < devinfo.byChanNum; iChannum++) {
		// System.out.println(iChannum + "==========" + devinfo.byStartChan);
		// }

		IntByReference ibrBytesReturned = new IntByReference(0);// 获取IP接入配置参数
		boolean bRet = false;

		HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
		m_strIpparaCfg.write();
		Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
		bRet = sdk.NET_DVR_GetDVRConfig(uid_turn, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0), lpIpParaConfig,
				m_strIpparaCfg.size(), ibrBytesReturned);
		m_strIpparaCfg.read();

		return bRet;
	}

	private boolean setUid_back(HCNetSDK sdk, NET_DVR_DEVICEINFO_V30 devinfo) {

		if (uid_back.longValue() > -1) {
			// 先注销
			sdk.NET_DVR_Logout_V30(uid_back);
			uid_back = new NativeLong(-1);
		}

		uid_back = sdk.NET_DVR_Login_V30(sdk_ip, Short.valueOf(sdk_port), sdk_user, sdk_password, devinfo);// 返回一个用户编号，同时将设备信息写入devinfo
		int Iuid = uid_back.intValue();
		if (Iuid < 0) {
			System.out.println("设备注册失败");
		}

		// 设备信息
		// for (int iChannum = 0; iChannum < devinfo.byChanNum; iChannum++) {
		// System.out.println(iChannum + "==========" + devinfo.byStartChan);
		// }

		IntByReference ibrBytesReturned = new IntByReference(0);// 获取IP接入配置参数
		boolean bRet = false;

		HCNetSDK.NET_DVR_IPPARACFG m_strIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
		m_strIpparaCfg.write();
		Pointer lpIpParaConfig = m_strIpparaCfg.getPointer();
		bRet = sdk.NET_DVR_GetDVRConfig(uid_back, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(0), lpIpParaConfig,
				m_strIpparaCfg.size(), ibrBytesReturned);
		m_strIpparaCfg.read();

		return bRet;
	}

	@ResponseBody
	@RequestMapping("/liveVideo")
	public String liveVideo() {
		date = new Date();
		new Thread() {
			@Override
			public void run() {
				try {
					live();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

		return rtmpVideo_url;
	}

	@ResponseBody
	@RequestMapping("/closeVideo")
	public String closeVideo() {

		date = new Date();
		if (p != null)
			p.destroy();

		return rtmpVideo_url;
	}

	public void live() throws Exception {

		date = new Date();

		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		executor.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				if (new Date().getTime() - date.getTime() >= 1000 * 60 * 30) {
					p.destroy();
				}
			}
		}, 0, 30, TimeUnit.MINUTES);

		String path = videoController.class.getClassLoader().getResource("").getPath();
		String commend = path + "ffmpeg -i " + "\"" + rtspVideo_url + "\" -b 400k -f flv -r 8 -s 1280x720 -an " + "\""
				+ rtmpVideo_url + "\"";

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

		public StreamGobbler() {

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

	@ResponseBody
	@RequestMapping("/playBackByTime")
	public String playBackByTime(String sDate, String eDate) {

		if (sdk_playBack == null) {
			sdk_playBack = initSdk("back");
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

		m_lPlayHandle = sdk_playBack.NET_DVR_PlayBackByTime(uid_back, lChannel, struStartTime, struStopTime, null);

		if (m_lPlayHandle.intValue() == -1) {
			logger.info("按时间回放失败!");
			return "";
		} else {
			FRealDataCallBack fPlayDataCallBack = new FRealDataCallBack();
			fileName = UUID.randomUUID().toString().substring(0, 5);
			sdk_playBack.NET_DVR_SetPlayDataCallBack(m_lPlayHandle, fPlayDataCallBack, uid_back.intValue());
			// 还要调用该接口才能开始回放
			sdk_playBack.NET_DVR_PlayBackControl(m_lPlayHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
			System.out.println("开始回放");
		}

		File f = new File(Device_Play.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
				+ "files\\");
		if (!f.exists()) {
			f.mkdirs();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!new File(f.getAbsolutePath() + "\\" + fileName + ".h264").exists()) {
			return "";
		}

		return fileName;
	}

	@ResponseBody
	@RequestMapping("/stopPlayBack")
	public String stopPlayBack() {
		sdk_playBack.NET_DVR_PlayBackControl(m_lPlayHandle, HCNetSDK.NET_DVR_PLAYSTOPAUDIO, 0, null);
		sdk_playBack.NET_DVR_StopPlayBack(m_lPlayHandle);
		// m_lPlayHandle.setValue(-1);
		System.out.println("关闭回放。。。。");

		return "success";
	}

	class FRealDataCallBack implements HCNetSDK.FPlayDataCallBack {
		@Override
		public void invoke(NativeLong lPlayHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, int dwUser) {
			System.out.println("回调函数。。。。。。。");
			try {
				File f = new File(
						Device_Play.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
								+ "files\\");
				if (!f.exists()) {
					f.mkdirs();
				}
				out = new FileOutputStream(new File(
						Device_Play.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
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
					out.flush();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			System.gc();
		}
	}

}