package chen.domain;

/**
 * EasyUI 分页帮助类
 * 
 * @author infcn
 * 
 */
public class PageHelper implements java.io.Serializable {

	private static final long serialVersionUID = 3580589097168801758L;
	private int page;// 当前页
	private int rows;// 每页显示记录数
	private String sort;// 排序字段
	private String order;// asc/desc
    private String ids = null;
    /**
     * 用来显示的数据
     */
    private Object data = null;
    
    /**
     * 数据总条数
     */
    private int total;
    /**
     * 用来判断获取元数据成功或者失败
     */
    private String msg;
	public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

}
