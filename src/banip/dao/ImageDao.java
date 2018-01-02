package banip.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import banip.bean.*;
import banip.bean.support.BeanList;
import banip.dao.query.*;
import banip.dao.util.PstmtPackage;

public class ImageDao extends SQLDao{	
	ImageQuery _nullQuery = new ImageQuery(null);
	
	public boolean updateQuery(ArrayList<? extends SQLBean> list,PstmtPackage pstmtPack) {
		boolean result = false;
		try {
			pstmtPack.setConnection(conn);
			result = pstmtPack.update(list);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			super.printException(e, "pstmt query");
		} 
		
		pstmtPack.close();
		return result;
	}
	
	public boolean addImage(ImageBean bean) {
		return  addImage( (ArrayList<ImageBean>) Arrays.asList(bean) );
	}
	
	public boolean addImage( ArrayList<ImageBean> list) {
		PstmtPackage pstmtPack = new ImageQuery(null).getAddImageQuery();
		return updateQuery(list,pstmtPack);
	}
	
	public boolean addTag(ImageTagBean bean) {
		return  addTag( (ArrayList<ImageTagBean>) Arrays.asList(bean) );
	}
	
	public boolean addTag(ArrayList<ImageTagBean> list ) {
		PstmtPackage pstmtPack = new ImageQuery(null).getAddTagQuery();
		return updateQuery(list,pstmtPack);
	}

	public ImageBean isExistImage(ArrayList<ImageBean> _imageList) {
		// TODO Auto-generated method stub
		Iterator<ImageBean> ImageIter = _imageList.iterator();
		while (ImageIter.hasNext()) {
			ImageBean bean = (ImageBean) ImageIter.next();
			if(this.isExistImage(bean)) return bean;
		}
		return null;
	}

	private boolean isExistImage(ImageBean bean) {
		// TODO Auto-generated method stub
		String query = _nullQuery.getIsExistQuery();
		try {
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			if(rs.next()) return rs.getInt(1) > 0 ? true : false;
		} catch (Exception e) {
			// TODO: handle exception
			super.printException(e, query);
		} finally {
			super.close(false);
		}
		return true;
	}

	public BeanList<ImageBean> searchImages(String searchWord,boolean isSearchByTag) {
		// TODO Auto-generated method stub
		String query;
		if(isSearchByTag) query = _nullQuery.getImagesByTagQuery(searchWord);
		 else query = _nullQuery.getImageSearchQuery(searchWord);
		
		
		try {
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			return new BeanList<ImageBean>(rs,ImageBean.class);

		} catch (Exception e) {
			// TODO: handle exception
			super.printException(e, query);
			
		}
		super.close(false);
		return null;
	}
	
	public BeanList<ImageTagBean> searchTags(String searchWord) {
		String query = _nullQuery.getTagSearchQuery(searchWord);
		try {
			rs = pstmt.executeQuery();
			return new BeanList<ImageTagBean>(rs,ImageTagBean.class);
		} catch (Exception e) {
			// TODO: handle exception
			super.printException(e, query);
			
		}
		super.close(false);
		return null;
	}

	

}
