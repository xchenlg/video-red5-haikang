package chen.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Entity
@JsonIgnoreProperties(value = {"chanels"})
public class TSdk {

	@Id
	@GeneratedValue
	private Long id;

	private String ip;

	private String port;

	private String user;

	private String password;
	
	private String title;
	
	private String content;
	
	private Date createDate;
	
	private Date updateDate;
	
	private Integer sdkOrder;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sdk")
    private List<TChanel> chanels;

	public TSdk() {
	}
	public TSdk(Long id, String ip, String user, String password) {
		super();
		this.id = id;
		this.ip = ip;
		this.user = user;
		this.password = password;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getSdkOrder() {
		return sdkOrder;
	}
	public void setSdkOrder(Integer sdkOrder) {
		this.sdkOrder = sdkOrder;
	}
	public List<TChanel> getChanels() {
		return chanels;
	}
	public void setChanels(List<TChanel> chanels) {
		this.chanels = chanels;
	}

}
