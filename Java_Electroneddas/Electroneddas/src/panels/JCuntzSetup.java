package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import dialogs.SerialUSB;
import main.Cuntzertu;
import main.Electroneddas;

public class JCuntzSetup extends JPanel implements FocusListener, ActionListener, AdjustmentListener{
	
		JComboBox<String> cuntz=new JComboBox<String>();
		JComboBox<String> crai=new JComboBox<String>();
		JComboBox<String> scalac=new JComboBox<String>();
		JComboBox<String> modal=new JComboBox<String>();	
		JComboBox<String> acc=new JComboBox<String>();
	
		JTextField nome=new JTextField(20);
		JTextArea descr=new JTextArea(5,20);
	
		boolean scala[]= {true,true,false,true,true,true,false};
				
		boolean syncing=false;
		
		
		byte crais_ms_fio[] ={9,11,12,13,14};
		byte crais_md_fio[] ={11,14,15,16,17};

		byte crais_ms_po[] ={11,12,13,14,15};
		byte crais_md_po[] ={14,15,16,17,18};

		byte crais_md_fiu[] ={16,18,19,20,21};
		
		byte crais_ms_med[] ={7,8,9,10,11};
		byte crais_md_med[] ={11,13,14,15,16};
		
		byte ms[]= new byte[5];
		byte md[]= new byte[5];
		
		byte t;
		
		Pianola piano=new Pianola(0,24,(byte)0);	
		
		JTextField crais[]=new JTextField[10];
		JTextField craisLab[]=new JTextField[10];
		
		JTextField craiT;
		JTextField craiTL;
		
		//Canna mancs=new Canna();
		//Canna mancd=new Canna();
		
		JLabel mancdLab=new JLabel("");
		JLabel mancsLab=new JLabel("");
		
		JVolPanel jvol;
		
		JCKnob fini=new JCKnob(0,-31,31,Color.yellow,"Puntu fini","S'acordadura fini de su cuntzertu",10);
		JLabel freqL= new JLabel("220");
		float freq=220;
		
		float[][] acco= {
				//Nat
				{1,1,1,1,1,1,1,1,1,1,1,1},	
				//Temp
				{1,0.993246651f,0.9977440429f,0.9910059292f,1.0079368399f,1.0011298906f,1.0056629777f,
				0.9988713846f,0.9921256575f,1.0090756983f,	0.9898874646f,1.0067992669f},	//
				//Pit
				{1,1.0011291504f,1f,0.987654321f,1.0125f,	1,1.0125f,1,1.0011291504f,
					1.0125f,0.987654321f,1.0125f},
				//Prova
				{1,1,0.9856632f,1,1,1,0.9964f,1,1,1,1,0.975f},
				};
		
		
		//Cuntzertu c;
		
