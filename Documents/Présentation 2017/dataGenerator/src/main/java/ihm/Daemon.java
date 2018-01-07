package ihm;

import javax.swing.SwingUtilities;

public class Daemon {
    public static void main( String[] args )
    {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 0; i < 10; i++) {
					System.out.println("I'm a deamon");
					try {Thread.sleep(1000);} catch (InterruptedException e) {}
				}
            }
        });
	  }
}
