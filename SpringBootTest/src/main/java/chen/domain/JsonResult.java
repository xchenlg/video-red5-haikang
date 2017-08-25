package chen.domain;

/**
 * 
 * Json返回值模型
 * 
 * 向前台返回的JSON对象
 * 
 * @author 杨彪
 * 
 */
public class JsonResult implements java.io.Serializable {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -4590562536287712997L;

    /**
     * 用来标识本次操作是否成功
     */
    private boolean success = false;

    /**
     * 用来向画面传送消息
     */
    private String msg = "";

    /**
     * 用来向画面传送一个对象信息
     */
    private Object obj = null;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }

}
