package banip.action.board;


import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import banip.util.*;
import banip.action.ActionBoard;
import banip.sql.BoardDao;
import banip.data.BoardID;
import banip.data.StatusCode;
import banip.data.User;
/**
 * 특정 게시글을 삭제하는 액션 객체
 * @author BANIP
 *
 */
public class BoardDeleteAction extends ActionBoard{
	/**
 	* 프로토콜 획득
 	*/
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "POST";
	}
	
/**
 	* 필수 파라미터 취득
 */
	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> array = new ArrayList<String>();
		array.add("board_id");
		array.add("user_pwd");
		array.add("user_name");		
		return array;
	}

	/**
	 * 아이디와 비밀번호가 올바른지 체크
	 */
	@Override
	protected boolean checkAuth(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getUser(request).isEffective();
	}

	/**
	 * @exception 게시글을 삭제할 권한이 존재하지 않음
	 * @exception 게시글을 사용자가 작성하지 않음
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub

		if(super.isBoardNull(request)) return new StatusCode(StatusCode.STATUS_UNDEFINED,"게시글이 존재하지 않습니다.");
		if(!isDeleteable(request)) return new StatusCode(StatusCode.STATUS_POWER,"게시글을 삭제할 권한이 없습니다.");
		return new StatusCode(StatusCode.STATUS_SUCCESS);
	}
	
	/**
	 * 해당 유저가 삭제권한을 가지고 있는지 체크
	 * @param request
	 * @return 가지고 있을시 true 반환
	 */
	private boolean isDeleteable(HttpServletRequest request) {
		User user = super.getUser(request);
		BoardID boardID = super.getBoardID(request);
		
		return super.isBoardWriten(user,boardID) || hasDeleteAuth(user);

	}

	private boolean hasDeleteAuth(User user) {
		// TODO Auto-generated method stub
		return user.getBean().isUSER_PERMISSION_REMOVE();
	}

	/**
	 * 게시글 삭제
	 */
	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		int boardID = super.getInt(request, "board_id");
		boolean isSuccess = deleteBoard(boardID);
		return getResultJSON(isSuccess);
	}

	/**
	 * 결과 획득
	 * @param isSuccess 게시글 삭제의 성공 여부
	 * @return
	 */
	private BoardJSON getResultJSON(boolean isSuccess) {
		BoardJSON json = new BoardJSON();
		StatusCode status = new StatusCode(isSuccess ? StatusCode.STATUS_SUCCESS : StatusCode.STATUS_SERVER);
		json.setStatus(status);
		return json;
	}

	private boolean deleteBoard(int boardID) {
		BoardDao dao = new BoardDao();
		boolean isSuccess =  dao.deleteBoard(boardID);
		dao.close(true);
		return isSuccess;
	}



}
