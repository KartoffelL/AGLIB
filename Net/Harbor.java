package Kartoffel.Licht.Net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;

import Kartoffel.Licht.Java.freeable;
import Kartoffel.Licht.Res.SerializationUtils;
import Kartoffel.Licht.Tools.Timer;
import Kartoffel.Licht.Tools.Tools;

/**
 * A simple class for communication between two clients using UDP using encryption.
 */
public class Harbor implements freeable{
	
	final public static byte[] FRAGMENTED_HEADER = new byte[] {-1, -1, -1, -1};
	final public static int FRAGMENTED_HEADER_SIZE = 12;
	final public static int ENCRYPTION_OVERHEAD = 16;
	
	final public static byte TRAP_BYTE = 60;
	
	final public static int ENCRYPTION_TYPE_NONE = 0,
							ENCRYPTION_TYPE_FAST= 1,
							ENCRYPTION_TYPE_SECURE = 2;
	
	static int PACKAGE_SIZE = 512;
	
	private double MAX_PPS = Double.POSITIVE_INFINITY;
	private double MAX_FLAG = Double.POSITIVE_INFINITY;
	
	private HashMap<String, Long> times = new HashMap<>();
	private HashMap<String, Integer> flag = new HashMap<>();
	private volatile PackageCallback call;
	private volatile DatagramSocket socket;
	private volatile DatagramPacket outgoing;
	private volatile DatagramPacket incoming;
	private volatile byte[] out_bytes;
	private volatile byte[] in_bytes;
	private byte[] buffer_bytes;
	private volatile boolean running = false;
	private Thread listener;
	private InetSocketAddress address;
	private String password = "Mozarella";
	private Chungus chungus = new Chungus(password);
	private String error = "";
	final private int dataHidingType; //0 = None, 1 = fast, 2 = encryption
	/**
	 * @param port A valid port value between 0 and 65535. A port number of zero will let the system pick up anephemeral port in a bind operation.
	 * @param dataHidingType one of <br><table><tr><td>{@link Harbor#ENCRYPTION_TYPE_NONE}</td><td>{@link Harbor#ENCRYPTION_TYPE_NONE}</td><td>{@link Harbor#ENCRYPTION_TYPE_NONE}</td></tr></table>
	 */
	public Harbor(int port, int dataHidingType) {
		this.address = new InetSocketAddress(port);
		this.dataHidingType = dataHidingType;
		try {checkInit();} catch (SocketException e) {}
	}
	/**
	 * A anephemeral port number will be selected by the system in a bind operation.
	 */
	public Harbor() {
		this.address = new InetSocketAddress(0);
		this.dataHidingType = 0;
	}
	
	private void onMessage(DatagramPacket pack) {
		if(MAX_PPS == Double.POSITIVE_INFINITY) {
			call.parse(pack.getData(), new InetSocketAddress(pack.getAddress(), pack.getPort()));
			return;
		}
		String name = pack.getAddress().toString()+":"+pack.getPort();
		long t = times.getOrDefault(name, (long) 0);
		double offset = (Timer.getTime()-t)/1000000000.0;
		int flags = flag.getOrDefault(name, 0);
		if(!(offset < 1/MAX_PPS)) {
			if(!(flags > MAX_FLAG))
				call.parse(pack.getData(), new InetSocketAddress(pack.getAddress(), pack.getPort()));
			times.put(name, Timer.getTime());
			flag.put(name, Math.max(flags-1, 0));
		}
		else 
			flag.put(name, flags+1);
	}
	
	/**
	 * Starts a thread listening for incoming packages.
	 * @param call a callback that may be called by another thread.
	 * @throws Exception if any exception occurs
	 */
	public void startListening(PackageCallback call) throws Exception {
		checkInit();
		if(running)
			return;
		listener = new Thread() {
			@Override
			public void run() {
				Tools.conm("Started listening on port " + socket.getLocalPort());
					try {
						while(running) {
							incoming.setData(in_bytes);
							socket.receive(incoming);
							incoming.setData(chungus.decrypt(incoming.getData(), dataHidingType == 1));
							onMessage(incoming);;
						}
					} catch (IOException e) {
						if(e.getMessage().contentEquals("Socket closed"))
							Tools.conm("Thread stopped listening");
						else
							e.printStackTrace();
					}
					Tools.conm("Terminated Thread");
			}
		};
		running = true;
		this.call = call;
		listener.start();
	}
	/**
	 * Stops the listening Thread.
	 */
	public void stopListening() {
		running = false;
		socket.close();
		socket = null;
	}
	/**
	 * Sends an buffered Message to the given address at the specified port. May be used if the data is bigger than the MAX_PACKAGE_SIZE.<br>
	 * Tries to wait 1ms before sending another buffered package.
	 * @param a address to send to
	 * @param port port to send to
	 * @param data data to send
	 * @throws IOException if any exception occurs
	 */
	public void sendBuffered(InetAddress a, int port, byte[] data) {
		if(buffer_bytes == null)
			buffer_bytes = new byte[PACKAGE_SIZE];
		int chunkSize = PACKAGE_SIZE-FRAGMENTED_HEADER_SIZE;
		short ID = (short) Tools.RANDOM.nextInt();
		for(int i = 0; i < data.length; i+=chunkSize) {
			System.arraycopy(FRAGMENTED_HEADER, 0, buffer_bytes, 0, 4);
			System.arraycopy(SerializationUtils.from(ID), 0, buffer_bytes, 4, 2);
			System.arraycopy(SerializationUtils.from((short)(i/chunkSize)), 0, buffer_bytes, 6, 2);
			System.arraycopy(SerializationUtils.from(data.length), 0, buffer_bytes, 8, 4);
			System.arraycopy(data, i, buffer_bytes, FRAGMENTED_HEADER_SIZE, Math.min(chunkSize, data.length-i));
			nsend(a, port, buffer_bytes);
		}
	}

