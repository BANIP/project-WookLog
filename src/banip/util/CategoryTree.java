package banip.util;


import java.util.ArrayList;
import java.util.Iterator;
import banip.bean.CategoryBean;

import org.json.simple.*;

/**
 * 카테고리를 트리구조로 관리하기위한 객체
 * @author BANIP
 *
 */
public class CategoryTree{
	/**
	 * 트리의 부모가되는 객체
	 */
	private CategoryTree parents;
	/**
	 * 트리의 빈
	 */
	private CategoryBean bean;
	/**
	 * 자식들 리스트
	 */
	private ArrayList<CategoryTree> childs;
	
	/**
	 * 
	 * @param parents 부모객체
	 * @param bean 현재 트리 노드의 빈
	 * @param childs 자식객체 리스트, 없어도 되며 addChild메서드로 이후에 추가 가능
	 */
	public CategoryTree(CategoryTree parents,CategoryBean bean,ArrayList<CategoryTree> childs){
		this.parents = parents;
	    this.bean = bean;
	    this.childs = childs;
	}

	public CategoryTree(CategoryTree parents,CategoryBean bean){
		this.parents = parents;
	    this.bean = bean;
	    initChild();
	}
	
	public CategoryTree(CategoryBean bean){
	    this.bean = bean;
	    initChild();
	}
	
	public void initChild() {
		this.childs = new ArrayList<CategoryTree>();
	}
	public CategoryTree getParents() {
		return parents;
	}

	public void setParents(CategoryTree parents) {
		this.parents = parents;
	}

	public CategoryBean getBean() {
		return bean;
	}

	public void setBean(CategoryBean bean) {
		this.bean = bean;
	}

	public ArrayList<CategoryTree> getChilds() {
		return childs;
	}

	public void setChilds(ArrayList<CategoryTree> childs) {
		this.childs = childs;
	}
	
	public boolean hasChild(){
		return childs.size() >= 1 ? true : false;
	}
	
	public CategoryTree addChild(CategoryTree node){
		childs.add(node);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject getJSON(){
		JSONObject rtnJSON = new JSONObject();
		rtnJSON.put("category_id", bean.getCATEGORY_ID() );
		rtnJSON.put("category_name", bean.getCATEGORY_NAME() );
		rtnJSON.put("category_prev_id", bean.getCATEGORY_PREV_ID() );
		rtnJSON.put("category_prev_name", bean.getCATEGORY_PREV_NAME() );
		rtnJSON.put("category_board_count", bean.getCATEGORY_BOARD_COUNT() );
		JSONArray childJSON = new JSONArray();
		Iterator<CategoryTree> childsiter=  childs.iterator();
		while(childsiter.hasNext()){
			CategoryTree child = childsiter.next();
			childJSON.add(child.getJSON());
		}
		rtnJSON.put("childs", bean.getCATEGORY_NAME());
		return rtnJSON;
	}

}