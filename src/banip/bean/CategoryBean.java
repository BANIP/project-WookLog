package banip.bean;

import java.sql.Timestamp;

import banip.query.CategoryQuery;

public class CategoryBean extends SQLBean{
	protected int CATEGORY_ID;
	protected String CATEGORY_NAME;
	protected int CATEGORY_PREV_ID;
	protected String CATEGORY_PREV_NAME;
	protected int CATEGORY_BOARD_COUNT;
	protected int CATEGORY_LIKE;
	protected int CATEGORY_HIT;
	protected Timestamp CATEGORY_UPDATE_DATE;

	public int getCATEGORY_LIKE() {
		return CATEGORY_LIKE;
	}

	public void setCATEGORY_LIKE(int cATEGORY_LIKE) {
		CATEGORY_LIKE = cATEGORY_LIKE;
	}

	public int getCATEGORY_HIT() {
		return CATEGORY_HIT;
	}

	public void setCATEGORY_HIT(int cATEGORY_HIT) {
		CATEGORY_HIT = cATEGORY_HIT;
	}

	public String getCATEGORY_UPDATE_DATE() {
		return super.toLocalString(CATEGORY_UPDATE_DATE);
	}

	public void setCATEGORY_UPDATE_DATE(Timestamp cATEGORY_UPDATE_DATE) {
		CATEGORY_UPDATE_DATE = cATEGORY_UPDATE_DATE;
	}

	public CategoryBean(){
		query = new CategoryQuery(this);
	}

	public int getCATEGORY_ID() {
		return CATEGORY_ID;
	}

	public void setCATEGORY_ID(int cATEGORY_ID) {
		CATEGORY_ID = cATEGORY_ID;
	}

	public String getCATEGORY_NAME() {
		return CATEGORY_NAME;
	}

	public void setCATEGORY_NAME(String cATEGORY_NAME) {
		CATEGORY_NAME = cATEGORY_NAME;
	}

	public int getCATEGORY_PREV_ID() {
		return CATEGORY_PREV_ID;
	}

	public void setCATEGORY_PREV_ID(int cATEGORY_PREV_ID) {
		CATEGORY_PREV_ID = cATEGORY_PREV_ID;
	}

	public String getCATEGORY_PREV_NAME() {
		return CATEGORY_PREV_NAME;
	}

	public void setCATEGORY_PREV_NAME(String cATEGORY_PREV_NAME) {
		CATEGORY_PREV_NAME = cATEGORY_PREV_NAME;
	}

	public int getCATEGORY_BOARD_COUNT() {
		return CATEGORY_BOARD_COUNT;
	}

	public void setCATEGORY_BOARD_COUNT(int cATEGORY_BOARD_COUNT) {
		CATEGORY_BOARD_COUNT = cATEGORY_BOARD_COUNT;
	}
}
