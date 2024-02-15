package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;

import main.Cuntzertu;
import main.Electroneddas;
import main.Stracasciu;
import main.Cuntzertu.Canna;
import main.Electroneddas.Preferences;

public class JStracPanel extends JPanel implements ActionListener {
	//Stracasciu strac;
	JStracRow[] jbCuntz=new JStracRow[Stracasciu.SIZE];//JButton[Stracasciu.SIZE];
	JLabel title;
	
	JRadioButton totu=new JRadioButton("Cuntzertu",true);
	JRadioButton mancs=new JRadioButton("Mancosa",false);
	JRadioButton mancd=new JRadioButton("Mancosedda",false);
	JRadioButton tumbu=new JRadioButton("Tumbu",false);
	
	//JCheckBox sul=new JCheckBox("Sulidu",false);
	//JCheckBox fbess=new JCheckBox("Filtru bess",false);
	JCheckBox struct=new JCheckBox("Strutura",true);
	JCheckBox puntuc=new JCheckBox("Puntu",true);
	JCheckBox accc=new JCheckBox("Acordadura crais",true);
	
	Color[] accColor= {
			new Color(0x550000), new Color(0x800000), new Color(0xd40000),
			new Color(0xff6600), new Color(0xffcc00), new Color(0x88aa00),
			new Color(0xc0f400), new Color(0xc8beb7), new Color(0x6f918a),
			new Color(0x5fd3bc), new Color(0x2ad4ff), new Color(0x2a7fff),
			new Color(0x5555ff), new Color(0x7f2aff), new Color(0xb380ff),
			new Color(0xe580ff), new Color(0xddafe9), new Color(0xff80b2),
			new Color(0xffaacc)
			};
	
	String[] cuntzLabel= {"Fio","PO","Med","MeP","Fiu","Spi","SpP","MeF","Sam","Mor","PaM","Fdd","1","2","3","***"};
	Color[] cuntzColor= {
			new Color(0xd40000), new Color(0xff6600), new Color(0x88aa00),
			new Color(0xc0f400), new Color(0xffdd55),new Color(0x2a7fff),
			new Color(0x2ad4ff),new Color(0x5fd3bc),new Color(0xc8beb7),
			new Color(0xe580ff),new Color(0xffaacc),new Color(0xafdde9),	
			new Color(0x0), new Color(0x0), new Color(0x0),
			
			new Color(0x6f7c91)
			};
	
	static JFileChooser sfc;
	
