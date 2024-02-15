package panels;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import main.Cuntzertu.Crai;
import main.Electroneddas;


public class JCraiPanel extends JPanel implements AdjustmentListener, ActionListener {

	JCKnob vol=new JCKnob(50,0,100,Color.GREEN,"Vol","Volùmini",0);
	JCKnob fini=new JCKnob(0,-31,31,Color.CYAN,"Fini","Crai fini",1);
	
	JCKnob volA=new JCKnob(10,0,100,Color.ORANGE,"Vol BQ",'S','D',"Volùmini de su filtru Biquad",2,true);
	JCKnob duty=new JCKnob(40,1,50,Color.YELLOW,"Duty","Duty cicle",3);
	
	Crai c;
	char canna_id;
	
	JBQPanel bq;
	
	int cid;
	
		public JCraiPanel(String name, char canna_id, int id, boolean tumbu, Crai crai) {
			super(new FlowLayout());
			
			
			setBorder(BorderFactory.createTitledBorder(name));
			this.c=crai;
			this.canna_id=canna_id;
			bq=new JBQPanel("Filtru D",0,c.getBQ());
			bq.setProprietario(this);
			
			cid=id;
			
			
			add (vol);
			vol.getKnob().addAdjustmentListener(this);
			
			if (tumbu) vol.setEnabled(false);
			
			add (fini);
			fini.getKnob().addAdjustmentListener(this);
								
			bq.setProprietario(this);
			add(bq);
						
			add (volA);
			volA.getKnob().addAdjustmentListener(this);
			
			add(duty);
			duty.getKnob().addAdjustmentListener(this);
			
		}
		public void sync(Crai c) {
			this.c=c;
			bq.sync(c.getBQ());
			
			vol.setValue((int)(c.vol*100));
			vol.trigger();
			
			fini.setValue((int)(12*32*Math.log(c.fini)/Math. log(2)));
			fini.trigger();
			
			volA.setValue((int)(c.volA*25));
			volA.trigger();
			
			duty.setValue((int)(c.duty*100));
			duty.trigger();
		}
		
		
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		// TODO Auto-generated method stub
		switch (e.getID()) {
		case 0:	//Volume
			c.vol=(float)e.getValue()/100; //*4	
			Electroneddas.serialPort.printCmd(canna_id+" C"+cid+" v "+c.vol+" "+c.volA);
			break;
		case 2: //VolArm		
			c.volA=(float)e.getValue()/25;
			Electroneddas.serialPort.printCmd(canna_id+" C"+cid+" v "+c.vol+" "+c.volA);
			break;
			
		case 1:	//Accordadura
			float fact=((float)e.getValue()/32);
			c.fini=(float) Math.pow(2, (fact/12));
			Electroneddas.serialPort.printCmd(canna_id+" C"+cid+" f "+c.fini);
			break;	
		
		case 3:
			c.duty=(float)e.getValue()/100;
			Electroneddas.serialPort.printCmd(canna_id+" C"+cid+" d "+c.duty);
			break;
			
		}
		this.knobChange(e.getID());
		
	}
	void knobChange(int n) {
		//System.out.println("Cambio:"+n);
	}
	

	@Override
	public void actionPerformed(ActionEvent e) {
		//Dal biquad
		Electroneddas.serialPort.printCmd(canna_id+" C"+cid+" F"+e.getActionCommand());
				
	}

}
