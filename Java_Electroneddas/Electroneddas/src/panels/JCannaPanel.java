package panels;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;

import main.Cuntzertu.Canna;
import main.Electroneddas;


public class JCannaPanel extends JPanel implements AdjustmentListener, ActionListener {

	final static String [] crais_label= {"Arrefinu","4a crai","3a crai","2a crai","1a crai"};
	
	JCKnob volArm=new JCKnob(10,0,100,Color.ORANGE,"Vol BQS","Volùmini de su filtru Biquad stàticu",2);
	JCKnob strobbu=new JCKnob(1,0,5,Color.YELLOW,"Strobbu","Strobbu de su puntu",3);
	JCKnob port=new JCKnob(1,0,5,Color.orange,"Portamentu","Portamentu",4);
	
	JCKnob timbru=new JCKnob(0,-10,10,Color.PINK,"Timbru","Timbru",5);
	
	JCKnob obDuty=new JCKnob(0,0,10,Color.RED,"Obertura duty","Fatori de obertura",6);
	JCKnob obVol=new JCKnob(0,0,10,Color.RED,"Obertura vol","Fatori de obertura",7);
	
	JComboBox sonu=new JComboBox();
		
	
	JBQPanel sbq;
	JBQPanel dbq;

	JCraiPanel[] crais;
		
	Canna canna;
	
	static JFileChooser fc;
	char canna_id;
	
	
	
	String cname;
	
		public JCannaPanel(String name,char canna_id, Canna canna) {
			super(new FlowLayout());
			
			this.canna_id=canna_id;
			cname=name;
			this.canna=canna;
			
			setBorder(BorderFactory.createTitledBorder(name));		
			this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
			
			//Filtri
			JPanel jPFilters=new JPanel();
		
			sbq=new JBQPanel("Filtru stàticu",0,canna.getSBQ());
			sbq.setProprietario(this);			
			jPFilters.add(sbq);
			
			dbq=new JBQPanel("Filtru dinàmicu",1,canna.getDBQ());
			dbq.setProprietario(this);
			jPFilters.add(dbq);
			
			if (canna.getNCrais()==1) {		//Tumbu
				dbq.setVisible(false);
				
			}
			
			/////
			
			JPanel jc1=new JPanel();
						
			jc1.add (strobbu);
			strobbu.getKnob().addAdjustmentListener(this);
			
			if (canna.getNCrais()!=1) {
				jc1.add (port);
				port.getKnob().addAdjustmentListener(this);
				
				jc1.add (obDuty);
				obDuty.getKnob().addAdjustmentListener(this);
				jc1.add (obVol);
				obVol.getKnob().addAdjustmentListener(this);
				
				jc1.add (timbru);
				timbru.getKnob().addAdjustmentListener(this);	
			
			}
			
			
			sonu.addItem( "P0");
			sonu.addItem( "P1");
			sonu.addItem( "P2");
			sonu.addItem( "P3");
			sonu.addItem( "C0");
			sonu.addItem( "C1");
			sonu.addItem( "C2");
			
			sonu.setPreferredSize(new Dimension(40,16));
			
			sonu.setActionCommand("Sonu");
			sonu.addActionListener(this);
			jc1.add (sonu);	
			
			jc1.add (volArm);
			volArm.getKnob().addAdjustmentListener(this);
			
			
			
			if (canna.getNCrais()==1) volArm.setEnabled(false);
			
			JPanel jCrais=new JPanel(new GridLayout(3,2));
			
			
			int ncrais=canna.getNCrais();
			crais=new JCraiPanel[ncrais];
			
			for (int i=0;i<ncrais;i++) {
				String lab;
				if (ncrais==1) lab="";	//Tumbu
				else lab=crais_label[i];
				jCrais.add(crais[i]=new JCraiPanel(lab,canna_id,i,ncrais==1,canna.getCrai(i)));
			}
					
			jCrais.add(jPFilters);
		
			
								
			JPanel jc3=new JPanel();
			jc3.setLayout(new BoxLayout(jc3,BoxLayout.PAGE_AXIS));
			JButton bs=new JButton("Save");
			bs.setActionCommand("Save");
			bs.addActionListener(this);
			
			JButton bl=new JButton("Load");
			bl.setActionCommand("Load");
			bl.addActionListener(this);
			
			jc3.add(bs);
			jc3.add(bl);
			
			jc1.add(jc3);
			add(jc1);
			add(jCrais);
			
			fc=new JFileChooser();
		    fc.setAcceptAllFileFilterUsed(false);	    
		    fc.addChoosableFileFilter(new FileNameExtensionFilter("Canna", "can"));
			
					
		}
		public void sync(Canna canna) {
			this.canna=canna;
			sbq.sync(canna.getSBQ());
			dbq.sync(canna.getDBQ());
			
			volArm.setValue((int)(canna.volArm*50));
			volArm.trigger();
				
			strobbu.setValue(canna.strb);
			port.setValue(canna.port);
			strobbu.trigger();
			
			if (canna.getNCrais()!=1) {
				timbru.setValue(canna.timbru);
				timbru.trigger();
				obDuty.setValue((int)((1-canna.obFactDuty)*20));			
				obVol.setValue((int)((canna.obFactVol-1)*20));
				obDuty.trigger();
			}
			sonu.setSelectedIndex(canna.sonu);
				
			for (int i=0;i<canna.getNCrais();i++) {
				crais[i].sync(canna.getCrai(i));
			}
									
						
		}
		
