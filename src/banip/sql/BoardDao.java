package banip.sql;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import banip.bean.*;
import banip.data.User;
import banip.query.*;
import banip.util.CategoryTree;
public class BoardDao extends SQLDao{

	protected static final String id = "BANIP";
	protected static final String pwd = "a5429516";
	protected static final String link = "jdbc:mysql://localhost:3306/education?useUnicode=true&characterEncoding=UTF-8";
	

	public BoardDao(){
		super();
	}
	

	/**
	 * 카테고리 빈 획득
	 * @param categoryID 획득할 카테고리의 ID
	 * @return 빈
	 */
	public CategoryBean getCategoryBean(int categoryID){
		CategoryQuery queryList = (CategoryQuery) new CategoryBean().getQuery();
		String query = queryList.getCategoryQuery(categoryID);
		CategoryBean bean = new CategoryBean();
		try {
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			if(rs.next()){
				bean.setCATEGORY_ID( rs.getInt(1) ); 
				bean.setCATEGORY_NAME( rs.getString(2) ); 
				bean.setCATEGORY_PREV_ID( rs.getInt(3) ); 
				bean.setCATEGORY_PREV_NAME( rs.getString(4) ); 
				bean.setCATEGORY_BOARD_COUNT( rs.getInt(5) );
			} else {
				return null;
			}
		} catch (SQLException ee){
			printException(ee, query);
			
		} finally {
			close(false);
		}
		return bean;
	}

