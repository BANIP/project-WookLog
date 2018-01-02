package banip.action.user;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import banip.util.BoardJSON;
import banip.data.StatusCode;
import banip.action.ActionUser;

public class UserGetUserAction extends ActionUser {
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "GET";
	}

	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add("user_name");
		return list;
	}
	
	

	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		if( super.getUser(request).isExist() ) return super.getStatusCode(StatusCode.STATUS_SUCCESS);
		return super.getStatusCode(StatusCode.STATUS_UNDEFINED,"사용자를 찾을 수 없습니다. 로그인에 실패했습니다.");
	}

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			
			BoardJSON json = new BoardJSON(StatusCode.STATUS_SUCCESS);
			json.putData("info", getUserInfoJSON(request) );
			return json;
			
		} catch(Exception ee) {
			ee.printStackTrace();
			return new BoardJSON(StatusCode.STATUS_SERVER,"서버상의 이유로 로그인에 실패했습니다.");
		}
	}

	private Object getUserInfoJSON(HttpServletRequest request) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		
		ArrayList<String> ignoreList = new ArrayList<String>();
		ignoreList.add("USER_PWD");
		return super.getUserBean(request).getJSON( ignoreList.iterator() );
	}

	
	



}
