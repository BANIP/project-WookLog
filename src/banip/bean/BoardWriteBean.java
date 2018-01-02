package banip.bean;

public class BoardWriteBean extends SQLBean {
	protected boolean IS_SUCCESS;
	protected int HISTORY_ID;
	protected int BOARD_ID;
	public boolean isIS_SUCCESS() {
		return IS_SUCCESS;
	}
	public void setIS_SUCCESS(boolean iS_SUCCESS) {
		IS_SUCCESS = iS_SUCCESS;
	}
	public int getHISTORY_ID() {
		return HISTORY_ID;
	}
	public void setHISTORY_ID(int hISTORY_ID) {
		HISTORY_ID = hISTORY_ID;
	}
	public int getBOARD_ID() {
		return BOARD_ID;
	}
	public void setBOARD_ID(int bOARD_ID) {
		BOARD_ID = bOARD_ID;
	}
}
