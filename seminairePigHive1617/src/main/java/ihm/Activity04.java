package ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ensai.seminairePigHive1617.App;

public class Activity04 extends Activity<String> {
	private static final long serialVersionUID = 1L;
	private GhostText[] fields;
	private JPanel jpanel;

	  // Create a form with the specified labels, tooltips, and sizes.
	  public Activity04() {
		String[] labels = new String[]{ "Prix mensuel (en euros)", "Prix de l'envoi d'un Kiss (en euros)", "Popularité", "Rétention" };
		String[] defaults = new String[]{ "10", "0.50", "Nombre d'inscription par jour", "Durée de vie d'un utilisateur en mois" };
		int[] widths = new int[]{ 5, 5, 20, 20 };
	    
		jpanel = new JPanel(new BorderLayout());
	    JPanel labelPanel = new JPanel(new GridLayout(labels.length, 1));
	    JPanel fieldPanel = new JPanel(new GridLayout(labels.length, 1));
	    jpanel.add(labelPanel, BorderLayout.WEST);
	    jpanel.add(fieldPanel, BorderLayout.CENTER);
	    fields = new GhostText[labels.length];

	    for (int i = 0; i < labels.length; i += 1) {
	      fields[i] = new GhostText(defaults[i]);
	      if (i < widths.length)
	        fields[i].setColumns(widths[i]);

	      JLabel lab = new JLabel(labels[i], JLabel.RIGHT);
	      lab.setLabelFor(fields[i].getTextfield());


	      labelPanel.add(lab);
	      JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
	      p.add(fields[i].getTextfield());
	      fieldPanel.add(p);
	    }
	  }

	  public String getText(int i) {
	    return (fields[i].getTextfield().getText());
	  }
	  

	@Override
	public String run(Map<String, String> globalMap) {
		final Activity04 form = this;
	    final JFrame frame = new JFrame("Setting 3/4");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add(form.jpanel, BorderLayout.NORTH);
	    JPanel panel = new JPanel();
	    JButton submit = new JButton("Next >");
	    submit.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	    	  boolean isOk=true;
	    	  for (int i = 0; i < fields.length; i++) {
	    		  try {
					Double.parseDouble(fields[i].getTextfield().getText());
					fields[i].getTextfield().setBackground(Color.WHITE);
				} catch (Exception e2) {
					isOk=false;
					fields[i].getTextfield().setBackground(Color.RED);
				}
			}
	    	  frame.setVisible(!isOk);
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
        globalMap.put(App.KEY_MONTH_PRICE, fields[0].getTextfield().getText());
        globalMap.put(App.KEY_KISS_PRICE, fields[1].getTextfield().getText());
        globalMap.put(App.KEY_POPULARITY, fields[2].getTextfield().getText());
        globalMap.put(App.KEY_CHURN, fields[3].getTextfield().getText());
		return this.getClass().getName();
	}

	}

