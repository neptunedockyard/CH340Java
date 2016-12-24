package com.neptunedockyard;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.NoSuchPortException;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class Window extends JFrame {
	private JPanel contentPane;
	private JTextField txtUsername;
	private JPasswordField pwdPassword;
	private JTextField txtRxAddress;
	private JTextField txtTxAddress;
	private JTextField textsendField;
	private JComboBox<String> comportBox;
	private JComboBox baudrateBox;
	private JButton btnConnect;
	private JComboBox encryptBox;
	private JButton btnSet;
	private JComboBox bitrateBox;
	private JComboBox frequencyBox;
	private JButton btnSend;
	private JCheckBox chckbxBroadcast;
	private static JTextArea textrecBox;
	private JScrollPane scrollPane;
	private JComboBox baudSetBox;
	private JComboBox deviceBox;

	public static Enumeration comportList;
	public static CommPortIdentifier comportID;
	public static SerialPort serial_tty;
	public static OutputStream outStream;
	public static InputStream inStream;
	public static SerialReader SR;

	public static Boolean connected = false;
	public static Boolean showUnicode = false;
	public static Boolean sysMessage = false;
	public static int maxSendChars = 22;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Window frame = new Window();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Window() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				toggleFields(true);
				getCOMPorts();
			}
		});
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				Window.class.getResource("/com/neptunedockyard/term_icon.ico")));
		setTitle("wJTerm");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		btnConnect = new JButton("Link");
		btnConnect.setBounds(5, 5, 73, 23);
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// JOptionPane.showMessageDialog(null, "Connecting");
				commConnect();
			}
		});

		comportBox = new JComboBox();
		comportBox.setBounds(5, 34, 73, 20);
		comportBox.setToolTipText("COM ports");
		comportBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// JOptionPane.showMessageDialog(null, "Generating Ports");
				getCOMPorts();
			}
		});
		comportBox.setModel(new DefaultComboBoxModel(new String[] { "COM" }));
		getCOMPorts();

		baudrateBox = new JComboBox();
		baudrateBox.setBounds(5, 60, 73, 20);
		baudrateBox.setModel(new DefaultComboBoxModel(new String[] { "BAUD",
				"4800", "9600", "14400", "19200", "38400", "115200" }));

		encryptBox = new JComboBox();
		encryptBox.setBounds(84, 6, 86, 20);
		encryptBox.setToolTipText("Encryption");
		encryptBox.setModel(new DefaultComboBoxModel(new String[] { "None",
				"DES", "AES" }));

		txtUsername = new JTextField();
		txtUsername.setToolTipText("username");
		txtUsername.setBounds(84, 34, 86, 20);
		txtUsername.setText("user");
		txtUsername.setColumns(10);

		pwdPassword = new JPasswordField();
		pwdPassword.setToolTipText("password");
		pwdPassword.setBounds(84, 60, 86, 20);
		pwdPassword.setText("password");

		txtRxAddress = new JFormattedTextField();
		AbstractDocument d1 = (AbstractDocument) txtRxAddress.getDocument();
		d1.setDocumentFilter(new fieldFilter());
		txtRxAddress.setToolTipText("Rx address");
		txtRxAddress.setBounds(180, 34, 86, 20);
		txtRxAddress.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (txtRxAddress.getText().length() >= 10) {
					getToolkit().beep();
					e.consume();
				}
			}
		});
		txtRxAddress.setText("RX");
		txtRxAddress.setColumns(10);

		txtTxAddress = new JTextField();
		AbstractDocument d2 = (AbstractDocument) txtTxAddress.getDocument();
		d2.setDocumentFilter(new fieldFilter());
		txtTxAddress.setToolTipText("Tx address");
		txtTxAddress.setBounds(180, 60, 86, 20);
		txtTxAddress.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (txtTxAddress.getText().length() >= 10) {
					getToolkit().beep();
					e.consume();
				}
			}
		});
		txtTxAddress.setText("TX");
		txtTxAddress.setColumns(10);

		btnSet = new JButton("Set");
		btnSet.setBounds(460, 33, 75, 23);
		btnSet.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// JOptionPane.showMessageDialog(null, "Sending config");
				SendConfig();
			}
		});

		bitrateBox = new JComboBox();
		bitrateBox.setToolTipText("data rate");
		bitrateBox.setBounds(272, 34, 86, 20);
		bitrateBox.setModel(new DefaultComboBoxModel(new String[] { "Rate",
				"250kbps", "1Mbps", "2Mbps" }));

		frequencyBox = new JComboBox();
		frequencyBox.setToolTipText("channel frequency");
		frequencyBox.setBounds(272, 60, 86, 20);
		frequencyBox.setModel(new DefaultComboBoxModel(new String[] {
				"Frequency", "2.400", "2.401", "2.402", "2.403", "2.404",
				"2.405", "2.406", "2.407", "2.408", "2.409", "2.410", "2.411",
				"2.412", "2.413", "2.414", "2.415", "2.416", "2.417", "2.418",
				"2.419", "2.420", "2.421", "2.422", "2.423", "2.424", "2.425",
				"2.426", "2.427", "2.428", "2.429", "2.430", "2.431", "2.432",
				"2.433", "2.434", "2.435", "2.436", "2.437", "2.438", "2.439",
				"2.440", "2.441", "2.442", "2.443", "2.444", "2.445", "2.446",
				"2.447", "2.448", "2.449", "2.450", "2.451", "2.452", "2.453",
				"2.454", "2.455", "2.456", "2.457", "2.458", "2.459", "2.460",
				"2.461", "2.462", "2.463", "2.464", "2.465", "2.466", "2.467",
				"2.468", "2.469", "2.470", "2.471", "2.472", "2.473", "2.474",
				"2.475", "2.476", "2.477", "2.478", "2.479", "2.480", "2.481",
				"2.482", "2.483", "2.484", "2.485", "2.486", "2.487", "2.488",
				"2.489", "2.490", "2.491", "2.492", "2.493", "2.494", "2.495",
				"2.496", "2.497", "2.498", "2.499", "2.500", "2.501", "2.502",
				"2.503", "2.504", "2.505", "2.506", "2.507", "2.508", "2.509",
				"2.510", "2.511", "2.512", "2.513", "2.514", "2.515", "2.516",
				"2.517", "2.518", "2.519", "2.520", "2.521", "2.522", "2.523",
				"2.524", "2.525" }));

		textsendField = new JTextField();
		textsendField.setToolTipText("enter message here");
		textsendField.setBounds(5, 244, 455, 20);
		textsendField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER)
					// JOptionPane.showMessageDialog(null, "Sending text");
					SendText();
			}
		});
		textsendField.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setBounds(478, 243, 57, 23);
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// JOptionPane.showMessageDialog(null, "Sending text");
				SendText();
			}
		});

		chckbxBroadcast = new JCheckBox("Broadcast");
		chckbxBroadcast.setToolTipText("broadcast motd");
		chckbxBroadcast.setBounds(460, 59, 73, 23);
		chckbxBroadcast.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
			}
		});

		scrollPane = new JScrollPane();
		scrollPane.setBounds(5, 91, 530, 146);

		textrecBox = new JTextArea();
		if (showUnicode)
			textrecBox.setFont(new Font("Bitstream Cyberbit", Font.PLAIN, 15));
		else
			textrecBox.setFont(new Font("Tahoma 11", Font.PLAIN, 13));
		textrecBox.setEditable(false);
		scrollPane.setViewportView(textrecBox);
		contentPane.setLayout(null);
		contentPane.add(textsendField);
		contentPane.add(btnSend);
		contentPane.add(scrollPane);
		contentPane.add(baudrateBox);
		contentPane.add(comportBox);
		contentPane.add(btnConnect);
		contentPane.add(pwdPassword);
		contentPane.add(encryptBox);
		contentPane.add(txtUsername);
		contentPane.add(txtRxAddress);
		contentPane.add(txtTxAddress);
		contentPane.add(btnSet);
		contentPane.add(chckbxBroadcast);
		contentPane.add(bitrateBox);
		contentPane.add(frequencyBox);

		baudSetBox = new JComboBox();
		baudSetBox.setToolTipText("set device baud rate");
		baudSetBox.setModel(new DefaultComboBoxModel(new String[] { "Baud",
				"4800", "9600", "14400", "19200", "38400", "115200" }));
		baudSetBox.setBounds(368, 34, 86, 20);
		contentPane.add(baudSetBox);

		deviceBox = new JComboBox();
		deviceBox.setToolTipText("radio model");
		deviceBox.setModel(new DefaultComboBoxModel(new String[] { "Device",
				"SE8R01", "NRF24L01" }));
		deviceBox.setSelectedIndex(2);
		deviceBox.setBounds(368, 60, 86, 20);
		contentPane.add(deviceBox);
	}

	public void getCOMPorts() {
		comportBox.removeAllItems();
		comportList = CommPortIdentifier.getPortIdentifiers();
		while (comportList.hasMoreElements()) {
			CommPortIdentifier cpi = (CommPortIdentifier) comportList
					.nextElement();
			if (cpi.getPortType() == CommPortIdentifier.PORT_SERIAL)
				comportBox.addItem(cpi.getName());
			// System.out.println("Port: " + cpi.getName());
		}
	}

	public void commConnect() {
		if (!connected) {
			try {
				comportID = CommPortIdentifier.getPortIdentifier(comportBox
						.getSelectedItem().toString());
				serial_tty = (SerialPort) comportID.open("wJTerm", 5000);
				if (baudrateBox.getSelectedIndex() != -1
						&& baudrateBox.getSelectedIndex() != 0) {
					serial_tty.setSerialPortParams(Integer
							.valueOf((String) baudrateBox.getSelectedItem()),
							SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
							SerialPort.PARITY_NONE);
					appendREC(SR, "SYSMSG: Setting to "
							+ baudrateBox.getSelectedItem().toString()
							+ " baud");
				} else {
					serial_tty.setSerialPortParams(4800, SerialPort.DATABITS_8,
							SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					appendREC(SR, "SYSMSG: Setting to default 4800 baud");
				}

				serial_tty.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				outStream = serial_tty.getOutputStream();
				inStream = serial_tty.getInputStream();

				SR = new SerialReader(inStream);

				// serial_tty.addEventListener(this);
				// serial_tty.addEventListener(new SerialReader(inStream));
				serial_tty.addEventListener(SR);
				serial_tty.notifyOnDataAvailable(true);

				appendREC(SR, "SYSMSG: Connected");

				// btnConnect.setText("Disconnect");
				// connected = true;
			} catch (NoSuchPortException e) {
				e.printStackTrace();
			} catch (PortInUseException e) {
				appendREC(SR, "COM Port in use");
				serial_tty.close();
				e.printStackTrace();
				return;
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (UnsupportedCommOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			} catch (TooManyListenersException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
		} else {
			// toggleFields(connected);
			serial_tty.close();
			appendREC(SR, "SYSMSG: Disconnected");
		}
		toggleFields(connected);
	}

	public void toggleFields(Boolean on) {
		if (on) {
			btnConnect.setText("Link");
			txtUsername.setEnabled(true);
			pwdPassword.setEnabled(true);
			txtRxAddress.setEnabled(true);
			txtTxAddress.setEnabled(true);
			textsendField.setEnabled(false);
			comportBox.setEnabled(true);
			baudrateBox.setEnabled(true);
			encryptBox.setEnabled(true);
			btnSet.setEnabled(false);
			bitrateBox.setEnabled(false);
			frequencyBox.setEnabled(false);
			btnSend.setEnabled(false);
			chckbxBroadcast.setEnabled(true);
			deviceBox.setEnabled(true);
			baudSetBox.setEnabled(false);
			connected = false;
		} else {
			btnConnect.setText("Unlink");
			txtUsername.setEnabled(false);
			pwdPassword.setEnabled(false);
			txtRxAddress.setEnabled(true);
			txtTxAddress.setEnabled(true);
			textsendField.setEnabled(true);
			comportBox.setEnabled(false);
			baudrateBox.setEnabled(false);
			encryptBox.setEnabled(false);
			btnSet.setEnabled(true);
			bitrateBox.setEnabled(true);
			frequencyBox.setEnabled(true);
			btnSend.setEnabled(true);
			chckbxBroadcast.setEnabled(true);
			deviceBox.setEnabled(false);
			baudSetBox.setEnabled(true);
			connected = true;
		}
	}

	public void SendText() {
		PrintStream outputStream = new PrintStream(outStream, true);
		// System.out.println("AT?");
		String text = textsendField.getText();

		// TODO strip down the text to only be a total maximum number per send,
		// then wait for it to send until the next one sends

		// TODO check for all AT commands here
		if (text.toUpperCase().contains("AT?")
				|| text.toUpperCase().contains("AT+")) {
			// outputStream.print(text.toUpperCase() + "\r\n");
			sysMessage = true;
			outputStream.print(text.toUpperCase());
			// appendREC(SR, text);
		} else {
			String[] longMsg;
			sysMessage = false;

			text = txtUsername.getText() + ":> " + textsendField.getText();
			longMsg = text.split("(?<=\\G.{20})");
			for (int i = 0; i < Array.getLength(longMsg); i++) {
				outputStream.print(longMsg[i]);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			outputStream.print("\r\n");
			// System.out.print(Arrays.toString(text.split("(?<=\\G.{22})")));
			appendREC(SR, text);
		}

		textsendField.setText("");
	}

	public void SendConfig() {
		PrintStream configStream = new PrintStream(outStream, true);
		String rxadd = null;
		String txadd = null;
		String rxa = null;
		String txa = null;
		String rate = null;
		String freq = null;
		String baudset = null;
		sysMessage = true;

		if (deviceBox.getSelectedItem().toString() == "NRF24L01") {
			if (bitrateBox.getSelectedIndex() == -1
					|| bitrateBox.getSelectedIndex() == 0)
				bitrateBox.setSelectedIndex(2);
			if (frequencyBox.getSelectedIndex() == -1
					|| frequencyBox.getSelectedIndex() == 0)
				frequencyBox.setSelectedItem(2);
			if (baudSetBox.getSelectedIndex() == -1
					|| baudSetBox.getSelectedIndex() == 0)
				baudSetBox.setSelectedItem(2);
			if (txtRxAddress.getText().isEmpty()
					|| txtRxAddress.getText().length() < 10)
				rxadd = "0xff,0xff,0xff,0xff,0xff";
			else
				rxadd = "0x" + txtRxAddress.getText().substring(0, 2) + ",0x"
						+ txtRxAddress.getText().substring(2, 4) + ",0x"
						+ txtRxAddress.getText().substring(4, 6) + ",0x"
						+ txtRxAddress.getText().substring(6, 8) + ",0x"
						+ txtRxAddress.getText().substring(8, 10);
			if (txtTxAddress.getText().isEmpty()
					|| txtTxAddress.getText().length() < 10)
				txadd = "0xff,0xff,0xff,0xff,0xff";
			else
				txadd = "0x" + txtTxAddress.getText().substring(0, 2) + ",0x"
						+ txtTxAddress.getText().substring(2, 4) + ",0x"
						+ txtTxAddress.getText().substring(4, 6) + ",0x"
						+ txtTxAddress.getText().substring(6, 8) + ",0x"
						+ txtTxAddress.getText().substring(8, 10);
			rate = "AT+RATE=" + bitrateBox.getSelectedIndex() + "\r\n";
			freq = "AT+FREQ=" + frequencyBox.getSelectedItem() + "G\r\n";
			baudset = "AT+BAUD=" + baudSetBox.getSelectedItem() + "\r\n";

			rxa = "AT+RXA=" + rxadd + "\r\n";
			txa = "AT+TXA=" + txadd + "\r\n";
		}
		String text = rate + freq + rxa + txa;

		try {
			configStream.print(rate);
			Thread.sleep(500);
			configStream.print(freq);
			Thread.sleep(500);
			configStream.print(rxa);
			Thread.sleep(500);
			configStream.print(txa);
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			appendREC(SR, "Error, tried: " + text);
		}
		// appendREC(SR, text);
	}

	public static void appendREC(SerialReader serialReader, final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					Document doc = textrecBox.getDocument();
					doc.insertString(doc.getLength(), text + "\r\n", null);
				} catch (BadLocationException ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	public static class SerialReader implements SerialPortEventListener {
		private InputStream in;
		private byte[] buffer = new byte[1024];

		public SerialReader(InputStream in) {
			this.in = in;
		}

		public void serialEvent(SerialPortEvent arg0) {
			int data;
			int[] unicodeBuffer = new int[2];

			try {
				int len = 0;
				int unilen = 0;
				while ((data = in.read()) > -1) {
					if (data == '\n') {
						break;
					}
					if (data < 0x0020 || data > 0x007f) {
						// System.out.println("Got unicode: "+(char)data);
						if (unilen == 0) {
							unicodeBuffer[0] = (int) data;
							unilen++;
						} else if (unilen == 1) {
							unicodeBuffer[1] = (int) data;
							int unicode = Integer.valueOf(String
									.valueOf(unicodeBuffer[0])
									+ String.valueOf(unicodeBuffer[1]));
							unilen = 0;
							String grossunicode = toNoLeadingUnicode((char) unicode);
							byte[] array = grossunicode.getBytes("UTF-8");
							String s = new String(array,
									Charset.forName("UTF-16"));
							// System.out.println(s);
							if (showUnicode)
								appendREC(this, s);
						}
					} else
						buffer[len++] = (byte) data;
				}
				String text = new String(buffer, 0, len);
				// System.out.print(new String(buffer,0,len));
				if (sysMessage) {
					text = sysMessageTag(text);
				}
				appendREC(this, text);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

		private static String sysMessageTag(String text) {
			String out = "\r\nCONFIGMSG:>\r\n"
					+ "------------------------------------\r\n" + text;
			sysMessage = false;
			return out;
		}

		private static String toNoLeadingUnicode(char ch) {
			// return String.format("\\u%04x", (int) ch);
			return String.format("%04x", (int) ch);
		}

		private static String toUnicode(char ch) {
			return String.format("\\u%04x", (int) ch);
			// return String.format("%04x", (int) ch);
		}

		public void test() throws UnsupportedEncodingException {
			String text = "こんにちは";
			byte[] array = text.getBytes("UTF-8");
			String s = new String(array, Charset.forName("UTF-8"));
			System.out.println(s); // Prints as expected
		}
	}
}
