package banip.storage;

import javax.servlet.http.HttpServletRequest;

import banip.servlet.BoardFrontController;

public class ApplicationAttribute {
	public static void set(String name, Object value) {
		HttpServletRequest request = BoardFrontController._request;
		request.getServletContext().setAttribute(name, value);
	}
	
	public static Object get(String name) {
		HttpServletRequest request = BoardFrontController._request;
		return request.getServletContext().getAttribute(name);
	}
}
