package banip.sql;
import java.util.HashMap;
import java.util.Iterator;
public class SQLWhere {
	HashMap<String,String> terms = new HashMap<String,String>();
	public SQLWhere(){};
	public SQLWhere(String key,String value){
		addTerms(key, value);
	}
	

	
	@Override
	public String toString() {
		String str = "WHERE ";
		Iterator<String> keys = terms.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			String value = terms.get(key);
			str += String.format("%s = %s AND ",key ,value);
		}
		str += "0=0 ";
		return str;
	}
	
	public void addTerms(String key,String value){
		terms.put(key, value);
	}
}