		public JCuntzSetup(Cuntzertu c) {
			//this.setLayout(new BoxLayout(this,BoxLayout.LINE_AXIS));
			//this.c=c;
			
			
			this.setLayout(new GridLayout(3,0));
			JPanel cx=new JPanel();
			//cx.setBackground(Color.black);
			cx.setSize(piano.getSize());
			cx.add(piano);
			this.add(cx);
			
			cuntz.addItem("Fioràssiu");
			cuntz.addItem("Punt'e organu");		
			cuntz.addItem("Mediana");
			cuntz.addItem("Mediana a pipia");	
			cuntz.addItem("Fiuda");
			cuntz.addItem("Spinellu");
			cuntz.addItem("Spinellu a pipia");
			cuntz.addItem("Mediana farsa");
			cuntz.addItem("Sampònnia");
			cuntz.addItem("Moriscu");
			cuntz.addItem("Su para e sa mòngia");
			cuntz.addItem("Fiuddedda");
			cuntz.addItem("-");
			cuntz.addItem("-");
			cuntz.addItem("-");
			cuntz.addItem("*Personali");

			cuntz.setActionCommand("Cuntz");
			cuntz.addActionListener(this);
			
			JPanel jp1=new JPanel(new BorderLayout());
			JPanel jpl=new JPanel(new GridLayout(0,1));
			JPanel jpcb=new JPanel(new GridLayout(0,1));
			
			jpl.add(new JLabel("Cuntzertu"));
			jpcb.add (cuntz);
		
			crai.addItem( "  Do   1");
			crai.addItem( "  Do#  1");
			crai.addItem( "  Re   1");
			crai.addItem( "  Re#  1");
			crai.addItem( "  Mi   1");
			crai.addItem( "  Fa   1");
			crai.addItem( "  Fa#  1");
			crai.addItem( "  Sol  1");
			crai.addItem( "  Sol# 1");
			crai.addItem( "  La   1");
			crai.addItem( "  Sib  1");
			crai.addItem( "  Si   1");

			crai.addItem( "  Do   2");
			crai.addItem( "  Do#  2");
			crai.addItem( "  Re   2");
			crai.addItem( "  Re#  2");
			crai.addItem( "  Mi   2");
			crai.addItem( "  Fa   2");
			crai.addItem( "  Fa#  2");
			crai.addItem( "  Sol  2");

			
			crai.addActionListener(this);
			crai.setActionCommand("Crai");
			jpl.add(new JLabel("Puntu"));
			jpcb.add(crai);
			
			scalac.addItem("Diatònica");
			scalac.addItem("Armònica");
			scalac.addItem("Melòdica");
			scalac.addItem("Pentatònica");
			
			scalac.addActionListener(this);
			scalac.setActionCommand("Scala");
			jpl.add(new JLabel("Scala"));
			jpcb.add(scalac);
			scalac.setEnabled(false);
			
			modal.addItem("I");
			modal.addItem("II");
			modal.addItem("III");
			modal.addItem("IV");
			modal.addItem("V");
			modal.addItem("VI");
			modal.addItem("VII");
			
			modal.addActionListener(this);
			modal.setActionCommand("Modal");
			jpl.add(new JLabel("Modali"));
			jpcb.add(modal);
			//modal.setEnabled(false);
			
			acc.addItem("Naturali");
			acc.addItem("Temperada");
			acc.addItem("Pitagòrica");
			acc.addItem("Test");
			
			acc.addActionListener(this);
			acc.setActionCommand("Acc");
			jpl.add(new JLabel("Acordadura"));
			jpcb.add(acc);
			//acc.setEnabled(false);
			
			jpl.setBorder(new EmptyBorder(0, 5, 0, 5));
			
			jp1.add(jpl,BorderLayout.EAST);
			jp1.add(jpcb,BorderLayout.CENTER);
			jp1.setBorder(new EmptyBorder(10, 10, 10, 10));
			
			
			JPanel panMs=new JPanel();
			panMs.setLayout(new BoxLayout(panMs,BoxLayout.PAGE_AXIS));
			
			JLabel jls=new JLabel("Mancosa");
			jls.setAlignmentX(JLabel.CENTER_ALIGNMENT);	
			panMs.add(jls);
			
			JPanel ctrl1=new JPanel(new GridLayout(0,3));
				
			for (int i=4;i>=0;i--)
			{
				crais[i]=new JTextField(1);
				crais[i].addFocusListener(this);
								
				ctrl1.add(new JLabel(" "+Pianola.crais[i%5]+"  ",SwingConstants.RIGHT));
				ctrl1.add(crais[i]);
				craisLab[i]=new JTextField(5);
				craisLab[i].setEditable(false);
				
				//craisLab[i].setText(numToNota(Pianola.crais[i%5],true));
				ctrl1.add(craisLab[i]);
			}
			
						
			//ctrl1.setSize(new Dimension(50,150));
			panMs.add(ctrl1);
			
			JPanel panMd=new JPanel();
			panMd.setLayout(new BoxLayout(panMd,BoxLayout.PAGE_AXIS));
			
			
			JLabel jld=new JLabel("Mancosedda");
			jld.setAlignmentX(JLabel.CENTER_ALIGNMENT);	
			panMd.add(jld);
			
			JPanel ctrl2=new JPanel(new GridLayout(0,3));
			for (int i=9;i>=5;i--)
			{
				crais[i]=new JTextField(1);
				crais[i].addFocusListener(this);
				
				ctrl2.add(new JLabel(" "+Pianola.crais[i%5]+"  ",SwingConstants.RIGHT));
				ctrl2.add(crais[i]);
				craisLab[i]=new JTextField(5);
				craisLab[i].setEditable(false);
				//craisLab[i].setText(numToNota(Pianola.crais[i%5],true));
				ctrl2.add(craisLab[i]);
				
			}
			//ctrl2.setSize(new Dimension(50,150));
			panMd.add(ctrl2);
			
			JPanel panT=new JPanel();
			panT.setLayout(new BoxLayout(panT,BoxLayout.PAGE_AXIS));
			
			
			JLabel jlt=new JLabel("Tumbu");
			jlt.setAlignmentX(JLabel.CENTER_ALIGNMENT);	
			panT.add(jlt);
			
			JPanel ctrl3=new JPanel(new GridLayout(0,3));
			craiT=new JTextField(1);
			craiT.addFocusListener(this);
			craiT.setBackground(Color.lightGray);
			craiT.setForeground(Color.yellow);
			
			ctrl3.add(new JLabel(" T  ",SwingConstants.RIGHT));
			ctrl3.add(craiT);
			craiTL=new JTextField(5);
			craiTL.setEditable(false);
			
			
			ctrl3.add(craiTL);
						
			panT.add(ctrl3);
			
			
			JPanel jpb=new JPanel(new GridLayout(0,2));
			
			JPanel middle=new JPanel();
			middle.add(jp1);
			middle.add(panMs);
			middle.add(panMd);
			middle.add(panT);
			this.add(middle);
			JPanel bot=new JPanel(new GridLayout(3,0));
			
			//bot.add(mancs);
			//bot.add(mancd);
			//jpb.add(bot);
			
			JPanel infop=new JPanel();	
			infop.setLayout(new BoxLayout(infop,BoxLayout.Y_AXIS));
			infop.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			infop.add(new JLabel("N�mini"));
			infop.add(nome);
			infop.add(new JLabel("Descritzioni"));
			infop.add(descr);
			
			jvol=new JVolPanel(c);
			
			
			fini.getKnob().addAdjustmentListener(this);
			
			JPanel jpfini=new JPanel();
			jpfini.setLayout(new BoxLayout(jpfini,BoxLayout.PAGE_AXIS));			
			jpfini.add(fini);
			jpfini.add(freqL);
			
			JPanel volfini=new JPanel();
			
			volfini.add(jvol);
			volfini.add(jpfini);
			
			jpb.add(volfini);
			
			freqL.setAlignmentX(Component.CENTER_ALIGNMENT);
			freqL.setBorder(new EmptyBorder(0, 10, 10, 10));
			
			nome.addFocusListener(this);
			descr.addFocusListener(this);
			descr.setLineWrap(true);
			jpb.add(infop);	
			this.add(jpb);
		}
		
			
		public void init(Cuntzertu c) {
			if (c.cuntz==15) {
				for (int i=0;i<=4;i++) {
					ms[i]=c.mancs.getCrai(i).puntu;
					md[i]=c.mancd.getCrai(i).puntu;
				}
				enableCrais(true);
			} else {
				ms=getCanna(c.mod,c.cuntz,true);
				md=getCanna(c.mod,c.cuntz,false);
				enableCrais(false);
			}
			
			t=c.tumbu.getCrai(0).puntu;
			
			for (int i=0;i<=4;i++) {
				//ms[i]=c.mancs.getCrai(i).puntu;
				crais[i].setText(""+ms[i]);
				craisLab[i].setText(numToNota(ms[i],true,c.puntu));
			}			
			
			for (int i=0;i<=4;i++) {
				//md[i]=c.mancd.getCrai(i).puntu;
				crais[i+5].setText(""+md[i]);
				craisLab[i+5].setText(numToNota(md[i],true,c.puntu));
				}
			
			craiT.setText(""+t);
			craiTL.setText(numToNota(t,true,c.puntu));
			
			piano.setCannas(t,ms,md);	
			piano.setPuntu(c.puntu);
			
			//mancs.setCanna(ms);
			//mancd.setCanna(md);
			
			
		}
		
