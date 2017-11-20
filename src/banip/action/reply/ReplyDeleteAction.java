package banip.action.reply;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import banip.action.ActionReply;
import banip.dao.BoardDao;
import banip.dao.UserDao;
import banip.data.StatusCode;
import banip.util.BoardJSON;
import banip.data.User;

public class ReplyDeleteAction extends ActionReply {
	@Override
	protected String getProtocol() { return "POST"; }
	
	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		 ArrayList<String> array = new  ArrayList<String>();
		 array.add("user_name");
		 array.add("user_pwd");
		 array.add("reply_id");
		 return array;
	}

	/**
	 * 아이디와 비밀번호가 올바른지 체크
	 * @param request
	 * @return
	 */
	@Override
	protected boolean checkAuth(HttpServletRequest request) {
		// TODO Auto-generated method stub
		User user = super.getUser(request);
		return user.isEffective();
	}

	/**
	 * @exception 해당 글이 존재하지 않을 경우
	 * @exception 덧글의 작성자와 해당 사용자의 닉이 다를 경우
	 * @exception 덧글 삭제권한을 가지고 있지 않을 경우
	 * @param request
	 * @return
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		int replyID = super.getInt(request,"reply_id");
		User user = super.getUser(request);
		if(super.isReplyNull(request)) return new StatusCode(StatusCode.STATUS_UNDEFINED,"삭제하고자 하는 덧글이 존재하지 않습니다.");
		if(isAuthLack(user,replyID)) return new StatusCode(StatusCode.STATUS_POWER,"해당 덧글을 삭제할 권한이 충분하지 않습니다.");
		return new StatusCode(StatusCode.STATUS_SUCCESS);
	}

	/**
	 * 사용자가 해당 덧글을 작성했거나 게시글 삭제 권한이 있는지 검사
	 * @param user
	 * @param replyID
	 * @return
	 */
	private boolean isAuthLack(User user,int replyID) {
		// TODO Auto-generated method stub
		boolean isDeleteable = hasDeleteAuth(user) || super.isReplyWriten(replyID, user);
		return !isDeleteable;
	}
	

	private boolean hasDeleteAuth(User user) {
		// TODO Auto-generated method stub
		return user.getBean().isUSER_PERMISSION_REMOVE();
	}

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		BoardDao dao = new BoardDao();
		int replyID = super.getInt(request, "reply_id");
		
		boolean isSuccess = dao.removeReply(replyID);
		dao.close(true);
		return getResultJSON(isSuccess);
	}

	private BoardJSON getResultJSON(boolean isSuccess) {
		// TODO Auto-generated method stub
		if(isSuccess) {
			return new BoardJSON(StatusCode.STATUS_SUCCESS);
		} else {
			return new BoardJSON(StatusCode.STATUS_SERVER,"서버상의 문제로 덧글 삭제에 실패했습니다.");
		}
		
	}

}
