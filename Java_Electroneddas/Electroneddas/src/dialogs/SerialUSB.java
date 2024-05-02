package dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

import main.Electroneddas;
import panels.SerialConnListener;
import panels.SerialListener;
import panels.Spia;

public class SerialUSB extends JDialog implements ActionListener {

	static SerialPort comPort;
	String serName="";
	
	static int attempts=0;

	public static boolean sending=true;
	public static boolean connected=false;

	public static String data;

	static Hashtable<Character, SerialListener> serListeners=new Hashtable<Character, SerialListener>();
	static Stack<SerialConnListener> serConnListeners=new Stack<SerialConnListener>();
	
	private final static int jta_max_len=35000;

	static JTextArea jta=new JTextArea(20,40);
	static JCheckBox wcb=new JCheckBox();
	JTextField	jtf=new JTextField(20);
	
	final static JDialog loading = new JDialog();
	
	static JTextArea jtad=new JTextArea();

	public SerialUSB() {
		
		this.setTitle("Serial");
		this.setResizable(false);

		JPanel jpan=new JPanel(new BorderLayout());
			
		JScrollPane scroll1 = new JScrollPane ( jta );
		scroll1.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

		jta.setBackground(Color.black);
		jta.setForeground(Color.green);

		JPanel nord=new JPanel();
		JButton b1=new JButton("Cls");
		b1.addActionListener(this);
		b1.setActionCommand("cls");
		
		wcb.setSelected(true);
		
		nord.add(new JLabel("Filter"));
		nord.add(wcb);
		nord.add(jtf);
		nord.add(b1);

		jpan.add(nord,BorderLayout.NORTH);

		jpan.add(scroll1,BorderLayout.CENTER);

		jtf.setActionCommand("send");
		jtf.addActionListener(this);

		this.getContentPane().add(jpan);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setLocation(1300, 0);
		setSize(500, 500);
		this.setVisible(false);	
				
	      JPanel p1 = new JPanel(new BorderLayout());
	      p1.add(new JLabel("Abetendi is Electroneddas ..."), BorderLayout.NORTH);
	      p1.add(jtad, BorderLayout.CENTER);
	      //loading.setUndecorated(true);
	      loading.getContentPane().add(p1);
	      loading.setSize(200, 100);
	      loading.setLocationByPlatform(true);
	      loading.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	      loading.setModal(true);
	    

	}

	private static void out(String s) {
		String c=jta.getText()+s;//+"\n";
		if (c.length()>jta_max_len) c=c.substring(c.length()-jta_max_len, c.length());
		jta.setText(c);
		jta.setCaretPosition(jta.getDocument().getLength());
	}

	public void closeSerial() {
		if ((comPort!=null)&&comPort.isOpen()) {
			comPort.closePort();
			
		}
	}

	public void addListener(char preamble, SerialListener l) {
		serListeners.put(preamble, l);
	}
	
	public void addConnListener(SerialConnListener l) {
		serConnListeners.push(l);
	}
	
	
	public static void printCmd(String data)  {
		if (sending) {
			if ((comPort!=null)&&comPort.isOpen()) {
				out(">"+data+"\n");
				comPort.writeBytes((data+" \n").getBytes(),(data+" \n").getBytes().length);				
			} else {					
				out("NS! "+data+"\n");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "send":
			printCmd(jtf.getText());	
			
			jtf.setText("");
			break;
		case "cls":
			jta.setText("");		
			break;
		case "serial":
			
			break;
		}
	}
	
	public static void timer() {
		TimerTask task = new TimerTask() {
	          public void run() {
	             System.out.println("Connecting ...");
	             jtad.setText("");
	             connect();
	          }
	      };

	      Timer timer = new Timer("Timer");
	      long delay = 3000L;
	      timer.schedule(task, delay);
	      loading.setVisible(true);
	      

	}
	public static void connect() {
		int num=-1;
		SerialPort[] comPorts = SerialPort.getCommPorts();

		for (int i=0;i<comPorts.length;i++)
		{
			if (comPorts[i].getDescriptivePortName().contains("Teensy")) num=i;
			else jtad.append("Agatada : "+comPorts[i].getDescriptivePortName());
		}

		if (num==-1) {
			System.err.println("No Teensy");
			connected=false;
			attempts++;
			if (attempts>4) System.exit(0);
			timer();
			return;
		}	
		comPort=comPorts[num];
		comPort.openPort();
		connected=true;
		attempts=0;
		
		//seriale.on();
		loading.setVisible(false);
		
		comPort.addDataListener(new SerialPortMessageListener() {

		   @Override
		   public int getListeningEvents() { return SerialPort.LISTENING_EVENT_PORT_DISCONNECTED|SerialPort.LISTENING_EVENT_DATA_RECEIVED; }
		   
		   @Override
		   public byte[] getMessageDelimiter() { return new byte[] { (byte)0x0A };}//, (byte)0x0D }; }
		   
		   @Override 
		   public boolean delimiterIndicatesEndOfMessage() { return true; }
		   
		   @Override
		   public void serialEvent(SerialPortEvent event)
		   {
			   
			   switch (event.getEventType()) {
			   case SerialPort.LISTENING_EVENT_PORT_DISCONNECTED:
				   System.err.println("Disconnected");
				   connected=false;
				   //seriale.off();
				   comPort.closePort();
				   
				   Iterator<SerialConnListener> value = serConnListeners.iterator(); 
			        while (value.hasNext()) { 
			            value.next().disconnected();
			        } 
			        
				   timer();	
				   break;
			   case SerialPort.LISTENING_EVENT_DATA_RECEIVED:				   
				   String actual=new String(event.getReceivedData());
								   
					if ((actual.charAt(0)=='?')&&(actual.length()>1)) { 				
						if (serListeners.containsKey(actual.charAt(1))) {
							
							((SerialListener)serListeners.get(actual.charAt(1))).action(actual.substring(1));
							
						} else {
							System.err.println("Unknown warning message! :"+actual.charAt(1));
						}
						if (!wcb.isSelected()) out(actual);
					} else out(actual);
				

				   break;
			   }
		   }
		});
		
	
		Iterator<SerialConnListener> value = serConnListeners.iterator(); 
        while (value.hasNext()) { 
            value.next().connected();
        } 
		//printCmd("J");

	}

}
