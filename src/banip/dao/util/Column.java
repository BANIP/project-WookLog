package banip.dao.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;

import banip.bean.SQLBean;

public class Column {
	Class<?> type;
	String name;

	public Column() {}
	
	public Column(Class<?> type, String beanName ) {
		setType(type);
		setBeanName(beanName);
	}
	
	public void setType(Class<?> type) {
		this.type = type;
	}
	public void setBeanName(String value) {
		this.name = value.toUpperCase();
	}
	
	public Object getBeanValue(SQLBean bean) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(type == null || name == null) throw new NullPointerException("Column 인스턴스의 초기값이 지정되지 않았습니다. 지정해주세요");
		Method method = null;
		try {
			method = bean.getClass().getDeclaredMethod("get" + name);
		} catch(NoSuchMethodException e) {

			System.out.println("Column클래스에서 reflect를 이용해 값을 추출하던중 예외 발생. 유효하지 않은 메서드입니다");
			System.out.println("빈 =>"+bean.getClass().getName());
			System.out.println("타입 =>"+type.getName());
			System.out.println("컬럼명 =>"+name);
		}

		
		return method.invoke(bean);
	}
	
	public Method getPstmtSetMethod(PreparedStatement pstmt) throws NoSuchMethodException, SecurityException {
		String typeName = type.getName().replaceAll("^.*\\.","");
		typeName = typeName.substring(0,1).toUpperCase() + typeName.substring(1);
		Method setMethod = null;
		try {
			setMethod = pstmt.getClass().getMethod("set" + typeName, int.class,type);
		} catch(NoSuchMethodException e) {
			System.out.println("Column클래스에서 reflect를 이용해 메서드을 추출하던중 예외 발생. 유효하지 않은 메서드입니다");
			System.out.println("호출할 메서드명 =>"+"set" + typeName);
			System.out.println("타입 =>"+type.getName());
			System.out.println("컬럼명 =>"+name);
		}
		return setMethod;
	}
}
