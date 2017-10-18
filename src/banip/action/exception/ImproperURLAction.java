package banip.action.exception;

import javax.servlet.http.HttpServletRequest;

import banip.action.ActionException;
import banip.data.StatusCode;
import banip.util.BoardJSON;

public class ImproperURLAction extends ActionException {
	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return new BoardJSON(StatusCode.STATUS_URL,"유효하지 않은 액션 클래스 URL입니다. 관리자에게 문의해주세요.");
	}
}
