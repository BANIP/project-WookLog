package banip.bean;

import java.sql.Timestamp;

public class ImageBean extends SQLBean{
public int IMAGE_ID;
public int getIMAGE_ID() {
	return IMAGE_ID;
}
public void setIMAGE_ID(int iMAGE_ID) {
	IMAGE_ID = iMAGE_ID;
}
public String getIMAGE_TITLE() {
	return IMAGE_TITLE;
}
public void setIMAGE_TITLE(String iMAGE_TITLE) {
	IMAGE_TITLE = iMAGE_TITLE;
}
public String getIMAGE_URL() {
	return IMAGE_URL;
}
public void setIMAGE_URL(String iMAGE_URL) {
	IMAGE_URL = iMAGE_URL;
}
public String getIMAGE_THUMB_URL() {
	return IMAGE_THUMB_URL;
}
public void setIMAGE_THUMB_URL(String iMAGE_THUMB_URL) {
	IMAGE_THUMB_URL = iMAGE_THUMB_URL;
}
public Timestamp getIMAGE_CREATE() {
	return IMAGE_CREATE;
}
public void setIMAGE_CREATE(Timestamp iMAGE_CREATE) {
	IMAGE_CREATE = iMAGE_CREATE;
}
public String getIMAGE_TAGS() {
	return IMAGE_TAGS;
}
public void setIMAGE_TAGS(String iMAGE_TAGS) {
	IMAGE_TAGS = iMAGE_TAGS;
}
public String IMAGE_TITLE;
public String IMAGE_URL;
public String IMAGE_THUMB_URL;
public Timestamp IMAGE_CREATE;
public String IMAGE_TAGS;
}
