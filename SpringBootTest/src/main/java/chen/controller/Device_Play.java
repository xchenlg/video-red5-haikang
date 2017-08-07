package chen.controller;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.examples.win32.W32API.HWND;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.win32.StdCallLibrary;

import chen.sdk.src.ClientDemo.HCNetSDK;
import chen.sdk.src.ClientDemo.HCNetSDK.NET_DVR_DEVICEINFO_V30;

public class Device_Play {

//	public static void main(String[] args) {

//		HCNetSDK sdk = (HCNetSDK) Native.loadLibrary(Device_Play.class.getClassLoader().getResource("").getPath().substring(1).replace("/", "\\")+"chen\\sdk\\HCNetSDK",
//	            HCNetSDK.class);
//		
//		if (!sdk.NET_DVR_Init()) {
//			System.out.println("SDK初始化失败");
//			return;
//		}
//
//		NativeLong uid = new NativeLong(-1);
//
//		NET_DVR_DEVICEINFO_V30 devinfo = new NET_DVR_DEVICEINFO_V30();// 设备信息
//		String ip = "111.198.22.200";
//		short port = 8000;
//		uid = sdk.NET_DVR_Login_V30(ip, port, "admin", "abc123456", devinfo);// 返回一个用户编号，同时将设备信息写入devinfo
//		int Iuid = uid.intValue();
//		if (Iuid < 0) {
//			System.out.println("设备注册失败");
//			return;
//		}
//
//		// 设备信息
//		for (int iChannum = 0; iChannum < devinfo.byChanNum; iChannum++) {
//			System.out.println(iChannum + "==========" + devinfo.byStartChan);
//		}
//
//		// 用户参数
//		HCNetSDK.NET_DVR_CLIENTINFO m_strClientInfo = new HCNetSDK.NET_DVR_CLIENTINFO();
//		m_strClientInfo.lChannel = new NativeLong(devinfo.byStartChan);
//
//		FRealDataCallBack fRealDataCallBack = new FRealDataCallBack();// 预览回调函数实现
//		// 预览句柄
//		NativeLong lPreviewHandle = sdk.NET_DVR_RealPlay_V30(uid, m_strClientInfo, null, null, true);
		//控制操作，左方向移动
//		sdk.NET_DVR_PTZControl(lPreviewHandle, HCNetSDK.UP_LEFT, 0);
		//控制操作，左方向停止
//		sdk.NET_DVR_PTZControl(lPreviewHandle, HCNetSDK.UP_LEFT, 1);
//	}
	static NativeLongByReference m_lPort = new NativeLongByReference(new NativeLong(-1));;//回调预览时播放库端口指针
	static class FRealDataCallBack implements HCNetSDK.FRealDataCallBack_V30 {
		// 预览回调
		public void invoke(NativeLong lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize,
				Pointer pUser) {
			switch (dwDataType) {
			case HCNetSDK.NET_DVR_SYSHEAD: // 系统头

				if (!playControl.PlayM4_GetPort(m_lPort)) // 获取播放库未使用的通道号
				{
					break;
				}

				if (dwBufSize > 0) {
					if (!playControl.PlayM4_SetStreamOpenMode(m_lPort.getValue(), PlayCtrl.STREAME_REALTIME)) // 设置实时流播放模式
					{
						break;
					}

					if (!playControl.PlayM4_OpenStream(m_lPort.getValue(), pBuffer, dwBufSize, 1024 * 1024)) // 打开流接口
					{
						break;
					}

//					if (!playControl.PlayM4_Play(m_lPort.getValue(), hwnd)) // 播放开始
//					{
//						break;
//					}
				}
			case HCNetSDK.NET_DVR_STREAMDATA: // 码流数据
				if ((dwBufSize > 0) && (m_lPort.getValue().intValue() != -1)) {
					if (!playControl.PlayM4_InputData(m_lPort.getValue(), pBuffer, dwBufSize)) // 输入流数据
					{
						break;
					}
				}
			}
		}
	}

	static PlayCtrl playControl = PlayCtrl.INSTANCE;

	interface PlayCtrl extends StdCallLibrary {
		PlayCtrl INSTANCE = (PlayCtrl) Native.loadLibrary(
				"D:\\chenlg\\shexiangtou\\CH-HCNetSDK(Windows32)V5.2.7.5_build20170217\\库文件\\PlayCtrl", PlayCtrl.class);

		public static final int STREAME_REALTIME = 0;
		public static final int STREAME_FILE = 1;

		boolean PlayM4_GetPort(NativeLongByReference nPort);

		boolean PlayM4_OpenStream(NativeLong nPort, ByteByReference pFileHeadBuf, int nSize, int nBufPoolSize);

		boolean PlayM4_InputData(NativeLong nPort, ByteByReference pBuf, int nSize);

		boolean PlayM4_CloseStream(NativeLong nPort);

		boolean PlayM4_SetStreamOpenMode(NativeLong nPort, int nMode);

		boolean PlayM4_Play(NativeLong nPort, HWND hWnd);

		boolean PlayM4_Stop(NativeLong nPort);

		boolean PlayM4_SetSecretKey(NativeLong nPort, NativeLong lKeyType, String pSecretKey, NativeLong lKeyLen);
	}
}