package dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import main.Electroneddas;
import panels.JStracPanel;
import panels.SerialListener;
import panels.Spia;

public class JRecorder  extends JDialog implements ActionListener, SerialListener{ 

	

	JProgressBar size=new JProgressBar();

	JButton rec=new JButton("Rec");
	JButton pl=new JButton("Play");
	
	JLabel stato=new JLabel("Stop",SwingConstants.CENTER);


	public JRecorder() {
		this.setTitle("Registradori");
		this.setResizable(false);

		Electroneddas.serialPort.addListener('R', this);

		JPanel pan1=new JPanel(new GridLayout(0,1));
		JPanel pan2=new JPanel();
		JPanel pan3=new JPanel();
		pan3.setLayout(new BoxLayout(pan3,BoxLayout.PAGE_AXIS));
		
		pan2.add(rec);
		pan2.add(pl);
		
		stato.setHorizontalAlignment(JLabel.CENTER);
		
		pan1.add(stato);
		pan1.add(size);
		
		size.setVisible(false);
		size.setValue(0);
	

		
		rec.addActionListener(this);
		pl.addActionListener(this);
		rec.setBackground(new Color(100,0,0));
		pl.setBackground(new Color(0,100,0));
		
		
		size.setStringPainted(true);

		this.getContentPane().add(pan1,BorderLayout.NORTH);
		this.getContentPane().add(pan2,BorderLayout.CENTER);
		this.getContentPane().add(pan3,BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setLocation(1200, 600);
		this.setSize(250,100);
		this.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
		if (arg0.getActionCommand().equals("Rec")) {
			SerialUSB.printCmd("E R 2");
			
		}
		if (arg0.getActionCommand().equals("Play")) {
			SerialUSB.printCmd("E R 3 1");
		}
		
			
	}

	@Override
	public void action(String msg) {
		// TODO Auto-generated method stub
		String state=msg.substring(1);
		if (state.contains("play")) {
			pl.setEnabled(false);	
			rec.setEnabled(false);
			stato.setText("Play");
			stato.setForeground(Color.green);
		
		}
		if (state.contains("end")) {
			pl.setEnabled(true);
			stato.setText("Stop");
			stato.setForeground(Color.lightGray);
			
			rec.setEnabled(true);
		}
		if (state.length()<4) {
			int progress=Integer.parseInt(state.substring(0, 1));		
			size.setValue(progress*10);
		}
		if (state.contains("ready")) {
			rec.setEnabled(false);	
			pl.setEnabled(false);	
			stato.setText("Ready");
			stato.setForeground(Color.yellow);
		}
		if (state.contains("stop")) {
			stato.setText("Stop");
			rec.setEnabled(true);	
			pl.setEnabled(true);	
			stato.setForeground(Color.lightGray);
			size.setVisible(false);
			
		}
		if (state.contains("rec")) {
			size.setVisible(true);
			size.setValue(0);
			stato.setText("Rec");
			stato.setForeground(Color.red);
			
		}
	
	}
	
}
