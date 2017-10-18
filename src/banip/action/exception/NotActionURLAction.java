package banip.action.exception;

import javax.servlet.http.HttpServletRequest;

import banip.action.ActionException;
import banip.data.StatusCode;
import banip.util.BoardJSON;

public class NotActionURLAction extends ActionException {
	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return new BoardJSON(StatusCode.STATUS_URL,"올바르지 않은 액션 URL 주소입니다. 관리자에게 문의해주세요.");
	}
}
