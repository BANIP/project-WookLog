package banip.action;

import javax.servlet.http.HttpServletRequest;

import banip.data.User;

public abstract class ActionLike extends ActionBoard {
	/**
	 * 존재하지 않는 사용자일 경우 -1반환, 그 외는 사용자 번호 반환
	 * @param request
	 * @return
	 */
	protected int getUserID(HttpServletRequest request) {
		// TODO Auto-generated method stub
		User user = super.getUser(request);
		
		if( !user.isEffective() ) return -1;
		return user.getID();
	}
	
}
