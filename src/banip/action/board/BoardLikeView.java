package banip.action.board;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import banip.util.BoardJSON;
import banip.action.ActionLike;
import banip.dao.BoardDao;
import banip.data.StatusCode;
import banip.data.User;

public class BoardLikeView extends ActionLike {
	/**
	 * 프로토콜 전달방식 획득
	 */
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "GET";
	}
	
	/**
	 * 필수 파라미터 획득
	 */
	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add("board_id");
		return list;
	}

	/** 
	 * board_id에 해당하는 게시글이 없는 경우
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		if(super.isBoardNull(request)) return new StatusCode(StatusCode.STATUS_UNDEFINED,"게시글이 존재하지 않습니다.");
		return new StatusCode(StatusCode.STATUS_SUCCESS);
	}
	
	/**
	 * user_name이 null이 아니나 올바른 사용자가 아닐 경우 user_ip로 검사
	 */
	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		User user = super.getUser(request);
		String userIP = request.getRemoteAddr();
		int boardID = super.getBoardID(request).getId();
		BoardDao boardDao = new BoardDao();
		boolean isLikeBoard ,isLogin = !user.isNull();
		if(isLogin) {
			if( !user.login() ) return new BoardJSON(StatusCode.STATUS_CERTIFY,"올바르지 않은 아이디 혹은 비밀번호입니다.");
			isLikeBoard = boardDao.isLikeBoard(user.getID(), userIP, boardID);
		} else {
			isLikeBoard = boardDao.isLikeBoard(-1, userIP, boardID);
		}
		
		boardDao.close(true);
		return getResultJSON(isLikeBoard,isLogin );



	}

	private BoardJSON getResultJSON(boolean isLikeBoard, boolean isUserLogin) {
		// TODO Auto-generated method stub
		BoardJSON json = new BoardJSON();
		json.putData("is_like_board", isLikeBoard );
		json.putData("is_user_login", isUserLogin );
		return json;
	}




}
