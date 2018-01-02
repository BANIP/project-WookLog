package banip.action;

import javax.servlet.http.HttpServletRequest;

import banip.bean.ReplyBean;
import banip.dao.BoardDao;
import banip.data.User;

public abstract class ActionReply extends ActionBoard {

	/**
	 * 해당 유저가 덧글을 올렸는지 확인
	 * @param boardID
	 * @param user
	 * @return 해당 유저가 글쓴이일시 true 반환
	 */
	protected boolean isReplyWriten(int replyID, User user) {
		BoardDao dao = new BoardDao();
		ReplyBean bean = dao.getReplyBean(replyID);
		dao.close(true);
		return bean.getREPLY_USER_NAME().equals(user.getName());
	}


	/**
	 *  reply_id 파라미터로 리플라이빈 획득
	 */
	protected ReplyBean getReplyBean(int replyID) {
		// TODO Auto-generated method stub
		BoardDao boarddao = new BoardDao();
		ReplyBean bean = boarddao.getReplyBean(replyID);
		boarddao.close(true);
		return bean;
	}

	public int getReplyID(HttpServletRequest request) {
		return super.getInt(request, "reply_id");
	}
	public boolean isReplyNull(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return getReplyBean( getReplyID(request) ) == null;
	}
}
