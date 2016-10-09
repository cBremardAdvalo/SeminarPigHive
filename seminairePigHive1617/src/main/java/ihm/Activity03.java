package ihm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.ensai.seminairePigHive1617.App;

public class Activity03 extends Activity<String> {
	private static final long serialVersionUID = 1L;
	private JPanel jpanel;
    private JList<PictureSelection> list;

	  // Create a form with the specified labels, tooltips, and sizes.
	  public Activity03() {
		  	jpanel = new JPanel(new GridLayout(1,2));
	        PictureSelection[] choices = new PictureSelection[3];
	        choices[0] = new PictureSelection("tchat1.png","tchat1");
	        choices[1] = new PictureSelection("tchat2.png","tchat2");
	        choices[2] = new PictureSelection("tchat3.png","tchat3");
			list =  new JList<PictureSelection>(choices);
	        list.setCellRenderer(new PictureSelectionRender());
	        list.setFixedCellHeight(PictureSelectionRender.height);
	        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION );
			list.doLayout();
	        JScrollPane jscroll = new JScrollPane(list,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	        jscroll.doLayout();
	        jscroll.setPreferredSize(new Dimension(
	        		jscroll.getPreferredSize().width + 30,
	        		Math.min(choices.length,3) * PictureSelectionRender.height + Math.max(choices.length,3)
	        		));
	        System.out.println("jscroll.getPreferredSize() = "+jscroll.getPreferredSize());
	        JLabel lab = new JLabel("Veuillez choisir une image de fond :    ", JLabel.CENTER);
	        jpanel.add(lab , BorderLayout.CENTER);
	        jpanel.add(jscroll,BorderLayout.CENTER);
	    }
	 
	    
	    public PictureSelection getSelected(){
	    	return(list.getSelectedValue());
	    }
	 

	@Override
	public String run(Map<String, String> globalMap) {
	    final JFrame frame = new JFrame("Setting 2/4");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.getContentPane().add(jpanel, BorderLayout.NORTH);
        System.out.println("jpanel.getSize() = "+jpanel.getSize());
	    JPanel panel = new JPanel();
	    JButton submit = new JButton("Next >");
	    submit.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {if(getSelected()!=null)frame.setVisible(false);}});
	    panel.add(submit);
	    frame.getContentPane().add(panel);
	    frame.pack();
        frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
        while(frame.isVisible()){
        	sleep(10);
        }
        globalMap.put(App.KEY_TCHAT_PICTURE, getSelected().path);
		return this.getClass().getName();
	}

	}

