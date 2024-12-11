package main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import dialogs.JMonitor;
import dialogs.JRecorder;
import dialogs.JDiskRecorder;
import dialogs.SerialUSB;
import main.Cuntzertu.Canna;
import panels.JBQPanel;
import panels.JCannaPanel;
import panels.JCraiDisplay;
import panels.JCraiPanel;
import panels.JCuntzPanel;
import panels.JCuntzSetup;
import panels.JImagePanel;
import panels.JInstrPanel;
import panels.JMapperPanel;
import panels.JStracPanel;
import panels.SerialConnListener;
import panels.SerialListener;

import panels.Spartiu;
import panels.Spia;


public class Electroneddas extends JFrame implements ActionListener, SerialListener, SerialConnListener {

	/**
	 * 
	 */
	
	private static final String version="2.4.0";
	
	private static final long serialVersionUID = 1L;
	public static SerialUSB serialPort;
	public static Cuntzertu c=new Cuntzertu();
	
	public static JMonitor	monitor;
	public static JRecorder	recorder;
	

	public static Stracasciu s=new Stracasciu("Default");
	public static byte strac_num=0;
	
	static boolean syncing;

	static JFileChooser fc;

	static Spartiu spart=new Spartiu(10);
	static JDialog spartdialog;

	static JCannaPanel mancosa;
	static JCannaPanel mancosedda;
	static JCannaPanel tumbu;
	static JCuntzPanel cuntz;
	
	static JMapperPanel mapper;
	static JImagePanel schema;

	static JStracPanel stracasciu;
	static int actCuntzertu=0;

	public static JCuntzSetup setup;

	public static Preferences prefs;
	
	public static ProgressMonitor progressMonitor; 

