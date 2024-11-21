package dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import panels.SerialListener;

/* TODO: Gestione spia clip /*
 * 
 */
public class JMonitor extends JDialog implements SerialListener, ActionListener{
	static JCraiDisplay msDisplay=new JCraiDisplay();
	static JCraiDisplay mdDisplay=new JCraiDisplay();
	static JSlider sulidu=new JSlider(JSlider.HORIZONTAL,0,100,0);
	
	static JCheckBox cr=new JCheckBox();
	static JCheckBox su=new JCheckBox();
	static JCheckBox clip=new JCheckBox();
	
	boolean cr_sel=false;
	
	
	
	public JMonitor() {
		this.setTitle("Monitor");
		this.setResizable(false);
		
		JPanel jpan=new JPanel();
		JPanel jpan2=new JPanel();
		
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
		jpan.add(cannas);

		//sulidu.setMinorTickSpacing(5);
		sulidu.setMajorTickSpacing(10);
		sulidu.setPaintTicks(true);
		sulidu.setPaintLabels(true);
		sulidu.setEnabled(false);
		jpan.add(sulidu);
		
		setListeners();
		Electroneddas.serialPort.addListener('s', this);
		
		this.getContentPane().add(jpan,BorderLayout.CENTER);
		this.getContentPane().add(jpan2,BorderLayout.SOUTH);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setLocation(1000, 500);
		setSize(400, 200);
		this.setVisible(false);
		this.sync();
	}
	public void sync() {
		int m=0;
		if (cr.isSelected()) m+=3;
		if (su.isSelected()) m+=4;
		if (clip.isSelected()) m+=16;
		
		Electroneddas.serialPort.printCmd("E m "+m);
	}
	@Override
	public void action(String msg) {
		// TODO Auto-generated method stub
		
		sulidu.setValue(Integer.parseInt(msg.substring(2,4).trim()));
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		sync();
	}
	public int getMsCrais() {
		return msDisplay.getCrais();
	}
	public int getMdCrais() {
		return mdDisplay.getCrais();
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
