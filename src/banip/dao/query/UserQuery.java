package banip.dao.query;

import banip.bean.UserBean;
import banip.data.User;

public class UserQuery extends SQLQuery{
	protected UserBean bean;
	public UserQuery(UserBean bean){
		if(isNull(bean)) this.bean = new UserBean();
		else this.bean = bean;
	}
	
	/**
	 * userid 또는 username으로 사용자의 bean 객체를 취득
	 * 파라미터를 사용하지 않는 경우 userID = -1로, userName = null로 지정
 	 * @반환쿼리값 <ol>
 	 * <li>USER_ID 유저테이블의 기본 키</li>
 	 * <li>USER_NAME 사용자의 이름</li>
 	 * <li>USER_PWD 사용자의 비밀번호</li>
 	 * <li>USER_EMAIL 사용자의 이메일</li>
 	 * <li>USER_DATE_CREATE 사용자의 계정 생성일</li>
 	 * <li>USER_DATE_LOGIN 사용자의 최종 로그인일</li>
 	 * <li>USER_PERMISSION_WRITE 사용자의 게시글 작성권한</li>
 	 * <li>USER_PERMISSION_COMMENT 사용자의 코멘트 작성권한</li>
 	 * <li>USER_PERMISSION_REMOVE 사용자의 코멘트 삭제권한</li>
 	 * </ol>
	 * @param userID 사용자의 id값
	 * @param userName 사용자의 이름
	 * @return 쿼리
	 */
	public String getUserQuery(int userID, String userName){
		String columnsName = bean.getFieldsString();
		String query = String.format("SELECT %s FROM USER_VIEW_MAIN ", columnsName)
				+ "WHERE ";
				if(userID > 0) query += String.format("USER_ID = %d ", userID);
				if(userName != null) query +=  String.format("USER_NAME = '%s';", userName);
		return query;
	}
	
	/**
	 * 새로운 사용자를 등록하는 쿼리
 	 * @반환쿼리값 int 4
	 * @param userName 사용자 이름
	 * @param pwd 사용자의 비밀번호
	 * @param email 사용자의 이메일
	 * @return 쿼리
	 */
	public String getaddUserQuery(User user){
		String userName = user.getName();
		String userPWD = user.getPWD();
		String query = String.format("CALL USER_DO_ADD('%s','%s');", userName, userPWD);
		return query;
	}
		
	/**
	 * 아이디와 비밀번호가 올바른지 체크하는 쿼리
	 * @param username과 비밀번호가 담긴 User쿼리
	 * @반환쿼리값 맞으면 1 없으면 0
	 * @return 쿼리
	 */
	public String getcheckUserQuery(User user){
		String userName = user.getName();
		String userPWD = user.getPWD();

		String query = "SELECT COUNT(*) FROM USER_VIEW_MAIN "
			+ String.format("WHERE USER_NAME = '%s' AND USER_PWD = password('%s');", userName, userPWD);
		return query;
	}
	
	/**
	 * 특정 이름을 가진 사용자가 이미 존재하는지 검색하는 쿼리
 	 * @반환쿼리값 해당 사용자의 튜플
	 * @param userName 사용자 이름
	 * @return 쿼리
	 */
	public String getisExistUserByNameQuery(String userName){
		String query = "SELECT USER_NAME, USER_PWD FROM USER_VIEW_MAIN "
				+ String.format("WHERE USER_NAME = '%s'", userName);
		return query;
	}
	
	/**
	 * 특정 이메일을 가진 사용자가 이미 존재하는지 검색하는 쿼리
 	 * @반환쿼리값 해당 사용자의 튜플
	 * @param mail 사용자 이메일 주소
	 * @return 쿼리
	 */
	public String getisExistUserByEmailQuery(String mail){
		String query = "SELECT USER_NAME, USER_PWD FROM USER_VIEW_MAIN "
				+ String.format("WHERE USER_EMAIL = '%s'", mail);
		return query;
	}
}
