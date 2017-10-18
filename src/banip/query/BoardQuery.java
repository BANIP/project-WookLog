package banip.query;

import banip.bean.*;

public class BoardQuery extends SQLQuery{
	protected BoardBean bean;
	
	public BoardQuery(BoardBean bean){
		this.bean = bean;
	}

	/**
	 * 게시글의 상세 내용을 조회하는 쿼리를 반환
	 * @사용테이블<ul>
	 *   <li>SELECT BOARD_VIEW_MAIN</li>
	 * </ul>
	 * @쿼리결과값 <ol>
	 * <li>int BOARD_ID 게시글의 기본키</li>
	 * <li>int BOARD_CATEGORY_ID 게시글이 참조하는 카테고리의 기본키</li>
	 * <li>int BOARD_HIT 게시글의 조회수 </li>
	 * <li>int BOARD_LIKE 게시글의 좋아요 수 </li>
	 * <li>String BOARD_CATEGORY_NAME 게시글이 참조하는 카테고리의 이름 </li>
	 * <li>String BOARD_USER_NAME 게시글의 작성자 </li>
	 * <li>String BOARD_TITLE 게시글의 제목 </li>
	 * <li>String(text) BOARD_CONTENT 게시글의 내용 </li>
	 * <li>int BOARD_REPLY_COUNT 덧글의 갯수 </li>
	 * <li>Date BOARD_DATE_CREATE 게시글의 만든날짜 </li>
	 * <li>Date BOARD_DATE_MODIFY 게시글의 수정날짜 </li>
	 * </ol>
	 * @param boardID 조회하려는 게시글의 id
	 * @return 쿼리
	 */
	public String getSelectDetailQuery(int boardID){
		String query = "SELECT "
				+ new BoardBean().getFieldsString() + " "
				+ "FROM BOARD_VIEW_MAIN "
				+ String.format("WHERE BOARD_ID = %d;", boardID);
		return query;
	}

	/**
	 * 게시글의 리스트를 조회하는 쿼리를 반환
	 * @사용테이블<ul>
	 *   <li>SELECT BOARD_VIEW_MAIN</li>
	 * </ul>
	 * @쿼리결과값 <ol>
	 * <li>int BOARD_ID 게시글의 기본키</li>
	 * <li>String BOARD_CATEGORY_NAME 게시글이 참조하는 카테고리의 이름 </li>
	 * <li>int BOARD_HIT 게시글의 조회수 </li>
	 * <li>int BOARD_LIKE 게시글의 좋아요 수 </li>
	 * <li>String BOARD_USER_NAME 게시글의 작성자 </li>
	 * <li>String BOARD_TITLE 게시글의 제목 </li>
	 * <li>Date BOARD_DATE_CREATE 게시글의 만든날짜 </li>
	 * <li>int BOARD_REPLY_COUNT 덧글의 갯수 </li>
	 * </ol>
	 * @param categoryID 조회하려는 게시글이 존재하는 카테고리의 id
	 * @param start 반환되는 첫번째 게시글의 위치
	 * @param limit 반환되는 게시글의 갯수
	 * @return 쿼리
	 */
	public String getSelectListQuery(int categoryID,int start, int limit){
		String query = "SELECT "
				+ "BOARD_ID, BOARD_CATEGORY_NAME, BOARD_HIT, BOARD_LIKE, BOARD_USER_NAME, BOARD_TITLE,BOARD_DATE_CREATE, BOARD_REPLY_COUNT "
				+ "FROM BOARD_VIEW_MAIN ";
		if(categoryID >= 0) query += String.format("WHERE BOARD_CATEGORY_ID = %d ", categoryID);
		query += "ORDER BY BOARD_DATE_CREATE DESC ";
		if(limit > 0) query += String.format("LIMIT %d,%d;",start - 1,limit);
		
		return query;
	}

