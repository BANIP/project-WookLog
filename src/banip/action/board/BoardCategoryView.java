package banip.action.board;
import java.util.Iterator;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import org.json.simple.*;

import banip.sql.BoardDao;
import banip.action.ActionBoard;
import banip.util.*;
import banip.bean.CategoryBean;
import banip.data.StatusCode;

public class BoardCategoryView extends ActionBoard{
	/**
	 * 프로토콜 획득
	 */
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "GET";
	}
	
	/**
	 * 필수 파라미터 획득
	 */
	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> array = new ArrayList<String>();
		array.add("category_id");
		return array;
	}

	/**
	 * category_id에 해당하는 카테고리가 존재하지 않을 때
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return getErrorResultStatus( isCategoryNull(request) );
	}
	
	
	private StatusCode getErrorResultStatus(boolean isCategoryNull) {
		// TODO Auto-generated method stub
		if(isCategoryNull) {
			return super.getStatusCode(StatusCode.STATUS_UNDEFINED,"해당 카테고리는 존재하지 않습니다.");
		} else {
			return super.getStatusCode(StatusCode.STATUS_SUCCESS);
		}
	}

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		BoardJSON boardJSON = new BoardJSON();
		BoardDao dao = new BoardDao();
		int categoryID = super.getInt(request, "category_id");
		CategoryTree tree = dao.getCategoryTree(categoryID);
		JSONObject treeJSON = getCategoryTreeJSON(tree);
		boardJSON.putData("info", treeJSON.get("info") );
		boardJSON.putData("child", treeJSON.get("child") );		
		
		dao.close(true);
		return boardJSON;
	}
	
	@SuppressWarnings({ "unchecked" })
	private JSONObject getCategoryJSON(CategoryBean bean) {
		return bean.getJSON();
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject getCategoryTreeJSON(CategoryTree tree){
		JSONObject JSON = new JSONObject();
		JSONArray childJSONs = new JSONArray();
		JSONObject infoJSON = getCategoryJSON( tree.getBean() );
		
		Iterator<CategoryTree> childTrees = tree.getChilds().iterator();
		while(childTrees.hasNext()){
			JSONObject childJSON = getCategoryTreeJSON( childTrees.next() );
			childJSONs.add(childJSON);
		}
		
		JSON.put("info", infoJSON);
		JSON.put("child", childJSONs);
		
		return JSON;
	}



}
