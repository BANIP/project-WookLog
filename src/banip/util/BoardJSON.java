package banip.util;
import org.json.simple.*;

import banip.data.StatusCode;

public class BoardJSON {

	JSONObject dataJSON = new JSONObject();
	StatusCode status =new StatusCode(StatusCode.STATUS_SUCCESS);

	/**
	 * 서버 통신의 성공 여부에 대한 코드를 저장합니다
	 * @param status 처리여부의 코드와 메세지가 담긴 데이터객체 StatusCode
	 */
	
	public BoardJSON() {}
	
	public BoardJSON(int statuscode) {
		setStatus(statuscode);
	}
	
	public BoardJSON(int statuscode,String statusMessage) {
		setStatus(statuscode,statusMessage);
	}
	
	public BoardJSON(StatusCode status) {
		setStatus(status);
	}
	
	public void setStatus(StatusCode status){
		this.status = status;
	}
	public void setStatus(int statuscode){
		this.status = new StatusCode(statuscode);
	}
	
	public void setStatus(int statuscode,String statusMessage){
		this.status = new StatusCode(statuscode,statusMessage);
	}
	
	public StatusCode getStatus(){
		return status;
	}
	
	@SuppressWarnings("unchecked")
	public void putData(String key,Object value){
		dataJSON.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public JSONObject create(){
		JSONObject json = new JSONObject();	
		
		json.put("cdn","http://localhost");
		json.put("data", dataJSON);
		json.put("status", status.getMessage());
		json.put("statuscode", status.getCode());
		
		return json;
	}
}
