package ihm;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import metier.GlobalStat;

public class CopyOfActivity06_old extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int fontSize = 20;
	private static final int currentFiguresSize = 6*fontSize;
	private static final int labelSize2 = 5*fontSize;
	private static final int littleButtonSize = 5*fontSize/2;
	private GlobalStat gs;
	private Image bg;
	private static int xSize = 500;
	private static int ySize = 500;
	private static long speed = 1000;
	private boolean pause = false;
	private JPanel contentPane;
	private final JButton play,speedUp,speedDown;
	private JLabel currentTime;
	private JLabel currentInscripts,labelInscripts;
	private JLabel currentActifs,labelActifs;
	private JLabel currentCA,labelCA;

	public CopyOfActivity06_old(GlobalStat gs) {
		super();
		this.gs = gs;
		bg = new ImageIcon(gs.getBackgroundPath()).getImage();
		
		currentTime = 		buildJLabel(0, 0, fontSize, xSize, true, JLabel.CENTER, gs.getTs());
		currentInscripts = 	buildJLabel(xSize-currentFiguresSize-labelSize2, 2*fontSize, fontSize, currentFiguresSize, true, JLabel.RIGHT, gs.getNbInscrit());
		labelInscripts = 		buildJLabel(xSize-labelSize2, 2*fontSize, fontSize, labelSize2, true, JLabel.LEFT, " inscrits");
		currentActifs = 	buildJLabel(xSize-currentFiguresSize-labelSize2, 3*fontSize, fontSize, currentFiguresSize, true, JLabel.RIGHT, gs.getNbConnected());
		labelActifs = 			buildJLabel(xSize-labelSize2, 3*fontSize, fontSize, labelSize2, true, JLabel.LEFT, " actifs");
		currentCA = 		buildJLabel(xSize-currentFiguresSize-labelSize2, 4*fontSize, fontSize, currentFiguresSize, true, JLabel.RIGHT, gs.getCa());
		labelCA = 				buildJLabel(xSize-labelSize2, 4*fontSize, fontSize, labelSize2, true, JLabel.LEFT, " de CA");
	    play = new JButton("||");
	    play.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		pause = !pause;
	    		if(pause){
	    			play.setText(">");
	    		}else{
	    			play.setText("||");
	    		}
	    	}
	    });
	    play.setOpaque(true);
		play.setSize(littleButtonSize,littleButtonSize);
		play.setFont(new Font(play.getFont().getName(), Font.BOLD, fontSize));
	    play.setLocation(xSize-3*littleButtonSize,ySize-littleButtonSize);
	    speedUp = new JButton(">>");
	    speedUp.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		speed = Math.max(0, speed-500);
	    		if(speed < 500)speedUp.setEnabled(false);
	    		System.out.println("SpeedUp to "+speed);
	    	}
	    });
	    speedUp.setOpaque(true);
	    speedUp.setSize(2*littleButtonSize,littleButtonSize);
	    speedUp.setFont(new Font(play.getFont().getName(), Font.BOLD, fontSize));
	    speedUp.setLocation(xSize-2*littleButtonSize,ySize-littleButtonSize);
		
	    speedDown = new JButton("<<");
	    speedDown.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		speed+=500;
	    		if(speed > 0 && !speedUp.isEnabled())speedUp.setEnabled(true);
	    		System.out.println("SpeedDown to "+speed);
	    	}
	    });
	    speedDown.setOpaque(true);
	    speedDown.setSize(2*littleButtonSize,littleButtonSize);
	    speedDown.setFont(new Font(play.getFont().getName(), Font.BOLD, fontSize));
	    speedDown.setLocation(xSize-5*littleButtonSize,ySize-littleButtonSize);
		

	    contentPane = new JPanel();
	    contentPane = new JPanel(new GridLayout(4, 1));
		contentPane.setOpaque(false);
		contentPane.setLayout(null);
		contentPane.add(currentTime);
		contentPane.add(currentInscripts);contentPane.add(labelInscripts);
		contentPane.add(currentActifs);contentPane.add(labelActifs);
		contentPane.add(currentCA);contentPane.add(labelCA);
		contentPane.add(play);
		contentPane.add(speedUp);
		contentPane.add(speedDown);

	
	}

	private JLabel buildJLabel( int x, int y, int height, int width, boolean opaque, int align, String text) {
		JLabel jlabel = new JLabel(text, align);
		jlabel.setOpaque(opaque);
		jlabel.setFont(new Font(jlabel.getFont().getName(), Font.BOLD, height));
		jlabel.setSize(width, height*5/4);//jlabel.setLocation(x,y);
		return jlabel;
	}

	private void updateSize(int x, int y) {
		currentTime.setSize(x, currentTime.getHeight());
		currentInscripts.setLocation(new Point(x-currentFiguresSize-labelSize2,(int)currentInscripts.getLocation().getY()));
		labelInscripts.setLocation(new Point(x-labelSize2,(int)labelInscripts.getLocation().getY()));
		currentActifs.setLocation(new Point(x-currentFiguresSize-labelSize2,(int)currentActifs.getLocation().getY()));
		labelActifs.setLocation(new Point(x-labelSize2,(int)labelActifs.getLocation().getY()));
		currentCA.setLocation(new Point(x-currentFiguresSize-labelSize2,(int)currentCA.getLocation().getY()));
		labelCA.setLocation(new Point(x-labelSize2,(int)labelCA.getLocation().getY()));
	    play.setLocation(x-3*littleButtonSize,y-littleButtonSize);
	    speedUp.setLocation(x-2*littleButtonSize,y-littleButtonSize);
	    speedDown.setLocation(x-5*littleButtonSize,y-littleButtonSize);
	}
	
	
	@Override
	public void paintComponent(Graphics g) {
		g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
	}
	
	


	public String run() {
		setLayout(new BorderLayout());
		add(contentPane, BorderLayout.NORTH);
		final JFrame frame = new JFrame(gs.getAppName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(contentPane);
		frame.setSize(xSize, ySize+39);
	    frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		frame.getRootPane().addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
        		updateSize(getWidth(),getHeight());            	
            }
        });

	    
		while(frame.isVisible()){
			try {Thread.sleep(speed);} catch (InterruptedException e) {}
			if(!pause){
				try {
					gs.nextStep();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				currentTime.setText(gs.getTs());
				currentInscripts.setText(gs.getNbInscrit());
				currentActifs.setText(gs.getNbConnected());
				currentCA.setText(gs.getCa());
				System.out.println(gs);
			}
		}
		return this.getClass().getName();
	}

}

