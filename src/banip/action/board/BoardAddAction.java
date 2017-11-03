package banip.action.board;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;

import banip.sql.BoardDao;
import banip.action.ActionBoard;
import banip.util.BoardJSON;
import banip.bean.BoardBean;
import banip.data.StatusCode;
import banip.data.User;


/**
 * 모델과 프런트 컨트롤러 사이를 이어주는 게시글 작성 액션 객체
 * @author USER
 *
 */
public class BoardAddAction extends ActionBoard{
	/**
	 * 프로토콜 획득
	 */
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "POST";
	}

	/**
	 * 필수 파라미터 획득
	 */
	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> array = new ArrayList<String>();
		array.add("board_title");
		array.add("board_content");
		array.add("category_id");
		array.add("user_pwd");
		array.add("user_name");
		return array;
	}
	
	/**
	 * 아이디와 비밀번호가 올바른지 체크
	 */
	@Override
	protected boolean checkAuth(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getUser(request).login();
	}
	
	/**
	 * @exception 게시글 작성 권한을 가지고 있지 않음
	 * @exception 게시글을 등록하려는 카테고리가 존재하지 않음
	 *  @exception 컨텐츠 또는 타이틀이 빈칸임
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		if( cannotWriteable(request) ) return super.getStatusCode(StatusCode.STATUS_POWER,"게시글을 작성할 권한을 가지고 있지 않습니다.");
		if( super.isCategoryNull(request) ) return super.getStatusCode(StatusCode.STATUS_UNDEFINED,"게시글을 등록하려는 카테고리가 존재하지 않습니다.");
		if( isContentNull(request) ) return super.getStatusCode(StatusCode.STATUS_PARAM,"타이틀 혹은 내용이 빈칸입니다");
		return  super.getStatusCode(StatusCode.STATUS_SUCCESS);
	}
	
	private boolean isContentNull(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String boardTitle = super.getString(request, "board_title");
		String boardContent = super.getString(request, "board_content");
		return boardTitle.length() == 0 || boardContent.length() == 0;
	}


	/**
	 * 게시글 작성 권한이 있는지 체크
	 * @param bean
	 * @return 권한 존재시 true 반환
	 */
	private boolean cannotWriteable(HttpServletRequest request) {
		return !super.getUserBean(request).isUSER_PERMISSION_WRITE();
	}
	

	/**
	 * 쿼리 실행
	 */
	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub

		BoardDao dao = new BoardDao();
		User user = super.getUser(request);
		BoardBean bean = getBean(request);
		
		BoardBean WritedBean = dao.addBoard(bean, user);
		dao.close(true);
		return getResultJSON(WritedBean);
	}
	
	private BoardJSON getResultJSON(BoardBean bean) {
		// TODO Auto-generated method stub
		boolean isNotNull = bean != null;
		if(isNotNull) {
			return bean.getBoardJSON();
		} else {
			return new BoardJSON(StatusCode.STATUS_SERVER,"서버상의 오류로 글 작성에 실패했습니다!");
		}
	}

	/**
	 * 새로운 글을 쓰기 위한 bean 객체 획득
	 * @param request
	 * @return
	 */
	private BoardBean getBean(HttpServletRequest request) {
		BoardBean bean = new BoardBean();
		bean.setBOARD_TITLE( super.getString(request, "board_title") );
		bean.setBOARD_CONTENT( super.getString(request, "board_content") );
		bean.setBOARD_CATEGORY_ID( super.getInt(request, "category_id") );
		return bean;
	}


	
	
	


}
