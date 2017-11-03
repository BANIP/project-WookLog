package banip.data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class SQLField {
	String mysqlDataType;
	Object value;
	public SQLField(String mysqlDataType,Object value) {
		this.mysqlDataType = mysqlDataType;
		this.value = value;
	}
	
	public String getJavaDataType() {
		String javaType = "null";
		switch(mysqlDataType) {
			case "TINYINT":
				javaType = "java.lang.Boolean";
			break;
			case "BIGINT" : case "INT UNSIGNED" : case "BIGINT UNSIGNED" : case "INT" :
				javaType = "int";				
			break;
			case "DECIMAL": case "BIGDECIMAL":
				javaType = "java.math.BigDecimal";	
			break;
			case "TIMESTAMP":
				javaType = "java.sql.Timestamp";
			break;
			case "Date":
				javaType = "java.sql.Date";
			break;
			case "VARCHAR": 
				javaType = "java.lang.String";
			break;
			default :
				javaType = "null";
			break;
			
		}
		
		return javaType;
	}
	
	public Object getValue() {
		String type = getJavaDataType();
		if(type == "java.lang.Boolean") return (Boolean) value;
		if(type == "int") return ((Long) value).intValue();
		if(type == "java.math.BigDecimal") {
			int outputValue = ((BigDecimal) value).intValue();
			return outputValue;
		}
		if(type == "java.sql.Timestamp") return (Timestamp) value;
		if(type == "java.sql.Date") return (Date) value;
		if(type == "java.lang.String") return (String) value;
		return value;
	}

	public boolean isNull() {
		// TODO Auto-generated method stub
		return value == null;
	}
	
}
