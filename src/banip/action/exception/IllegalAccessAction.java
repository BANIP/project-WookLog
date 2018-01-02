package banip.action.exception;

import javax.servlet.http.HttpServletRequest;
import banip.action.ActionException;
import banip.data.StatusCode;
import banip.util.BoardJSON;

public class IllegalAccessAction extends ActionException {

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return new BoardJSON(StatusCode.STATUS_URL,"서버단의 액션 클래스의 접근제한자가 잘못되었습니다. 관리자에게 문의해주세요.");
	}

}