		public void aggiorna(Cuntzertu c) {
			for (int i=0;i<10;i++) {
				byte n=(byte) (Integer.parseInt(crais[i].getText()));
				if ((n<0)||(n>32)) crais[i].setText("");
				else {
					if (i>=5) c.mancd.getCrai(i-5).puntu=n; else c.mancs.getCrai(i).puntu=n;
					craisLab[i].setText(numToNota(n,true,c.puntu));
				}
			}
			
			byte n=(byte) (Integer.parseInt(craiT.getText()));
			if ((n<0)||(n>32)) craiT.setText("0");
			else c.tumbu.getCrai(0).puntu=n;
			
			piano.setCannas(t,ms,md);
			piano.setPuntu(c.puntu);
		}
		
				
		public byte [] getCanna(int modal, int cuntz, boolean mancosa) {
			byte [] canna=new byte[5];
			
			byte [] cannaIn={0,1,2,3,4};
			
			switch (cuntz) {
			case 0://Fiorassiu
				if (mancosa) cannaIn=crais_ms_fio;
				else cannaIn=crais_md_fio;
				break;
			case 1://punt'e organu
				if (mancosa) cannaIn=crais_ms_po;
				else cannaIn=crais_md_po;
				break;
			case 2://mediana
				if (mancosa) cannaIn=crais_ms_med;
				else cannaIn=crais_md_med;
				break;
			case 3://mediana a pipia
				if (mancosa) cannaIn=crais_ms_med;
				else cannaIn=crais_md_fio;
				break;
			case 4://Fiuda
				if (mancosa) cannaIn=crais_ms_med;
				else cannaIn=crais_ms_fio;
				break;
			case 5://Spinellu
				if (mancosa) cannaIn=crais_md_po;
				else cannaIn=crais_md_med;
				break;
			case 6://Spinellu a pipia
				if (mancosa) cannaIn=crais_md_po;
				else cannaIn=crais_md_fio;
				break;
			case 7://mediana farsa
				if (mancosa) cannaIn=crais_ms_po;
				else cannaIn=crais_md_med;
				break;
			case 8://samponnia
				if (mancosa) cannaIn=crais_ms_po;
				else cannaIn=crais_md_fio;
				break;
			case 9://moriscu
				if (mancosa) cannaIn=crais_md_med;
				else cannaIn=crais_ms_po;
				break;
			case 10://para
				if (mancosa) cannaIn=crais_md_po;
				else cannaIn=crais_md_po;
				break;
			case 11://fiuddedda
				if (mancosa) cannaIn=crais_md_po;
				else cannaIn=crais_md_fiu;
				break;
			default:
				
			}
			
			for (int i=0;i<=4;i++)
			{
				canna[i]=diatToCroma(cannaIn[i],modal);
			}
			
			return canna;
			
		}
				
