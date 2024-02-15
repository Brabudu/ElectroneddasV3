package panels;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import dialogs.SerialUSB;
import main.Cuntzertu.Biquad;


import main.Cuntzertu;
import main.Electroneddas;

public class JCuntzPanel extends JPanel implements ActionListener, SerialListener, AdjustmentListener {

		
		public static JRevPanel jrev;
	
		Spia clip=new Spia(Color.red,Color.green,12);
		 
		JCKnob vol=new JCKnob(50,0,100,Color.YELLOW,"Vol","Vol�mini",6);
		
		//JBQPanel out;
		
		JComboBox<String> filt=new JComboBox<String>();
		
		JTextField nome=new JTextField();
		JTextField crai=new JTextField();
		
		JSulPanel sulidu;
		Cuntzertu c;
		
		
	
		
		
		final String[] flabs= {"Pranu","Passa bàsciu","Presèntzia"};
		
		public JCuntzPanel(Cuntzertu c) {
			super ();
			this.c=c;
			setBorder(BorderFactory.createTitledBorder("Cuntzertu"));
			
			Electroneddas.serialPort.addListener('C', this);
			
			JPanel centro=new JPanel();
			centro.setLayout(new BoxLayout(centro,BoxLayout.PAGE_AXIS));
			
			JPanel nome_crai=new JPanel();
			
			nome.setEditable(false);
			nome.setFont(new Font("Arial", Font.PLAIN, 24));
			nome.setPreferredSize(new Dimension(400,30));
			nome_crai.add(nome);
			nome_crai.add(clip.getPanel("CLIP"));
			centro.add(nome_crai);
			
			//JPanel jpfini=new JPanel();
			//jpfini.setLayout(new BoxLayout(jpfini,BoxLayout.PAGE_AXIS));
			//jpfini.setBorder(BorderFactory.createTitledBorder("Puntu fini"));
			//jpfini.add(fini);
			//jpfini.add(freqL);
			//jpfini.setPreferredSize(new Dimension(200,200));
			//centro.add(jpfini);
			/*
			out=new JBQPanel("Filtru 'e bessida",0,-100,200,c.getBQ());
			out.freqs.setMinimum(350);
			out.freqs.setMaximum(425);
			out.setProprietario(this);
			centro.add(out);
			*/
			filt=new JComboBox(flabs);
			filt.addActionListener(this);
			centro.add(filt);
			
			sulidu=new JSulPanel();
			centro.add(sulidu);
						
			
								
			vol.getKnob().addAdjustmentListener(this);
			vol.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
			
			add(vol);
			
			jrev=new JRevPanel(c);
			centro.add(jrev);
			
			add(centro);
						
		    this.setMinimumSize(new Dimension(300,360));
			
		}

		public void sync(Cuntzertu c) {	
			
			sulidu.sync(c);
			
			
			//System.out.println("BQ"+c.getBQ().freq);
			//out.sync(c.getBQ());
			filt.setSelectedIndex(Cuntzertu.prefs.filterMode);
			//jvol.sync(c);		
		
			jrev.sync(c);
			nome.setText(c.nome);	
			//crai.setText(JCuntzSetup.numToNota(c.puntu, true, 0));
			
			vol.setValue((int)(Cuntzertu.prefs.vol*50));	
			vol.trigger();
			
		}
		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			//Electroneddas.serialPort.printCmd("F"+e.getActionCommand());
			SerialUSB.printCmd("Fp "+filt.getSelectedIndex());
			SerialUSB.printCmd("E u");
			
		}

		
		
		public static void refreshCuntz(Cuntzertu c) {			
			if (Electroneddas.isSyncing()) return;
			Electroneddas.setup.init(c);
			
			
		}
		
		@Override
		public void action(String msg) {
			
				if (!clip.status) {
			  		  clip.on();
			  		  abascia(0.95f);
			  		  
			  		  Timer timer = new Timer(200, new ActionListener() {
			  		        
								@Override
								public void actionPerformed(ActionEvent e) {
									// TODO Auto-generated method stub
									clip.off();
								}
			  		  });
			  		  
			  		  timer.setRepeats(false);
			  		  timer.start(); 
				}
		}
		
		void abascia(float percent) {
			vol.setValue((int)(c.vol*50*percent));
			vol.trigger();
			
		}

		@Override
		public void adjustmentValueChanged(AdjustmentEvent e) {
		
			c.vol=(float)e.getValue()/50;
			SerialUSB.printCmd("vc "+c.vol);
			SerialUSB.printCmd("E u");
			
		}
		

}		
		

