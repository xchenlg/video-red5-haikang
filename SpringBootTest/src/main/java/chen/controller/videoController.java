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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;

import chen.domain.EasyUIDataGrid;
import chen.domain.JsonResult;
import chen.domain.PChanel;
import chen.domain.PageHelper;
import chen.domain.TChanel;
import chen.domain.TSdk;
import chen.domain.Tree;
import chen.sdk.src.ClientDemo.HCNetSDK;
import chen.sdk.src.ClientDemo.HCNetSDK.NET_DVR_DEVICEINFO_V30;
import chen.sdk.src.ClientDemo.HCNetSDK.NET_DVR_TIME;
import chen.service.ChanelService;
import chen.service.SdkService;
import net.sf.ehcache.util.concurrent.ConcurrentHashMap;

/**
 * 测试
 * 
 */
@Controller
@RequestMapping("/video")
public class VideoController {

	protected static Logger logger = LoggerFactory.getLogger(VideoController.class);

	@Autowired
	private SdkService sdkService;
	@Autowired
	private ChanelService chanelService;

	private Map<String, Sdk> sdks = new ConcurrentHashMap<>();
	// 文件操作句柄
//	private Map<String, NativeLong> file_handler = new HashMap<>();
	// 通道对应lPreviewHandle
	private Map<String, NativeLong> chanel_lPreviewHandle = new HashMap<>();

	private BlockingQueue<Process> liveBlock = new LinkedBlockingQueue<>();
	// private Set<String> chanel_live = new HashSet<>();
	// 回放每个用户对应的回调函数
	private Map<String, Integer> userBackMap = new ConcurrentHashMap<>();
	private FRealDataCallBack fPlayDataCallBack;
	private FRealDataCallBack1 fPlayDataCallBack1;
	private FRealDataCallBack2 fPlayDataCallBack2;
	private FRealDataCallBack3 fPlayDataCallBack3;
	private FRealDataCallBack4 fPlayDataCallBack4;
	private String fileName;
	private String fileName1;
	private String fileName2;
	private String fileName3;
	private String fileName4;

	public static AtomicInteger countBack = new AtomicInteger();
	// 直播的名称后缀
	public static AtomicInteger live = new AtomicInteger(0);

	public static Date date = new Date();

	// @Value("${rtspVideo_url}")
	// private String rtspVideo_url;
	@Value("${rtmpVideo_url}")
	private String rtmpVideo_url;
	@Value("${queue_size}")
	private String queue_size;

	@RequestMapping("/videoJsp")
	public String videoJsp() {
		return "videoJsp";
	}

	@RequestMapping("/sdkJsp")
	public String sdkJsp() {
		return "sdkJsp";
	}

	@RequestMapping("/chanelJsp")
	public String chanelJsp() {
		return "chanelJsp";
	}

	@RequestMapping("/dataGrid")
	@ResponseBody
	public EasyUIDataGrid dataGrid(PageHelper ph) {
		return sdkService.dataGrid(ph);
	}

	@RequestMapping("/chaneldataGrid")
	@ResponseBody
	public EasyUIDataGrid chaneldataGrid(String id, PageHelper ph) {
		if (StringUtils.isBlank(id)) {
			return new EasyUIDataGrid();
		}
		return chanelService.dataGrid(Long.valueOf(id), ph);
	}

	@RequestMapping("/addPage")
	public String addPage() {
		return "add";
	}

	@RequestMapping("/addChanelPage")
	public String addChanelPage(String sdkid, HttpServletRequest request) {

		PChanel p = new PChanel();
		p.setSdkId(Long.valueOf(sdkid));
		request.setAttribute("chanel", p);

		return "chanelAdd";
	}

	@RequestMapping("/editPage")
	public String editPage(String id, HttpServletRequest request) {

		request.setAttribute("sdk", sdkService.findById(id));
		return "add";
	}

	@RequestMapping("/editChanelPage")
	public String editChanelPage(String id, HttpServletRequest request) {

		request.setAttribute("chanel", chanelService.findById(id));
		return "chanelAdd";
	}

	@ResponseBody
	@RequestMapping("/getSdkTree")
	public List<Tree> getSdkTree(HttpServletRequest request) {
		return sdkService.getSdkTree();
	}

