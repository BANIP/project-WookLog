package banip.bean;

public class ImageTagBean extends SQLBean{

	protected int TAG_ID;
	protected String TAG_NAME;
	protected int TAG_IMAGE_ID;
	protected String TAG_IMAGE_TITLE;
	public int getTAG_ID() {
		return TAG_ID;
	}
	public void setTAG_ID(int tAG_ID) {
		TAG_ID = tAG_ID;
	}
	public String getTAG_NAME() {
		return TAG_NAME;
	}
	public void setTAG_NAME(String tAG_NAME) {
		TAG_NAME = tAG_NAME;
	}
	public int getTAG_IMAGE_ID() {
		return TAG_IMAGE_ID;
	}
	public void setTAG_IMAGE_ID(int tAG_IMAGE_ID) {
		TAG_IMAGE_ID = tAG_IMAGE_ID;
	}
	public String getTAG_IMAGE_TITLE() {
		return TAG_IMAGE_TITLE;
	}
	public void setTAG_IMAGE_TITLE(String tAG_IMAGE_TITLE) {
		TAG_IMAGE_TITLE = tAG_IMAGE_TITLE;
	}

}
