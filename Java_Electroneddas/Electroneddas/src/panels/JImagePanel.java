package panels;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.Electroneddas;

public class JImagePanel extends JPanel{
	public JImagePanel(String img) {
		try {
			JLabel jl=new JLabel();
		
			BufferedImage url = ImageIO.read(JImagePanel.class.getResourceAsStream("/img/"+img));
			Image dimg = url.getScaledInstance(1000, 450,
			        Image.SCALE_SMOOTH);
			jl.setIcon(new ImageIcon(dimg));
			//jl.setPreferredSize(new Dimension(1052,745));
			add(jl);
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