		public JStracPanel(Stracasciu s) {
		//strac=s;
		this.setLayout(new BorderLayout());
		JPanel cuntzs=new JPanel(new GridLayout(8,2));
		cuntzs.setBackground(Color.yellow);
		title=new JLabel(s.getName());
		add(title,BorderLayout.NORTH);
		
		for (int i=0;i<Stracasciu.SIZE;i++) {
			jbCuntz[i]=new JStracRow(i,this);
			cuntzs.add(jbCuntz[i]);
		}
		add(cuntzs,BorderLayout.CENTER);
		
		JPanel pan=new JPanel();
		pan.setLayout(new BoxLayout(pan,BoxLayout.PAGE_AXIS));
		
		JPanel puls=new JPanel(new GridLayout(0,1));
		//puls.setLayout(new BoxLayout(puls,BoxLayout.PAGE_AXIS));

		BufferedImage url;
		JButton bi=new JButton();
		JButton bo=new JButton();
		JButton bdi=new JButton();
		JButton bdo=new JButton();
		JButton bnew=new JButton("Nou");

		try {
			url = ImageIO.read(JStracPanel.class.getResourceAsStream("/img/str_out.png"));
			bi.setIcon(new ImageIcon(url));

			url = ImageIO.read(JStracPanel.class.getResourceAsStream("/img/str_in.png"));
			bo.setIcon(new ImageIcon(url));

			url = ImageIO.read(JStracPanel.class.getResourceAsStream("/img/disk_out.png"));
			bdi.setIcon(new ImageIcon(url));

			url = ImageIO.read(JStracPanel.class.getResourceAsStream("/img/disk_in.png"));
			bdo.setIcon(new ImageIcon(url));
			
			


		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bi.setText("Piga de su stracàsciu");
			bo.setText("Poni in su stracàsciu");
			bdi.setText("Piga su stracàsciu de su discu");
			bdo.setText("Poni su stracàsciu in su discu");
		} 

		bi.setToolTipText("Piga su stracàsciu de is electroneddas");
		bi.setActionCommand("Import");
		bo.setToolTipText("Poni in su stracàsciu de is electroneddas");
		bo.setActionCommand("Export");
		bdi.setToolTipText("Piga su stracàsciu de su discu");
		bdi.setActionCommand("Load");
		bdo.setToolTipText("Poni su stracàsciu in su discu");
		bdo.setActionCommand("Save");
		bnew.setToolTipText("Cuncorda unu stracàsciu nou");
		bnew.setActionCommand("New");
		bi.addActionListener(this);
		bo.addActionListener(this);
		bdi.addActionListener(this);
		bdo.addActionListener(this);
		bnew.addActionListener(this);

		puls.add(bi);
		puls.add(bo);
		puls.add(bdi);
		puls.add(bdo);
		puls.add(bnew);
		
		JPanel opt=new JPanel(new GridLayout(8,1));
		opt.setBorder(BorderFactory.createTitledBorder("Piga"));	
			
		opt.add(totu);
		opt.add(tumbu);
		opt.add(mancs);
		opt.add(mancd);
		
		ButtonGroup bt=new ButtonGroup();
		bt.add(totu);
		bt.add(tumbu);
		bt.add(mancs);
		bt.add(mancd);
		
		
		opt.add(new JLabel("Sovrascrii"));	
		//opt.add(sul);
		//opt.add(fbess);
		opt.add(struct);
		opt.add(puntuc);
		opt.add(accc);
		
		
		pan.add(opt);
		
		pan.add(puls);
		
		if (!Electroneddas.prefs.modeAdvanced) {
			Electroneddas.enableComponent(opt,false);
			
		}
		
		add(pan,BorderLayout.EAST);

		sfc=new JFileChooser();
		sfc.setAcceptAllFileFilterUsed(false);	    
		sfc.addChoosableFileFilter(new FileNameExtensionFilter("Stracasciu", "str"));
		sfc.setCurrentDirectory(new File(Electroneddas.prefs.stracDir));
		
		refresh(s);
	}
	public void refresh(Stracasciu s) {
		title.setText(s.getName());
		//int sel=s.getActual();
		for (int i=0;i<Stracasciu.SIZE;i++) {
			//jbCuntz[i].setText(i+" - "+strac.getCuntzertu(i).nome);
			jbCuntz[i].refresh(s.getCuntzertu(i));
			//if (i==sel) jbCuntz[i].setBackground(Color.red);
			//else jbCuntz[i].setBackground(Color.LIGHT_GRAY);
		}
	}

	private class JStracRow extends JPanel implements ActionListener{
		JLabel nome=new JLabel();
		JLabel desc=new JLabel();
		JLabel puntu=new JLabel("",SwingConstants.CENTER);
		JLabel cuntz=new JLabel("",SwingConstants.CENTER);
		JButton jb1a=new JButton();
		
		
		JButton jb2=new JButton();
		JButton jb3=new JButton();
		JStracPanel babbu;

