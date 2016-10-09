package ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class PictureSelectionRender extends JPanel implements ListCellRenderer<PictureSelection> {
	private static final long serialVersionUID = 1L;
	public static final int height = 100;
	private JLabel lbIcon = new JLabel();
    private JLabel lbAuthor = new JLabel();
 
    public PictureSelectionRender() {
        setLayout(new BorderLayout());
        JPanel panelText = new JPanel(new GridLayout(0, 1));
        panelText.add(lbAuthor);
        add(lbIcon, BorderLayout.WEST);
        add(panelText, BorderLayout.CENTER);
    }
 
    public Component getListCellRendererComponent(JList<? extends PictureSelection> list,
    		PictureSelection picture, int index, boolean isSelected, boolean cellHasFocus) {
        	lbIcon.setIcon(new ImageIcon(picture.path));
	        lbAuthor.setText(picture.name);
			lbAuthor.setForeground(Color.blue);
			   // set Opaque to change background color of JLabel
		    lbAuthor.setOpaque(true);
		    lbIcon.setOpaque(true);
		    // when select item
		    if (isSelected) {
		        lbAuthor.setBackground(list.getSelectionBackground());
		        lbIcon.setBackground(list.getSelectionBackground());
		        setBackground(list.getSelectionBackground());
		    } else { // when don't select
		        lbAuthor.setBackground(list.getBackground());
		        lbIcon.setBackground(list.getBackground());
		        setBackground(list.getBackground());
		    }
        return this;
    }

}
