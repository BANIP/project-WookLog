package banip.action.board;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import banip.util.BoardJSON;
import banip.data.StatusCode;
import banip.data.User;
import banip.action.ActionLike;
import banip.dao.BoardDao;

public class BoardLikeAction extends ActionLike {

	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add("board_id");
		list.add("is_like_board");
		return list;
	}

	/**
	 * @exception board_id에 해당하는 게시글이 존재하지 않을 경우
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return getErrorStatusCode( isBoardExist(request) );
	}

	private StatusCode getErrorStatusCode(boolean boardExist) {
		// TODO Auto-generated method stub
		if(boardExist) {
			return super.getStatusCode(StatusCode.STATUS_SUCCESS);
		} else {
			return super.getStatusCode(StatusCode.STATUS_PARAM);
		}
	}

	private boolean isBoardExist(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getBoardID(request).isExist();
	}

	/**
	 * user_name이 null이 아니나 올바른 사용자가 아닐 경우 user_ip로 검사
	 */
	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		BoardDao boardDao = new BoardDao();
		String userIP = request.getRemoteAddr();
		int boardID = super.getBoardID(request).getId();
		User user = super.getUser(request);
		boolean isLogin = user.isNull();
		if( isLogin ) {
			if( !user.login() ) return new BoardJSON(StatusCode.STATUS_CERTIFY,"올바르지 않은 아이디 혹은 비밀번호입니다.");
			boardDao.addLikeBoard( user.getID() , userIP, boardID );
		} else {
			boardDao.addLikeBoard( -1, userIP, boardID );
		}
		
		boardDao.close(true);
		return getResultJSON( isLogin );
	}
	

	private BoardJSON getResultJSON(boolean isUserLogin) {
		// TODO Auto-generated method stub
		BoardJSON json = new BoardJSON();
		json.putData("is_user_login", isUserLogin );
		return json;
	}

	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
