package banip.action.reply;


import java.util.ArrayList;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.*;

import banip.util.*;
import banip.sql.BoardDao;
import banip.action.ActionReply;
import banip.bean.ReplyBean;
import banip.data.StatusCode;


public class ReplyListView extends ActionReply{
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "GET";
	}
	
	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> array = new ArrayList<String>();
		array.add("board_id");
		return array;
	}
	/**
	 * @exception board_id에 해당하는 게시글이 존재하지 않을 경우
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return getErrorCode( super.isBoardNull(request) );
	}

		
	private StatusCode getErrorCode(boolean isBoardNull) {
		// TODO Auto-generated method stub
		if(isBoardNull) {
			return super.getStatusCode(StatusCode.STATUS_UNDEFINED,"해당 게시글을 찾을 수 없습니다.");
		} else {
			return super.getStatusCode(StatusCode.STATUS_SUCCESS);
		}
	}
	
	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		int boardID = super.getInt(request, "board_id");
		BoardDao dao = new BoardDao();
		ArrayList<ReplyBean> replyList = dao.getReplyList(boardID);
		dao.close(true);
		return getResultJSON(replyList.iterator());
	}
	
	@SuppressWarnings("unchecked")
	private BoardJSON getResultJSON(Iterator<ReplyBean> replyList) {
		// TODO Auto-generated method stub
		BoardJSON resultJSON = new BoardJSON();
		JSONArray dataArray = new JSONArray();
		resultJSON.putData("list", dataArray);
		while(replyList.hasNext()) {
			ReplyBean bean = replyList.next();
			dataArray.add( getReplyJSON(bean) );
		}
		return resultJSON;
	}
	
	@SuppressWarnings("unchecked")
	private Object getReplyJSON(ReplyBean bean) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		json.put("tREPLY_ID",bean.getREPLY_ID());
		json.put("tREPLY_BOARD_ID",bean.getREPLY_BOARD_ID());
		json.put("tREPLY_USER_ID",bean.getREPLY_USER_ID());
		json.put("tREPLY_USER_NAME",bean.getREPLY_USER_NAME());
		json.put("tREPLY_DATE",bean.getREPLY_DATE());
		json.put("tREPLY_DATE_MODIFY",bean.getREPLY_DATE_MODIFY());
		json.put("tREPLY_CONTENT",bean.getREPLY_CONTENT());
		return json;
	}




}
