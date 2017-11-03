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
		BoardJSON json = bean.getBoardJSON();
		return json;
	}



}
