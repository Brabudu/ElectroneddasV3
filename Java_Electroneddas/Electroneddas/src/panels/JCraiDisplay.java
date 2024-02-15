package panels;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class JCraiDisplay extends JPanel implements SerialListener{
	private Spia spie[]=new Spia[4];
	
	private int value;
	
	public JCraiDisplay() {
		this.setLayout(new BoxLayout(this,BoxLayout.PAGE_AXIS));
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(2,2,2,2),
				BorderFactory.createLineBorder(Color.black)
				));
		
		for (int i=0;i<4;i++) {
			spie[i]=new Spia(Color.YELLOW,Color.BLACK,8);
			this.add(spie[i]);
		}
	}
	public void update(int val) {
		value=val;
		int n=8;
		for (int i=0;i<4;i++)
		{
			if ((val&n)==0) {
				spie[i].off();
			} else {
				spie[i].on();
			}
			n/=2;
		}
	}
	public int getCrais() {
		return this.value;
	}
	@Override
	public void action(String msg) {
		// TODO Auto-generated method stub
		update(Integer.parseInt(msg.substring(2,3),16));
	}
}