		public JStracRow(int num,JStracPanel b) {
			babbu=b;
			
			this.setBorder(BorderFactory.createBevelBorder(0));
			nome.setPreferredSize(new Dimension(150,16));
			nome.setForeground(Color.white);
			//nome.setEditable(false);
			//desc.setEditable(false);
			//desc.setBorder(BorderFactory.createBevelBorder(0));
			desc.setPreferredSize(new Dimension(330,16));
			puntu.setBorder(BorderFactory.createBevelBorder(0));
			puntu.setPreferredSize(new Dimension(40,16));
			cuntz.setBorder(BorderFactory.createBevelBorder(0));
			cuntz.setPreferredSize(new Dimension(30,16));
			jb1a.addActionListener(this);
			jb1a.setActionCommand("Pia"+num);
			jb1a.setToolTipText("Piga su cuntzertu");
			
		/*	jb1t.addActionListener(this);
			jb1t.setActionCommand("Pit"+num);
			jb1ms.addActionListener(this);
			jb1ms.setActionCommand("Pis"+num);
			jb1md.addActionListener(this);
			jb1md.setActionCommand("Pid"+num);*/
			jb2.addActionListener(this);
			
			jb2.setActionCommand("Po "+num);
			jb2.setToolTipText("Poni su cuntzertu");
			
			jb3.addActionListener(this);
			jb3.setActionCommand("Del"+num);
			jb3.setToolTipText("Boga su cuntzertu");
			
			
			
			BufferedImage url;
			try {
				url = ImageIO.read(JStracPanel.class.getResourceAsStream("/img/get.png"));
			
			jb1a.setIcon(new ImageIcon(url));
			url = ImageIO.read(JStracPanel.class.getResourceAsStream("/img/put.png"));
			jb2.setIcon(new ImageIcon(url));
			url = ImageIO.read(JStracPanel.class.getResourceAsStream("/img/del.png"));
			jb3.setIcon(new ImageIcon(url));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.add(new JLabel(""+num));
			this.add(nome);
			
			this.add(puntu);
			this.add(cuntz);
			this.add(jb1a);
			/*this.add(jb1t);
			this.add(jb1ms);
			this.add(jb1md);*/
			this.add(jb2);
			this.add(jb3);
			this.add(desc);
			
			jb2.setPreferredSize(new Dimension(20,22));
			jb3.setPreferredSize(new Dimension(20,22));
			jb1a.setPreferredSize(new Dimension(20,22));
		}

		public void refresh(Cuntzertu c) {
			if (!c.isVoid()) {
				nome.setText(c.nome);
				nome.setBackground(Color.white);
				desc.setText(c.descr);
				desc.setBackground(Color.white);
				//if (c.descr!="") nome.setToolTipText(c.descr);
				puntu.setText(JCuntzSetup.numToNota(c.puntu, true, 0));
				puntu.setBackground(accColor[c.puntu]);
				cuntz.setText(cuntzLabel[c.cuntz]);
				cuntz.setBackground(cuntzColor[c.cuntz]);
			} else {
				nome.setText("");
				//nome.setToolTipText("");
				desc.setText("");
				desc.setBackground(Color.LIGHT_GRAY);
				nome.setBackground(Color.LIGHT_GRAY);
				puntu.setText("");
				puntu.setBackground(Color.LIGHT_GRAY);
				cuntz.setBackground(Color.LIGHT_GRAY);
			}

			this.jb1a.setEnabled(!c.isVoid());
			/*this.jb1t.setEnabled(!c.isVoid());
			this.jb1ms.setEnabled(!c.isVoid());
			this.jb1md.setEnabled(!c.isVoid());*/
			this.jb3.setEnabled(!c.isVoid());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			int i=Integer.parseInt(e.getActionCommand().substring(3));
			//System.out.println(e.getActionCommand()+" "+i);
			if (e.getActionCommand().substring(0,2).equals("Pi")) {
				Cuntzertu t=Electroneddas.s.pigaCuntzertu(i);
				Cuntzertu temp=Cuntzertu.clone(Electroneddas.c);
				
				if (totu.isSelected()) {						
					Electroneddas.c=t;
								
				} else
				if (tumbu.isSelected()) {							
					Electroneddas.c.tumbu=t.tumbu;
					
				} else
				if (mancs.isSelected()) {	
					Electroneddas.c.mancs=t.mancs;
					
				} else
				if (mancd.isSelected()) {	
					Electroneddas.c.mancd=t.mancd;
					
					
				}
				/*
				if (!sul.isSelected()) {
					Electroneddas.c.ssens=temp.ssens;
					Electroneddas.c.szero=temp.szero;
					Electroneddas.c.slim=temp.slim;
					System.err.println(temp.ssens+" "+temp.szero+" "+temp.slim);
					
				}
				if (!fbess.isSelected()) {
					
					Electroneddas.c.bq=temp.getBQ();	
					System.out.println(temp.getBQ().freq);
				}
				*/
				if (!puntuc.isSelected()) {
					Electroneddas.c.fini=temp.fini;
					Electroneddas.c.puntu=temp.puntu;	
				}
				if (!struct.isSelected()) {
					Electroneddas.c.cuntz=15;
					for (int j=0;j<=4;j++) {
						Electroneddas.c.mancd.getCrai(j).puntu=temp.mancd.getCrai(j).puntu;
						Electroneddas.c.mancs.getCrai(j).puntu=temp.mancs.getCrai(j).puntu;
						
					}
					Electroneddas.c.tumbu.getCrai(0).puntu=temp.tumbu.getCrai(0).puntu;
				}
				if (!accc.isSelected()) {
					
					for (int j=0;j<=4;j++) {
						Electroneddas.c.mancd.getCrai(j).fini=temp.mancd.getCrai(j).fini;
						Electroneddas.c.mancs.getCrai(j).fini=temp.mancs.getCrai(j).fini;
						
					}
					Electroneddas.c.tumbu.getCrai(0).fini=temp.tumbu.getCrai(0).fini;
				}
				
				Electroneddas.sync();
				
				((JStracRow)((Container)e.getSource()).getParent()).babbu.refresh(Electroneddas.s);
			}
			if (e.getActionCommand().substring(0,2).equals("Po")) {
				Electroneddas.s.poniCuntzertu(i, Electroneddas.c);
				((JStracRow)((Container)e.getSource()).getParent()).refresh(Electroneddas.c);
			}
			if (e.getActionCommand().substring(0,3).equals("Del")) {
				Cuntzertu n=new Cuntzertu();
				Electroneddas.s.setCuntzertu(i, n);
				((JStracRow)((Container)e.getSource()).getParent()).refresh(n);
			}
		}
	}

