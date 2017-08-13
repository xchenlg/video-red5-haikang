package chen.controller;

import java.util.List;

import com.sun.jna.NativeLong;

import chen.sdk.src.ClientDemo.HCNetSDK;

public class Sdk {

	// sdk
	private HCNetSDK sdk;
	// 预览句柄
	private NativeLong lPreviewHandle_turn;
	// 预览句柄
	private NativeLong lPreviewHandle_back;
	// 登录后的id
	private NativeLong uid;
	// 通道号
	private List<NativeLong> chanels;

	public HCNetSDK getSdk() {
		return sdk;
	}

	public void setSdk(HCNetSDK sdk) {
		this.sdk = sdk;
	}

	public NativeLong getUid() {
		return uid;
	}

	public void setUid(NativeLong uid) {
		this.uid = uid;
	}

	public List<NativeLong> getChanels() {
		return chanels;
	}

	public void setChanels(List<NativeLong> chanels) {
		this.chanels = chanels;
	}

	public NativeLong getlPreviewHandle_turn() {
		return lPreviewHandle_turn;
	}

	public void setlPreviewHandle_turn(NativeLong lPreviewHandle_turn) {
		this.lPreviewHandle_turn = lPreviewHandle_turn;
	}

	public NativeLong getlPreviewHandle_back() {
		return lPreviewHandle_back;
	}

	public void setlPreviewHandle_back(NativeLong lPreviewHandle_back) {
		this.lPreviewHandle_back = lPreviewHandle_back;
	}

}
