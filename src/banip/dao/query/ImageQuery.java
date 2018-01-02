package banip.dao.query;
import banip.bean.ImageBean;
import banip.dao.util.PstmtPackage;

public class ImageQuery extends SQLQuery{
	protected ImageBean bean;
	public ImageQuery(ImageBean bean){
		if(isNull(bean)) this.bean = new ImageBean();
		else this.bean = bean;
	}
	
	public PstmtPackage getAddTagQuery() {
		String query = "INSERT INTO image_tag(TAG_IMAGE_ID, TAG_NAME ) VALUES( IMAGE_GETIMAGEID(?), ?);";
		PstmtPackage pack = new PstmtPackage(query);
		pack.addColumn(String.class,"TAG_IMAGE_TITLE")
			   .addColumn(String.class,"TAG_NAME");
		
		return pack;
	}
	
	public PstmtPackage getAddImageQuery() {
		String query = "INSERT INTO image_main(IMAGE_TITLE, IMAGE_URL, IMAGE_THUMB_URL) VALUES(?,?,?);";
		PstmtPackage pack = new PstmtPackage(query);
		pack.addColumn(String.class,"IMAGE_TITLE")
			   .addColumn(String.class,"IMAGE_URL")
			   .addColumn(String.class,"IMAGE_THUMB_URL");
		
		return pack;
	}

	public String getIsExistQuery() {
		// TODO Auto-generated method stub
		String query = String.format("SELECT COUNT(*) FROM IMAGE_MAIN WHERE IMAGE_TITLE = '%s';", bean.getIMAGE_TITLE() );
		return query;
	}
	
	
	public String getImagesByTagQuery(String searchWord) {
		// TODO Auto-generated method stub
		String tag = searchWord.substring(1);
		String query = String.format("CALL IMAGE_VIEW_BY_TAG('%s');",tag);
		return query;
	}
	
	public String getImageSearchQuery(String searchWord) {
		// TODO Auto-generated method stub
		String query = String.format("CALL IMAGE_VIEW_SEARCH('%s')",searchWord);
		return query;
	}

	public String getTagSearchQuery(String searchWord) {
		// TODO Auto-generated method stub
		String query = String.format("CALL image_tag_search('%s')", searchWord);
		return query;
	}
	
}
