package banip.action.image;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import banip.action.Action;
import banip.bean.ImageBean;
import banip.bean.ImageTagBean;
import banip.bean.support.BeanList;
import banip.dao.ImageDao;
import banip.data.StatusCode;
import banip.util.BoardJSON;

public class ImageLoadAction extends Action{
	/*
	 *  받는값 : search_word : 검색어, #검색어
	 *  검색어
	 *  LIKES로 타이틀 이미지 검색 LIKES로 태그 검색
	 *  
	 *  #검색어
	 *  일반 WHERE로 이미지 검색
	 */
	
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "GET";
	}

	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> list = new ArrayList<String>();
		list.add("search_word");
		return list;
	}

	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		String searchWord = super.getString(request, "search_word");
		boolean isTagSearch = isTagSearch(searchWord);
		BoardJSON boardJSON = new BoardJSON(StatusCode.STATUS_SUCCESS);
		
		ImageDao imageDao = new ImageDao();
		BeanList<ImageBean> imageList = imageDao.searchImages(searchWord, isTagSearch);
		boardJSON.putData("images", imageList.getJSONArray());
		
		BeanList<ImageTagBean> tagList = imageDao.searchTags(searchWord);
		boardJSON.putData("tags", tagList.getJSONArray());
		
		imageDao.close(true);
		
		return boardJSON;
	}

	private boolean isTagSearch(String word) {
		return word.substring(0,1).equals("#");
	}

	
	
}
