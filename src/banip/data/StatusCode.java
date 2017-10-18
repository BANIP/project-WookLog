package banip.data;

public class StatusCode {
	int code = 0;
	String message;
	
	/**
	 * 존재하지 않는 값
	 */
	public static final int  STATUS_NULL = -1;
	/**
	 * 인증성공
	 */
	public static final int  STATUS_SUCCESS = 0;
	/**
	 * 서버오류
	 */
	public static final int  STATUS_SERVER = 1;
	/**
	 * 필수 파라미터 부족
	 */
	public static final int  STATUS_PARAM = 2;
	/**
	 * 출력자료 없음
	 */
	public static final int  STATUS_UNDEFINED = 3;
	/**
	 * 인증실패
	 */
	public static final int  STATUS_CERTIFY = 4;
	/**
	 * 권한부족
	 */
	public static final int  STATUS_POWER = 5;
	/**
	 * 올바르지 않은 http 프로토콜 방식
	 */
	public static final int  STATUS_PROTOCOL = 6;
	/**
	 * 이미 존재하는 값
	 */
	public static final int  STATUS_EXIST = 7;
	/**
	 * URL오류
	 */
	public static final int STATUS_URL = 100;
	
	public StatusCode(int code, String message) {
		this(code);
		this.message = message;
	}
	
	public StatusCode(int code) {
		this.code = code;
	}
	
	public StatusCode() {
		this.code = STATUS_NULL;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMessage() {
		if(message == null) return getDefaultMessage();
		return message;
	}
	public String getDefaultMessage() {
		
		String message = "정상적인 통신을 수행하지 못하였습니다.";
		switch(code){
			case STATUS_SUCCESS:
				message = "통신 완료!";
				break;
			case STATUS_SERVER:
				message = "서버 내부 오류로 게시물 작성 또는 열람에 실패했습니다. 관리자에게 문의해주세요";
				break;
			case STATUS_PARAM:
				message = "필수 입력 사항을 제대로 입력하지 않아 게시물 작성 또는 열람할 수 없습니다.";
				break;
			case STATUS_NULL:
				message = "반환할 데이터가 존재하지 않습니다.";
				break;
			case STATUS_CERTIFY:
				message = "올바르지 않은 아이디 혹은 비밀번호입니다.";
				break;
			case STATUS_POWER:
				message = "권한이 부족합니다.";
				break;
			case STATUS_PROTOCOL:
				message = "올바르지 않은 http 프로토콜 방식으로 접근하였습니다.";
				break;
		}
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public boolean isNull() {
		return code == STATUS_NULL;
	}
	public boolean isError() {
		return code != STATUS_SUCCESS;
	}

}
