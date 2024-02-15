package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Canna extends JPanel{
	
	
	byte canna[]= new byte[5];
	
	
	static final int M=10;
	static final int W=350;
	static final int WM=3;
	static final int H=16;
	
	static final int maxcrai=30;
		
	public Canna() {
		this.setPreferredSize(new Dimension(H+20,W+20));
	}
	
	public void setCanna(byte [] canna) {
		this.canna=canna;
		this.setBackground(Color.black);
		this.repaint();
		
	}
	
			
	public void paint(Graphics g)  { 
		g.setColor(Color.YELLOW);
		g.fillRect(10, 10, W,H);
		g.setColor(Color.BLACK);
		g.fillRect(15, 10, 20, H);
		for (int i=0;i<5;i++) {
			int x=(int)Math.pow(Math.abs((double)canna[i]-12-maxcrai),0.95)*15;
			int w=6;
			if (i==0) g.fillRect(x-80, 16, w*4, H-12);
			else g.fillRect(x-80, 15, w, H-10);
		}
		
		
    
	}
	

}
