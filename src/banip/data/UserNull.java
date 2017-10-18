package banip.data;

public class UserNull extends User{
	public UserNull() {}
	
	public UserNull(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isEffective() {
		return false;
	}

	public boolean isNull() { return true;}
}
