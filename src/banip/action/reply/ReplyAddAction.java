package banip.action.reply;

import javax.servlet.http.HttpServletRequest;

import banip.action.ActionReply;
import banip.util.BoardJSON;
import banip.bean.ReplyBean;
import banip.bean.ReplyWriteBean;
import banip.dao.BoardDao;
import banip.data.StatusCode;
import banip.data.User;

import java.util.ArrayList;

public class ReplyAddAction extends ActionReply {

	@Override
	protected String getProtocol() { return "POST"; }
	
	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> array = new ArrayList<String>();
		array.add("reply_content");
		array.add("board_id");
		array.add("user_name");
		array.add("user_pwd");
		return array;
	}

	/**
	 * 사용자의 아이디와 비밀번호가 일치하는지 확인
	 */
	@Override
	protected boolean checkAuth(HttpServletRequest request) {
		// TODO Auto-generated method stub
		User user = super.getUser(request);
		return user.login();
	}

	/**
	 * @exception reply_content 길이가 0 이하인 경우
	 * @exception board_id에 해당하는 게시글이 존재하지 않는 경우
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String replyContent = super.getString(request, "reply_content");
		if(replyContent.isEmpty()) return new StatusCode(StatusCode.STATUS_PARAM,"내용이 없는 덧글은 작성할 수 없어요!");
		if(super.isBoardNull(request)) return new StatusCode(StatusCode.STATUS_PARAM,"작성하려는 게시글이 존재하지 않아요!");
		return new StatusCode(StatusCode.STATUS_SUCCESS);
	}
	
	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		BoardDao boardDao = new BoardDao();
		ReplyBean bean = getReplyBean(request);
		ReplyWriteBean writebean = boardDao.addReply( bean, super.getUser(request) );
		boardDao.close(true);
		return getResultJSON(writebean);

	}

	/**
	 * 덧글 작성 결과에 대응하는 json 객체 반환
	 * @param writebean  덧글작성이 성공하였는가?
	 * @return baordjson 객체
	 */
	private BoardJSON getResultJSON(ReplyWriteBean writebean ) {
		// TODO Auto-generated method stub
		if(writebean.isIS_SUCCESS()) {
			return writebean.getBoardJSON();
		} else {
			return new BoardJSON(StatusCode.STATUS_SERVER,"서버상의 오류로 덧글 작성에 실패하였습니다. 관리자에게 문의해주세용");
		}
	}

	/**
	 * 빈객체 획득 후 반환, 빈에는 REPLY_BOARD_ID, REPLY_CONTENT이 담김
	 * @param request
	 * @return 빈객체
	 */
	private ReplyBean getReplyBean(HttpServletRequest request) {
		// TODO Auto-generated method stub
		ReplyBean bean = new ReplyBean();
		bean.setREPLY_BOARD_ID( super.getInt(request, "board_id") );
		bean.setREPLY_CONTENT( super.getString(request, "reply_content") );
		return bean;
	}



		


}
