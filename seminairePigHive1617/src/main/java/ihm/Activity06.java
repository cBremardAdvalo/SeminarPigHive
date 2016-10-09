package ihm;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import metier.GlobalStat;

public class Activity06 extends JPanel {
	private static final long serialVersionUID = 1L;
	private GlobalStat gs;
	private Image bg;
	private static int xSize = 500;
	private static int ySize = 500;
	private long speed = 0;
	private JPanel contentPane;
	private JLabel currentTime;

	public Activity06(GlobalStat gs) {
		super();
		this.gs = gs;
		bg = new ImageIcon(gs.getBackgroundPath()).getImage();
		currentTime = new JLabel(gs.getTs(), JLabel.CENTER);
		currentTime.setOpaque(true);
		currentTime.setFont(new Font(currentTime.getFont().getName(), Font.BOLD, 30));
		currentTime.setSize(xSize, 30);currentTime.setLocation(0,0);
		contentPane = new JPanel();
		contentPane.setOpaque(false);
		contentPane.setLayout(null);
		contentPane.add(currentTime);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
	}


	public String run() {
		setLayout(new BorderLayout());
		add(contentPane);
		final JFrame frame = new JFrame(gs.getAppName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(this);
		frame.setSize(xSize, ySize);
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		while(frame.isVisible()){
			try {Thread.sleep(speed);} catch (InterruptedException e) {}
			gs.nextStep();
			currentTime.setText(gs.getTs());
			System.out.println(gs);
		}
		return this.getClass().getName();
	}

}