		private byte diatToCroma(int diat, int modal) {
			byte out=0;
			//out+=crai;
			
			for (int i=0;i<diat;i++)
			{
				if (scala[(i+modal)%7]) out+=2;
				else out+=1;
			}
			//System.out.println("In: "+diat+" out: "+out);
			return out;
		}

		@Override
		public void focusGained(FocusEvent e) {
			// TODO Auto-generated method stub
			
		}
		public static String numToNota(int nota, boolean notaz, int base) {
			String notazA[]= {"C","C#/Db","D","D#/Eb","E","F","F#/Gb","G","G#/Ab","A","A#,Bb","B"};
			String notazE[]= {"DO","DO#","RE","RE#","MI","FA","FA#","SOL","SOL#","LA","SIb","SI"};
			
			nota+=base+2;
			if (notaz) return notazE[(nota+10)%12]+"-"+(int)(nota+10)/12;
			return notazA[(nota+10)%12]+"-"+(int)(nota+10)/12;
		}
		
		public void enableCrais(boolean en) {
			for (int i=0;i<10;i++)
			{
				if (en) {
					crais[i].setEditable(true);
					crais[i].setBackground(Color.lightGray);
					crais[i].setForeground(Color.yellow);
				}				
				else {
					crais[i].setEditable(false);
					crais[i].setBackground(Color.gray);
					crais[i].setForeground(Color.black);
				}
			}
			modal.setEnabled(!en);
		}
		
