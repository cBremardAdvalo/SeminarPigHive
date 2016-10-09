package ihm;

import java.util.Map;

import javax.swing.JPanel;

public abstract class Activity<T>  extends JPanel {
	private static final long serialVersionUID = 1L;

	
	protected void sleep(long millis) {
		try {Thread.sleep(millis);} catch (InterruptedException e) {}
	}
	
	public T launch(Map<String, String> globalMap){
		System.out.println(this.getClass().getName()+" start");
		T output = run(globalMap);
		System.out.println(this.getClass().getName()+" end");
		return output;
	}

	abstract protected T run(Map<String, String> globalMap);
	



}
