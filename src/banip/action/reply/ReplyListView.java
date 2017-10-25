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
		while(replyList.hasNext()) {
			ReplyBean bean = replyList.next();
			dataArray.add( getReplyJSON(bean) );
		}
		resultJSON.putData("list", dataArray);
		return resultJSON;
	}
	
	private JSONObject getReplyJSON(ReplyBean bean) {
		// TODO Auto-generated method stub
		try {
			return bean.getJSON();
		} catch (Exception e) {
			// TODO: handle exception
		    e.printStackTrace();
			return null;
		}

	}




}
