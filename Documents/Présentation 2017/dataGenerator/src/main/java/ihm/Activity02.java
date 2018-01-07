package ihm;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ensai.dataGenerator.App;

public class Activity02 extends Activity<String> {
	private static final long serialVersionUID = 1L;
	private GhostText[] fields;
	private JPanel jpanel;

	  // Create a form with the specified labels, tooltips, and sizes.
	  public Activity02() {
		  fields = new GhostText[1];
		jpanel = new JPanel(new BorderLayout());
		JPanel topPanel = new JPanel(new GridLayout(1, 2));
	    jpanel.add(topPanel, BorderLayout.CENTER);
	   
	    JLabel lab = new JLabel("Nom de votre application", JLabel.RIGHT);
	    fields[0] = new GhostText("Mon premier réseau social");
	    lab.setLabelFor(fields[0].getTextfield());
	    topPanel.add(lab);
	    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
	    p.add(fields[0].getTextfield());
	    topPanel.add(p);
	  }

	  public String getText(int i) {
	    return (fields[i].getTextfield().getText());
	  }
	  

	@Override
	public String run(Map<String, String> globalMap) {
		final Activity02 form = this;

		final JFrame frame = new JFrame("Setting 1/4");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add(form.jpanel, BorderLayout.NORTH);
	    JPanel panel = new JPanel();
	    JButton submit = new JButton("Next >");
	    submit.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  for (int i = 0; i < fields.length; i++) {
	    		  System.out.println("parameter "+i+" = "+form.getText(i));
			}
	    	  frame.setVisible(false);
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
        globalMap.put(App.KEY_APP_NAME, form.getText(0));
		return this.getClass().getName();
	}

	}

