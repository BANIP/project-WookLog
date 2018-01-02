package banip.action.user;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import banip.action.ActionUser;
import banip.bean.UserBean;
import banip.dao.UserDao;
import banip.data.StatusCode;
import banip.util.BoardJSON;

public class UserAddAction extends ActionUser{

	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "POST";
	}

	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub

		ArrayList<String> list = new ArrayList<String>();
		list.add("user_pwd");
		list.add("user_name");
		return list ;
	}

	/**
	 * 이미 존재하는 아이디인지 체크
	 * @param request
	 * @return
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return getResultStatus( super.getUser(request).isExist() );
	}

	private StatusCode getResultStatus(boolean isexist) {
		// TODO Auto-generated method stub
		if(isexist) {
			return new StatusCode(StatusCode.STATUS_EXIST,"회원가입에 실패하였습니다. 이미 존재하는 사용자입니다.");
		} else {
			return new StatusCode(StatusCode.STATUS_SUCCESS);			
		}
	}

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		UserBean bean = addUser(request);
		return getResultJSON(bean);
	}

	private BoardJSON getResultJSON(UserBean bean) {
		// TODO Auto-generated method stub
		if (bean == null) {
			return new BoardJSON(StatusCode.STATUS_SERVER);
		} else {
			return bean.getBoardJSON();
		}
	}

	private UserBean addUser(HttpServletRequest request) {
		UserDao userDao = new UserDao();
		UserBean bean = userDao.addUser(super.getUser(request));
		userDao.close(true);
		return bean;
	}
	
}
