package dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
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
	
	JSpinner num=new JSpinner(new SpinnerNumberModel(0,0,100,1));
	
	JTextField name=new JTextField();

	JLabel stato2=new JLabel(" ");

	public JRecorder() {
		this.setTitle("Registradori");
		this.setResizable(false);

		Electroneddas.serialPort.addListener('R', this);

		JPanel pan1=new JPanel(new GridLayout(0,1));
		JPanel pan2=new JPanel();
		JPanel pan3=new JPanel();
		JPanel pan4=new JPanel();
		pan4.setLayout(new BoxLayout(pan4,BoxLayout.PAGE_AXIS));
		
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
		BufferedImage url;
		
		try {
			url = ImageIO.read(Electroneddas.class.getResourceAsStream("/img/lau_disk.png"));
			ImageIcon t = new ImageIcon(new ImageIcon(url).getImage()
					.getScaledInstance(64, 40, Image.SCALE_SMOOTH)); // Set the desired size here
			JButton save=new JButton(t);
			save.setToolTipText("Sarva in su discu");
			save.setActionCommand("Save");
			save.addActionListener(this);
			pan3.add(new JPanel().add(save));
			
			url = ImageIO.read(Electroneddas.class.getResourceAsStream("/img/lau_ram.png"));
			 t = new ImageIcon(new ImageIcon(url).getImage()
					.getScaledInstance(64, 40, Image.SCALE_SMOOTH)); // Set the desired size here
			JButton load=new JButton(t);
			load.setToolTipText("Càrriga de su discu");
			load.setActionCommand("Load");
			load.addActionListener(this);
			pan3.add(new JPanel().add(load));
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		pan3.add(num);
		
		pan4.add(pan3);
		
		pan4.add(new JPanel().add(name));
		pan4.add(stato2);
		
		
		this.getContentPane().add(pan1,BorderLayout.NORTH);
		this.getContentPane().add(pan2,BorderLayout.CENTER);
		this.getContentPane().add(pan4,BorderLayout.SOUTH);
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		this.setLocation(1200, 600);
		this.setSize(250,220);
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
		
		if (arg0.getActionCommand().equals("Load")) {
			stato2.setText(" ");
			SerialUSB.printCmd("E R l "+num.getValue());
			
		}
		
		if (arg0.getActionCommand().equals("Save")) {
			String nome=name.getText();
			if (nome.length()>31) nome.subSequence(0, 31);
			SerialUSB.printCmd("E R n "+nome);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		SerialUSB.printCmd("E R s "+num.getValue());
		stato2.setText("OK");
		
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
		
		if (state.contains("err")) {
			name.setText(" ");
			stato2.setText("ERROR");
		}
		if (state.contains("load")) {
			name.setText(state.substring(4));
			stato2.setText("OK");
		}
		if (state.contains("save")) {
			
		}	
	
	}
	
}
