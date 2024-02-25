package panels;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dialogs.SerialUSB;
import main.Cuntzertu;
import main.Electroneddas;

public class JSulPanel extends JPanel implements ActionListener {
	
	JComboBox<String> sens=new JComboBox<String>();
	JComboBox<String> zero=new JComboBox<String>();
	JComboBox<String> gate=new JComboBox<String>();
	
	
	public JSulPanel() {
		this.setLayout(new GridLayout(4,2));
		this.setBorder(BorderFactory.createTitledBorder("Sulidu"));
		
		add(new JLabel("Sensibilidadi"));
		sens.addItem("A");
		sens.addItem("M");
		sens.addItem("B");
		sens.addActionListener(this);
		add(sens);
		add(new JLabel("Pressioni"));
		zero.addItem("0");
		zero.addItem("1");
		zero.addItem("2");
		zero.addItem("3");
		zero.addItem("4");
		zero.addItem("5");
		zero.addActionListener(this);
		add(zero);
		add(new JLabel("Gate"));
		gate.addItem("No");
		gate.addItem("Crais");
		gate.addItem("Sulidu");
		gate.addItem("Allui e Studa");
		gate.addItem("Crais & sulidu");
		gate.addActionListener(this);
		add(gate);
	}
	
	void sync(Cuntzertu c) {
		
		//TODO
		zero.setSelectedIndex(Cuntzertu.prefs.sprogZ);
		sens.setSelectedIndex(Cuntzertu.prefs.sprogS);
		gate.setSelectedIndex(Cuntzertu.prefs.gateMode);
		
		
	}
	
	void setSul(int sens, int zero) {
		SerialUSB.printCmd("z "+" "+sens+" "+zero);
		Electroneddas.c.ssens=(byte)sens;
		Electroneddas.c.szero=(byte)zero;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource()==(JComboBox<String>)gate) {
			SerialUSB.printCmd("E g "+gate.getSelectedIndex());
		} else if (!Electroneddas.isSyncing()) {
			
			setSul(sens.getSelectedIndex(),zero.getSelectedIndex());
		}
		if (!Electroneddas.isSyncing()) SerialUSB.printCmd("E u");
	}
	
}
