package banip.action;

import javax.servlet.http.HttpServletResponse; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.lang3.StringEscapeUtils;

import banip.util.BoardJSON;
import banip.bean.*;
import banip.data.StatusCode;
import banip.data.StatusCodeNull;
import banip.data.User;
import banip.data.UserNull;

/**
 * 모델과 프론트 컨트롤러를 이어주는 액션 페이지의 조상
 * <pre>
 * 		<b> history </b>
 *    			1.0 액션 페이지 작성
 *    			1.1 javadoc추가
 *    			1.2 각종 예외상황 작성
 *    			1.3  json으로 출력되는 에러코드, 출력되는 메세지, 예외상황 순으로 열거함
 *    			
 *  </pre>
 * @author BANIP
 * @version 1.4 권한 부족 / 인증 실패 추가
 * @exception
 *		
 */
public abstract class Action {
	
	public class createJSONBuilder{
		public HttpServletRequest _request;
		private BoardJSON _boardJSON;
		private StatusCode _status;

		public createJSONBuilder(HttpServletRequest request) {
			_request = request;
			// TODO Auto-generated constructor stub
		}

		public createJSONBuilder setBoardJSON(BoardJSON boardJSON) {
			_boardJSON = boardJSON;
			return this;
		}
		
		public createJSONBuilder setStatusCode(StatusCode status) {
			_status = status;
			return this;
		}
				
		public createJSONBuilder setStatusCode(int code) {
			_status = new StatusCode(code);
			return this;
		}
		
		public createJSONBuilder setStatusCode(int code,String message) {
			_status = new StatusCode(code,message);
			return this;
		}
		
		
		/**
		 * createJSON 실행
		 * statusCode가 null일시 해당 인스턴스 생성
		 * statusCode가 null이 아니고 boardjson도 null이 아니면 boardjson의 statuscode 지정
		 * boardjson이 null이면 인스턴스 생성
		 * @return
		 */
		public boolean execute() {
			if(_status == null || _status.isNull()) _status = new StatusCode(StatusCode.STATUS_NULL);
			else if(_boardJSON != null ) _boardJSON.setStatus(_status);
			if(_boardJSON == null) _boardJSON = new BoardJSON(_status);
			return createJSON(_boardJSON, _request);
		}
	}


	/**
	 * 프런트 컨트롤러에서 호출되는 메인 메서드
	 * @param request 
	 * @param response
	 * @return
	 * @throws IOException checkAuth
	 * @throws ServletException 
	 */
	public boolean execute(HttpServletRequest request, HttpServletResponse response){
		createJSONBuilder builder = new createJSONBuilder(request);
		String nowProgress = "로직 시작";

		try {
			
			//http프로토콜이 올바른지 체크
			nowProgress = "프로토콜 통신 타입 체크";
			if(!isValidProtocol(request)) return builder.setStatusCode(StatusCode.STATUS_PROTOCOL).execute();
			
			//필수 파라미터의 null체크
			nowProgress = "파라미터 존재여부 검사";
			boolean isNullError = !checkNullParameters( getRequireParam() ,request);
			if(isNullError) return builder.setStatusCode(StatusCode.STATUS_PARAM).execute();
	
			//비즈니스로직을 실행할 권한을 가지고 있는지 체크
			nowProgress = "권한 검사";
			boolean isexcuteable = checkAuth(request);
			if(!isexcuteable) return builder.setStatusCode(StatusCode.STATUS_CERTIFY).execute();
	
			// 다른 서버상의 오류가 있는지 체크
			nowProgress = "파라미터 유효값 검사";
			StatusCode isOtherError  = checkOtherError(request);
			if(isOtherError.isError()) return builder.setStatusCode(isOtherError).execute();
	
			//로직 실행
			nowProgress = "로직 실행";
			BoardJSON boardJSON = executeMain(request);
			return builder.setBoardJSON(boardJSON).execute();

		} catch (Exception e) {
			// TODO: handle exception
			String errorMessage = getErrorMessage(nowProgress,"알 수 없는 오류",e);
			return builder.setStatusCode(StatusCode.STATUS_SERVER, errorMessage).execute();
		}
	}


	private String getErrorMessage(String nowProgress,String errorType,Exception e) {
		return String.format("%s중에 %s가 발생했습니다. (%s)", nowProgress,errorType,e.getMessage());
	};
	

	protected StatusCode getNullStausCode() {
		// TODO Auto-generated method stub
		return new StatusCodeNull();
	}



