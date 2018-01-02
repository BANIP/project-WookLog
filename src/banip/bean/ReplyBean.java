package banip.bean;

import java.sql.Timestamp;

import banip.dao.query.ReplyQuery;
public class ReplyBean extends SQLBean{
	protected int REPLY_ID;
	protected int REPLY_BOARD_ID;
	protected int REPLY_USER_ID;
	protected String REPLY_USER_NAME;
	protected Timestamp REPLY_DATE;
	protected Timestamp REPLY_DATE_MODIFY;
	protected String REPLY_CONTENT;

	public int getREPLY_ID() {
		return REPLY_ID;
	}

	public void setREPLY_ID(int rEPLY_ID) {
		REPLY_ID = rEPLY_ID;
	}

	public int getREPLY_BOARD_ID() {
		return REPLY_BOARD_ID;
	}

	public void setREPLY_BOARD_ID(int rEPLY_BOARD_ID) {
		REPLY_BOARD_ID = rEPLY_BOARD_ID;
	}

	public int getREPLY_USER_ID() {
		return REPLY_USER_ID;
	}

	public void setREPLY_USER_ID(int rEPLY_USER_ID) {
		REPLY_USER_ID = rEPLY_USER_ID;
	}

	public String getREPLY_USER_NAME() {
		return REPLY_USER_NAME;
	}

	public void setREPLY_USER_NAME(String rEPLY_USER_NAME) {
		REPLY_USER_NAME = rEPLY_USER_NAME;
	}

	public String getREPLY_DATE() {
		return super.toLocalString(REPLY_DATE);
	}

	public void setREPLY_DATE(Timestamp REPLY_DATE) {
		this.REPLY_DATE = REPLY_DATE;
	}

	public String getREPLY_DATE_MODIFY() {
		return super.toLocalString(REPLY_DATE_MODIFY);
	}

	public void setREPLY_DATE_MODIFY(Timestamp REPLY_DATE_MODIFY) {
		this.REPLY_DATE_MODIFY = REPLY_DATE_MODIFY;
	}

	public String getREPLY_CONTENT() {
		return REPLY_CONTENT;
	}

	public void setREPLY_CONTENT(String rEPLY_CONTENT) {
		REPLY_CONTENT = rEPLY_CONTENT;
	}

	public ReplyBean(){
		query = new ReplyQuery(this);
	}

	
	
}
