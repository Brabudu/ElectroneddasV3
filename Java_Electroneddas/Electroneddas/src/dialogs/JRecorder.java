package dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;

import main.Electroneddas;
import panels.JStracPanel;
import panels.SerialListener;
import panels.Spia;

public class JRecorder  extends JDialog implements ActionListener, SerialListener{ 

	JRadioButton recram=new JRadioButton(); 
	JRadioButton recdisc=new JRadioButton(); 

	JProgressBar size=new JProgressBar();

	JButton rec=new JButton("Rec");
	JButton pl=new JButton("Play");
	
	JLabel stato=new JLabel("Stopped");


	public JRecorder() {
		this.setTitle("Registradori");
		this.setResizable(false);

		Electroneddas.serialPort.addListener('R', this);

		JPanel pan1=new JPanel();//new GridLayout(0,1))
		JPanel pan2=new JPanel();
		JPanel pan3=new JPanel();
		
		pan2.add(rec);
		pan2.add(pl);
		
		pan3.add(stato);
		pan3.add(size);
		
		size.setVisible(false);

		BufferedImage url;
		JLabel bdisk=new JLabel();
		JLabel bram=new JLabel();

		ButtonGroup bp=new ButtonGroup();
		bp.add(recram);
		bp.add(recdisc);
		recram.setSelected(true);
		recram.addActionListener(this);
		recdisc.addActionListener(this);
		rec.addActionListener(this);
		pl.addActionListener(this);
		rec.setBackground(new Color(100,0,0));
		pl.setBackground(new Color(0,100,0));
		try {
			url = ImageIO.read(JStracPanel.class.getResourceAsStream("/img/lau_disk.png"));
			bdisk.setIcon(new ImageIcon(url));

			url = ImageIO.read(JStracPanel.class.getResourceAsStream("/img/lau_ram.png"));
			bram.setIcon(new ImageIcon(url));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bdisk.setText("Sarva in su discu");
			bram.setText("Sarva in is electroneddas");

		} 

		bdisk.setToolTipText("Arregistra in su discu");
		bram.setToolTipText("Arregistra in is electroneddas");


		
		pan1.add(bdisk);
		pan1.add(recdisc);
		pan1.add(bram);
		pan1.add(recram);
		size.setValue(50);
		size.setStringPainted(true);

		this.getContentPane().add(pan1,BorderLayout.NORTH);
		this.getContentPane().add(pan2,BorderLayout.CENTER);
		this.getContentPane().add(pan3,BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setLocation(1000, 500);
		this.pack();
		this.setVisible(false);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (recdisc.isSelected()) {
			
			pl.setEnabled(false);
		} else {
			
			pl.setEnabled(true);
		}
		if (arg0.getActionCommand().equals("Rec")) {
			if (recram.isSelected()) SerialUSB.printCmd("E R 2");
			else {
				this.setVisible(false);
				new JDiskRecorder();
			}
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
			stato.setText("Playing");
			stato.setForeground(Color.green);
			enableRadio(false);
		}
		if (state.contains("end")) {
			pl.setEnabled(true);
			stato.setText("Stopped");
			stato.setForeground(Color.lightGray);
			enableRadio(true);
			if (recram.isSelected()) rec.setEnabled(true);
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
			stato.setText("Stopped");
			rec.setEnabled(true);	
			pl.setEnabled(true);	
			stato.setForeground(Color.lightGray);
			size.setVisible(false);
			enableRadio(true);
		}
		if (state.contains("rec")) {
			if (recram.isSelected()) {
				size.setVisible(true);
				size.setValue(0);
			}
			stato.setText("Recording");
			stato.setForeground(Color.red);
			enableRadio(false);
		}
	
	}
	private void enableRadio(boolean e) {
		recram.setEnabled(e);
		recdisc.setEnabled(e);
	}
}
