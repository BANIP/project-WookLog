package banip.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.simple.JSONObject;

import banip.query.SQLQuery;
/**
 * 비지니스 로직을 보다 효율적으로 수행하기 위해
 * 기존 자바빈에 DBMS용 update, delete같은 쿼리를 뽑아내기 위한 메서드를 추가한 추상 클래스.
 * 유지보수 비용을 줄이기 위해 reflect 클래스로 빈 내부의 필드와 값들을 뽑아내도록 구성됨
 * 
 *  @author : BANIP
 *  @version : 1.0
 */
public abstract class SQLBean {
	protected SQLQuery query;
	public SQLQuery getQuery(){
		return query;
	}
	
	public void printThisClassName() {
		System.out.println(this.getClass().getName());
	}
	public ArrayList<String> getFieldNameList(){
		ArrayList<String> rtn = new ArrayList<String>();
		Field[] fields = this.getClass().getDeclaredFields();
		for(int i = 0;i < fields.length;i++) {
			Field field = fields[i];
			rtn.add( field.getName() );
		}
		return rtn;
		
	}
	
	public ArrayList<String> getFieldNameList( Iterator<String> ignoreList ){
		ArrayList<String> rtn = this.getFieldNameList();
		while(ignoreList.hasNext()) {
			String ignoreName = ignoreList.next();
			if( rtn.contains(ignoreName) ) {
				rtn.remove(ignoreName);
			}
		}
		return rtn;
	}
	
	public String getFieldsString() {
		return String.join(", ", this.getFieldNameList());
	}
	
	public String getFieldsString( Iterator<String> ignoreList ) {
		return String.join(", ", this.getFieldNameList(ignoreList));
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSON( Iterator<String> ignoreList ) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		JSONObject json = new JSONObject();
		Iterator<String> listIter = getFieldNameList( ignoreList ).iterator();
		while(listIter.hasNext()) {
			String fieldName = listIter.next();
			json.put( fieldName.toLowerCase(), getFieldValue(fieldName) );
		 }
		return json;
	}
	
	private Object getFieldValue(String fieldName) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		// TODO Auto-generated method stub
		Field field = this.getClass().getDeclaredField(fieldName);
		String fieldType = field.getType().getName();
		switch(fieldType) {
			case "java.sql.Date" :
					return ( (Date) field.get(this) ).toLocaleString();
			default:
					return field.get(this);
		}

	}

	public JSONObject getJSON(  ) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		return getJSON( new ArrayList<String>().iterator() );
	}

	protected String toLocalString(Timestamp date) {
		// TODO Auto-generated method stub
		return date == null ? null : date.toLocaleString();
	}
	
}
