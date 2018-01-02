package banip.bean;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import banip.dao.query.*;

public class BoardBean extends SQLBean{
	
	protected int BOARD_ID;
	protected int BOARD_HISTORY_ID;
	protected int BOARD_CATEGORY_ID;
	protected int BOARD_HIT;
	protected int BOARD_LIKE;
	protected String BOARD_CATEGORY_NAME;
	protected String BOARD_USER_NAME;
	protected String BOARD_TITLE;
	protected String BOARD_CONTENT;
	protected int BOARD_REPLY_COUNT;
	protected Timestamp BOARD_DATE_CREATE;
	protected Timestamp BOARD_DATE_MODIFY;

	public int getBOARD_REPLY_COUNT() {
		return BOARD_REPLY_COUNT;
	}

	public void setBOARD_REPLY_COUNT(int bOARD_REPLY_COUNT) {
		BOARD_REPLY_COUNT = bOARD_REPLY_COUNT;
	}
	public BoardBean(){
		query = new BoardQuery(this);
	}
	
	public int getBOARD_ID() {
		return BOARD_ID;
	}
	public void setBOARD_ID(int bOARD_ID) {
		BOARD_ID = bOARD_ID;
	}
	public int getBOARD_HISTORY_ID() {
		return BOARD_HISTORY_ID;
	}
	public void setBOARD_HISTORY_ID(int bOARD_HISTORY_ID) {
		BOARD_HISTORY_ID = bOARD_HISTORY_ID;
	}
	public int getBOARD_CATEGORY_ID() {
		return BOARD_CATEGORY_ID;
	}
	public void setBOARD_CATEGORY_ID(int bOARD_CATEGORY_ID) {
		BOARD_CATEGORY_ID = bOARD_CATEGORY_ID;
	}
	public int getBOARD_HIT() {
		return BOARD_HIT;
	}
	public void setBOARD_HIT(int bOARD_HIT) {
		BOARD_HIT = bOARD_HIT;
	}
	public int getBOARD_LIKE() {
		return BOARD_LIKE;
	}
	public void setBOARD_LIKE(int bOARD_LIKE) {
		BOARD_LIKE = bOARD_LIKE;
	}
	public String getBOARD_CATEGORY_NAME() {
		return BOARD_CATEGORY_NAME;
	}
	public void setBOARD_CATEGORY_NAME(String bOARD_CATEGORY_NAME) {
		BOARD_CATEGORY_NAME = bOARD_CATEGORY_NAME;
	}
	public String getBOARD_USER_NAME() {
		return BOARD_USER_NAME;
	}
	public void setBOARD_USER_NAME(String bOARD_USER_NAME) {
		BOARD_USER_NAME = bOARD_USER_NAME;
	}
	public String getBOARD_TITLE() {
		return BOARD_TITLE;
	}
	public void setBOARD_TITLE(String bOARD_TITLE) {
		BOARD_TITLE = bOARD_TITLE;
	}
	public String getBOARD_CONTENT() {
		return BOARD_CONTENT;
	}
	public void setBOARD_CONTENT(String bOARD_CONTENT) {
		BOARD_CONTENT = bOARD_CONTENT;
	}
	public String getBOARD_DATE_CREATE() {
		return super.toLocalString(BOARD_DATE_CREATE);
	}
	public void setBOARD_DATE_CREATE(Timestamp bOARD_DATE_CREATE) {
		BOARD_DATE_CREATE = bOARD_DATE_CREATE;
	}
	public String getBOARD_DATE_MODIFY() {
		return super.toLocalString(BOARD_DATE_MODIFY);
	}
	public void setBOARD_DATE_MODIFY(Timestamp bOARD_DATE_MODIFY) {
		BOARD_DATE_MODIFY = bOARD_DATE_MODIFY;
	}

	public Iterator<String> getListIgnore() {
		ArrayList<String> list = new ArrayList<String>();
		list.add("BOARD_CONTENT");
		return list.iterator();
		
	}



	
}