		public void compara(byte [] canna_in) {
			// TODO
			
		}
		
		public void sync(Cuntzertu c) {
			//this.c=c;
			
			init(c);
			cuntz.setSelectedIndex(c.cuntz);
			crai.setSelectedIndex(c.puntu);
			modal.setSelectedIndex(c.mod);
			acc.setSelectedIndex(c.num_acordadura);
			
			
			send(ms,md);
			
			nome.setText(c.nome);
			descr.setText(c.descr);
			SerialUSB.printCmd("n "+c.nome);	
			SerialUSB.printCmd("d "+c.descr);
			
			jvol.sync(c);	
			
			fini.setValue((int)(384*(Math.log(c.fini) / Math.log(2))));
			fini.trigger();	
					
		}
		public void send(byte[] ms, byte[] md) {
			for (int i=0;i<=4;i++) {
				SerialUSB.printCmd("S C"+i+" p "+ms[i]);
				SerialUSB.printCmd("D C"+i+" p "+md[i]);
			}
			SerialUSB.printCmd("T C0 p "+t);
		}
		@Override
		public void focusLost(FocusEvent e) {
			// TODO Auto-generated method stub
			aggiorna(Electroneddas.c);
			init(Electroneddas.c);
			send(ms,md);
			
			SerialUSB.printCmd("n "+nome.getText());	
			SerialUSB.printCmd("d "+descr.getText());
			Electroneddas.c.nome=nome.getText();
			Electroneddas.c.descr=descr.getText();
			
			if (!Electroneddas.isSyncing()) SerialUSB.printCmd("E u");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			switch (e.getActionCommand()) {
			case "Cuntz":
				Electroneddas.c.cuntz=(byte) cuntz.getSelectedIndex();
				SerialUSB.printCmd("c "+Electroneddas.c.cuntz+" "+Electroneddas.c.mod);					
				if (!Electroneddas.isSyncing()) {
					init(Electroneddas.c);			
					send(ms,md);
				}
				break;
				
				
			case "Modal":		
				Electroneddas.c.mod=(byte) modal.getSelectedIndex();		
				SerialUSB.printCmd("c "+Electroneddas.c.cuntz+" "+Electroneddas.c.mod);		
				if (!Electroneddas.isSyncing()) {
					init(Electroneddas.c);			
					send(ms,md);
				}
				break;
			
			case "Crai":
				Electroneddas.c.puntu=(byte) ((JComboBox<?>)e.getSource()).getSelectedIndex();		
				SerialUSB.printCmd("p "+Electroneddas.c.puntu);		//duplicato
				init(Electroneddas.c);
				break;
			case "Acc":		
				
				SerialUSB.printCmd("a "+acc.getSelectedIndex());
				
				break;
			}
			if (!Electroneddas.isSyncing()) SerialUSB.printCmd("E u");
			
		}
		
		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
			switch (e.getID()) {
			
			case 10:			
				float fact=((float)e.getValue()/32);
				Electroneddas.c.fini=(float) Math.pow(2, (fact/12));
				//System.out.println("Acordadura fini: "+cuntzd.accFini);
				freqL.setText(Float.toString(freq*Electroneddas.c.fini));
				SerialUSB.printCmd("f "+Electroneddas.c.fini);		//duplicato
			}
			if (! Electroneddas.isSyncing()) SerialUSB.printCmd("E u");
			//System.out.println("BQ"+Electroneddas.c.getBQ().freq);
		}

		
		
	}

