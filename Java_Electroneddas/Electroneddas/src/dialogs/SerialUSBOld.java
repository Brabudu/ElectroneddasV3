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
import java.util.Observable;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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
import javax.swing.Timer;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import main.Electroneddas;
import panels.SerialListener;
import panels.Spia;

public class SerialUSBOld extends JDialog implements SerialPortEventListener, ActionListener {

	SerialPort serialPort;
	String serName="";

	public static boolean sending=true;
	public static boolean connected=false;

	public static String data;

	private String buffer="";

	Hashtable<Character, SerialListener> serListeners=new Hashtable<Character, SerialListener>();

	private final static int jta_max_len=5000;

	JTextArea jta=new JTextArea(20,40);
	JComboBox<String> ser=new JComboBox<String>();
	JTextField	jtf=new JTextField(20);

	Spia seriale;

	public SerialUSBOld(Spia seriale) {
		this.seriale=seriale;
		this.setTitle("Serial");
		this.setResizable(false);

		JPanel jpan=new JPanel(new BorderLayout());
		//addPorts(true);

		//if (!connected) JOptionPane.showMessageDialog(null, "Nisciuna seriali","Error",JOptionPane.ERROR_MESSAGE);
			
		JScrollPane scroll1 = new JScrollPane ( jta );
		scroll1.setVerticalScrollBarPolicy ( ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS );

		jta.setBackground(Color.black);
		jta.setForeground(Color.green);

		JPanel nord=new JPanel();
		JButton b1=new JButton("Cls");
		b1.addActionListener(this);
		b1.setActionCommand("cls");
		ser.addActionListener(this);
		ser.setActionCommand("serial");

		nord.add(ser);
		nord.add(jtf);
		nord.add(b1);


		jpan.add(nord,BorderLayout.NORTH);

		jpan.add(scroll1,BorderLayout.CENTER);

		jtf.setActionCommand("send");
		jtf.addActionListener(this);

		this.getContentPane().add(jpan);
		this.setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.setLocation(1000, 0);
		setSize(500, 500);
		this.setVisible(false);
		
		//Start serial poller
		
		int delay = 5000; //milliseconds
		new Timer(delay, new SerialPoller()).start();
		//this.pack();

	}

	private void out(String s) {
		String c=jta.getText()+s;//+"\n";
		if (c.length()>jta_max_len) c=c.substring(c.length()-jta_max_len, c.length());
		jta.setText(c);
		jta.setCaretPosition(jta.getDocument().getLength());
	}

	private void openPort() {
		try {
			serialPort.openPort();//Open serial port
			serialPort.setParams(SerialPort.BAUDRATE_115200, 

					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			int mask = SerialPort.MASK_RXCHAR;//Prepare mask
			serialPort.setEventsMask(mask);//Set mask
			serialPort.addEventListener(this);//Add SerialPortEventListener
			seriale.on();
			connected=true;
		}
		catch (SerialPortException ex) {
			System.out.println(ex);
			seriale.off();
			connected=false;
			
		}
	}

	private void addPorts(boolean open) {
		closeSerial();
		ser.removeAllItems();

		String[] portNames = SerialPortList.getPortNames();
		if (portNames.length!=0) {
			for(int i = 0; i < portNames.length; i++){
				System.out.println("Agatada: "+portNames[i]);
				serName=portNames[i];
				ser.addItem(serName);
				//ser.setEnabled(false);
			}
			if (open) {
				serialPort = new SerialPort(serName);
				openPort();
								
			}
		} else {
			ser.addItem("NO SERIAL");
			
			//if (open) JOptionPane.showMessageDialog(null, "Nisciuna seriali","Error",JOptionPane.ERROR_MESSAGE);
			connected=false;
			
		}
	}

	public void closeSerial() {
		if ((serialPort!=null)&&serialPort.isOpened()) {
			try {
				serialPort.closePort();
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void addListener(char preamble, SerialListener l) {
		serListeners.put(preamble, l);
	}
	@Override
	//! error
	public void serialEvent(SerialPortEvent event) {
		// TODO Auto-generated method stub
		if (event.isRXCHAR() && event.getEventValue() > 0) {//If data is available
			int bytesCount = event.getEventValue();

			try {
				String msg=serialPort.readString(bytesCount);
				buffer+=msg;

				while (buffer.indexOf(10)>=0) {


					String actual=buffer.substring(0, buffer.indexOf(10)+1);	
					buffer= buffer.substring(buffer.indexOf(10)+1);

					char c=actual.charAt(0);
					//System.err.println("End :"+actual+"% buff :"+buffer+"% Char0: "+c);
					
					if (serListeners.containsKey(c)) ((SerialListener)serListeners.get(c)).action(actual);

					if (c=='!') {
						actual=" <"+actual;
					}
					out(actual);
				} 



			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else {
			//System.out.println("Event: "+event.getEventType());
			//System.out.println("Value: "+event.getEventValue());
		}
	
	}

	public void printCmd(String data)  {
		if (sending) {
			if ((serialPort!=null)&&serialPort.isOpened()) {

				try {
					out(">"+data+"\n");
					serialPort.writeBytes((data+" \n").getBytes());

				} catch (SerialPortException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//Write data to port
			} else {
				//System.err.println("Nessuna seriale");
				seriale.off();
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
			/*
			String sel=(String)ser.getSelectedItem();
			if (sel!=null) {
				if ((String)ser.getSelectedItem()!="NO SERIAL") {
					closeSerial();
					serialPort = new SerialPort((String)ser.getSelectedItem());
					openPort();
					System.out.println("Opened: "+ser.getSelectedItem());

				}
			}*/
			break;
		}
	}

	
	
	class SerialPoller implements ActionListener{
		

		@Override
		public void actionPerformed(ActionEvent e) {
			
			 if (!SerialUSBOld.connected) {
				
				 addPorts(true);
				 Electroneddas.init();
			 }
			
			
		}
	}

}