	/**
	 * 새로운 게시글의 작성하는 쿼리를 반환
	 * @필요변수<ul>
	 * 	<li>BOARD_TITLE</li> 
	 *  <li>BOARD_CONTENT</li>
	 *  <li>BOARD_CATEGORY_ID</li>
	 * </ul>
	 * @사용테이블<ul>
	 *   <li>INSERT BOARD_HISTORY(HISTORY_TITLE, HISTORY_CONTENT)</li>
	 *   <li>INSERT BOARD_MAIN(BOARD_CATEGORY_ID, BOARD_USER_ID, BOARD_HISTORY_ID)</li>
	 * </ul>
	 * @쿼리결과값 int 5
	 * @메커니즘<ol>
	 * 	<li> BOARD_HISTORY에 새로운 글 내용을 작성 </li>
	 * 	<li> BOARD_MAIN에 history를 참조하는 튜플 작성 </li>
	 * 	<li> history의 튜플을 board_main을 참조하도록 업데이트 </li>
	 * </ol>
	 * @param boardUserID 글의 작성자로 사용할 사용자 아이디
	 * @return 쿼리
	 */
	public String getAddBoardQuery(int boardUserID){
		String query;
		String boardTitle = bean.getBOARD_TITLE();
		String boardContent = bean.getBOARD_CONTENT();
		int boardCategoryID = bean.getBOARD_CATEGORY_ID();
		
		query = String.format("INSERT INTO BOARD_HISTORY(HISTORY_TITLE, HISTORY_CONTENT) VALUES('%s','%s');",boardTitle,boardContent)
		+ "SET @ID_HISTORY = LAST_INSERT_ID();"
		+ String.format("INSERT INTO BOARD_MAIN(BOARD_CATEGORY_ID, BOARD_USER_ID, BOARD_HISTORY_ID) VALUES(%d,%d,@ID_HISTORY);",boardCategoryID,boardUserID)
		+ "SET @ID_BOARD = LAST_INSERT_ID();"
		+ "UPDATE BOARD_HISTORY SET HISTORY_BOARD_ID = @ID_BOARD WHERE HISTORY_BOARD_ID = @ID_BOARD;";
		
		return query;
	}
	
	/**
	 * 게시글을 삭제하는 쿼리를 반환
	 * @사용테이블<ul>
	 *   <li>DELETE BOARD_MAIN</li>
	 * </ul>
	 * @쿼리결과값 int 1
	 * @param boardID 게시글의 ID
	 * @return 쿼리
	 */
	public String getDeleteBoardQuery(int boardID){
		String query = String.format("DELETE FROM BOARD_MAIN WHERE BOARD_ID = %d;", boardID);
		return query;
	}
	
	/**
	 * 게시글을 수정하는 쿼리를 반환
	 * @메커니즘<ol>
	 * 	<li> BOARD_HISTORY에 새로운 글 내용 작성 </li>
	 * 	<li> BOARD_MAIN의 history 참조 컬럼을 업데이트 </li>
	 * </ol>
	 * @필요변수<ul>
	 * 	<li>BOARD_ID</li> 
	 * 	<li>BOARD_TITLE</li> 
	 * 	<li>BOARD_CONTENT</li> 
	 * 	<li>BOARD_CATEGORY_ID</li> 
	 * </ul>
	 * @사용테이블<ul>
	 *   <li>INSERT BOARD_HISTORY(HISTORY_TITLE, HISTORY_CONTENT)</li>
	 *   <li>UPDATE BOARD_MAIN(BOARD_HISTORY_ID, BOARD_CAREGORY)</li>
	 * </ul>
	 * @쿼리결과값 int 3
	 * @return 쿼리
	 */
	public String getModifyBoardQuery(){
		int boardID = bean.getBOARD_ID();
		int boardCategoryID = bean.getBOARD_CATEGORY_ID();
		String boardTitle = bean.getBOARD_TITLE();
		String boardContent = bean.getBOARD_CONTENT();
		String query = 
				String.format("INSERT INTO BOARD_HISTORY(HISTORY_TITLE, HISTORY_CONTENT) VALUES (%s,%s);", boardTitle, boardContent)
				+ "SET @HISTORY_ID = LAST_INSERT_ID();"
				+ String.format("UPDATE BOARD_MAIN BOARD_HISTORY_ID = @HISTORY_ID, BOARD_CATEGORY_ID = %d SET WHERE BOARD_ID = %d;",boardCategoryID,boardID);
		return query;
	}
	
