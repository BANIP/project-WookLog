package banip.data;

import banip.bean.BoardBean;

public class BoardIDNull extends BoardID {
	public boolean isNull() {return true;}

	@Override
	public boolean isExist() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BoardBean getBean() {
		// TODO Auto-generated method stub
		return null;
	}
}
