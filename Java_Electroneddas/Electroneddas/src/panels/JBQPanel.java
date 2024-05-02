package panels;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Cuntzertu.Biquad;
import main.Electroneddas;

public class JBQPanel extends JPanel implements ChangeListener, ActionListener, AdjustmentListener  {
	
	/**
	 * 
	 */
		
	JCKnob qk;
	JSlider freqs=new JSlider(200,410,200);
	JComboBox<String> tipo=new JComboBox<String>();
	JLabel value=new JLabel("0",JLabel.HORIZONTAL);
	JComboBox<String> mult=new JComboBox<String>();
		
	Biquad bq;
	int id;
	
	
	
	ActionListener proprietario;
	
	////////////
	Biquad [] bqs;		//Po predefinius
	
	JComboBox predef;
	
	public JBQPanel(String title,int id, int qstart, int qend, Biquad bq, Biquad[] pre, String[] lab) {
		this(title,id,qstart,qend, bq);
		
		bqs=pre;
		
		predef=new JComboBox();
		
		for (int i=0;i<lab.length;i++) {
			predef.addItem(lab[i]);
		}
		predef.setActionCommand("Predef");
		predef.addActionListener(this);	
		this.add(predef,BorderLayout.SOUTH);
	}
	public JBQPanel(String title, int id, Biquad bq) {
		this(title,id,-100,200, bq);
	}
	public JBQPanel(String title,int id, int qstart, int qend, Biquad bq) {
	
		super(new BorderLayout());
		
		this.bq=bq;
		this.id=id;
		
		setBorder(BorderFactory.createTitledBorder(title));	
		
		qk=new JCKnob(0,qstart,qend,Color.RED,"Q","Quality",0,false);
				
		freqs.setPaintTicks(true);
		
		tipo.addItem("L");
		tipo.addItem("B");
		tipo.addItem("H");
		tipo.addItem("N");
		tipo.addItem("l");
		tipo.addItem("h");
		
		tipo.setSelectedItem("N");
		bq.type='N';
		tipo.setActionCommand("Tipu");
		tipo.addActionListener(this);
		
		mult.addItem("0");
		mult.addItem("1");
		mult.addItem("2");
		mult.addItem("3");
		
		mult.setSelectedItem("0");
		bq.mult=0;
		mult.setActionCommand("Molt");
		mult.addActionListener(this);
		
		
		JPanel slid=new JPanel(new GridLayout(0,1));
		
		slid.add(value);
		slid.add(freqs);
		
		JPanel bott=new JPanel(new GridLayout(1,0));
		
		bott.add(tipo);
		bott.add(mult);
		
		slid.add(bott);
		
			
		add (slid,BorderLayout.CENTER);
		add (qk,BorderLayout.EAST);
		freqs.addChangeListener(this);
		
		qk.getKnob().addAdjustmentListener(this);
		
		this.setPreferredSize(new Dimension(160,80));
		
		
				
	}
	
	public String getCmdString() {
		return (" "+bq.freq+" "+bq.q+" "+(String)tipo.getSelectedItem()+" "+mult.getSelectedItem());
			
	}
	@Override
	public void setEnabled(boolean en) {
		super.setEnabled(en);
		this.tipo.setEnabled(en);
		this.qk.setEnabled(en);
		this.freqs.setEnabled(en);
		this.mult.setEnabled(en);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		bq.freq= (int) Math.pow(10, (float)((JSlider)e.getSource()).getValue()/100);
		value.setText(Integer.toString((int)bq.freq));
		if ((proprietario!=null)&&(!Electroneddas.isSyncing())) proprietario.actionPerformed(new ActionEvent(this,id,getCmdString()));
		if (predef!=null) predef.setSelectedIndex(0);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand()=="Tipu") {
			bq.type=((String)((JComboBox)e.getSource()).getSelectedItem()).charAt(0);
			if (((JComboBox<?>)e.getSource()).getSelectedIndex()!=3) {
				qk.setEnabled(true);
				freqs.setEnabled(true);
				value.setEnabled(true);
			} else {
				qk.setEnabled(false);
				freqs.setEnabled(false);
				value.setEnabled(false);
			}
			if (!Electroneddas.isSyncing()) proprietario.actionPerformed(new ActionEvent(this,id,getCmdString()));
			if (predef!=null) predef.setSelectedIndex(0);
		} else if (e.getActionCommand()=="Molt"){
			bq.mult=mult.getSelectedIndex();
			this.sync(bq);
		} else {
			int s=((JComboBox)e.getSource()).getSelectedIndex();
			if (s!=0) {
				bq.freq=bqs[s].freq;
				bq.q=bqs[s].q;
				bq.type=bqs[s].type;
				bq.mult=bqs[s].mult;
				this.sync(bq);
			}
		
		}
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		bq.q=(float) Math.pow(10, ((float)e.getValue()/200));
		if (!Electroneddas.isSyncing()) proprietario.actionPerformed(new ActionEvent(this,id,getCmdString()));
	}
	
	public void setProprietario(ActionListener p) {
		this.proprietario=p;
	}
	public JCKnob getQualityKnob() {
		return this.qk;
	}
	
	public void sync(Biquad bq) {
		this.bq=bq;
		
		qk.setValue((int) (Math.log10(bq.q)*200));
				
		freqs.setValue((int)(Math.log10(bq.freq)*100));
		tipo.setSelectedItem(""+bq.type);
		mult.setSelectedItem(""+bq.mult);
		
		proprietario.actionPerformed(new ActionEvent(this,id,getCmdString()));
		
	}
	
}
	
