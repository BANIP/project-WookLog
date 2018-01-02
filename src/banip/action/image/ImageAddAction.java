package banip.action.image;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import banip.action.Action;
import banip.bean.ImageBean;
import banip.bean.ImageTagBean;
import banip.dao.ImageDao;
import banip.data.StatusCode;
import banip.util.BoardJSON;

public class ImageAddAction extends Action {

	private ArrayList<ImageBean> _imageList = new ArrayList<ImageBean>();
	private ArrayList<ImageTagBean> _tagList = new ArrayList<ImageTagBean>();
	@Override
	protected String getProtocol() {
		// TODO Auto-generated method stub
		return "POST";
	}

	@Override
	protected ArrayList<String> getRequireParam() {
		// TODO Auto-generated method stub
		ArrayList<String> result = new ArrayList<String>();
		result.add("user_name");
		result.add("user_pwd");
		result.add("image_list");
		return result;
	}

	@Override
	protected boolean checkAuth(HttpServletRequest request) {
		// TODO Auto-generated method stub
		return super.getUser(request).isEffective();
	}
	
	/**
	 * @exception 게시글 작성 권한을 가지고 있지 않음
	 * @exception 썸네일이랑 이미지파일이 이미지가 맞는지 체크
	 */
	@Override
	protected StatusCode checkOtherError(HttpServletRequest request) {
		// TODO Auto-generated method stub
		try {
			JSONArray listJSON = getImageList(request);
			_imageList = getBeanList(listJSON);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			if( IsNotImage(_imageList) )  return super.getStatusCode(StatusCode.STATUS_PARAM,"이미지 리스트 파라미터의 형식이 이상해요.");
		}
		
		if( IscannotWriteable(request) ) return super.getStatusCode(StatusCode.STATUS_CERTIFY,"이미지를 업로드하기 위한 권한이 부족해요.");
		if( IsNotImage(_imageList) )  return super.getStatusCode(StatusCode.STATUS_PARAM,"이미지의 형식이 올바르지 못해요.");
		return super.getStatusCode(StatusCode.STATUS_SUCCESS);
	}
	
	
	
	private ArrayList<ImageBean> getBeanList(JSONArray listJSON) {
		// TODO Auto-generated method stub
		ArrayList<ImageBean> imageList = new ArrayList<ImageBean>();
		Iterator<JSONObject> jsonIter = listJSON.iterator();
		while(jsonIter.hasNext()) {
			JSONObject imageJSON = jsonIter.next();
			ImageBean imageBean = new ImageBean();
			imageBean.setIMAGE_URL( (String) imageJSON.get("image_url") );
			imageBean.setIMAGE_TITLE( (String) imageJSON.get("image_title") );
			imageBean.setIMAGE_THUMB_URL( (String) imageJSON.get("image_thumb_url") );
			imageList.add(imageBean);
			addTags( (JSONArray) imageJSON.get("image_tags"), imageBean.getIMAGE_TITLE());
		}

		return imageList;
	}
	
	private void addTags(JSONArray tags, String title) {
		// TODO Auto-generated method stub
		Iterator<String> tagIter = tags.iterator();
		while (tagIter.hasNext()) {
			String tag = tagIter.next();
			ImageTagBean bean = new ImageTagBean();
			bean.setTAG_IMAGE_TITLE( title );
			bean.setTAG_NAME( tag );
			_tagList.add( bean );
		}
	}

	private JSONArray getImageList(HttpServletRequest request) throws ParseException {
		// TODO Auto-generated method stub
		return (JSONArray) new JSONParser().parse(super.getString(request, "image_list") );
	}
	
	private boolean IsNotImage(ArrayList<ImageBean> imageList) {
		// TODO Auto-generated method stub
		Iterator<ImageBean> imageIter = imageList.iterator();
		while (imageIter.hasNext()) {
			ImageBean imageBean = imageIter.next();
			if( isNotImage(imageBean) ) return true;
		}
		return false;
	}
	
	private boolean isNotImage(ImageBean imageBean) {
		// TODO Auto-generated method stub
		String url = imageBean.getIMAGE_THUMB_URL();
		String urlThumb = imageBean.getIMAGE_URL();
		boolean isImage = isImage(url) && isImage(urlThumb);
		return !isImage;
	}

	private boolean isImage(String url) {
		// TODO Auto-generated method stub
		try {
			URL imageLink = new URL(url);
			return ImageIO.read(imageLink) != null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
		
	}

	/**
	 * 게시글 작성 권한이 있는지 체크
	 * @param bean
	 * @return 권한 존재시 true 반환
	 */
	private boolean IscannotWriteable(HttpServletRequest request) {
		return !super.getUserBean(request).isUSER_PERMISSION_WRITE();
	}
	
	@Override
	protected BoardJSON executeMain(HttpServletRequest request) {
		// TODO Auto-generated method stub
		ImageDao dao = new ImageDao();
		ImageBean existImage = dao.isExistImage(_imageList);
		if(existImage != null) return getFailJSON(existImage);
		
		boolean imageResult = dao.addImage(_imageList);
		boolean tagResult = dao.addTag(_tagList);
		
		return getResultJSON(imageResult,tagResult);
	}

	private BoardJSON getFailJSON(ImageBean existImage) {
		// TODO Auto-generated method stub
		String title = existImage.getIMAGE_TITLE();
		return new BoardJSON(StatusCode.STATUS_PARAM,title + "은 이미 존재하는 타이틀이에요. 다른걸로 바꿔주세요");
	}

	private BoardJSON getResultJSON(boolean imageResult, boolean tagResult) {
		// TODO Auto-generated method stub
		if(imageResult == false) return new BoardJSON(StatusCode.STATUS_SERVER,"이미지 정보를 서버에 추가하려던 중 원인불명의 오류가 발생했어요.");
		if(tagResult == false) return new BoardJSON(StatusCode.STATUS_SERVER,"태그 정보를 서버에 추가하려던 중 원인불명의 오류가 발생했어요.");
		return new BoardJSON(StatusCode.STATUS_SUCCESS);
	}

}
