package panels;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

import main.Electroneddas;

public class Spartiu extends JPanel implements ActionListener  {  
	int manc;
	int mancs=255;
	
	int sul=0;
	
	Timer timer;
	
	static Color tav[]= {Color.red,Color.orange,Color.green,Color.cyan,Color.magenta};
	
	public Spartiu(int time) {
		timer=new Timer(time, this);
	}
	public void start() {
		timer.start();
		
	}
	public void stop() {
		timer.stop();
	}
    public void paint(Graphics g)  { 
    	
    	int w=2;
    	g.copyArea(0, 0, 800, 200, -w, 0);
    	g.setColor(Color.black);
    	g.fillRect(780,10,w,50);
    	g.fillRect(780,70,w,50);
    	
    	int n=8;
		for (int i=0;i<5;i++)
		{
			if ((manc&n)==0) {
				g.setColor(tav[i]);
			
				g.fillRect(780-w,10+10*i,w,10);
		        break;
			} 
			n/=2;
		}
		
		n=8;
		for (int i=0;i<5;i++)
		{
			if ((mancs&n)==0) {
				g.setColor(tav[i]);
				g.fillRect(780-w,70+10*i,w,10);
		        break;
			} 
			n/=2;
		}
		
		g.setColor(Color.yellow);
		g.fillRect(780-w,125+(sul/2),w,1);
    	
     }
    public void actionPerformed(ActionEvent ev){
        if(ev.getSource()==timer){
        //this.getGraphics().copyArea(0, 0, 200, 200, 2, 0);
          manc=Electroneddas.monitor.getMdCrais();
          mancs=Electroneddas.monitor.getMsCrais();
          sul=Electroneddas.monitor.getSulidu();
          
          repaint();// this will call at every 1 second
          //System.err.println(mancs);
        }
    }
 }// fine classe che eredita da Canva{


