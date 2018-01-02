package banip.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import banip.exception.IsNotActionURLException;

public class StringUtil {
	public static String removeLast(String str,String remove){
		String result = str.substring(0,str.length() - remove.length());
		String subed = str.substring(str.length() - remove.length());
		return subed.equals(remove) ? result : str;
	}
	
	public String getActionClassName(String uri) throws IsNotActionURLException {
		Pattern p = Pattern.compile("\\/([a-zA-Z_]+)\\.do");
		Matcher m = p.matcher(uri);
		if(m.find()) {
			String classShortName =  m.group(1);
			return getClassFullName(classShortName);
		} else {
			throw new IsNotActionURLException();
		}
	}

	private String getClassFullName(String classShortName) throws IsNotActionURLException {
		// TODO Auto-generated method stub
		String prefix = getClassPrefix(classShortName);
		return String.format("banip.action.%s.%s",prefix,classShortName);
	}

	private String getClassPrefix(String classShortName) throws IsNotActionURLException  {
		// TODO Auto-generated method stub
		Pattern p = Pattern.compile("^([A-Z][a-z]+)");
		Matcher m = p.matcher(classShortName);
		if(m.find()) {
			return m.group(1).toLowerCase();
		} else {
			throw new IsNotActionURLException();
		}
	}
	

}
