package panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class Pianola extends JPanel{
	boolean scala[]= {true,true,false,true,true,true,false};
	
	int ss=0;
	int sp=16;
	
	byte puntu=0;
	
	byte ms[]= new byte[5];
	byte md[]= new byte[5];
	
	byte t;
	
	static final int M=10;
	static final int W=30;
	static final int WM=3;
	static final int H=120;
	
	static final char crais[]= {'A','4','3','2','1'};
	static Color tav[]= {Color.red,Color.orange,Color.green,Color.cyan,Color.magenta,Color.gray};
	
	public Pianola(int start,int stop, byte puntu) {
		ss=start;
		sp=stop;
		this.puntu=puntu;
		this.setPreferredSize(new Dimension((W+1)*(stop-start),180));
	}
	
	public void setCannas(byte tumbu, byte [] mancs, byte [] mancd) {
		ms=mancs;
		md=mancd;
		t=tumbu;
		this.repaint();
		
	}
	
	public void setPuntu(byte p) {
		puntu=p;
	}
		
	public void paint(Graphics g)  { 
		g.setColor(Color.DARK_GRAY);
		g.fillRect(0, 0, (sp-ss)*W+2*M,H+2*M);
		
		g.setColor(Color.white);
    	for (int i=ss;i<sp;i++) {     	
        	g.fillRect(M+i*W,M,(W-WM),H);
    	}
    	
    	g.setColor(Color.black);
    	for (int i=ss;i<sp-1;i++) {
    		if (scala[i%7]) {
    			g.fillRect(M+i*W+(int)(W*0.6),M,(int)(W*0.7),(int)(H*0.5));
    		}
    	}
    	
    	
    	for (int i=0;i<=4;i++) {
    		g.setColor(tav[i]);
    		g.fillOval(M+W/2+(int)(cromaToDiat(ms[i]+puntu-12)*W)-10, (int)(H*0.65), 16, 16);
    		g.setColor(Color.BLACK);
    		g.drawString(crais[i]+"",2*M+(int)(cromaToDiat(ms[i]+puntu-12)*W), 90);
    	}
    	
    	for (int i=0;i<=4;i++) {
    		g.setColor(tav[i]);
    		g.fillOval(M+W/2+(int)(cromaToDiat(md[i]+puntu-12)*W)-10, (int)(H*0.80), 16, 16);
    		g.setColor(Color.BLACK);
    		g.drawString(crais[i]+"",2*M+(int)(cromaToDiat(md[i]+puntu-12)*W), 107);
    
    	}
    	
    	g.setColor(tav[5]);
		g.fillOval(M+W/2+(int)(cromaToDiat(t+puntu)*W)-10, (int)(H*0.95), 16, 16);
		g.setColor(Color.BLACK);
		g.drawString("T",2*M+(int)(cromaToDiat(t+puntu)*W), 124);
		
    	//System.err.println(cromaToPiano(5));
    
	}
	private float cromaToDiat(int croma) {
		float val=0;
		val=((croma-croma%12)/12)*7;
		croma=croma%12;
		int n=0;
		for (int i=1;i<=croma;i++)
		{
			if (scala[n/2]) {
				n+=1;
			}
			else n+=2;
		}
		return val+(float)n/2;		
	}
		
	
}