	/**
	 * 添加
	 * 
	 * @return
	 */
	@RequestMapping("/add")
	@ResponseBody
	public JsonResult add(TSdk sdk, String create_date) {
		JsonResult j = new JsonResult();
		try {
			sdk.setUpdateDate(new Date());
			if (sdk.getId() == null) {
				sdk.setCreateDate(new Date());
				Integer max = sdkService.findMaxOrder();
				sdk.setSdkOrder(max == null ? 0 : max + 1);
			} else {
				DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
				// 时间解析
				DateTime dateTime = DateTime.parse(create_date, format);
				sdk.setCreateDate(dateTime.toDate());
			}
			sdkService.add(sdk);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public JsonResult delete(String id) {

		JsonResult j = new JsonResult();
		sdkService.delete(id);
		j.setMsg("删除成功！");
		j.setSuccess(true);
		return j;
	}

	/**
	 * 向上或者向下移动一位
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/moveUpOrDown")
	public JsonResult moveUpOrDown(String flag, String id) {

		JsonResult result = new JsonResult();
		sdkService.moveUpOrDown(id, flag);
		result.setSuccess(true);

		return result;
	}

	/**
	 * 添加
	 * 
	 * @return
	 */
	@RequestMapping("/addChanel")
	@ResponseBody
	public JsonResult addChanel(TChanel sdk, String sdkId,String create_date) {
		JsonResult j = new JsonResult();
		try {
			sdk.setUpdateDate(new Date());
			sdk.setSdk(sdkService.findById(sdkId));
			if (sdk.getId() == null) {
				sdk.setCreateDate(new Date());
				Integer max = chanelService.findMaxOrder(Long.valueOf(sdkId));
				sdk.setChanelOrder(max == null ? 0 : max + 1);
			} else {
				DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS");
				// 时间解析
				DateTime dateTime = DateTime.parse(create_date, format);
				sdk.setCreateDate(dateTime.toDate());
			}
			chanelService.add(sdk);
			j.setSuccess(true);
			j.setMsg("添加成功！");
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
	}

	/**
	 * 删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteChanel")
	@ResponseBody
	public JsonResult deleteChanel(String id) {

		JsonResult j = new JsonResult();
		chanelService.delete(id);
		j.setMsg("删除成功！");
		j.setSuccess(true);
		return j;
	}

	/**
	 * 向上或者向下移动一位
	 * 
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/moveUpOrDownChanel")
	public JsonResult moveUpOrDownChanel(String flag, String id, String sdkId) {

		JsonResult result = new JsonResult();
		chanelService.moveUpOrDown(id, flag, Long.valueOf(sdkId));
		result.setSuccess(true);

		return result;
	}

	/**
	 * 左上操作
	 */
	@ResponseBody
	@RequestMapping("/turnLeftUp")
	public void turnLeftUp(String ip, String port, String chanel) {
		turnOperate(HCNetSDK.UP_LEFT, ip, port, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 左下操作
	 */
	@ResponseBody
	@RequestMapping("/turnDownLeft")
	public void turnDownLeft(String ip, String port, String chanel) {
		turnOperate(HCNetSDK.DOWN_LEFT, ip, port, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 右上操作
	 */
	@ResponseBody
	@RequestMapping("/turnUpRight")
	public void turnUpRight(String ip, String port, String chanel) {
		turnOperate(HCNetSDK.UP_RIGHT, ip, port, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 右下操作
	 */
	@ResponseBody
	@RequestMapping("/turnDownRight")
	public void turnDownRight(String ip, String port, String chanel) {
		turnOperate(HCNetSDK.DOWN_RIGHT, ip, port, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 上操作
	 */
	@ResponseBody
	@RequestMapping("/turnUp")
	public void turnUp(String ip, String port, String chanel) {
		turnOperate(HCNetSDK.TILT_UP, ip, port, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 下操作
	 */
	@ResponseBody
	@RequestMapping("/turnDown")
	public void turnDown(String ip, String port, String chanel) {
		turnOperate(HCNetSDK.TILT_DOWN, ip, port, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 左操作
	 */
	@ResponseBody
	@RequestMapping("/turnLeft")
	public void turnLeft(String ip, String port, String chanel) {
		turnOperate(HCNetSDK.PAN_LEFT, ip, port, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 右操作
	 */
	@ResponseBody
	@RequestMapping("/turnRight")
	public void turnRight(String ip, String port, String chanel) {
		turnOperate(HCNetSDK.PAN_RIGHT, ip, port, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 近操作
	 */
	@ResponseBody
	@RequestMapping("/turnIn")
	public void turnIn(String ip, String port, String chanel) {
		turnOperate(HCNetSDK.ZOOM_IN, ip, port, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 远操作
	 */
	@ResponseBody
	@RequestMapping("/turnOut")
	public void turnOut(String ip, String port, String chanel) {
		turnOperate(HCNetSDK.ZOOM_OUT, ip, port, new NativeLong(Long.valueOf(chanel)));
	}

	/**
	 * 操作云控制
	 * 
	 * @param operate
	 */
	private synchronized void turnOperate(int operate, String ip, String port, NativeLong chanel) {

		if (ip != null && !sdks.containsKey(ip)) {
			initSdk(ip, port);
		}

		Sdk s = sdks.get(ip + port);

		// 用户参数
		HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
		m_strClientInfo.lChannel = chanel;

		NativeLong lPreviewHandle = s.getlPreviewHandle_turn();
		NativeLong uid = s.getUid();
		HCNetSDK sdk = s.getSdk();
		// 预览句柄
		if (chanel_lPreviewHandle.containsKey(ip + chanel)) {
			s.setlPreviewHandle_turn(chanel_lPreviewHandle.get(ip + chanel));
		} else {
			lPreviewHandle = sdk.NET_DVR_RealPlay_V30(uid, m_strClientInfo, null, null, true);
			s.setlPreviewHandle_turn(lPreviewHandle);
			chanel_lPreviewHandle.put(ip + chanel + "", lPreviewHandle);
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
	public List<PChanel> getChanels(String id) {

		if (StringUtils.isBlank(id)) {
			return new ArrayList<>();
		}

		return chanelService.findBySdk_IdOrderByChanelOrderAsc(id);
	}

	@ResponseBody
	@RequestMapping("/getSdks")
	public List<TSdk> getSdks() {

		return sdkService.findAll();
	}

	private void initSdk(String ip, String port) {

		Sdk s = new Sdk();
		HCNetSDK sdk = (HCNetSDK) Native.loadLibrary(
				VideoController.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
						+ "chen\\sdk\\HCNetSDK",
				HCNetSDK.class);

		if (!sdk.NET_DVR_Init()) {
			System.out.println("SDK初始化失败");
		}

		NET_DVR_DEVICEINFO_V30 m_strDeviceInfo = new NET_DVR_DEVICEINFO_V30();// 设备信息

		// 根据ip查询sdk配置情况
		List<TSdk> tsdks = sdkService.findByIp(ip, port);
		if (tsdks.size() <= 0) {
			return;
		}

		NativeLong uid = sdk.NET_DVR_Login_V30(tsdks.get(0).getIp(), Short.valueOf(tsdks.get(0).getPort()),
				tsdks.get(0).getUser(), tsdks.get(0).getPassword(), m_strDeviceInfo);// 返回一个用户编号，同时将设备信息写入devinfo
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

		sdks.put(ip + port, s);
	}

	@ResponseBody
	@RequestMapping("/liveVideo")
	public String liveVideo(TSdk sdk, String chanel) {

		if (liveBlock.size() >= Integer.valueOf(queue_size)) {
			try {
				liveBlock.take().destroy();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Integer liveNum = live.incrementAndGet();
		new Thread() {
			@Override
			public void run() {
				try {
					live(sdk, chanel, liveNum);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

		return rtmpVideo_url + liveNum;
	}

	@ResponseBody
	@RequestMapping("/closeVideo")
	public String closeVideo() {

		date = new Date();
		// if (p != null)
		// p.destroy();

		return rtmpVideo_url;
	}

	public void live(TSdk sdk, String chanel, Integer size) throws Exception {

		logger.info("直播通道号为====={}", chanel);
		// chanel_live.add(chanel);
		Process p = null;
		String path = VideoController.class.getClassLoader().getResource("").getPath();

		String rtsp = "rtsp://" + sdk.getUser() + ":" + sdk.getPassword() + "@" + sdk.getIp() + ":" + sdk.getPort()
				+ "/h264/ch" + chanel + "/main/av_stream";
		String commend = path + "ffmpeg -i " + "\"" + rtsp + "\" -b 400k -f flv -r 8 -s 1280x720 -an " + "\""
				+ rtmpVideo_url.replace("live", "live" + size) + "\"";

		Runtime rt = Runtime.getRuntime();
		p = rt.exec(commend);
		// 将直播进程放到map中
		liveBlock.put(p);

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

	@ResponseBody
	@RequestMapping("/playBackByTime")
	public String playBackByTime(String ip, String port, String sDate, String eDate, String chanel, String user) {

		if (ip != null && !sdks.containsKey(ip)) {
			initSdk(ip, port);
		}

		Sdk s = sdks.get(ip + port);

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

		File f = new File(
				VideoController.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
						+ "files\\");
		if (!f.exists()) {
			f.mkdirs();
		}

		if (user == null) {
			return "nouser";
		}

		if (userBackMap.get(user) == null) {
			if (userBackMap.size() == 0 || userBackMap.size() == 3) {
				return playBack1(s, chanel, struStartTime, struStopTime, sdk, uid, f, user);
			} else if (userBackMap.size() == 1) {
				return playBack2(s, chanel, struStartTime, struStopTime, sdk, uid, f, user);
			} else if (userBackMap.size() == 2) {
				return playBack3(s, chanel, struStartTime, struStopTime, sdk, uid, f, user);
			} else if (userBackMap.size() == 3) {
				return playBack4(s, chanel, struStartTime, struStopTime, sdk, uid, f, user);
			} else if (userBackMap.size() == 4) {
				return playBack5(s, chanel, struStartTime, struStopTime, sdk, uid, f, user);
			}
		} else if (userBackMap.get(user) == 1) {
			return playBack1(s, chanel, struStartTime, struStopTime, sdk, uid, f, user);
		} else if (userBackMap.get(user) == 2) {
			return playBack2(s, chanel, struStartTime, struStopTime, sdk, uid, f, user);
		} else if (userBackMap.get(user) == 3) {
			return playBack3(s, chanel, struStartTime, struStopTime, sdk, uid, f, user);
		} else if (userBackMap.get(user) == 4) {
			return playBack4(s, chanel, struStartTime, struStopTime, sdk, uid, f, user);
		} else if (userBackMap.get(user) == 5) {
			return playBack5(s, chanel, struStartTime, struStopTime, sdk, uid, f, user);
		}

		return "";

	}

	/**
	 * 
	 * 第一回调函数
	 * 
	 * @param chanel
	 * @param struStartTime
	 * @param struStopTime
	 * @param sdk
	 * @param uid
	 * @param f
	 * @return
	 */
	private String playBack1(Sdk s, String chanel, NET_DVR_TIME struStartTime, NET_DVR_TIME struStopTime, HCNetSDK sdk,
			NativeLong uid, File f, String user) {
		if (s.getlPreviewHandle_back() != null) {
			stopPlayBack(s, 1);
		}
		NativeLong m_lPlayHandle = sdk.NET_DVR_PlayBackByTime(uid, new NativeLong(Long.valueOf(chanel)), struStartTime,
				struStopTime, null);
		if (m_lPlayHandle.intValue() == -1) {
			logger.info("按时间回放失败!");
			return "";
		}
		fileName = UUID.randomUUID().toString().substring(0, 5);
		if (fPlayDataCallBack == null) {
			fPlayDataCallBack = new FRealDataCallBack();
		}
		sdk.NET_DVR_SetPlayDataCallBack(m_lPlayHandle, fPlayDataCallBack, uid.intValue());
		s.setlPreviewHandle_back(m_lPlayHandle);
		// 还要调用该接口才能开始回放
		sdk.NET_DVR_PlayBackControl(m_lPlayHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!new File(f.getAbsolutePath() + "\\" + fileName + ".h264").exists()) {
			return "";
		}
		userBackMap.put(user, 1);
		countBack.addAndGet(1);

		return fileName;
	}

	/**
	 * 
	 * 第二回调函数
	 * 
	 * @param chanel
	 * @param struStartTime
	 * @param struStopTime
	 * @param sdk
	 * @param uid
	 * @param f
	 * @return
	 */
	private String playBack2(Sdk s, String chanel, NET_DVR_TIME struStartTime, NET_DVR_TIME struStopTime, HCNetSDK sdk,
			NativeLong uid, File f, String user) {
		if (s.getlPreviewHandle_back1() != null) {
			stopPlayBack(s, 2);
		}
		NativeLong m_lPlayHandle = sdk.NET_DVR_PlayBackByTime(uid, new NativeLong(Long.valueOf(chanel)), struStartTime,
				struStopTime, null);
		if (m_lPlayHandle.intValue() == -1) {
			logger.info("按时间回放失败!");
			return "";
		}
		fileName1 = UUID.randomUUID().toString().substring(0, 5);
		if (fPlayDataCallBack1 == null) {
			fPlayDataCallBack1 = new FRealDataCallBack1();
		}
		sdk.NET_DVR_SetPlayDataCallBack(m_lPlayHandle, fPlayDataCallBack1, uid.intValue());
		s.setlPreviewHandle_back1(m_lPlayHandle);
		// 还要调用该接口才能开始回放
		sdk.NET_DVR_PlayBackControl(m_lPlayHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!new File(f.getAbsolutePath() + "\\" + fileName1 + ".h264").exists()) {
			return "";
		}
		userBackMap.put(user, 2);
		countBack.addAndGet(1);

		return fileName1;
	}

	/**
	 * 
	 * 第三回调函数
	 * 
	 * @param chanel
	 * @param struStartTime
	 * @param struStopTime
	 * @param sdk
	 * @param uid
	 * @param f
	 * @return
	 */
	private String playBack3(Sdk s, String chanel, NET_DVR_TIME struStartTime, NET_DVR_TIME struStopTime, HCNetSDK sdk,
			NativeLong uid, File f, String user) {
		if (s.getlPreviewHandle_back2() != null) {
			stopPlayBack(s, 3);
		}
		NativeLong m_lPlayHandle = sdk.NET_DVR_PlayBackByTime(uid, new NativeLong(Long.valueOf(chanel)), struStartTime,
				struStopTime, null);
		if (m_lPlayHandle.intValue() == -1) {
			logger.info("按时间回放失败!");
			return "";
		}
		fileName2 = UUID.randomUUID().toString().substring(0, 5);
		if (fPlayDataCallBack2 == null) {
			fPlayDataCallBack2 = new FRealDataCallBack2();
		}
		sdk.NET_DVR_SetPlayDataCallBack(m_lPlayHandle, fPlayDataCallBack2, uid.intValue());
		s.setlPreviewHandle_back2(m_lPlayHandle);
		// 还要调用该接口才能开始回放
		sdk.NET_DVR_PlayBackControl(m_lPlayHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!new File(f.getAbsolutePath() + "\\" + fileName2 + ".h264").exists()) {
			return "";
		}
		userBackMap.put(user, 3);
		countBack.addAndGet(1);

		return fileName2;
	}

	/**
	 * 
	 * 第三回调函数
	 * 
	 * @param chanel
	 * @param struStartTime
	 * @param struStopTime
	 * @param sdk
	 * @param uid
	 * @param f
	 * @return
	 */
	private String playBack4(Sdk s, String chanel, NET_DVR_TIME struStartTime, NET_DVR_TIME struStopTime, HCNetSDK sdk,
			NativeLong uid, File f, String user) {
		if (s.getlPreviewHandle_back3() != null) {
			stopPlayBack(s, 4);
		}
		NativeLong m_lPlayHandle = sdk.NET_DVR_PlayBackByTime(uid, new NativeLong(Long.valueOf(chanel)), struStartTime,
				struStopTime, null);
		if (m_lPlayHandle.intValue() == -1) {
			logger.info("按时间回放失败!");
			return "";
		}
		fileName3 = UUID.randomUUID().toString().substring(0, 5);
		if (fPlayDataCallBack3 == null) {
			fPlayDataCallBack3 = new FRealDataCallBack3();
		}
		sdk.NET_DVR_SetPlayDataCallBack(m_lPlayHandle, fPlayDataCallBack3, uid.intValue());
		s.setlPreviewHandle_back3(m_lPlayHandle);
		// 还要调用该接口才能开始回放
		sdk.NET_DVR_PlayBackControl(m_lPlayHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!new File(f.getAbsolutePath() + "\\" + fileName3 + ".h264").exists()) {
			return "";
		}
		userBackMap.put(user, 4);
		countBack.addAndGet(1);

		return fileName3;
	}

	/**
	 * 
	 * 第三回调函数
	 * 
	 * @param chanel
	 * @param struStartTime
	 * @param struStopTime
	 * @param sdk
	 * @param uid
	 * @param f
	 * @return
	 */
	private String playBack5(Sdk s, String chanel, NET_DVR_TIME struStartTime, NET_DVR_TIME struStopTime, HCNetSDK sdk,
			NativeLong uid, File f, String user) {
		if (s.getlPreviewHandle_back2() != null) {
			stopPlayBack(s, 5);
		}
		NativeLong m_lPlayHandle = sdk.NET_DVR_PlayBackByTime(uid, new NativeLong(Long.valueOf(chanel)), struStartTime,
				struStopTime, null);
		if (m_lPlayHandle.intValue() == -1) {
			logger.info("按时间回放失败!");
			return "";
		}
		fileName4 = UUID.randomUUID().toString().substring(0, 5);
		if (fPlayDataCallBack4 == null) {
			fPlayDataCallBack4 = new FRealDataCallBack4();
		}
		sdk.NET_DVR_SetPlayDataCallBack(m_lPlayHandle, fPlayDataCallBack4, uid.intValue());
		s.setlPreviewHandle_back4(m_lPlayHandle);
		// 还要调用该接口才能开始回放
		sdk.NET_DVR_PlayBackControl(m_lPlayHandle, HCNetSDK.NET_DVR_PLAYSTART, 0, null);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!new File(f.getAbsolutePath() + "\\" + fileName4 + ".h264").exists()) {
			return "";
		}
		userBackMap.put(user, 5);
		countBack.addAndGet(1);

		return fileName4;
	}

	@ResponseBody
	@RequestMapping("/stopPlayBack")
	public String stopPlayBack(String fileName) {

		// if (file_handler.get(fileName) != null) {
		// HCNetSDK sdk = s.getSdk();
		// NativeLong handle = s.getlPreviewHandle_back();
		// sdk.NET_DVR_PlayBackControl(handle, HCNetSDK.NET_DVR_PLAYSTOPAUDIO,
		// 0, null);
		// sdk.NET_DVR_StopPlayBack(handle);
		// s.setlPreviewHandle_back(null);
		// countBack.decrementAndGet();
		// System.out.println("关闭回放。。。。");
		// } else if (filePortMap.containsKey(fileName)) {
		// connectCloseBack(fileName, filePortMap.get(fileName));
		// }

		return "success";
	}

	private void stopPlayBack(Sdk s, Integer backNum) {

		HCNetSDK sdk = s.getSdk();
		NativeLong handle = null;
		if (backNum == 1) {
			handle = s.getlPreviewHandle_back();
			s.setlPreviewHandle_back(null);
		} else if (backNum == 2) {
			handle = s.getlPreviewHandle_back1();
			s.setlPreviewHandle_back1(null);
		} else if (backNum == 3) {
			handle = s.getlPreviewHandle_back2();
			s.setlPreviewHandle_back2(null);
		} else if (backNum == 4) {
			handle = s.getlPreviewHandle_back3();
			s.setlPreviewHandle_back3(null);
		} else if (backNum == 5) {
			handle = s.getlPreviewHandle_back4();
			s.setlPreviewHandle_back4(null);
		}
		sdk.NET_DVR_PlayBackControl(handle, HCNetSDK.NET_DVR_PLAYSTOPAUDIO, 0, null);
		sdk.NET_DVR_StopPlayBack(handle);
		countBack.decrementAndGet();

		System.out.println("关闭回放....");

	}

	class FRealDataCallBack implements HCNetSDK.FPlayDataCallBack {

		@Override
		public void invoke(NativeLong lPlayHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, int dwUser) {

			System.out.println("回调函数操作文件:" + fileName);
			OutputStream out = null;
			try {

				out = new FileOutputStream(new File(
						VideoController.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
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

	class FRealDataCallBack1 implements HCNetSDK.FPlayDataCallBack {

		@Override
		public void invoke(NativeLong lPlayHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, int dwUser) {

			System.out.println("回调函数1操作文件=========:" + fileName1);
			OutputStream out = null;
			try {

				out = new FileOutputStream(new File(
						VideoController.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
								+ "files\\" + fileName1 + ".h264"),
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

	class FRealDataCallBack2 implements HCNetSDK.FPlayDataCallBack {

		@Override
		public void invoke(NativeLong lPlayHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, int dwUser) {

			System.out.println("回调函数2操作文件=========:" + fileName2);
			OutputStream out = null;
			try {

				out = new FileOutputStream(new File(
						VideoController.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
								+ "files\\" + fileName2 + ".h264"),
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

	class FRealDataCallBack3 implements HCNetSDK.FPlayDataCallBack {

		@Override
		public void invoke(NativeLong lPlayHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, int dwUser) {

			System.out.println("回调函数3操作文件=========:" + fileName3);
			OutputStream out = null;
			try {

				out = new FileOutputStream(new File(
						VideoController.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
								+ "files\\" + fileName3 + ".h264"),
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

	class FRealDataCallBack4 implements HCNetSDK.FPlayDataCallBack {

		@Override
		public void invoke(NativeLong lPlayHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, int dwUser) {

			System.out.println("回调函数4操作文件=========:" + fileName4);
			OutputStream out = null;
			try {

				out = new FileOutputStream(new File(
						VideoController.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")
								+ "files\\" + fileName4 + ".h264"),
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
