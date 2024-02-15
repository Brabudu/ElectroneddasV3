package panels;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class JInstrPanel extends JPanel {
	String comandus1= "<html><P STYLE=\"margin-bottom: 0cm\">       n [nome]                    \r\n"
			+ "		//Nomini de su cuncertu (max 32)</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       d [descrizione]             \r\n"
			+ "		//Descritzioni de su cuncertu (max 64)</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       p [nota]                    \r\n"
			+ "		//Puntu (0-20)</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       f [acc. fine]               		\r\n"
			+ "//Acordadura fini (0.96-1.06)</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       c [cuntz] [modal]           		\r\n"
			+ "//</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       vc [volume]                 \r\n"
			+ "		//0-2</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       vt [volume] [bilanciamento]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       vs [volume] [bilanciamento]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       vd [volume] [bilanciamento]</P>\r\n"
			//+ "<P STYLE=\"margin-bottom: 0cm\">       \r\n"
			//+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       s [lim] [span] [zero]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\"><b>       F [BIQUAD]</b></P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       \r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       T [CANNA]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       S [CANNA]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       D [CANNA]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\"><BR>\r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       J //Serialize USB</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       B //Serialize Bluetooth</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\"><BR>\r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       E [ELECTRONEDDAS] \r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">         g [mode] [param]          \r\n"
			+ "		//Gate mode 0=Nudda 1=Crais 2=Sulidu</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">         m [mode]                  		\r\n"
			+ "//Monitor mode (FLAG: CRAIS 1- ESA   2-SUL   4- BT    8 - CLIP  16)</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">         s [file num]              \r\n"
			+ "		//Sarva \r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">         n [nome]                  \r\n"
			+ "		//Crea file [nome] e salva da console fino all'inserimento di #</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">         l [file num]              \r\n"
			+ "		//Carriga file</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">         r [file num]              		\r\n"
			+ "//Manda file in sa console</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">         d                         \r\n"
			+ "		// Lista files (array)</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">         D                         \r\n"
			+ "		// Lista files</P>\r\n"
			
			
			+ "</html>\r\n";
	String comandus2="<html>"
			+ "<P STYLE=\"margin-bottom: 0cm\">         x [file num]              		\r\n"
			+ "// Cancella file</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">         h                         \r\n"
			+ "		//Reset controller</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">         c                         		\r\n"
			+ "//Reset cuntzertu \r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\"><BR>\r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       CANNA:</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\"><BR>\r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       v [volArm]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       c [sonu]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       s [strobbu] [portamentu]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       p [crai]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       t [timbru]   //-10 - +10</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       Cx [CRAI]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       F [BIQUAD stat] \r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       D [BIQUAD dinF] \r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       o [obfactor duty] [obfactor vol]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\"><BR>\r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       CRAI:</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\"><BR>\r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       v [vol] [volArm]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       d [duty]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       p [nota] \r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       f [acc. fine]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       F [BIQUAD]</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\"><BR>\r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       BIQUAD:</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\"><BR>\r\n"
			+ "</P>\r\n"
			+ "<P STYLE=\"margin-bottom: 0cm\">       [freq] [q] [type]</P>\n";

	
	public JInstrPanel() {
		JLabel l1 = new JLabel(comandus1);
		JLabel l2 = new JLabel(comandus2);/* {
            public Dimension getPreferredSize() {
                return new Dimension(200, 200);
            }
            public Dimension getMinimumSize() {
                return new Dimension(200, 200);
            }
            public Dimension getMaximumSize() {
                return new Dimension(200, 200);
            }
        };*/
		setLayout(new GridLayout(1,2));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(l1);
        add(l2);
	}
	
}