	/**
	 * 게시글의 조회수를 추가하는 쿼리를 반환
	 * @사용테이블<ul>
	 *   <li>INSERT BOARD_HIT(HIT_IP, HIT_USER_ID, HIT_BOARD_ID)</li>
	 * </ul>
	 * @쿼리결과값 int 1
	 * @param userID 조회한 사용자 아이디의 primary key
	 * @param userIP 조회한 사용자의 아이피
	 * @param boardID 조회한 게시글의 primary key
	 * @return 쿼리
	 */
	public String getAddHitQuery(int userID, String userIP, int boardID){
		if(userID < 0) {
			return String.format("INSERT INTO BOARD_HIT(HIT_USER_ID, HIT_BOARD_ID, HIT_IP) VALUES (%d,%d,'%s');",userID,boardID,userIP);
		} else {
			return String.format("INSERT INTO BOARD_HIT(HIT_BOARD_ID, HIT_IP) VALUES (%d,'%s');",boardID,userIP);
		}
	}
	
	/**
	 * 게시글의 좋아요를 추가하는 쿼리를 반환
	 * @사용테이블<ul>
	 *   <li>INSERT BOARD_LIKE(LIKE_IP, LIKE_USER_ID, LIKE_BOARD_ID)</li>
	 * </ul>
	 * @쿼리결과값 int 1
	 * @param userID 좋아요 할 사용자 아이디의 primary key
	 * @param userIP 좋아요 할 사용자의 아이피
	 * @param boardID 좋아요 할 게시글의 primary key
	 * @return 쿼리
	 */
	public String getInsertLikeQuery(int userID, String userIP, int boardID){
		if(userID > 0) {
			return String.format("INSERT INTO BOARD_LIKE(LIKE_USER_ID, LIKE_BOARD_ID, LIKE_IP) VALUES (%d,%d,'%s');",userID,boardID,userIP);
		} else {
			return String.format("INSERT INTO BOARD_LIKE(LIKE_BOARD_ID, LIKE_IP) VALUES (%d,'%s');",boardID,userIP);
		}

	}

	/**
	 * 게시글의 좋아요를 삭제하는 쿼리를 반환
	 * @사용테이블<ul>
	 *   <li>DELETE BOARD_LIKE(LIKE_USER_ID, LIKE_BOARD_ID)</li>
	 * </ul>
	 * @쿼리결과값 int 1
	 * @param userIP 좋아요를 지울 사용자의 아이피
	 * @param boardID 좋아요를 지울 게시글의 primary key
	 * @return 쿼리
	 */
	public String getDeleteLikeQuery(int boardID,String userIP,int userID){
		String query = "DELETE FROM BOARD_LIKE ";
		if(userID > 0) {
			query += String.format("WHERE LIKE_BOARD_ID = %d AND LIKE_USER_ID = %d;",boardID,userID);
		} else {
			query += String.format("WHERE LIKE_BOARD_ID = %d AND LIKE_IP = '%s';",boardID,userIP);
		}
		return query;
	}

	/**
	 * 게시글의 좋아요 상태를 확인하는 쿼리를 반환
	 * @사용테이블<ul>
	 *   <li>SELECT COUNT(*) FROM BOARD_LIKE</li>
	 * </ul>
	 * @쿼리결과값 존재하면 1 존재하지 않으면 0
	 * @param userID 좋아요를 확인할 사용자의 고유번호, 고유번호가 0 이하일 시 userIP로 획득
	 * @param userIP 좋아요를 확인할 사용자의 아이피
	 * @param boardID 좋아요를 확인할 게시글의 primary key
	 * @return 쿼리
	 */
	public String getGetLikeQuery(int boardID,String userIP,int userID){
		String query = "SELECT COUNT(*) FROM BOARD_LIKE ";
		if(userID > 0) {
			query += String.format("WHERE LIKE_BOARD_ID = %d AND LIKE_USER_ID = %d;",boardID,userID);
		} else {
			query += String.format("WHERE LIKE_BOARD_ID = %d AND LIKE_IP = '%s';",boardID,userIP);
		}
		return query;
	}
}