	public Electroneddas() {
		super("Electroneddas "+version);

		setSize(1300, 600);
		//setResizable(false);
		
		Container thePane = getContentPane();
		thePane.setLayout(new BorderLayout());

		serialPort=new SerialUSB();
		
		prefs=new Preferences();
		loadPrefs();

		//// Menu de manu manca 

		JPanel tools=new JPanel(new GridLayout(6,1,10,10));
		
		if (prefs.modeAdvanced) {
			
			
			JButton b1=new JButton("Serial");
			b1.setActionCommand("Serial");
			b1.addActionListener(this);
			
			tools.add(b1);
		}
		JButton b2=new JButton("Sonada");
		b2.setActionCommand("Sonada");
		b2.addActionListener(this);
		tools.add(b2);

	
		/*
		JButton b3=new JButton("Monitor");
		b3.setActionCommand("Monitor");
		b3.addActionListener(this);
		tools.add(b3);
		*/
		recorder=new JRecorder();
		JButton b4=new JButton("Record");
		b4.setActionCommand("Record");
		b4.addActionListener(this);
		tools.add(b4);

		thePane.add(tools,BorderLayout.LINE_START);

		//// Listeners
		
		serialPort.addListener('P', this);		//Preferentzias
		serialPort.addListener('{', this);		//Cuntzertu
		serialPort.addListener('%', this);		//Stracasciu
		serialPort.addListener('*', this);		//Cambiu
		
		serialPort.addConnListener(this);
		
		//Pannellus

		JTabbedPane cannasPanel = new JTabbedPane();

		cuntz=new JCuntzPanel(c);
		mancosa=new JCannaPanel("Mancosa",'S',c.mancs);
		mancosedda=new JCannaPanel("Mancosedda",'D',c.mancd);
		tumbu=new JCannaPanel("Tumbu",'T',c.tumbu);
		
		if (!prefs.modeAdvanced) {
			enableComponent(tumbu,false);
			enableComponent(mancosa,false);
			enableComponent(mancosedda,false);
		}
		
		setup=new JCuntzSetup(c);

		
		
		mapper=new JMapperPanel();

		schema=new JImagePanel("Schema4b.png");

		

		setup.init(c);
		JPanel ctools=new JPanel();
		JPanel cuntzsub=new JPanel();
		cuntzsub.setLayout(new BoxLayout(cuntzsub,BoxLayout.PAGE_AXIS));

		cuntzsub.add(cuntz);
		cuntzsub.add(ctools);

		BufferedImage url;
		try {
			url = ImageIO.read(Electroneddas.class.getResourceAsStream("/img/disk_in.png"));
			Image newimg = url.getScaledInstance( 78, 40,  java.awt.Image.SCALE_SMOOTH ) ;
			JButton bs=new JButton(new ImageIcon(newimg));
			bs.setToolTipText("Sarva in su discu");
			bs.setActionCommand("Save");
			bs.addActionListener(this);

			url = ImageIO.read(Electroneddas.class.getResourceAsStream("/img/disk_out.png"));

			ImageIcon t = new ImageIcon(new ImageIcon(url).getImage()
					.getScaledInstance(78, 40, Image.SCALE_SMOOTH)); // Set the desired size here


			JButton bl=new JButton(t);
			bl.setToolTipText("Càrriga de su discu");
			bl.setActionCommand("Load");
			bl.addActionListener(this);

			/*url = ImageIO.read(Electroneddas.class.getResourceAsStream("/img/pc_in.png"));

			t = new ImageIcon(new ImageIcon(url).getImage()
					.getScaledInstance(64, 40, Image.SCALE_SMOOTH)); // Set the desired size here


			JButton be=new JButton(t);
			be.setToolTipText("Sincroniza");
			be.setActionCommand("Sync");
			be.addActionListener(this);
			*/
			url = ImageIO.read(Electroneddas.class.getResourceAsStream("/img/scambiu.png"));

			t = new ImageIcon(new ImageIcon(url).getImage()
					.getScaledInstance(64, 40, Image.SCALE_SMOOTH)); // Set the desired size here


			JButton sc=new JButton(t);
			sc.setToolTipText("Scàmbia su sonu de is cannas");
			sc.setActionCommand("Scambia");
			sc.addActionListener(this);

			//ctools.add(bi);
			ctools.add(bs);
			ctools.add(bl);
			//ctools.add(be);
			ctools.add(sc);
					
			

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JPanel theSubPane=new JPanel(new GridLayout(1,0));

		theSubPane.add(cuntzsub);
		
		cannasPanel.addTab("Cuntzertu", theSubPane);

		s.setCuntzertu(0, c);
		
		cannasPanel.addTab("Cuntzertu 2", setup);
		
		stracasciu=new JStracPanel(s);
		cannasPanel.addTab("Stracàsciu", stracasciu);

		if (prefs.modeAdvanced) cannasPanel.addTab("Mancosa", mancosa);
		if (prefs.modeAdvanced) cannasPanel.addTab("Mancosedda", mancosedda);
		if (prefs.modeAdvanced) cannasPanel.addTab("Tumbu", tumbu);

		//if (prefs.modeAdvanced) cannasPanel.addTab("Funtzionis",  mapper);
	

		if (prefs.modeAdvanced)cannasPanel.addTab("Schema",  schema);

		thePane.add(cannasPanel,BorderLayout.CENTER);

		fc=new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);	    
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Cuntzertu", "jso"));

		fc.setCurrentDirectory(new File(Electroneddas.prefs.cuntzDir));

		//pack();
		
/*
		//Listener de F1, F2 e F3

				JRootPane rootPane = this.getRootPane();
				rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F4"), "stop");
				rootPane.getActionMap().put("stop", new AbstractAction("stop") {
					public void actionPerformed(ActionEvent evt) {
						cuntz.jvol.mute();
					}});

				rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F1"), "stopTumbu");
				rootPane.getActionMap().put("stopTumbu", new AbstractAction("stopTumbu") {
					public void actionPerformed(ActionEvent evt) {
						cuntz.jvol.muteT();
					}});

				rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F2"), "stopMs");
				rootPane.getActionMap().put("stopMs", new AbstractAction("stopMs") {
					public void actionPerformed(ActionEvent evt) {
						cuntz.jvol.muteMs();
					}});

				rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("F3"), "stopMd");
				rootPane.getActionMap().put("stopMd", new AbstractAction("stopMd") {
					public void actionPerformed(ActionEvent evt) {
						cuntz.jvol.muteMd();
					}});

				///
*/
		//setVisible(true);

		//Dialog de su spartiu

		spartdialog=new JDialog(this,"Sonada");
		spartdialog.setResizable(false);
		monitor=new JMonitor();
		
		
		spartdialog.getContentPane().add(spart,BorderLayout.CENTER);
		spartdialog.getContentPane().add(monitor,BorderLayout.EAST);
		
		spartdialog.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {

				Electroneddas.spart.stop();
				//Electroneddas.monitor.stopCraisMonitor();
				Electroneddas.spartdialog.setVisible(false);

			}
		});
		spartdialog.setLocation(100, 550);
		spartdialog.setSize(1100,220);     
		spartdialog.setVisible(false);

		// Listener de sa bessida

		addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {  
				SerialUSB.printCmd("E w 1");
				serialPort.closeSerial();
				savePrefs();
				System.exit(0);	            
			}
		});

		// Icona

		try {
			this.setIconImage(ImageIO.read(Electroneddas.class.getResourceAsStream("/img/elettro2.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Start USB
		SerialUSB.connect();



	}

	public static void sync() {
		syncing=true;
		SerialUSB.printCmd("vc 0");
		
		tumbu.sync(c.tumbu);			
		mancosa.sync(c.mancs);		
		mancosedda.sync(c.mancd);
		setup.sync(c);
		cuntz.sync(c);
		
		syncing=false;
		
		SerialUSB.printCmd("E u"); //Update display
	}
	
	public static boolean isSyncing() {
		return syncing;
	}
	
	public static void init() {
		SerialUSB.printCmd("E m 0");
		SerialUSB.printCmd("J");
		SerialUSB.printCmd("E w 0");
	}

	public static void main(String[] args) {
		/*
		// TODO Auto-generated method stub
		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
		} 
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}*/
		
		try {
		    UIManager.setLookAndFeel( new FlatDarkLaf() );
		} catch( Exception ex ) {
		    System.err.println( "Failed to initialize LaF" );
		}
		
		
		Electroneddas el=new Electroneddas();

	}



	@Override
	public void actionPerformed(ActionEvent e) {

		switch (e.getActionCommand()) {

		case "Serial":
			serialPort.setVisible(!serialPort.isVisible());
			break;

		/*case "Monitor":
			monitor.setVisible(!monitor.isVisible());
			break;*/
		case "Record":
			recorder.setVisible(!recorder.isVisible());
			
			break;

		case "Sonada":		
			if (spartdialog.isVisible()) {
				spart.stop();
				//monitor.stopCraisMonitor();            
			} else {
				spart.start();
				//monitor.startCraisMonitor();			
			}
			spartdialog.setVisible(!spartdialog.isVisible());
			break;

		case "Scambia":
			Canna t=c.new Canna(5);
			t=c.mancd;
			c.mancd=c.mancs;
			c.mancs=t;
			sync();
			break;

		case "Load": {
			int rv=0;

			fc.setDialogTitle("Càrriga su cuntzertu");
			fc.setDialogType(JFileChooser.OPEN_DIALOG);

			rv = fc.showOpenDialog(this);

			if (rv == JFileChooser.APPROVE_OPTION) {
				int dialogResult = JOptionPane.showConfirmDialog (null, "Siguru ca bolis carrigai de unu file?","Atentzioni",JOptionPane.OK_CANCEL_OPTION);
				if(dialogResult == JOptionPane.YES_OPTION){
					String filename=fc.getSelectedFile().getAbsolutePath();
					c=(Cuntzertu)loadData(filename,c);
					sync();

					String dir=fc.getSelectedFile().getParent();
					prefs.cuntzDir=dir;			
				}	
			}

		}

		break;

		case "Save": {
			int rv=0;

			fc.setDialogTitle("Sarva su cuntzertu");
			fc.setDialogType(JFileChooser.OPEN_DIALOG);

			rv = fc.showOpenDialog(this);

			if (rv == JFileChooser.APPROVE_OPTION) {
				String filename=fc.getSelectedFile().getAbsolutePath();
				if (!filename.endsWith(".jso")) filename+=".jso";
				saveData(filename,c);

				String dir=fc.getSelectedFile().getParent();
				prefs.cuntzDir=dir;	
			}	
		}
		break;
		case "Sync": {
			SerialUSB.printCmd("J");
		}

		}
	}

	public static Object loadData(String filename, Object cosa) {
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			String line=in.readLine();
			Gson gson = new Gson();
			cosa=gson.fromJson(line, cosa.getClass());
			in.close();

		} catch (IOException i) {
			JOptionPane.showMessageDialog(null, i.getLocalizedMessage(),"Errori",JOptionPane.ERROR_MESSAGE);

			i.printStackTrace();
		}

		return cosa;

	}
	public static void saveData(String filename,Object cosa) {
		//c.nome=cuntz.getName();
		//c.descr=cuntz.getDescr();

		try {
			FileOutputStream fileOut = new FileOutputStream(filename);
			PrintWriter out = new PrintWriter(fileOut);

			Gson gson = new Gson();
			out.println(gson.toJson(cosa)); 
			out.close();

		} catch (IOException i) {
			JOptionPane.showMessageDialog(null, i.getLocalizedMessage(),"Errori",JOptionPane.ERROR_MESSAGE);
			i.printStackTrace();
		}

	}
	public void getData(String line) {
		//Gson gson = new Gson();
		Gson gson = new GsonBuilder()
				.setLenient()
				.create();

		System.out.println("JSON>"+line);
		if (actCuntzertu==0) {
			try {
				c=gson.fromJson(line, c.getClass());
			} catch (com.google.gson.JsonSyntaxException mc) {
				System.err.println("JSON malformed!");
			}
			SerialUSB.sending=false;	//nessun segnale su serial
			sync();	
			SerialUSB.sending=true;
		}
		else {
			try {
				s.setCuntzertu(actCuntzertu-1,gson.fromJson(line, c.getClass()));
			} catch (com.google.gson.JsonSyntaxException mc) {
				System.err.println("JSON malformed!");
			}

		}
		System.out.println("Pigau cuntz n."+(actCuntzertu-1));
	}
	
	public void getPreferences(String line) {
		
		Gson gson = new GsonBuilder()
				.setLenient()
				.create();

		line=line.substring(1);
		
		System.out.println("JSON>"+line);
				
			try {
				Cuntzertu.prefs=gson.fromJson(line, Cuntzertu.prefs.getClass());
			} catch (com.google.gson.JsonSyntaxException mc) {
				System.err.println("JSON malformed!");
			}
			SerialUSB.sending=false;	//nessun segnale su serial
			
			sync();	
			

			SerialUSB.sending=true;
		}
		
	
	
	public void getStracasciu(String line) {
		Gson gson = new Gson();
		//Convert from % to []
		line='['+line.substring(1, line.length()-1)+']';
		System.out.println("Files:"+line);
		
		java.lang.reflect.Type type = new TypeToken<String[]>(){}.getType();
		String[] files =  gson.fromJson(line, type );

		SwingUtilities.invokeLater(new Runnable() 
		{
			public void run()
			{
				for (int i=0;i<files.length-1;i++) {
					
					if (files[i].endsWith(".JSO")) {
						int n=Integer.parseInt(files[i].substring(0,files[i].indexOf('.')));
						if ((n>=strac_num*20)&&(n<strac_num*20+20)) {
							int nn=n-strac_num*20;
							actCuntzertu=nn+1;
							SerialUSB.printCmd("E r "+n);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							progressMonitor.setProgress(actCuntzertu);

						}
					}

				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stracasciu.refresh(s);
				monitor.sync();
				JOptionPane.showMessageDialog(null, "Pigaus "+actCuntzertu+" cuntzertus","Stracàsciu",JOptionPane.INFORMATION_MESSAGE);
				actCuntzertu=0;
				progressMonitor.close();
			}
		});


	}

	@Override
	public void action(String msg) {
		// TODO Auto-generated method stub
		if (msg.charAt(0)=='{') {
			getData(msg);
		}
		if (msg.charAt(0)=='%') {
			getStracasciu(msg);
		} 
		if (msg.charAt(0)=='*') {
			SerialUSB.printCmd("J");
		} 
		if (msg.charAt(0)=='P') {
			getPreferences(msg);
		} 
	}
	public static void savePrefs() {
		Electroneddas.saveData("preferences.ini", prefs);
	}
	public static void loadPrefs() {
		//prefs=(Preferences) Electroneddas.loadData("preferences.ini", prefs);

	}

	public class Preferences {
		public String cuntzDir=new String();
		public String stracDir=new String();
		public String funcDir=new String();
		public boolean modeAdvanced=true;

	}
	
	private static Component[] getComponents(Component container) {
        ArrayList<Component> list = null;

        try {
            list = new ArrayList<Component>(Arrays.asList(
                  ((Container) container).getComponents()));
            for (int index = 0; index < list.size(); index++) {
                for (Component currentComponent : getComponents(list.get(index))) {
                    list.add(currentComponent);
                }
            }
        } catch (ClassCastException e) {
            list = new ArrayList<Component>();
        }

        return list.toArray(new Component[list.size()]);
        }
    public static void enableComponent(Container c, boolean enable)
    {
		for(Component component : getComponents(c)) {
		    component.setEnabled(enable);
		}
    }

	@Override
	public void connected() {	
		SerialUSB.printCmd("J");
		SerialUSB.printCmd("P");
		SerialUSB.printCmd("E w 0");
		this.setVisible(true);		
	}

	@Override
	public void disconnected() {	
		this.setVisible(false);
	}
}



