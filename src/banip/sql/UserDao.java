package banip.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

import banip.bean.*;
import banip.data.User;
import banip.sql.query.*;

public class UserDao extends SQLDao{
	protected static final String id = "BANIP";
	protected static final String pwd = "a5429516";
	protected static final String link = "jdbc:mysql://localhost:3306/education?useUnicode=true&characterEncoding=UTF-8";
	
	/**
	 * 사용자의 아이디와 비밀번호가 올바른지 체크
	 * userName혹은 userID중 하나는 null이어야 함
	 * @param userName 문자로된 사용자 id
	 * @param pwd 비밀번호
	 * @return
	 */
	public boolean isEffectiveUser(User user){
		   String query = new UserQuery(null).getcheckUserQuery(user);
			try {
				pstmt = conn.prepareStatement(query);
				rs = pstmt.executeQuery();
				if(rs.next()){
					return rs.getInt(1) == 1 ? true : false;
				}
			} catch(SQLException ee){
				printException(ee, query);
			} finally{
				close(false);
			}
			return false;
	}

	/**
	 * 새로운 사용자 등록
	 * @param userName 사용자명
	 * @param userPwd 사용자패스워드
	 * @return
	 */
	public UserBean addUser(User user){
		   String query = new UserQuery(null).getaddUserQuery(user);
			try {
				pstmt = conn.prepareStatement(query);
				rs = pstmt.executeQuery();
				if(rs.next()) {
					UserBean bean = new UserBean();
					bean.setFieldAll(rs);
					return bean;
				}
			} catch(SQLException ee){
				printException(ee, query);
			} finally{
				close(false);
			}
			return null;
	}
	
	/**
	 * 유저빈 획득
	 * @param userName 획득할 유저명, null일 경우 userid로 검사
	 * @param userID 획득할 유저 아이디, 이 파라미터로 획득할 경우 username 파라미터가 null이어야함
	 * @return
	 */
	public UserBean getUserBean(String userName, int userID) {
		   String query = new UserQuery(null).getUserQuery(userID, userName);
		   UserBean bean = null;
			try {
				pstmt = conn.prepareStatement(query);
				rs = pstmt.executeQuery();
				if(rs.next()) {
					bean = new UserBean();
					bean.setFieldAll(rs);
				}
			} catch(SQLException ee){
				printException(ee, query);
			} finally{
				close(false);
			}
			return bean;
	}
	
	/**
	 * 사용자의 기본키로 유저네임 획득
	 */
	public String getUserName(int userID) {
		UserBean bean = getUserBean(null,userID);
		return bean.getUSER_NAME();
	}
	
	/**
	 * username으로 사용자의 기본키 획득
	 */
	public int getUserID(String userName) {
		return getUserBean(userName,-1).getUSER_ID();
	}
	
	/**
	 * 이미 등록된 사용자인지 체크
	 * @param userName 
	 * @return 사용 가능한 이름이면 true, 사용 불가능시 false 
	 */
	public boolean isExistName(String userName){
		   String query = new UserQuery(null).getisExistUserByNameQuery(userName);
			try {
				pstmt = conn.prepareStatement(query);
				rs = pstmt.executeQuery();
				if(rs.next()){
					return true;
				} else {
					return false;
				}
			} catch(SQLException ee){
				printException(ee, query);
			} finally{
				close(false);
			}
			return false;
	}

	/**
	 * 이미 등록된 이메일인지 체크
	 * @param userName 
	 * @return 사용 가능한 이메일이면 true, 사용 불가능시 false 
	 */
	public boolean isExistEmail(String email){
		   String query = new UserQuery(null).getisExistUserByEmailQuery(email);
			try {
				pstmt = conn.prepareStatement(query);
				rs = pstmt.executeQuery();
				if(rs.next()){
					return true;
				} else {
					return false;
				}
			} catch(SQLException ee){
				printException(ee, query);
			} finally{
				close(false);
			}
			return false;
	}
}
