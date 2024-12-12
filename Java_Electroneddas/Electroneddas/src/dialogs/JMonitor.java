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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;

import main.Electroneddas;
import panels.JCraiDisplay;
import panels.JStracPanel;
import panels.SerialListener;

/* TODO: Gestione spia clip /*
 * 
 */
public class JMonitor extends JPanel implements SerialListener, ActionListener{
	static JCraiDisplay msDisplay=new JCraiDisplay();
	static JCraiDisplay mdDisplay=new JCraiDisplay();
	static JSlider sulidu=new JSlider(JSlider.HORIZONTAL,0,100,0);
	
	//static JProgressBar sulidu=new JProgressBar();
	static JCheckBox cr=new JCheckBox();
	static JCheckBox su=new JCheckBox();
	static JCheckBox clip=new JCheckBox();
	
	boolean cr_sel=false;
	
	
	
	public JMonitor() {
		//this.setTitle("Monitor");
		//this.setResizable(false);
		this.setLayout(new BorderLayout());
		
		JPanel jpan=new JPanel();
		jpan.setLayout(new BoxLayout(jpan,BoxLayout.PAGE_AXIS));
		JPanel jpan2=new JPanel();
		JPanel jpan3=new JPanel();
		
		jpan2.add(new JLabel("Crais"));
		jpan2.add(cr);
		jpan2.add(new JLabel("Sulidu"));
		jpan2.add(su);
		jpan2.add(new JLabel("Clip"));
		jpan2.add(clip);
		
		cr.addActionListener(this);
		su.addActionListener(this);
		clip.addActionListener(this);
		
		JPanel cannas=new JPanel();
		cannas.add(msDisplay);
		cannas.add(mdDisplay);
		jpan3.add(cannas);
		
		JButton jbr=new JButton();
		jbr.setActionCommand("REC");
		BufferedImage url;
		try {
			url = ImageIO.read(JStracPanel.class.getResourceAsStream("/img/lau_disk.png"));
			jbr.setIcon(new ImageIcon(url));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jbr.setText("Sarva in su discu");
			

		} 

		jbr.setToolTipText("Arregistra in su discu");
		jbr.addActionListener(this);
		jpan3.add(jbr);
		
		jpan.add(jpan3);

		sulidu.setMinorTickSpacing(5);
		sulidu.setMajorTickSpacing(10);
		sulidu.setPaintTicks(true);
		sulidu.setPaintLabels(true);
		//sulidu.setMaximum(100);
	
		sulidu.setEnabled(false);
		jpan.add(sulidu);
		
		setListeners();
		System.out.println("liste");
		Electroneddas.serialPort.addListener('s', this);
		
		this.add(jpan,BorderLayout.CENTER);
		this.add(jpan2,BorderLayout.SOUTH);
		
		su.setSelected(true);
		cr.setSelected(true);
		
		this.sync();
	}
	public void sync() {
		int m=0;
		if (cr.isSelected()) m+=3;
		if (su.isSelected()) m+=4;
		if (clip.isSelected()) m+=16;
		
		SerialUSB.printCmd("E m "+m);
	}
	@Override
	public void action(String msg) {
		// TODO Auto-generated method stub	
		sulidu.setValue(Integer.parseInt(msg.substring(1).trim()));
		
		//sulidu.setToolTipText(sulidu.getValue()+"%");
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand()=="REC") {
			new JDiskRecorder();
		} else {
			sync();
		}
		
	}
	public int getMsCrais() {
		return msDisplay.getCrais();
	}
	public int getMdCrais() {
		return mdDisplay.getCrais();
	}
	public int getSulidu() {
		return sulidu.getValue();
	}
	
	
	public void startCraisMonitor() {
		cr_sel=cr.isSelected();
		cr.setSelected(true);
		sync();
	}
	public void stopCraisMonitor() {	
		cr.setSelected(cr_sel);
		sync();
	}
	
	public static void setListeners() {
		Electroneddas.serialPort.addListener('S', msDisplay);
		Electroneddas.serialPort.addListener('D', mdDisplay);
		
	}
	

}
