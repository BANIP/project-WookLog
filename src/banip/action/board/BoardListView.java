package banip.action.board;

import java.util.Iterator;
import java.util.ArrayList;


import javax.servlet.http.HttpServletRequest;
import org.json.simple.*;

import banip.sql.BoardDao;
import banip.util.*;
import banip.action.ActionBoard;
import banip.bean.BoardBean;
import banip.data.StatusCode;

/**
 * 게시글 열람 액션 객체
 * 
 * @author BANIP
 *
 */
public class BoardListView extends ActionBoard{
	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> array = new ArrayList<String>();
		array.add("board_list_offset");
		array.add("category_id");
		return array;
	}

	/**
	 * db에서 긁어올 글 갯수의 한도
	 */
	public final static int LIMIT = 10;

	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "GET";
	}

	/**
	 * board_list_offset이 올바르지 않은 값일 경우
	 * category_id가 존재하지 않는 경우
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		if(super.isCategoryNull(request)) return super.getStatusCode(StatusCode.STATUS_UNDEFINED,"카테고리가 존재하지 않습니다.");
		if(isBoardOffsetWrong(request)) return super.getStatusCode(StatusCode.STATUS_PARAM,"게시글 리스트의 인덱스가 잘못되었습니다.");
		
		int boardCount = getBoardCount( getCategoryID(request) );
		if(isBoardListNull(boardCount)) return super.getStatusCode(StatusCode.STATUS_UNDEFINED,"게시글이 존재하지 않습니다.");
		if(isBoardIndexOverflow(request,boardCount)) return super.getStatusCode(StatusCode.STATUS_UNDEFINED,"인덱스의 마지막입니다.");
		return super.getStatusCode(StatusCode.STATUS_SUCCESS);
	}
	private int getCategoryID(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getInt(request, "category_id");
	}
	
	private int getBoardListOffset(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getInt(request, "board_list_offset");
	}

	private boolean isBoardOffsetWrong(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return getBoardListOffset(request) < 1;
	}

	
	private boolean isBoardListNull(int boardCount) {
		// TODO Auto-generated method stub
		return boardCount == 0;
	}

	private boolean isBoardIndexOverflow(HttpServletRequest request,int boardCount) {
		// TODO Auto-generated method stub
		return getStartIndex(request) > boardCount;
	}


	private int getStartIndex(HttpServletRequest request) {
		return ( getBoardListOffset(request) - 1) * LIMIT + 1;
	}
	
	/**
	 * 게시글 갯수 획득
	 * @param categoryID 게시글 갯수를 취득할 카테고리의 고유키
	 * @return 글갯수
	 */
	private int getBoardCount(int categoryID) {
		return super.getCategoryBean(categoryID).getCATEGORY_BOARD_COUNT();
	};

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
			BoardJSON boardJSON = new BoardJSON();
			BoardDao dao = new BoardDao();
			int startIndex = getStartIndex(request);
			int categoryID = getCategoryID(request);

			ArrayList<BoardBean> beans = dao.getBoardList(categoryID  , startIndex, LIMIT);
			boardJSON.putData("list", getListJSON(beans.iterator()) );
			dao.close(true);
			
			return boardJSON;
	}
	
	@SuppressWarnings("unchecked")
	private JSONArray getListJSON(Iterator<BoardBean> beanIter) {
		JSONArray array = new JSONArray();
		while(beanIter.hasNext()) {
			BoardBean bean = beanIter.next(); 
			JSONObject json = new JSONObject();
			json.put( "board_id", bean.getBOARD_ID());
			json.put( "board_category_name", bean.getBOARD_CATEGORY_NAME());
			json.put( "board_hit", bean.getBOARD_HIT());
			json.put( "board_like", bean.getBOARD_LIKE());
			json.put( "board_user_name", bean.getBOARD_USER_NAME());
			json.put( "board_title", bean.getBOARD_TITLE());
			json.put( "board_date_create", bean.getBOARD_DATE_CREATE());
			json.put( "board_reply_count", bean.getBOARD_REPLY_COUNT());
			array.add(json);
		}
		return array;
	}





}
