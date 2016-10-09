package ihm;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Activity01 extends Activity<String> {
	private static final long serialVersionUID = 1L;
	private static int xSize;
	private static int ySize;

	private Image requestImage() {
        Image image = null;
        try {
            image = ImageIO.read(new File("groupchatui.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

	@Override
	public String run(Map<String, String> globalMap) {
		final Image image = requestImage();
		final JFrame frame = new JFrame("My First Chat !");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, null);
            }
        };
        xSize = image.getWidth(frame);
        ySize = image.getHeight(frame) + 45;

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton button = new JButton("Let's GO !!!");
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Calibry", Font.PLAIN, 20));
        button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
			}
		});
        panel.add(Box.createRigidArea(new Dimension(15, ySize)));
        panel.add(button);

        panel.setPreferredSize(new Dimension(xSize,ySize+45));

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        while(frame.isVisible()){
        	sleep(10);
        	//frame.setVisible(false);
        }
		return getClass().getName();
	}


}
