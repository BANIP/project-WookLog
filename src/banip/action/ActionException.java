package banip.action;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import banip.util.BoardJSON;

public abstract class ActionException extends Action {
	
	
	@Override
	public boolean execute(HttpServletRequest request, HttpServletResponse response){
		// TODO Auto-generated method stub
		//로직 실행
		BoardJSON boardJSON = executeMain(request);
		return new createJSONBuilder(request).setBoardJSON(boardJSON).setStatusCode(super.getNullStausCode()).execute();
	};
	
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		return null;
	}
}

