package panels;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.Timer;

import main.Cuntzertu;
import main.Electroneddas;

public class JVolPanel extends JPanel implements  AdjustmentListener{
	
	JCKnob volT=new JCKnob(50,0,100,Color.GREEN,"Vol T","Volùmini tumbu",0);
	 JCKnob bilT=new JCKnob(0,-100,100,Color.MAGENTA,"Bil T","Bilanciamentu tumbu",1);
	
	 JCKnob volMs=new JCKnob(50,0,100,Color.GREEN,"Vol Ms","Volùmini mancosa",2);
	 JCKnob bilMs=new JCKnob(0,-100,100,Color.MAGENTA,"Bil Ms","Bilanciamentu mancosa",3);
	
	 JCKnob volMd=new JCKnob(50,0,100,Color.GREEN,"Vol Md","Volùmini mancosedda",4);
	 JCKnob bilMd=new JCKnob(0,-100,100,Color.MAGENTA,"Bil Md","Bilanciamentu mancosedda",5);
	
	Cuntzertu c;
	
	
	
	boolean tumbuStopped=false;
	boolean msStopped=false;
	boolean mdStopped=false;
	
	public JVolPanel(Cuntzertu c) {
		super();
		this.c=c;
		
		JPanel top=new JPanel();
		top.setLayout(new BoxLayout(top,BoxLayout.PAGE_AXIS));
		//top.add(clip.getPanel("CLIP"));
		//top.add(vol);
		
		
		
		JPanel jv=new JPanel(new GridLayout(1,0));
		jv.setBorder(BorderFactory.createTitledBorder("Volumi"));	
		top.add(jv);	
		
		
		jv.add (volT);	
		volT.getKnob().addAdjustmentListener(this);	
		
		jv.add (volMs);
		volMs.getKnob().addAdjustmentListener(this);
		
		jv.add (volMd);
		volMd.getKnob().addAdjustmentListener(this);
		
		jv.add (bilT);
		bilT.getKnob().addAdjustmentListener(this);
		
		jv.add (bilMs);
		bilMs.getKnob().addAdjustmentListener(this);
		
		jv.add (bilMd);
		bilMd.getKnob().addAdjustmentListener(this);
		
		add(top);
		
	}
	
	 void sync(Cuntzertu c) {
		this.c=c;
		
		
		if (tumbuStopped) {
			volT.setValue((int)(0));
			Electroneddas.serialPort.printCmd("vt 0 0");
		}
		else {
			volT.setValue((int)(c.volT*50));
			bilT.setValue((int)(c.bilT*100));
			bilT.trigger();
		}
		
		if (msStopped) {
			volMs.setValue((int)(0));
			Electroneddas.serialPort.printCmd("vs 0 0");
		}
		else {
			volMs.setValue((int)(c.volMs*50));
			bilMs.setValue((int)(c.bilMs*100));
			bilMs.trigger();
		}
		
		if (mdStopped) {
			volMd.setValue((int)(0));
			Electroneddas.serialPort.printCmd("vd 0 0");
		}
		else {
			volMd.setValue((int)(c.volMd*50));
			bilMd.setValue((int)(c.bilMd*100));
			bilMd.trigger();
		}
		
	}
	
	
	
	public void muteT() {
		tumbuStopped=!tumbuStopped;
		sync(c);
		
	}
	public void muteMs() {
		msStopped=!msStopped;
		sync(c);
	}
	public void muteMd() {
		mdStopped=!mdStopped;
		sync(c);
	}
	public void mute() {
		mdStopped=true;
		msStopped=true;
		tumbuStopped=true;
		sync(c);
	}

	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		switch (e.getID()) {
		case 0:	//Volume tumbu
			c.volT=(float)e.getValue()/50; //*4
			
			Electroneddas.serialPort.printCmd("vt "+c.volT+" "+c.bilT);
			break;
		case 1:	//Bilanciamento
			c.bilT=(float)e.getValue()/100;
			Electroneddas.serialPort.printCmd("vt "+c.volT+" "+c.bilT);
			
			break;	
		case 2:	//Volume
			c.volMs=(float)e.getValue()/50; //*4
			Electroneddas.serialPort.printCmd("vs "+c.volMs+" "+c.bilMs);
			break;
		case 3:	//Bilanciamento
			c.bilMs=(float)e.getValue()/100;
			Electroneddas.serialPort.printCmd("vs "+c.volMs+" "+c.bilMs);
			break;	
		case 4:	//Volume
			c.volMd=(float)e.getValue()/50; //*4
			Electroneddas.serialPort.printCmd("vd "+c.volMd+" "+c.bilMd);
			break;
		case 5:	//Bilanciamento
			c.bilMd=(float)e.getValue()/100;
			Electroneddas.serialPort.printCmd("vd "+c.volMd+" "+c.bilMd);
			break;	
		
		
		}
		if (!Electroneddas.isSyncing()) Electroneddas.serialPort.printCmd("E u");
	}
}
