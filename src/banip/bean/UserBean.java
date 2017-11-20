package banip.bean;
import java.sql.Timestamp;

import banip.dao.query.UserQuery;

public class UserBean extends SQLBean{
	protected int USER_ID;
	protected String USER_NAME;
	protected String USER_PWD;
	protected String USER_EMAIL;
	protected Timestamp USER_DATE_CREATE;
	protected Timestamp USER_DATE_LOGIN;
	protected boolean USER_PERMISSION_WRITE;
	protected boolean USER_PERMISSION_COMMENT;
	protected boolean USER_PERMISSION_REMOVE;
	
	
	public UserBean(){
		query = new UserQuery(this);
	}
	
	public int getUSER_ID() {
		return USER_ID;
	}


	public void setUSER_ID(int uSER_ID) {
		USER_ID = uSER_ID;
	}


	public String getUSER_NAME() {
		return USER_NAME;
	}


	public void setUSER_NAME(String uSER_NAME) {
		USER_NAME = uSER_NAME;
	}


	public String getUSER_PWD() {
		return USER_PWD;
	}


	public void setUSER_PWD(String uSER_PWD) {
		USER_PWD = uSER_PWD;
	}


	public String getUSER_EMAIL() {
		return USER_EMAIL;
	}


	public void setUSER_EMAIL(String uSER_EMAIL) {
		USER_EMAIL = uSER_EMAIL;
	}


	public String getUSER_DATE_CREATE() {
		return super.toLocalString(USER_DATE_CREATE);
	}


	public void setUSER_DATE_CREATE(Timestamp uSER_DATE_CREATE) {
		USER_DATE_CREATE = uSER_DATE_CREATE;
	}


	public String getUSER_DATE_LOGIN() {
		return super.toLocalString(USER_DATE_LOGIN);
	}


	public void setUSER_DATE_LOGIN(Timestamp uSER_DATE_LOGIN) {
		USER_DATE_LOGIN = uSER_DATE_LOGIN;
	}


	public boolean isUSER_PERMISSION_WRITE() {
		return USER_PERMISSION_WRITE;
	}


	public void setUSER_PERMISSION_WRITE(boolean uSER_PERMISSION_WRITE) {
		USER_PERMISSION_WRITE = uSER_PERMISSION_WRITE;
	}


	public boolean isUSER_PERMISSION_COMMENT() {
		return USER_PERMISSION_COMMENT;
	}


	public void setUSER_PERMISSION_COMMENT(boolean uSER_PERMISSION_COMMENT) {
		USER_PERMISSION_COMMENT = uSER_PERMISSION_COMMENT;
	}


	public boolean isUSER_PERMISSION_REMOVE() {
		return USER_PERMISSION_REMOVE;
	}


	public void setUSER_PERMISSION_REMOVE(boolean uSER_PERMISSION_REMOVE) {
		USER_PERMISSION_REMOVE = uSER_PERMISSION_REMOVE;
	}
	
}