	private byte scioberaStrac() {
		String inputValue = JOptionPane.showInputDialog("Sciobera su stracàsciu",0); 
		if (inputValue!=null ) {
			int num=-1;
			try {
				num=Integer.parseInt(inputValue);
			} catch (NumberFormatException en) {}

			if ((num<0)||(num>100)) {
				JOptionPane.showMessageDialog(null, "Errori", "Poni unu nùmeru intra de 0 e 100", JOptionPane.ERROR_MESSAGE);
				return (-1);
			} 
			return (byte) num;
		}
		return -1;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {	
		case "New": {
			int dialogResult = JOptionPane.showConfirmDialog (null, "Siguru ca bolis sboidai su stracàsciu?","Atentzioni",JOptionPane.OK_CANCEL_OPTION);
			if(dialogResult == JOptionPane.YES_OPTION){
				Electroneddas.s=new Stracasciu("Nou");
				refresh(Electroneddas.s);
			}
		}
		break;

		case "Import": {
			byte num=scioberaStrac();
			if (num!=-1) {
				int dialogResult = JOptionPane.showConfirmDialog (null, "Siguru ca bolis carrigai su stracàsciu de su strumentu?","Atentzioni",JOptionPane.OK_CANCEL_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION){
					Electroneddas.s=new Stracasciu("El"+num);
					Electroneddas.strac_num=(byte) num;
					Electroneddas.serialPort.printCmd("E m 0");
					Electroneddas.serialPort.printCmd("E d");
				}
			}
		}	
		break;
		case "Export": {
			byte num=scioberaStrac();
			if (num!=-1) {
				int dialogResult = JOptionPane.showConfirmDialog (null, "Siguru ca bolis carrigai su stracàsciu in su strumentu?\nIn custa manera as a cancellai su chi nc'est aintru de su strumentu","Atentzioni",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
				if(dialogResult == JOptionPane.YES_OPTION){
					
					for (int i=0;i<16;i++)
					{
						if (!Electroneddas.s.getCuntzertu(i).isVoid()) {
							Electroneddas.serialPort.printCmd("E n "+(num*20+i)+".JSO");				
							Gson gson = new Gson();
							Electroneddas.serialPort.printCmd(gson.toJson(Electroneddas.s.getCuntzertu(i))+"@");	
							try {
								Thread.sleep(200);
							} catch (InterruptedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} else {
							Electroneddas.serialPort.printCmd("E x "+(num*20+i)+".JSO");
						}
					}
				}

			}
		}
		break;
		case "Load": {

			int rv=0;

			sfc.setDialogTitle("Càrriga su stracàsciu");
			sfc.setDialogType(JFileChooser.OPEN_DIALOG);

			rv = sfc.showOpenDialog(this);

			if (rv == JFileChooser.APPROVE_OPTION) {
				int dialogResult = JOptionPane.showConfirmDialog (null, "Siguru ca bolis carrigai de unu file?","Atentzioni",JOptionPane.OK_CANCEL_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION){
					String filename=sfc.getSelectedFile().getAbsolutePath();
					Electroneddas.s=(Stracasciu)Electroneddas.loadData(filename,Electroneddas.s);
					refresh(Electroneddas.s);
					String dir=filename.substring(0, filename.lastIndexOf('\\'));
					Electroneddas.prefs.stracDir=dir;	
				}	
			}

		}
		break;
		case "Save": {
			int rv=0;

			sfc.setDialogTitle("Sarva su stracàsciu");
			sfc.setDialogType(JFileChooser.OPEN_DIALOG);

			rv = sfc.showOpenDialog(this);

			if (rv == JFileChooser.APPROVE_OPTION) {
				String filename=sfc.getSelectedFile().getAbsolutePath();
				if (!filename.endsWith(".str")) filename+=".str";
				Electroneddas.saveData(filename,Electroneddas.s);
				String dir=filename.substring(0, filename.lastIndexOf('\\'));
				Electroneddas.prefs.stracDir=dir;	
			}	
		}
		break;


		}
	}
}