	protected abstract String getProtocol();
	/**
	 * 필수 파라미터 취득, 오버라이딩 필수
	 * @return 필수 파라미터들
	 */
	protected abstract ArrayList<String> getRequireParam();

	/**
	 * html decode의 예외 대상으로 지정할 파라미터들, 필요시 자식단에서 오버라이딩
	 * @param request 
	 * @return 디코드 하지 않을 파라미터의 이름 리스트
	 */
	protected ArrayList<String> getExceptionDecode() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	/**
	 * 권한체크, 필요시 자식단에서 오버라이딩
	 * @param request
	 * @param response
	 * @return 실행 가능시 BoardJSON.STATUS_SUCCESS 불가시 false
	 */
	protected boolean checkAuth(HttpServletRequest request) {
		return true;
	};
	
	/**
	 * 권한체크 / 프로토콜 이외 다른 에러가 있는지 체크, 필요시 자식단에서 오버라이딩
	 * @param request
	 * @return
	 */
	protected StatusCode checkOtherError(HttpServletRequest request){
		return new StatusCode(StatusCode.STATUS_SUCCESS,null);
	}
	
	/**
	 * dao단과 연결하여 비즈니스로직 수행, 오버라이딩 필수
	 * @param request
	 * @param response
	 * @return 데이터가 포함된 boardjson
	 */
	protected abstract BoardJSON executeMain(HttpServletRequest request);
	
	/**
	 * 자식단에서 원하는 프로토콜로 호출되었는지 검사
	 * @param request
	 * @return
	 */
	protected boolean isValidProtocol(HttpServletRequest request) {
		return request.getMethod().equals( getProtocol() );
	};
	
	/**
	 * 서블릿 리퀘스트에서 int값의 프로퍼티를 취득
	 * @param request 인자가 포함된 서블릿 리퀘스트 객체
	 * @param name 인자의 이름
	 * @return 얻어온 인자
	 */
	protected int getInt(HttpServletRequest request,String name){
		String stringValue = String.valueOf( request.getParameter(name) );
		return Integer.parseInt(stringValue);
	}
	
	/**
	 * 서블릿 리퀘스트에서 String값의 프로퍼티를 취득
	 * @param request 인자가 포함된 서블릿 리퀘스트 객체
	 * @param name 인자의 이름
	 * @return 얻어온 인자
	 */
	protected String getString(HttpServletRequest request,String name){
		String value = String.valueOf( request.getParameter(name) );
		boolean isNull = value == null;
		boolean isDecodeRequire = !getExceptionDecode().contains(name);
		if(!isNull && isDecodeRequire) value= StringEscapeUtils.unescapeHtml4(value);
		return value;
	}
	
	/**
	 *  파라미터에 null이 존재하는지 존재하지 않는지 체크
	 *  
	 * @param keys 체크할 프로퍼티들의 키값
	 * @param request 체크할 인자들이 포함된 servletrequest객체
	 * @return	정상적이면 true, null이 하나라도 존재할 시 false
	 */
 	private boolean checkNullParameters(ArrayList<String> keys,HttpServletRequest request){
 		Iterator<String> keysIter = keys.iterator();
		while( keysIter.hasNext()){
			String key = keysIter.next();
			if(request.getParameter(key) == null) return false;
		}
		return true;
	}
 	
	protected boolean createJSON(BoardJSON boardJSON,HttpServletRequest request){
		request.setAttribute("boardJSON", boardJSON.create() );
		return !boardJSON.getStatus().isError();
	}
	
	/**
	 * user_name 파라미터로 유저빈 획득
	 */
	protected UserBean getUserBean(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return this.getUser(request).getBean();
	}
	
	/**
	 * request객체에서 User객체 획득
	 */
	protected User getUser(HttpServletRequest request) {
		String userName = getString(request, "user_name");
		if(userName == null) return new UserNull();
		
		String userPWD = getString(request, "user_pwd");
		if(userPWD == null) return new UserNull( getString(request, "user_name") );
		
		User user = new User(userName, userPWD);
		return user;
	}

	/**
	 * statusCode 객체 획득
	 * @param statusCode 스테이터스의 상수 코드
	 * @param message 상태 메세지
	 * @return statusCode 객체 
	 */
	public StatusCode getStatusCode(int statusCode, String message) {
		// TODO Auto-generated method stub
		return new StatusCode(statusCode,message);
	}
	public StatusCode getStatusCode(int statusCode) {
		// TODO Auto-generated method stub
		return new StatusCode(statusCode);
	}
	

}
