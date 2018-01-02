package banip.action;

import javax.servlet.http.HttpServletRequest;

import banip.bean.CategoryBean;
import banip.dao.BoardDao;
import banip.data.BoardID;
import banip.data.BoardIDNull;
import banip.data.User;

public abstract class ActionBoard extends Action {
	/**
	 * 카테고리 키로 카테고리 bean 획득
	 * @param categoryID
	 * @return
	 */
	protected CategoryBean getCategoryBean(int categoryID) {
		// TODO Auto-generated method stub
		BoardDao dao = new BoardDao();
		CategoryBean bean = dao.getCategoryBean(categoryID);
		return bean;
	}
	
	/**
	 * board_id로 BoardID객체 획득
	 */
	protected BoardID getBoardID(HttpServletRequest request) {
		int boardID = getInt(request, "board_id");
		if(boardID <= 0 ) return new BoardIDNull();

		return new BoardID(boardID);
	}


	protected boolean isCategoryNull(HttpServletRequest request) {
		// TODO Auto-generated method stub
		int categoryID = super.getInt(request, "category_id");
		return getCategoryBean(categoryID) == null;
	}

	protected boolean isBoardNull(HttpServletRequest request) {
		// TODO Auto-generated method stub
		BoardID boardID = getBoardID(request);
		return boardID.isNull() || !boardID.isExist();
	}

	public boolean isBoardWriten(User user, BoardID boardID) {
		// TODO Auto-generated method stub
		return boardID.getBean().getBOARD_USER_NAME().equals( user.getName() );
	}


}
