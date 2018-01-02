package banip.support.util;

public class CustomDocument extends org.jsoup.nodes.Document {

	public CustomDocument(String baseUri) {
		super(baseUri);
		// TODO Auto-generated constructor stub
	}
	
	public String selectString(String selector) {
		return super.select(selector).first().text();
	}
	
	public int selectInt(String selector) {
		return Integer.parseInt(this.selectString(selector));
	}

}