	//Networking test
//	public static void main(String[] args) throws Exception {
//		PACKAGE_SIZE = 32;
//		Harbor h = new Harbor(6060, 0);
//		h.startListening(new PrintPacketCallback());
//		
//		Harbor h2 = new Harbor(6061, 0);
//		h2.startListening(new PrintPacketCallback());
//		for(int i = 0; i < 32; i++)  {
//			String a = "";
//			for(int l = 0; l < i; l++)
//				a += "h";
//			h.send(new InetSocketAddress("localhost", 6060), new PMessage(a));
//		}
//		
//	}
	private void nsend(InetAddress a, int port, byte[] data) {
		try {
			checkInit();
			outgoing.setAddress(a);
			outgoing.setPort(port);
			System.arraycopy(data, 0, out_bytes, 0, data.length);
			outgoing.setData(dataHidingType == 0 ? out_bytes : chungus.encrypt(out_bytes, dataHidingType == 1));
			socket.send(outgoing);
		} catch (IOException e) {
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(b));
			error = new String(b.toByteArray());
		}
	}
	/**
	 * Sends an Message to the given address at the specified port.
	 * @param a address to send to
	 * @param port port to send to
	 * @param data data to send
	 * @throws IOException if any exception occurs
	 */
	public void send(InetAddress a, int port, byte[] data) {
		if(data.length > PACKAGE_SIZE)
			sendBuffered(a, port, data);
		else
			nsend(a, port, data);
	}
	/**
	 * Sends an Message to the given address at the specified port.
	 * @param address address to send to
	 * @param data data to send
	 * @throws IOException if any exception occurs
	 */
	public void send(InetSocketAddress address, byte[] data) {
		if(data.length > PACKAGE_SIZE)
			sendBuffered(address.getAddress(), address.getPort(), data);
		else
			nsend(address.getAddress(), address.getPort(), data);
	}
	/**
	 * Sends an Message to the given address at the specified port.
	 * @param a address to send to
	 * @param port port to send to
	 * @param pack package to send
	 */
	public void send(InetAddress a, int port, Package pack) {
		send(a, port, pack.Nserialize());
	}
	/**
	 * Sends an Message to the given address at the specified port.
	 * @param address a address to send to
	 * @param pack package to send
	 */
	public void send(InetSocketAddress address, Package pack) {
		send(address.getAddress(), address.getPort(), pack.Nserialize());
	}
	private void checkInit() throws SocketException{
		if(socket == null)
			socket = new DatagramSocket(address);
		if(outgoing == null) {
			outgoing = new DatagramPacket(new byte[0], 0);
			out_bytes = new byte[PACKAGE_SIZE];
		}
		if(incoming == null) {
			int ov = dataHidingType == 2 ? ENCRYPTION_OVERHEAD : 0; //Secure encryption adds 16 bytes to the total size
			incoming = new DatagramPacket(new byte[0], 0);
			in_bytes = new byte[PACKAGE_SIZE+ov];
		}
	}
	/**
	 * Sets the password for encryption
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
		this.chungus.updatePassword(password);
	}
	/**
	 * Sets the callback
	 * @param call
	 */
	public void setCall(PackageCallback call) {
		this.call = call;
	}
	public PackageCallback getCall() {
		return call;
	}
	public int getEncryptionType() {
		return dataHidingType;
	}
	public void setMAX_FLAG(double mAX_FLAG) {
		MAX_FLAG = mAX_FLAG;
	}
	public double getMAX_FLAG() {
		return MAX_FLAG;
	}
	public double getMAX_PPS() {
		return MAX_PPS;
	}
	public void setMAX_PPS(double mAX_PPS) {
		MAX_PPS = mAX_PPS;
	}
	public InetSocketAddress getAddress() {
		return address;
	}
	public DatagramSocket getSocket() {
		return socket;
	}
	/**
	 * Latest error message.
	 * @return
	 */
	public String getError() {
		return error;
	}

	@Override
	public void free() {
		socket.close();
		running = false;
	}

}