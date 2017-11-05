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
	private class BoardListBulider {		Integer categoryID;
		Integer offset;
		Integer limit;
		String regex;
		
		public BoardListBulider() {
			regex = ".*";
		}
		
		public BoardListBulider setCategoryID(Integer categoryID) {
			this.categoryID = categoryID;
			return this;
		}
		public BoardListBulider setOffset(Integer offset) {
			this.offset = offset;
			return this;
		}
		public BoardListBulider setLimit(Integer limit) {
			this.limit = limit;
			return this;
		}
		
		public BoardListBulider setRegex(String searchWord) {

			if(searchWord == null || searchWord == "null") return this;
			regex = searchWord.replace(" ", "|");
			
			return this;
		}
		
		public BeanList build() {
			
			BoardDao dao = new BoardDao();
			ArrayList<BoardBean> beanList = dao.getBoardList(categoryID, offset, limit, regex);
			dao.close(true);
			
			return new BeanList( beanList );
		}
	}
	private class BeanList{
		ArrayList<BoardBean> beanList;
		
		public BeanList(ArrayList<BoardBean> beanList){
			this.beanList = beanList;
		}
		
		private Iterator<BoardBean> getIterator() {
			return beanList.iterator();
		}
		
		@SuppressWarnings("unchecked")
		private JSONArray getListJSON( ) {
			 Iterator<BoardBean> beanIter = this.getIterator();
			JSONArray array = new JSONArray();
			while(beanIter.hasNext()) {
				BoardBean bean = beanIter.next(); 
				JSONObject json = bean.getJSON(bean.getListIgnore());
				array.add(json);
			}
			return array;
		}
	}
	
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
		return  super.getInt(request, "board_list_offset");
	}
	
	private String getSearchWord(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return  super.getString(request, "board_search_word");
	}
	
	private boolean isBoardOffsetWrong(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return getBoardListOffset(request) < 0;
	}
	private boolean isBoardListNull(int boardCount) {
		// TODO Auto-generated method stub
		return boardCount == 0;
	}

	private boolean isBoardIndexOverflow(HttpServletRequest request,int boardCount) {
		// TODO Auto-generated method stub

		return getBoardListOffset(request) * LIMIT  > boardCount;
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

			BoardListBulider listBuilder = new BoardListBulider();
			listBuilder.setCategoryID( getCategoryID(request) )
						  .setOffset( getBoardListOffset(request) )
						  .setLimit( LIMIT )
						  .setRegex( getSearchWord(request) );
			
			BeanList beanList = listBuilder.build();

			return getResultJSON(beanList);
	}
	
	private BoardJSON getResultJSON(BeanList beanList) {
		BoardJSON boardJSON = new BoardJSON();
		boardJSON.putData("list", beanList.getListJSON() );
		return boardJSON;
	}

}
