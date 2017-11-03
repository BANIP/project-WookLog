package banip.action.board;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import banip.util.*;
import banip.sql.BoardDao;
import banip.action.ActionBoard;
import banip.bean.BoardBean;
import banip.data.BoardID;
import banip.data.StatusCode;
import banip.data.User;

/**
 * request로 들어와야할 인자
 * 선택 => board_title, board_content
 * 필수 => board_pwd board_name board_num
 * @author BANIP
 *
 */
public class BoardModifyAction extends ActionBoard{
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "POST";
	}

	
	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> array = new ArrayList<String>();
		array.add("board_title");
		array.add("board_content");
		array.add("board_id");
		array.add("user_pwd");
		array.add("user_name");
		return array;
	}

	@Override
	protected boolean checkAuth(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getUser(request).isEffective();
	}

	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		BoardID boardID = super.getBoardID(request);
		User user = super.getUser(request);
		boolean isWriten = super.isBoardWriten(user,boardID );
		if(isWriten) return new StatusCode(StatusCode.STATUS_SUCCESS);
		return new StatusCode(StatusCode.STATUS_POWER);
	}
	

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		BoardJSON boardJSON = new BoardJSON();
		BoardDao boardDao = new BoardDao();
		BoardBean bean = getModifyBean(request);
		
		boolean isSuccess = boardDao.modifyBoard(bean);
		boardDao.close(true);
		boardJSON.setStatus( isSuccess ? StatusCode.STATUS_SUCCESS : StatusCode.STATUS_SERVER);
		return boardJSON;
	}
	
	private BoardBean getModifyBean(HttpServletRequest request) {
		BoardBean bean = new BoardBean();
		bean.setBOARD_CONTENT(super.getString(request, "board_content"));
		bean.setBOARD_TITLE(super.getString(request, "board_title"));
		return bean;
	}


}
