package banip.query;
import banip.bean.CategoryBean;

public class CategoryQuery extends SQLQuery{
	protected CategoryBean bean;
	public CategoryQuery(CategoryBean bean){
		this.bean = bean;
	}
	
	/**
	 * 카테고리 빈 획득<br>
	 * @사용테이블 <ul>
		 * 	<li>SELECT BOARD_CATEGORY_VIEW</li>
		 * </ul>
	 * @쿼리결과값<ol>
		 * <li>int CATEGORY_ID 카테고리의 기본키</li> 
		 * <li>String CATEGORY_NAME 카테고리의 이름</li> 
		 * <li>int CATEGORY_PREV_ID 부모 카테고리의 기본키</li> 
		 * <li>String CATEGORY_PREV_NAME 부모 카테고리의 이름</li> 
		 * <li>int CATEGORY_BOARD_COUNT 게시글의 갯수</li>
	 * </ol>
	 * @param categoryID 획득할 데이터의 카테고리 아이디
	 * @return 쿼리 
	 */
	public String getCategoryQuery(int categoryID){
		String query = "SELECT "
				+ "CATEGORY_ID, CATEGORY_NAME, CATEGORY_PREV_ID, CATEGORY_PREV_NAME, CATEGORY_BOARD_COUNT, CATEGORY_LIKE, CATEGORY_HIT, CATEGORY_UPDATE_DATE "
				+ "FROM BOARD_CATEGORY_VIEW ";
		if(categoryID >= 0) query += String.format("WHERE CATEGORY_ID = %d;", categoryID);
		
		return query;
	}
	
	/**
	 * 자식 카테고리를 획득하는 쿼리를 반환
	 * @사용테이블 <ul>
		 * 	<li>SELECT BOARD_CATEGORY_VIEW</li>
		 * </ul>
	 * @쿼리결과값<ol>
		 * <li>int CATEGORY_ID 카테고리의 기본키</li> 
		 * <li>String CATEGORY_NAME 카테고리의 이름</li> 
		 * <li>int CATEGORY_PREV_ID 부모 카테고리의 기본키</li> 
		 * <li>String CATEGORY_PREV_NAME 부모 카테고리의 이름</li> 
		 * <li>int CATEGORY_BOARD_COUNT 게시글의 갯수</li>
	 * </ol>
	 * @param parentCategoryID 획득할 데이터의 부모 카테고리 아이디
	 * @return 쿼리
	 */
	public String getChildCategoryQuery(int parentCategoryID){
		String query = "SELECT "
				+ "CATEGORY_ID, CATEGORY_NAME, CATEGORY_PREV_ID, CATEGORY_PREV_NAME, CATEGORY_BOARD_COUNT, CATEGORY_LIKE, CATEGORY_HIT, CATEGORY_UPDATE_DATE "
				+ "FROM BOARD_CATEGORY_VIEW "
				+ String.format("WHERE CATEGORY_PREV_ID = %d;", parentCategoryID);
		return query;
	}

	/**
	 * 카테고리의 id로 이름을 얻어오는 쿼리를 반환
	 * @사용테이블 <ul>
		 * 	<li>SELECT BOARD_CATEGORY</li>
		 * </ul>
	 * @쿼리결과값<ol>
		 * <li>String CATEGORY_NAME 카테고리의 이름</li> 
	 * </ol>
	 * @param categoryID 획득할 데이터의 카테고리 아이디
	 * @return 쿼리
	 */
	public String getCategoryNameQuery(int categoryID){
		String query = "SELECT CATEGORY_NAME FROM BOARD_CATEGORY "
				+ String.format("WHERE CATEGORY_ID = %d", categoryID);
		
		return query;
	}
	
}
