package banip.action.reply;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import banip.data.StatusCode;
import banip.util.BoardJSON;
import banip.action.ActionReply;
import banip.bean.ReplyBean;
import banip.dao.BoardDao;

public class ReplyModifyAction extends ActionReply {

	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add("reply_id");
		list.add("user_name");
		list.add("user_pwd");
		list.add("reply_content");
		return list;
	}

	/**
	 * @exception 아이디와 비밀번호가 맞지 않을 경우
	 * @param request
	 * @return
	 */
	@Override
	protected boolean checkAuth(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getUser(request).isEffective();
	}

	/**
	 * reply_id에 해당하는 튜플이 없을 경우
	 * reply_content가 비어있을 경우
	 * 
	 * @param request
	 * @return
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		if(super.isReplyNull(request) ) return super.getStatusCode(StatusCode.STATUS_UNDEFINED,"존재하지 않는 덧글입니다.");
		if(isNullContent(request) ) return super.getStatusCode(StatusCode.STATUS_PARAM,"덧글 내용이 존재하지 않습니다.");
		return super.getStatusCode(StatusCode.STATUS_SUCCESS);
	}

	private boolean isNullContent(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getString(request, "reply_content").length() == 0;
	}

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		BoardDao dao = new BoardDao();
		ReplyBean bean = dao.modifyReply( getModifyReplyBean(request), super.getUser(request));
		dao.close(true);
		return getResultJSON(bean);
	}

	private BoardJSON getResultJSON(ReplyBean bean ) {
		// TODO Auto-generated method stub
		if(bean != null) {
			return bean.getBoardJSON();
		} else {
			return new BoardJSON(StatusCode.STATUS_SERVER,"서버상의 오류로 덧글을 수정할 수 없습니다.");
		}
	}

	private ReplyBean getModifyReplyBean(HttpServletRequest request) {
		// TODO Auto-generated method stub
		ReplyBean bean = new ReplyBean();
		bean.setREPLY_ID( super.getInt(request, "reply_id") );
		bean.setREPLY_CONTENT( super.getString(request,"reply_content") );

		return bean;
	}

	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "POST";
	}


}
