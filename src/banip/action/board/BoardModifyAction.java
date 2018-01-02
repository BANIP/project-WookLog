package banip.action.board;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import banip.util.*;
import banip.action.ActionBoard;
import banip.bean.BoardBean;
import banip.dao.BoardDao;
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
		array.add("category_id");
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
		BoardDao boardDao = new BoardDao();
		BoardBean bean = getModifyBean(request);
		User user = super.getUser(request);
		BoardBean responseBean = boardDao.modifyBoard(bean, user);
		boardDao.close(true);
		
		
		return getBoardJSON(responseBean);
	}


	private BoardJSON getBoardJSON(BoardBean responseBean ) {
		boolean isBeanNull = responseBean == null;
		if(isBeanNull) return new BoardJSON(StatusCode.STATUS_SERVER,"게시글 수정에 실패했습니다.");
		else return responseBean.getBoardJSON();
	}
	
	private BoardBean getModifyBean(HttpServletRequest request) {
		BoardBean bean = new BoardBean();
		bean.setBOARD_CONTENT(super.getString(request, "board_content"));
		bean.setBOARD_TITLE(super.getString(request, "board_title"));
		bean.setBOARD_ID(super.getInt(request, "board_id"));
		bean.setBOARD_CATEGORY_ID(super.getInt(request, "category_id"));
		return bean;
	}


}
