package entity;

import java.io.Serializable;

public class Result implements Serializable {
	
    private boolean flag;//是否成功
    private Integer code;// 返回码
    private String message;//返回信息
    private Object data;// 返回数据

	public Result(boolean flag, Integer code, String message, Object data) {
		super();
		this.flag = flag;
		this.code = code;
		this.message = message;
		this.data = data;
	}

    public Result() {
    }
  
	public Result(boolean flag, Integer code, String message) {
		super();
		this.flag = flag;
		this.code = code;
		this.message = message;
	}

	public static Result error(String message){
		return new Result(false, StatusCode.ERROR, message, null);
	}

	public static Result success(String message){
		return new Result(true, StatusCode.OK, message, null);
	}

	public static Result success(String message,Object obj){
		return new Result(true, StatusCode.OK, message, obj);
	}
	
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
	
}