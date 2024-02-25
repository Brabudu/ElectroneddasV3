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

public class JRevPanel extends JPanel implements AdjustmentListener{
	JCKnob vol=new JCKnob(15,0,30,Color.YELLOW,"Vol","Volùmini",0);
	
	 JCKnob damp=new JCKnob(50,0,100,Color.GREEN,"Damp","Assorbimentu",1);
	 JCKnob room=new JCKnob(30,0,50,Color.MAGENTA,"Room","Aposentu",2);
	
	Cuntzertu c;

	public JRevPanel(Cuntzertu c) {
		super();
		this.c=c;
		
						
		JPanel jv=new JPanel(new GridLayout(0,3));
		setBorder(BorderFactory.createTitledBorder("Reverb"));
		
		jv.add (vol);
		vol.getKnob().addAdjustmentListener(this);
		
		jv.add (damp);
		damp.getKnob().addAdjustmentListener(this);
		
		jv.add (room);
		room.getKnob().addAdjustmentListener(this);
		
		add(jv);
		
	}
	
	 void sync(Cuntzertu c) {
		this.c=c;
		
		vol.setValue((int)(Cuntzertu.prefs.revVol*100));

		//vol.trigger();
		
		damp.setValue((int)(Cuntzertu.prefs.revDamp*100));
		
		//damp.trigger();
		
		room.setValue((int)(Cuntzertu.prefs.revRoom*100));
		
		room.trigger(); //bastat unu
		
	
	}
	
	
	
	
	
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		switch (e.getID()) {
		case 0:	//Volume
			c.revVol=(float)e.getValue()/100; 
			break;
		case 1:	
			c.revDamp=(float)e.getValue()/100;
			break;	
		case 2:	
			c.revRoom=(float)e.getValue()/100; //*4
			break;
		
		}
		Electroneddas.serialPort.printCmd("r "+c.revVol+" "+c.revDamp+" "+c.revRoom);
		if (!Electroneddas.isSyncing()) Electroneddas.serialPort.printCmd("E u");
	}
}
