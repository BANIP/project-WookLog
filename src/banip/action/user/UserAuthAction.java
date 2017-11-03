package banip.action.user;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import banip.util.BoardJSON;
import banip.action.ActionUser;

public class UserAuthAction extends ActionUser {
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "POST";
	}

	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add("user_name");
		list.add("user_pwd");
		return list;
	}

	
	@Override
	protected boolean checkAuth(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getUser(request).login();
	}

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getUser(request).getBean().getBoardJSON();
	}

}
