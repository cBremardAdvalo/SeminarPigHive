package metier;

import java.util.ArrayList;
import java.util.Collection;

public class Users extends ArrayList<User> {
	private static final long serialVersionUID = 1L;
	public Users() {
		super();
	}
	public Users(int initialCapacity) {
		super(initialCapacity);
	}
	public Users(Collection<? extends User> c) {
		super(c);
	}
	

}
