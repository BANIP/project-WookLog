package banip.data;

import banip.bean.BoardBean;
import banip.sql.BoardDao;

public class BoardID {
	int id;
	public BoardID() {}
	public BoardID(int id) {
		this.id = id;
	}

	public boolean isExist() {
		BoardDao boarddao = new BoardDao();
		BoardBean bean = boarddao.getBoardBean(id);
		boarddao.close(true);
		return bean != null;
	}
	
	public int getId() {
		return id;
	}
	
	public BoardBean getBean() {
		BoardDao dao = new BoardDao();
		BoardBean bean = dao.getBoardBean(id);
		dao.close(true);
		return bean;
	}

	public boolean isNull() {return false;}
}
