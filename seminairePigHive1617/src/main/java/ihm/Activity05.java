package ihm;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ensai.seminairePigHive1617.App;

public class Activity05 extends Activity<String> {
	private static final long serialVersionUID = 1L;
	private JPanel jpanel;
	private JFileChooser fc;
	  // Create a form with the specified labels, tooltips, and sizes.
	public Activity05() {
		jpanel = new JPanel(new BorderLayout());
        JLabel lab = new JLabel("Veuillez choisir une image de fond", JLabel.CENTER);
        jpanel.add(lab , BorderLayout.CENTER);
		fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.setControlButtonsAreShown(false);
		jpanel.add(fc);
	}

	 
	@Override
	public String run(Map<String, String> globalMap) {
		final Activity05 form = this;
	    final JFrame frame = new JFrame("Setting 4/4");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add(form.jpanel, BorderLayout.NORTH);
	    JPanel panel = new JPanel();
	    JButton submit = new JButton("Finish");
	    submit.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  frame.setVisible(fc.getSelectedFile()==null);
	      }
	    });
	    panel.add(submit);
	    frame.getContentPane().add(panel, BorderLayout.SOUTH);
	    frame.pack();
        frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
        while(frame.isVisible()){
        	sleep(10);
        }
        globalMap.put(App.KEY_STAGING_PATH,fc.getSelectedFile().getPath());
		return this.getClass().getName();
	}

	}

