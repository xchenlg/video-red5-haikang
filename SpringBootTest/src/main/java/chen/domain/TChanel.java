package chen.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value = { "sdk" })
public class TChanel {

	@Id
	@GeneratedValue
	private Long id;

	private String title;

	private String chanel;

	private String content;

	private Integer chanelOrder;

	private Date createDate;

	private Date updateDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sdk_id")
	private TSdk sdk;

	public TChanel() {
	}

	public TChanel(Long id, String title, String chanel) {
		super();
		this.id = id;
		this.title = title;
		this.chanel = chanel;
	}

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

	public TSdk getSdk() {
		return sdk;
	}

	public void setSdk(TSdk sdk) {
		this.sdk = sdk;
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
