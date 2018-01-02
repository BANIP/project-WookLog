package banip.support;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletContext;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import banip.action.Action;
import banip.servlet.BoardFrontController;
import banip.storage.ApplicationAttribute;
import banip.support.util.CustomDocument;

public class SettingBean {
	

	private int BOARD_LIMIT;
	private String DB_USERNAME;
	private String DB_PASSWORD;
	private String DB_PATH;
	private String DB_DATABASE;
	
	public int getBOARD_LIMIT() {
		return BOARD_LIMIT;
	}

	private void setBOARD_LIMIT(int bOARD_LIMIT) {
		BOARD_LIMIT = bOARD_LIMIT;
	}

	public String getDB_USERNAME() {
		return DB_USERNAME;
	}

	private void setDB_USERNAME(String dB_USERNAME) {
		DB_USERNAME = dB_USERNAME;
	}

	public String getDB_PASSWORD() {
		return DB_PASSWORD;
	}

	private void setDB_PASSWORD(String dB_PASSWORD) {
		DB_PASSWORD = dB_PASSWORD;
	}

	public String getDB_PATH() {
		return DB_PATH;
	}

	private void setDB_PATH(String dB_PATH) {
		DB_PATH = dB_PATH;
	}

	public String getDB_DATABASE() {
		return DB_DATABASE;
	}

	private void setDB_DATABASE(String dB_DATABASE) {
		DB_DATABASE = dB_DATABASE;
	}

		

	
	private static final String LINK_XML = "/setting.xml";
	private static SettingBean _bean;
	
	public static SettingBean getInstance() throws IOException {
		//SettingBean globalBean = (SettingBean)ApplicationAttribute.get("settingBean");
		//if(globalBean != null) _bean = globalBean;
		if(_bean == null ) _bean = new SettingBean();
		return _bean;
	}
	
	private SettingBean() throws IOException {
		InputStream xmlInput = this.getClass().getResourceAsStream(LINK_XML);
		String xmlString = FileUtil.read(xmlInput);
		setAllBean(xmlString);
		//ApplicationAttribute.set("settingBean", this);
	}
	
	private void setAllBean(String xmlString) {

		Document doc =Jsoup.parse(xmlString);
		
		this.setBOARD_LIMIT( getDocumentInt( doc, "board limit") );
		this.setDB_USERNAME( getDocumentString( doc,"db username") );
		this.setDB_PASSWORD( getDocumentString( doc,"db password") );
		this.setDB_DATABASE( getDocumentString( doc,"db database") );
		this.setDB_PATH(getDocumentString( doc,"db path") );
	}
	
	private String getDocumentString(Document doc,String selector) {
		return doc.select(selector).get(0).text();
	}
	
	private int getDocumentInt(Document doc,String selector) {
		return Integer.parseInt(getDocumentString(doc,selector) );
	}
	
}