	public void disable() {
	    
	}
		
		
	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		// TODO Auto-generated method stub
		switch (e.getID()) {
		
		case 2:	//Volume Biquad1
			canna.volArm=(float)e.getValue()/50;
			Electroneddas.serialPort.printCmd(canna_id+" v "+canna.volArm);
			break;
		
		case 3:	//Strobbu
			canna.strb=(byte)e.getValue();
			Electroneddas.serialPort.printCmd(canna_id+" s "+canna.strb+" "+canna.port);
			break;
		case 4: // Portamentu
			canna.port=(byte)e.getValue();
			Electroneddas.serialPort.printCmd(canna_id+" s "+canna.strb+" "+canna.port);
			break;
		case 5:
			canna.timbru=(byte)e.getValue();
			Electroneddas.serialPort.printCmd(canna_id+" t "+canna.timbru);
			break;
		
		case 6:
			canna.obFactDuty=1-(float)e.getValue()/20;	//1-0.5			
			Electroneddas.serialPort.printCmd(canna_id+" o "+canna.obFactDuty+" "+canna.obFactVol);
			break;
		case 7:
			canna.obFactVol=1+(float)e.getValue()/20; //1-1.5
			Electroneddas.serialPort.printCmd(canna_id+" o "+canna.obFactDuty+" "+canna.obFactVol);
			break;
		}
		this.knobChange(e.getID());
		
		
	}
	void knobChange(int n) {
		//System.out.println("Cambio:"+n);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		int rv=0;
		
		switch (e.getActionCommand()) {
			case "Sonu":
				canna.sonu=(byte)((JComboBox)e.getSource()).getSelectedIndex();
				Electroneddas.serialPort.printCmd(canna_id+" c "+canna.sonu);
					
			break;
			
			case "Save":
				
				fc.setDialogTitle("Sarva sa "+this.cname);
				fc.setDialogType(JFileChooser.SAVE_DIALOG);
				rv = fc.showOpenDialog(this);
				
		        if (rv == JFileChooser.APPROVE_OPTION) {
		        	saveData(fc.getSelectedFile().getAbsolutePath());
		        	
		        }
				break;
			case "Load":
			fc.setDialogTitle("C�rriga sa "+this.cname);
			fc.setDialogType(JFileChooser.OPEN_DIALOG);
					
			rv = fc.showOpenDialog(this);

			if (rv == JFileChooser.APPROVE_OPTION) {		           
			    loadData(fc.getSelectedFile().getAbsolutePath());
			}
				
				break;
				
							
			default:	
				if (e.getID()==0) {
					Electroneddas.serialPort.printCmd(canna_id+" F"+e.getActionCommand());
				}
				if (e.getID()==1) {
					Electroneddas.serialPort.printCmd(canna_id+" D"+e.getActionCommand());
				}
									
			}
		
	}
	
	public void loadData(String filename) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line=in.readLine();
			Gson gson = new Gson();

			canna=gson.fromJson(line, canna.getClass());
			in.close();

		} catch (IOException i) {
			i.printStackTrace();
		}

		sync(canna);	
	}
	public void saveData(String filename) {
		
		try {

			FileOutputStream fileOut = new FileOutputStream(filename);
			PrintWriter out = new PrintWriter(fileOut);


			Gson gson = new Gson();
			out.println(gson.toJson(canna)); 
			out.close();

		} catch (IOException i) {
			i.printStackTrace();
		}


	}
	/*
	public String getJSON() {
		Gson gson = new Gson();
	    return gson.toJson(this.cd); 
	    
	}
	public void setJSON(String json) {
		Gson gson = new Gson(); 
	    cd=gson.fromJson(json, cd.getClass());
	    
	}
	*/
	

}
