package panels;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;

import main.Electroneddas;

public class Spartiu extends JPanel implements ActionListener, MouseListener {  
	int manc;
	int mancs=255;
	
	int sul=0;
	
	Timer timer;
	
	boolean mode=false;
	
	BufferedImage image=new BufferedImage(800,200,BufferedImage.TYPE_INT_RGB);
	
	static Color tav[]= {Color.red,Color.orange,Color.green,Color.cyan,Color.magenta};
	
	public Spartiu(int time) {
		timer=new Timer(time, this);
		this.addMouseListener(this);
		this.setBackground(Color.black);
		
	}
	public void start() {
		timer.start();
		
	}
	public void stop() {
		timer.stop();
	}
	
	public void paintComponent(Graphics g)  {
		
		super.paintComponent(g);
		g.drawImage(image, 0, 0, this); 
	}
	
	private void updateSonada() {
		
		Graphics g=image.createGraphics();
		
    	int w=2;
    	
    	g.copyArea(0, 0, 800, 200, -w, 0);
    	//g.fillRect(0,0,780,200);
    	g.setColor(Color.black);
    	g.fillRect(780,10,w,50);
    	g.fillRect(780,70,w,50);
    	
    	int n=8;
    	int arref=0;
    	if (mode) arref++;
    	
		for (int i=0;i<4+arref;i++)
		{
			if ((manc&n)==0) {
				g.setColor(tav[i]);
			
				g.fillRect(780-w,10+10*i,w,10);
		        if (mode) break;
			} 
			n/=2;
		}
		
		n=8;
		for (int i=0;i<4+arref;i++)
		{
			if ((mancs&n)==0) {
				g.setColor(tav[i]);
				g.fillRect(780-w,70+10*i,w,10);
				if (mode) break;
			} 
			n/=2;
		}
		
		g.setColor(Color.lightGray);
		g.fillRect(780-w,125,w,54);
		g.setColor(Color.black);
		g.fillRect(780-w,125+(sul/2),w,1);
		
		
	}
	
	
	
    public void paint(Graphics g)  { 
    	super.paint(g);
    	
    
    	
     }
    public void actionPerformed(ActionEvent ev){
        if(ev.getSource()==timer){
        //this.getGraphics().copyArea(0, 0, 200, 200, 2, 0);
          manc=Electroneddas.monitor.getMdCrais();
          mancs=Electroneddas.monitor.getMsCrais();
          sul=Electroneddas.monitor.getSulidu();
     
          updateSonada();
          
          
          repaint();
          //System.err.println(mancs);
        }
    }
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		mode=!mode;
	}
	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
 }// fine classe che eredita da Canva{


