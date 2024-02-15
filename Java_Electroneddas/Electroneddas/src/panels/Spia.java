package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Spia extends JComponent {
	
	boolean status;
	Color onColor;
	Color offColor;
	int radius;
	JComponent panel;
	String label="";
	
	
	public Spia(Color onc, Color offc, int r) {
		this.label=label;
		onColor=onc;
		offColor=offc;
		status=false;
		radius=r;
		this.setPreferredSize(new Dimension(r*2,r*3));
		this.repaint();
	}
	
	public JPanel getPanel(String label) {
		JPanel SpiaPan=new JPanel();
		//SpiaPan.setLayout(new BoxLayout(SpiaPan,BoxLayout.PAGE_AXIS));
		//SpiaPan.add(new JLabel(label));
		SpiaPan.add(this);
		return SpiaPan;
	}
	
	public void on() {
		this.status=true;
		this.repaint();
	}
	public void off() {
		this.status=false;
		this.repaint();
		
	}
	public void alarm() {
		
	}
	
	public void paint(Graphics g) {	
		g.setColor(Color.DARK_GRAY);
		g.drawOval(2, 2, 2*radius-4, 2*radius-4);
		if (status) g.setColor(onColor);
		else g.setColor(offColor);
		g.fillOval(4,4,2*radius-8,2*radius-8);
	}
	
}
