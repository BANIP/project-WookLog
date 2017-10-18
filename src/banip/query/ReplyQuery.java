package banip.query;

import banip.bean.ReplyBean;

public class ReplyQuery extends SQLQuery{
	protected ReplyBean bean;
	public ReplyQuery(ReplyBean bean){
		this.bean = bean;
	}
	
	/**
	 * 덧글의 ID로 빈을 획득하는 쿼리 반환
	 * @사용테이블 BOARD_REPLY_VIEW
	 * @반환쿼리값<ol>
	<li>int REPLY_ID 덧글의 기본 키</li>
	<li>int REPLY_BOARD_ID 덧글이 참조하는 게시글의 기본 키</li>
	<li>int REPLY_USER_ID 덧글의 작성자의 기본 키</li>
	<li>String REPLY_USER_NAME 덧글의 작성자</li>
	<li>Date REPLY_DATE 덧글의 작성일</li>
	<li>Date REPLY_DATE_MODIFY 덧글의 수정일</li>
	<li>String(text) REPLY_CONTENT 덧글의 내용</li>
	 * <ol>
	 * @param boardID 조회할 게시글의 기본키
	 * @return 쿼리
	 */
	public String getSelectReplyQuery(int replyID){
		String query = "SELECT REPLY_ID, REPLY_BOARD_ID, REPLY_USER_ID, REPLY_HISTORY_ID, REPLY_DATE "
				+ "FROM BOARD_REPLY_VIEW "
				+ String.format("WHERE REPLY_ID = %d;", replyID);
		return query;
	}
	
/**
 * 특정 게시글의 덧글을 조회하는 쿼리 반환
 * @사용테이블 BOARD_REPLY_VIEW
 * @반환쿼리값<ol>
<li>int REPLY_ID 덧글의 기본 키</li>
<li>int REPLY_BOARD_ID 덧글이 참조하는 게시글의 기본 키</li>
<li>int REPLY_USER_ID 덧글의 작성자의 기본 키</li>
<li>String REPLY_USER_NAME 덧글의 작성자</li>
<li>Date REPLY_DATE 덧글의 작성일</li>
<li>Date REPLY_DATE_MODIFY 덧글의 수정일</li>
<li>String(text) REPLY_CONTENT 덧글의 내용</li>
 * <ol>
 * @param boardID 조회할 게시글의 기본키
 * @return 쿼리
 */
	public String getSelectListQuery(int boardID){
		String query = "SELECT REPLY_ID, REPLY_BOARD_ID, REPLY_USER_ID, REPLY_HISTORY_ID, REPLY_DATE "
				+ "FROM BOARD_REPLY_VIEW "
				+ String.format("WHERE REPLY_BOARD_ID = %d;", boardID);
		
		return query;
	}
	
	
	
	/**
	 * 덧글을 등록하는 쿼리를 반환
	 * @메커니즘<ol>
	 * <li>BOARD_REPLY_HISTORY에 덧글내용을 등록</li>
	 * <li>BOARD_REPLY의 새로운 튜플 등록 후 HISTORY에 등록한 튜플 참조</li>
	 * <li>BOARD_REPLY_HISTORY에 메인 테이블의 컬럼 참조</li>
	 * </ol>
	 * @필요변수<ul>
	 * 	<li>REPLY_BOARD_ID</li>
	 *	<li>REPLY_CONTENT</li> 
	 * </ul>
	 * @사용테이블<ul>
	 * 	<li>INSERT BOARD_REPLY_HISTORY(REPLY_CONTENT)</li>
	 *	<li>INSERT BOARD_REPLY(REPLY_BOARD_ID, REPLY_USER_ID, REPLY_HISTORY_ID)</li> 
	 * </ul>
	 * @쿼리결과값 int 5
	 * @param userID 덧글의 작성자 ID
	 * @return 쿼리
	 */
	public String getAddReplyQuery(int userID){
		String replyContent = bean.getREPLY_CONTENT();
		int replyBoardID = bean.getREPLY_BOARD_ID();
		String query = String.format("INSERT INTO BOARD_REPLY_HISTORY(REPLY_CONTENT) VALUES('%s');",replyContent)
				+ "SET @HISTORY_ID = LAST_INSERT_ID();"
				+ "INSERT INTO BOARD_REPLY(REPLY_BOARD_ID, REPLY_USER_ID, REPLY_HISTORY_ID)"
				+ String.format(" VALUES(%d, %d, @HISTORY_ID);",replyBoardID,userID)
				+ "SET @REPLY_ID = LAST_INSERT_ID();"
				+ "UPDATE BOARD_REPLY_HISTORY SET HISTORY_REPLY_ID = @REPLY_ID WHERE HISTORY_ID = @HISTORY_ID;";
		return query;
	}

	/** 덧글을 수정하는 쿼리 반환
	 * @메커니즘<ol>
	 * <li>BOARD_REPLY_HISTORY에 수정할 덧글내용을 등록</li>
	 * <li>BOARD_REPLY을 HISTORY에 등록한 튜플 참조하도록 업데이트</li>
	 * </ol>
	 * @필요변수<ul>
	 * 	<li>REPLY_ID</li>
	 *	<li>REPLY_CONTENT</li> 
	 * </ul>
	 * @사용테이블<ul>
	 * 	<li>INSERT BOARD_REPLY_HISTORY(HISTORY_CONTENT, HISTORY_REPLY_ID)</li>
	 *	<li>UPDATE BOARD_REPLY(REPLY_HISTORY_ID, REPLY_ID)</li> 
	 * </ul>
	 * @쿼리결과값 int 3
	 * @return 쿼리
	 */
	public String getModifyReplyQuery(){
		String replyContent = bean.getREPLY_CONTENT();
		int replyID = bean.getREPLY_ID();
		String query = String.format("INSERT INTO BOARD_REPLY_HISTORY(HISTORY_CONTENT, HISTORY_REPLY_ID) VALUES('%s',%d);",replyContent,replyID)
				+ "SET @HISTORY_ID = LAST_INSERT_ID();"
				+ String.format("UPDATE BOARD_REPLY SET REPLY_HISTORY_ID = @HISTORY_ID WHERE REPLY_ID = %d;",replyID);
		return query;
	}
	
	/**
	 * 덧글을 삭제하는 쿼리 반환
	 * @사용테이블<ul>
	 * 	<li>DELETE BOARD_REPLY</li>
	 * </ul>
	 * @쿼리결과값 int 3
	 * @param replyID 삭제할 덧글의 ID값
	 * @return 쿼리
	 */
	public String getDeleteReplyQuery(int replyID){
		String query = "DELETE FROM BOARD_REPLY"
				+ String.format("WHERE REPLY REPLY_ID = %d",replyID);
		return query;
	}
	
	/**
	 * 덧글의 작성자를 확인하는 쿼리 반환
	 * @사용테이블<ul>
	 * 	<li>SELECT BOARD_REPLY</li>
	 * </ul>
	 * @쿼리결과값 int 3
	 * @param replyID 확인할 덧글의 ID값
	 * @param userID 확인할 덧글의 사용자ID값
	 * @return 쿼리
	 */
	public String getIsWriterQuery(int replyID, int userID){
		String query = "SELECT * FROM BOARD_REPLY "
				+ String.format("WHERE REPLY_ID = %d AND REPLY_USER_ID = %d;", replyID,userID);
		return query;
	}

	

}
