package banip.action.board;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import banip.sql.BoardDao;
import banip.action.ActionBoard;
import banip.util.*;
import banip.bean.BoardBean;
import banip.data.StatusCode;
import banip.data.User;

public class BoardViewDetail extends ActionBoard{
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
	 * @exception 게시글이 존재하지 않음
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return getErrorResultStatus( super.isBoardNull(request) );
	}

	private StatusCode getErrorResultStatus(boolean notExistBoard) {
		// TODO Auto-generated method stub
		if(notExistBoard) {
			return super.getStatusCode(StatusCode.STATUS_UNDEFINED,"해당 게시글은 존재하지 않습니다.");
		} else {
			return super.getStatusCode(StatusCode.STATUS_SUCCESS);
		}
	}

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		addBoardView(request);
		return getBoardJSON(request );
	}

	private void addBoardView(HttpServletRequest request) {
		// TODO Auto-generated method stub
		BoardDao dao = new BoardDao();
		String userIP = request.getRemoteAddr();
		System.out.println(super.getUser(request).getName() );
		int userID = getUserID(request);
		int boardID = super.getInt(request, "board_id");
		dao.addHitBoard(userID, userIP, boardID);
	}

	
	private int getUserID(HttpServletRequest request) {
		// TODO Auto-generated method stub
		User user = super.getUser(request);
		return user.isExist() ? user.getID() : -1;
	}

	private BoardJSON getBoardJSON(HttpServletRequest request) {
		// TODO Auto-generated method stub
		BoardBean bean = super.getBoardID(request).getBean();
		BoardJSON json = new BoardJSON(StatusCode.STATUS_SUCCESS);
		
		json.putData("board_id",bean.getBOARD_ID());
		json.putData("board_category_id",bean.getBOARD_CATEGORY_ID());
		json.putData("board_hit",bean.getBOARD_HIT());
		json.putData("board_like",bean.getBOARD_LIKE());
		json.putData("board_category_name",bean.getBOARD_CATEGORY_NAME());
		json.putData("board_user_name",bean.getBOARD_USER_NAME());
		json.putData("board_title",bean.getBOARD_TITLE());
		json.putData("board_content",bean.getBOARD_CONTENT());
		json.putData("board_reply_count",bean.getBOARD_REPLY_COUNT());
		json.putData("board_date_create",bean.getBOARD_DATE_CREATE());
		json.putData("board_date_modify",bean.getBOARD_DATE_MODIFY());
		return json;
	}



}
