package chen.domain;

import java.util.Date;

public class PChanel {

	private Long id;

	private String title;

	private String chanel;

	private String content;

	private Integer chanelOrder;

	private Date createDate;

	private Date updateDate;

	private Long sdkId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getChanel() {
		return chanel;
	}

	public void setChanel(String chanel) {
		this.chanel = chanel;
	}

	public Long getSdkId() {
		return sdkId;
	}

	public void setSdkId(Long sdkId) {
		this.sdkId = sdkId;
	}

	public Integer getChanelOrder() {
		return chanelOrder;
	}

	public void setChanelOrder(Integer chanelOrder) {
		this.chanelOrder = chanelOrder;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

}
