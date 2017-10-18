package banip.data;

import banip.bean.UserBean;
import banip.sql.UserDao;

public class User {
	protected String name;
	@SuppressWarnings("unused")
	private String PWD;
	
	public User() {}
	public User(String name) {
		this.name = name;
	}
	public User(String name,String pwd) {
		this.name = name;
		this.PWD = pwd;
	}
	
	public String getName() {
		return name;
	}
	public String getPWD() {
		return PWD;
	}
	
	/**
	 * 로그인 가능한 아이디와 비밀번호인지 체크
	 * @return
	 */
	public boolean isEffective() {
		if(isNull()) return false;
		UserDao userDao = new UserDao();
		boolean rtn = userDao.isEffectiveUser(this);
		userDao.close(true);
		return rtn;
	}
	
	/**
	 * 존재하는 아이디인지 체크
	 * @return
	 */
	public boolean isExist() {
		UserDao userDao = new UserDao();
		boolean isExist = userDao.isExistName(name);
		userDao.close(true);
		return isExist;
	}
		
	public boolean isNull() { return false;}

	public int getID() {
		// TODO Auto-generated method stub
		return this.getBean().getUSER_ID();
	}
	
	public UserBean getBean() {
		UserDao userDao = new UserDao();
		UserBean bean = userDao.getUserBean(this.name, -1);
		userDao.close(true);
		return bean;
	}
	
	/**
	 * 아이디와 비밀번호가 같으면 true 반환
	 * 아이디가 존재하지 않을 시 생성 후 true 반환
	 * 
	 * 아이디는 존재하나 비밀번호가 다를 시 false 반환
	 * @return
	 */
	public boolean login() {
		if(this.isEffective()) return true;
		return register();
	}
	
	/**
	 * 아이디 생성
	 */
	public boolean register() {
		UserDao userDao = new UserDao();
		boolean isExistName = userDao.isExistName(this.name);
		if(!isExistName) userDao.addUser(this);
		userDao.close(true);
		return !isExistName;
	}
}
