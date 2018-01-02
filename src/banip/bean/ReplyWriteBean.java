package banip.bean;

public class ReplyWriteBean extends SQLBean {
	protected boolean IS_SUCCESS;
	protected int REPLY_ID;
	protected int HISTORY_ID;
	public boolean isIS_SUCCESS() {
		return IS_SUCCESS;
	}
	public void setIS_SUCCESS(boolean iS_SUCCESS) {
		IS_SUCCESS = iS_SUCCESS;
	}
	public int getREPLY_ID() {
		return REPLY_ID;
	}
	public void setREPLY_ID(int rEPLY_ID) {
		REPLY_ID = rEPLY_ID;
	}
	public int getHISTORY_ID() {
		return HISTORY_ID;
	}
	public void setHISTORY_ID(int hISTORY_ID) {
		HISTORY_ID = hISTORY_ID;
	}
}
