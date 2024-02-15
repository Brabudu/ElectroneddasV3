package panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import main.Electroneddas;
import main.Stracasciu;

public class JMapperPanel extends JPanel implements ChangeListener, MouseListener, ActionListener, SerialListener {

	private JSlider[] mapper = new JSlider[100];

	private Function function = new Function();

	boolean dinamic = true;

	static JFileChooser sfc;

	private final int MINIMUM = 11;
	private final int MAXIMUM = 110;

	int olds = MINIMUM;

	private SpinnerNumberModel snw = new SpinnerNumberModel(2, 0, 8, 1);
	private SpinnerNumberModel snmz = new SpinnerNumberModel(50, 10, 90, 1);
	private SpinnerNumberModel snms = new SpinnerNumberModel(50, 10, 90, 1);

	private JSpinner width = new JSpinner(snw);
	private JSpinner zero = new JSpinner(snmz);
	private JSpinner span = new JSpinner(snms);

	private SpinnerNumberModel snnum = new SpinnerNumberModel(0, 0, 9, 1);
	private JSpinner num = new JSpinner(snnum);

	private JTextField desc = new JTextField();
	
	public JMapperPanel() {

		setLayout(new BorderLayout());

		JPanel tools = new JPanel();
		JPanel map = new JPanel();

		map.setLayout(new GridLayout(1, 100));

		tools.add(new JLabel("Width"));
		tools.add(width);

		tools.add(new JLabel("Zero"));
		tools.add(zero);

		tools.add(new JLabel("Span"));
		tools.add(span);

		JButton reset = new JButton("Flat");
		reset.setActionCommand("Flat");
		reset.addActionListener(this);
		tools.add(reset);

		JButton linear = new JButton("Linear");
		linear.setActionCommand("Linear");
		linear.addActionListener(this);
		tools.add(linear);

		JButton sigmoid = new JButton("Sigmoid");
		sigmoid.setActionCommand("Sigmoid");
		sigmoid.addActionListener(this);
		tools.add(sigmoid);

		for (int i = 0; i < mapper.length; i++) {
			mapper[i] = new JSlider(JSlider.VERTICAL, -100, 100, 0);
			mapper[i].setName("" + i);
			mapper[i].addChangeListener(this);
			mapper[i].addMouseListener(this);
			mapper[i].setBackground(Color.LIGHT_GRAY);
			map.add(mapper[i]);
		}

		// mapper[mapper.length-1].setMajorTickSpacing(10);
		// mapper[mapper.length-1].setPaintTicks(true);
		// mapper[mapper.length-1].setPaintLabels(true);

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel puls = new JPanel(new GridLayout(4, 1));
		// puls.setLayout(new BoxLayout(puls,BoxLayout.PAGE_AXIS));

		BufferedImage url;
		JButton bi = new JButton();
		JButton bo = new JButton();
		JButton bdi = new JButton();
		JButton bdo = new JButton();

		try {
			url = ImageIO.read(Electroneddas.class.getResourceAsStream("/img/str_out.png"));
			bi.setIcon(new ImageIcon(url));

			url = ImageIO.read(Electroneddas.class.getResourceAsStream("/img/str_in.png"));
			bo.setIcon(new ImageIcon(url));

			url = ImageIO.read(Electroneddas.class.getResourceAsStream("/img/disk_out.png"));
			bdi.setIcon(new ImageIcon(url));

			url = ImageIO.read(Electroneddas.class.getResourceAsStream("/img/disk_in.png"));
			bdo.setIcon(new ImageIcon(url));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			bi.setText("Piga de is electroneddas");
			bo.setText("Poni in is electroneddas");
			bdi.setText("Piga de su discu");
			bdo.setText("Poni in su discu");
		}

		bi.setToolTipText("Piga de is electroneddas");
		bi.setActionCommand("Import");
		bo.setToolTipText("Poni in is electroneddas");
		bo.setActionCommand("Export");
		bdi.setToolTipText("Piga de su discu");
		bdi.setActionCommand("Load");
		bdo.setToolTipText("Poni in su discu");
		bdo.setActionCommand("Save");

		bi.addActionListener(this);
		bo.addActionListener(this);
		bdi.addActionListener(this);
		bdo.addActionListener(this);

		JPanel fData = new JPanel();
		fData.setLayout(new BoxLayout(fData, BoxLayout.PAGE_AXIS));

		fData.add(new JLabel("Numero"));
		fData.add(num);

		fData.add(new JLabel("Descrizione"));
		fData.add(desc);

		puls.add(bi);
		puls.add(bo);
		puls.add(bdi);
		puls.add(bdo);

		fData.add(puls);

		add(tools, BorderLayout.NORTH);
		add(map, BorderLayout.CENTER);
		add(fData, BorderLayout.EAST);

		Electroneddas.serialPort.addListener('s', this);
		Electroneddas.serialPort.addListener('!', this);

		sfc = new JFileChooser();
		sfc.setAcceptAllFileFilterUsed(false);
		sfc.addChoosableFileFilter(new FileNameExtensionFilter("Funtzioni", "fun"));
		if (Electroneddas.prefs.funcDir != null)
			sfc.setCurrentDirectory(new File(Electroneddas.prefs.funcDir));

	}

	public void setFunction(float[] function) {
		this.function = new Function();
		this.function.setFunction(function);
		updateMapper();
	}

	public float convertToFloat(int val) {
		float v= val / 100f;
		
		return v;
	}