	/**
	 * 글 목록 출력
	 * @param categoryIndex 가져올 글의 카테고리
	 * @param startIndex 가져올 글의 시작부분
	 * @param limit 가져올 글의 최대한도
	 * @return 보드빈의 리스트
	 */
	public ArrayList<BoardBean> getBoardList(int categoryID,int startIndex, int limit){
		ArrayList<BoardBean> beanList = new ArrayList<BoardBean>();
		BoardQuery queryList = (BoardQuery) new BoardBean().getQuery();
		String query = queryList.getSelectListQuery(categoryID, startIndex, limit);
		try {
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			while(rs.next()){
				BoardBean bean = new BoardBean();
				bean.setBOARD_ID( rs.getInt(1) );
				bean.setBOARD_CATEGORY_NAME( rs.getString(2) );
				bean.setBOARD_HIT( rs.getInt(3) );
				bean.setBOARD_LIKE( rs.getInt(4) );
				bean.setBOARD_USER_NAME( rs.getString(5) );
				bean.setBOARD_TITLE( rs.getString(6) );
				bean.setBOARD_DATE_CREATE( rs.getTimestamp(7) );
				bean.setBOARD_REPLY_COUNT( rs.getInt(8) );
				beanList.add(bean);
			}
			
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return beanList;
	}
	/**
	 * 보드빈 획득
	 * @exception 게시글이 없을시 null 출력
	 * @param index
	 * @return 게시글의 bean 객체
	 */
	public BoardBean getBoardBean(int boardID){

		BoardBean bean = new BoardBean();
		BoardQuery queryList = (BoardQuery) new BoardBean().getQuery();
		String query = queryList.getSelectDetailQuery(boardID);
		try {
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();

			if(rs.next()){
				bean.setBOARD_ID( rs.getInt(1) );
				bean.setBOARD_CATEGORY_ID( rs.getInt(2) );
				bean.setBOARD_HIT( rs.getInt(3) );
				bean.setBOARD_LIKE( rs.getInt(4) );
				bean.setBOARD_CATEGORY_NAME( rs.getString(5) );
				bean.setBOARD_USER_NAME( rs.getString(6) );
				bean.setBOARD_TITLE( rs.getString(7) );
				bean.setBOARD_CONTENT( rs.getString(8) );
				bean.setBOARD_REPLY_COUNT( rs.getInt(9) );
				bean.setBOARD_DATE_CREATE( rs.getTimestamp(10) );
				bean.setBOARD_DATE_MODIFY( rs.getTimestamp(11) );
				return bean;
			}
			
		} catch(Exception ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return null;
	}
	/**
	 * 새로운 글 등록
	 * @필요변수<ul>
	 * 	<li>BOARD_TITLE</li> 
	 *  <li>BOARD_CONTENT</li>
	 *  <li>BOARD_CATEGORY_ID</li>
	 *  <li>BOARD_USER_ID</li>
	 * </ul>
	 * @param bean 게시글의 빈 객체
	 * @param boardUserID 등록할 유저의 ID키
	 * @return 등록된 게시글의 기본키, 등록실패시 -1
	 */
	public BoardWriteBean addBoard(BoardBean bean,User user){
		BoardQuery queryList = (BoardQuery) bean.getQuery();
		String query = queryList.getAddBoardQuery(user);
		try{
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			BoardWriteBean writebean = new BoardWriteBean();
			if(rs.next()){
				writebean.setIS_SUCCESS( rs.getBoolean(1));
				writebean.setHISTORY_ID( rs.getInt(2));
				writebean.setBOARD_ID( rs.getInt(3));
				return writebean;
			}
			
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return null;
	}
	
		
	/**
	 * 게시글의 수정
	 * @param bean 게시글의 빈 객체
	 * @return 게시글이 수정됬는지 아닌지의 여부
	 */
	public boolean modifyBoard(BoardBean bean){
		BoardQuery queryList = (BoardQuery) bean.getQuery();
		String query = queryList.getModifyBoardQuery();
		try{
			pstmt = conn.prepareStatement(query);
			int result = pstmt.executeUpdate();
			if(result != 3) return false;
			return true;
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return false;
	}
	/**
	 * 게시글의 삭제
	 * @param bean 게시글의 빈 객체
	 * @return 게시글이 삭제되었는지 아닌지의 여부
	 */
	public boolean deleteBoard(int boardID){
		String query = new BoardQuery(null).getDeleteBoardQuery(boardID);
		try{
			pstmt = conn.prepareStatement(query);
			int result = pstmt.executeUpdate();
			if(result != 1) return false;
			return true;
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return false;
	}
	/**
	 * 게시글 조회수 추가
	 * @param bean
	 * @return
	 */
	public boolean addHitBoard(int userID, String userIP, int boardID){
		String query = new BoardQuery(null).getAddHitQuery(userID, userIP, boardID);
		try{
			pstmt = conn.prepareStatement(query);
			int result = pstmt.executeUpdate();
			if(result == 0) return false;
			return true;
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return false;
	}

	public boolean addLikeBoard(int userID, String userIP, int boardID) {
		String query = new BoardQuery(null).getInsertLikeQuery(userID, userIP, boardID);
		try{
			pstmt = conn.prepareStatement(query);
			int result = pstmt.executeUpdate();
			if(result == 1) return true;
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return false;
	}
	
	public boolean isLikeBoard(int userID, String userIP, int boardID) {
		String query = new BoardQuery(null).getGetLikeQuery(boardID, userIP, userID);
		try{
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()) {
				return rs.getInt(1) == 1;
			}
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return false;
	}
	
	public boolean removeLikeBoard(int userID, String userIP, int boardID){
		String query = new BoardQuery(null).getDeleteLikeQuery(boardID,userIP,userID);
		try{
			pstmt = conn.prepareStatement(query);
			int result = pstmt.executeUpdate();
			if(result == 1) return true;
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return false;
	}

	
	/**
	 * 덧글 등록
	 * @param bean 덧글의 빈 객체
	 * @param userID 덧글남기는 사용자의 id
	 * @return 제대로 등록 되었는지 여부
	 */
	public ReplyWriteBean addReply(ReplyBean bean,User user){
		ReplyQuery queryList = (ReplyQuery) bean.getQuery();
		String query = queryList.getAddReplyQuery(user);
		ReplyWriteBean writebean = new ReplyWriteBean();
		try{
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				writebean.setIS_SUCCESS( rs.getBoolean(1) );
				writebean.setREPLY_ID( rs.getInt(2) );
				writebean.setHISTORY_ID( rs.getInt(3) );
			}
		} catch(SQLException ee){
			printException(ee, query);
			writebean.setIS_SUCCESS( false );
		} finally{
			close(false);
		}
		return writebean;
	}	
	/** 
	 * 덧글 목록 획득
	 * @param boardIndex 덧글을 가져올 게시글의 ID
	 * @return 빈 리스트
	 */
	public ArrayList<ReplyBean> getReplyList(int boardID){
		ArrayList<ReplyBean> beanArray = new ArrayList<ReplyBean>();
		String query = new ReplyQuery( new ReplyBean() ).getSelectListQuery(boardID);
		try{
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			while(rs.next()){
				ReplyBean bean = new ReplyBean();
				bean.setREPLY_ID( rs.getInt(1) );
				bean.setREPLY_BOARD_ID( rs.getInt(2) );
				bean.setREPLY_USER_ID( rs.getInt(3) );
				bean.setREPLY_USER_NAME( rs.getString(4) );
				bean.setREPLY_DATE( rs.getTimestamp(5) );
				bean.setREPLY_DATE_MODIFY( rs.getTimestamp(6) );
				bean.setREPLY_CONTENT( rs.getString(7) );
				beanArray.add(bean);
			}
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return beanArray;
	}
	
	/**
	 * 덧글 삭제
	 * @param replyID 삭제할 덧글의 ID
	 * @return 삭제됬으면 true 실패했으면 false
	 */
	public boolean removeReply(int replyID){
		String query = new ReplyQuery(null).getDeleteReplyQuery(replyID);
		try{
			pstmt = conn.prepareStatement(query);
			int result = pstmt.executeUpdate();
			if(result == 3) return true;
			return false;
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return false;
	}
	
	/**
	 * 덧글 수정
	 * @param bean 수정할 덧글의 빈객체
	 * @return 성공여부
	 */
	public boolean modifyReply(ReplyBean bean){
		String query =( (ReplyQuery) bean.getQuery() ).getModifyReplyQuery();
		try{
			pstmt = conn.prepareStatement(query);
			int result = pstmt.executeUpdate();
			if(result == 3){
				return true;
			}
			return false;
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return false;
	}
	
	
	/**
	 * 카테고리명 획득
	 * @param categoryID 대상이되는 카테고리의 ID
	 * @return
	 */
	public String getCategoryName(int categoryID){
		String query = new CategoryQuery(null).getCategoryNameQuery(categoryID);
		String result = null;
		try {
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			if(rs.next()){
				result = rs.getString(1);
			}
			
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return result;
	}
	
	/**
	 * 카테고리 빈으로 획득
	 * @param where
	 * @return 빈
	 */
	public CategoryBean getCategory(int categoryID){
		String query = new CategoryQuery(null).getCategoryQuery(categoryID);
		CategoryBean bean = new CategoryBean();
		try {
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			if(rs.next()){
				bean.setCATEGORY_ID( rs.getInt(1) );
				bean.setCATEGORY_NAME( rs.getString(2) );
				bean.setCATEGORY_PREV_ID( rs.getInt(3) );
				bean.setCATEGORY_PREV_NAME( rs.getString(4) );
				bean.setCATEGORY_BOARD_COUNT( rs.getInt(5) );
				bean.setCATEGORY_LIKE( rs.getInt(6) );
				bean.setCATEGORY_HIT( rs.getInt(7) );
				bean.setCATEGORY_UPDATE_DATE( rs.getTimestamp(8) );
			}
			
		} catch(SQLException ee){
			printException(ee, query);
		} finally{
			close(false);
		}
		return bean;
	}
	
	/**
	 * 자식 카테고리 획득
	 * @param CategoryID 부모 카테고리명의 id
	 * @return 빈
	 */
	public ArrayList<CategoryBean> getCategoryChild(int categoryID){
		ArrayList<CategoryBean> list = new ArrayList<CategoryBean>();
		String query = new CategoryQuery(null).getChildCategoryQuery(categoryID);
		try{
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			while(rs.next()){
				CategoryBean bean = new CategoryBean();
				bean.setCATEGORY_ID( rs.getInt(1) );
				bean.setCATEGORY_NAME( rs.getString(2) );
				bean.setCATEGORY_PREV_ID( rs.getInt(3) );
				bean.setCATEGORY_PREV_NAME( rs.getString(4) );
				bean.setCATEGORY_BOARD_COUNT( rs.getInt(5) );
				bean.setCATEGORY_LIKE( rs.getInt(6) );
				bean.setCATEGORY_HIT( rs.getInt(7) );
				bean.setCATEGORY_UPDATE_DATE( rs.getTimestamp(8) );
				list.add(bean);
			}
			return list;
		} catch (SQLException ee){
			printException(ee, query);
		} finally {
			close(false);
		}
		
		return null;
	}

	/**
	 * 카테고리 트리로 획득
	 * @param categoryID 카테고리의 id
	 * @return 트리객체
	 */
	public CategoryTree getCategoryTree(int categoryID){
		
		CategoryBean bean = getCategory(categoryID);
		CategoryTree tree = new CategoryTree(bean);
		
		Iterator<CategoryBean> childIter = getCategoryChild(categoryID).iterator();
		while(childIter.hasNext()){
			CategoryBean childBean = childIter.next();
			CategoryTree childTree = getCategoryTree( childBean.getCATEGORY_ID() );
			tree.addChild(childTree);
			childTree.setParents(tree);
		}
		
		return tree;
	}

	/**
	 * 덧글의 빈 객체 획득
	 * @param replyID 객체를 획득할 덧글의 고유 키
	 */
	public ReplyBean getReplyBean(int replyID) {
		// TODO Auto-generated method stub
		String query = new ReplyQuery(null).getSelectReplyQuery(replyID);
		try{
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			if(rs.next()){
				ReplyBean bean = new ReplyBean();
				bean.setREPLY_ID( rs.getInt(1) );
				bean.setREPLY_BOARD_ID( rs.getInt(2) );
				bean.setREPLY_USER_ID( rs.getInt(3) );
				bean.setREPLY_USER_NAME( rs.getString(4) );
				bean.setREPLY_DATE( rs.getTimestamp(5) );
				bean.setREPLY_DATE_MODIFY( rs.getTimestamp(6) );
				bean.setREPLY_CONTENT( rs.getString(7) );
				return bean;
			}
		} catch (SQLException ee){
			printException(ee, query);
		} finally {
			close(false);
		}
		
		return null;
	}
	
	

}
