package ihm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import metier.GlobalStat;

public class Activity06 extends JPanel {
	private static final long serialVersionUID = 1L;
	private static final int fontSize = 20;
	private GlobalStat gs;
	private static long speed = 750;
	private static long speedStep = 250;
	private boolean pause = false;
	private JPanel contentPane;
	private final JButton play,speedUp,speedDown;
	private JLabel currentTime;
	private JLabel currentInscripts;
	private JLabel currentActifs;
	private JLabel currentCA;
	private JLabel warningZone;

	public Activity06(GlobalStat gs) {
		super();
		this.gs = gs;
		
		currentTime = buildJLabel(gs.getTs(), JLabel.CENTER);
		currentInscripts = buildJLabel(gs.getNbInscrit(), JLabel.RIGHT);
		currentActifs = buildJLabel(gs.getNbConnected(), JLabel.RIGHT);
		currentCA = buildJLabel(gs.getCa(), JLabel.RIGHT);
		warningZone = buildJLabel("/!\\ computer overheating /!\\", JLabel.CENTER);
		warningZone.setFont(new Font(warningZone.getFont().getName(), Font.BOLD, 3*fontSize/2));
		warningZone.setForeground(Color.red);
		warningZone.setVisible(false);
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
		play.setFont(new Font(play.getFont().getName(), Font.BOLD, fontSize));
	    speedUp = new JButton(">>");
	    speedUp.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		speed = Math.max(0, speed-speedStep);
	    		if(speed < speedStep)speedUp.setEnabled(false);
	    		System.out.println("SpeedUp to "+speed);
	    	}
	    });
	    speedUp.setFont(new Font(play.getFont().getName(), Font.BOLD, fontSize));
	    speedDown = new JButton("<<");
	    speedDown.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent e) {
	    		speed+=speedStep;
	    		if(speed > 0 && !speedUp.isEnabled())speedUp.setEnabled(true);
	    		System.out.println("SpeedDown to "+speed);
	    	}
	    });
	    speedDown.setFont(new Font(play.getFont().getName(), Font.BOLD, fontSize));
		
	    contentPane = new JPanel();
	    contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		contentPane.setOpaque(false);
	    JPanel j1 = new JPanel();
	    JPanel j11 = new JPanel(new GridLayout(5,1));
	    JPanel j12 = new JPanel(new GridLayout(5,1));
	    JPanel j2 = new JPanel(new GridLayout(1,3));
	    JPanel j3 = new JPanel(new GridLayout(1,1));
	    JPanel j4 = new JPanel(new GridLayout(3,1));
	    JPanel j5 = new JPanel(new GridLayout(1,1));
	    j11.add(buildJLabel("Coût mensuel : ", JLabel.LEFT));j12.add(buildJLabel(gs.getMonthCost(), JLabel.LEFT));
	    j11.add(buildJLabel("Abonnement : ", JLabel.LEFT));j12.add(buildJLabel(gs.getMonthPrice(), JLabel.LEFT));
	    j11.add(buildJLabel("Coût d'une fleur : ", JLabel.LEFT));j12.add(buildJLabel(gs.getFlowerPrice(), JLabel.LEFT));
	    j11.add(buildJLabel("Nombre d'inscriptions journalières : ", JLabel.LEFT));j12.add(buildJLabel(gs.getPopularity(), JLabel.LEFT));
	    j11.add(buildJLabel("Durée de vie : ", JLabel.LEFT));j12.add(buildJLabel(gs.getChurn(), JLabel.LEFT));
	    j1.add(j11);j1.add(j12);
	    
	    j2.add(speedDown);j2.add(play);j2.add(speedUp);
	    j3.add(currentTime, JLabel.CENTER);
	    j4.add(currentInscripts);j4.add(buildJLabel(" inscrits", JLabel.LEFT));
	    j4.add(currentActifs);j4.add(buildJLabel(" connectés", JLabel.LEFT));
	    j4.add(currentCA);j4.add(buildJLabel(" de CA", JLabel.LEFT));
	    j5.add(warningZone);
	    contentPane.add(j1);
	    contentPane.add(Box.createVerticalStrut(20));
	    contentPane.add(j2);
	    contentPane.add(Box.createVerticalStrut(10));
	    contentPane.add(j3);
	    contentPane.add(Box.createVerticalStrut(20));
	    contentPane.add(j4);
	    contentPane.add(Box.createVerticalStrut(10));
	    contentPane.add(j5);
	}

	private JLabel buildJLabel(String text, int align) {
		JLabel jlabel = new JLabel(text, align);
		jlabel.setOpaque(true);
		jlabel.setFont(new Font(jlabel.getFont().getName(), Font.BOLD, fontSize));
		return jlabel;
	}




	public String run() {
		setLayout(new BorderLayout());
		final JFrame frame = new JFrame(gs.getAppName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(contentPane);
	    frame.pack();
		frame.setLocationByPlatform(true);
		frame.setVisible(true);
		
		while(frame.isVisible()){
			try {Thread.sleep(speed);} catch (InterruptedException e) {}
			if(!pause){
				try {
					gs.nextStep();
				} catch (Exception e) {
					e.printStackTrace();
				}
				warningZone.setVisible(gs.isOverheating());
				currentTime.setText(gs.getTs());
				currentInscripts.setText(gs.getNbInscrit());
				currentActifs.setText(gs.getNbConnected());
				currentCA.setText(gs.getCa());
			}else{
				try {Thread.sleep(1000);} catch (InterruptedException e) {}
			}
		}
		return this.getClass().getName();
	}

}