	public int convertFromFloat(float val) {
		return (int) (val * 100);
	}

	private void updateMapper() {
		dinamic = false;
		for (int i = 0; i < mapper.length; i++) {
			mapper[i].setValue(convertFromFloat(function.getFunctionValue(i)));
			mapper[i].setToolTipText(""+mapper[i].getValue());

		}
		dinamic = true;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		if (dinamic) {
			JSlider js = (JSlider) e.getSource();
			int num = Integer.parseInt(js.getName());

			js.setToolTipText(""+js.getValue());
			
			double delta = function.getFunctionValue(num);
			function.setFunctionValue(num, convertToFloat(js.getValue()));
			delta = function.getFunctionValue(num) - delta;
			
			
			dinamic = false;
			int w = (int) width.getValue();
			if (w > 0) {
				for (int i = -w * 4; i < w * 4 + 1; i++) {
					if ((((num + i) >= 0) && ((num + i) < 100)) && (i != 0)) {
						double delta2 = 1 / Math.cosh((double)i / w);
						float v = function.getFunctionValue(num + i);
						v = (float) (v + delta2 * delta);
						function.setFunctionValue(num + i, v);
						mapper[num + i].setValue(convertFromFloat(v));
						mapper[num+ i].setToolTipText(""+mapper[num+i].getValue());
					}
				}
			}

			dinamic = true;
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		switch (e.getActionCommand()) {
		case "Flat":

			for (int i = 0; i < mapper.length; i++) {
				function.setFunctionValue(i, 0);
			}
			updateMapper();

			break;
		case "Linear":

			for (int i = 0; i < mapper.length; i++) {
				function.setFunctionValue(i, -1 + (2 * (float) i) / 99f);

			}
			updateMapper();
			break;
		case "Sigmoid":

			for (int i = 0; i < mapper.length; i++) {
				function.setFunctionValue(i,
						(float) Math.tanh((double) (i - (int) zero.getValue()) / (int) span.getValue()));
			}
			updateMapper();

			break;
		case "Load": {

			int rv = 0;

			sfc.setDialogTitle("Càrriga sa funtzioni");
			sfc.setDialogType(JFileChooser.OPEN_DIALOG);

			rv = sfc.showOpenDialog(this);

			if (rv == JFileChooser.APPROVE_OPTION) {
				int dialogResult = JOptionPane.showConfirmDialog(null, "Siguru ca bolis carrigai de unu file?",
						"Atentzioni", JOptionPane.OK_CANCEL_OPTION);
				if (dialogResult == JOptionPane.YES_OPTION) {
					String filename = sfc.getSelectedFile().getAbsolutePath();
					function = (Function) Electroneddas.loadData(filename, function);
					updateMapper();
					String dir = filename.substring(0, filename.lastIndexOf('\\'));
					Electroneddas.prefs.funcDir = dir;
				}
			}

		}
			break;
		case "Save": {
			int rv = 0;

			sfc.setDialogTitle("Sarva sa funtzioni");
			sfc.setDialogType(JFileChooser.OPEN_DIALOG);

			rv = sfc.showOpenDialog(this);

			if (rv == JFileChooser.APPROVE_OPTION) {
				String filename = sfc.getSelectedFile().getAbsolutePath();
				if (!filename.endsWith(".fun"))
					filename += ".fun";
				Electroneddas.saveData(filename, function);
				String dir = filename.substring(0, filename.lastIndexOf('\\'));
				Electroneddas.prefs.funcDir = dir;
			}
		}
			break;

		case "Export": {
			/*
			 * Electroneddas.serialPort.printCmd("E n "+num.getValue()+".fun"); Gson gson =
			 * new Gson(); Electroneddas.serialPort.printCmd(gson.toJson(function)+"@");
			 */
			Gson gson = new Gson();
			Electroneddas.serialPort.printCmd("Z"+num.getValue()+" " + gson.toJson(function));
		}
			break;

		case "Import": {
			Electroneddas.serialPort.printCmd("E f " + num.getValue() + ".fun");
		}
			break;

		}

	}

	@Override
	public void action(String msg) {
		// TODO Auto-generated method stub

		switch (msg.charAt(0)) {
		case 's': {

			int s = Integer.parseInt(msg.substring(1, 3).trim());

			if (s < MINIMUM)
				s = MINIMUM;
			if (s > MAXIMUM)
				s = MAXIMUM;

			mapper[olds - MINIMUM].setBackground(Color.LIGHT_GRAY);
			mapper[s - MINIMUM].setBackground(Color.red);

			olds = s;

		}
			break;
		case '!': {
			Gson gson = new GsonBuilder().setLenient().create();
			try {
				function = gson.fromJson(msg.substring(1), function.getClass());
			} catch (com.google.gson.JsonSyntaxException mc) {
				System.err.println("JSON malformed!");
			}
			updateMapper();
		}
			break;

		}

	}

	class Function {
		private float[] function = new float[100];
		private String name = "";
		private String description = "";

		protected float[] getFunction() {
			return function;
		}

		protected void setFunction(float[] f) {
			function = f;
		}

		protected float getFunctionValue(int i) {
			return function[i];
		}

		protected void setFunctionValue(int i, float val) {
			if (val>1) val=1;
			if (val<-1) val=-1;
			function[i] = val;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		JSlider js = (JSlider) e.getSource();
		int num = Integer.parseInt(js.getName());
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
