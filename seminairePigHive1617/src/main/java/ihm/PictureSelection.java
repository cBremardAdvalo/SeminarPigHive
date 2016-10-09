package ihm;

public class PictureSelection {
	public String path;
	public String name;
	public PictureSelection(String path, String name) {
		super();
		this.path = path;
		this.name = name;
	}
	@Override
	public String toString() {
		return path;
	}
	
	
}
