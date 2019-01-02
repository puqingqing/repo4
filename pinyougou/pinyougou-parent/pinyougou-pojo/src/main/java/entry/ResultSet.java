package entry;

import java.io.Serializable;

public class ResultSet implements Serializable {
	private Boolean success;
	private String message;
	
	
	public ResultSet(Boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "ResultSet [success=" + success + ", message=" + message + "]";
	}
	
	
	

}
