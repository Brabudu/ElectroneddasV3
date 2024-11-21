package dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;

import main.Cuntzertu;
import main.Electroneddas;
import panels.JCraiDisplay;
import panels.SerialListener;

/* TODO: Gestione spia clip /*
 * 
 */
public class JRecorder extends JDialog implements SerialListener, ActionListener{
	
	static JFileChooser fc;
	FileWriter myWriter;
	long zero;
	
	SerialListener md;
	SerialListener ms;
	
	public JRecorder() {
		this.setTitle("Recording");
		this.setResizable(false);
		
		JPanel jpan=new JPanel();
		
		JButton quit=new JButton("STOP");
		quit.addActionListener(this);
		jpan.add(quit);
		
		Electroneddas.serialPort.addListener('S', this);
		Electroneddas.serialPort.addListener('D', this);
		
		
		this.getContentPane().add(jpan,BorderLayout.CENTER);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.setLocation(1000, 500);
		setSize(200, 50);
		this.setVisible(true);
		int rv=0;
		fc=new JFileChooser();
		fc.setDialogTitle("Sciobera su file");
		fc.setDialogType(JFileChooser.OPEN_DIALOG);

		rv = fc.showOpenDialog(this);

		if (rv == JFileChooser.APPROVE_OPTION) {
			String filename=fc.getSelectedFile().getAbsolutePath();
			
		
		try {
			myWriter = new FileWriter(filename);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		    
		zero=System.currentTimeMillis();
		} else {
			JMonitor.setListeners();
			dispose();
		}
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
			myWriter.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JMonitor.setListeners();
		dispose();
	}

	@Override
	public void action(String msg) {
		// TODO Auto-generated method stub
		String line=msg.substring(0, 1)+","+msg.substring(2, 3)+","+(System.currentTimeMillis()-zero)+"\n";
		//System.out.println(line);
		try {
			myWriter.append(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	

}
