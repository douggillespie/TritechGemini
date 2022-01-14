package tritechplugin;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.net.SocketException;

public class UDPrxTest {

	private int port = 52904;
	
	public static void main(String[] args) {

		new UDPrxTest().run();
	}

	private void run() {
		DatagramSocket socket = null;
		DatagramPacket packet = null;
		int bufLen = 10;
		byte[] buf = new byte[bufLen];
		
		try {
			InetAddress locHost = InetAddress.getByName("127.0.0.1");
			socket = new DatagramSocket(port, locHost);
//			socket.joinGroup(locHost);
//			socket = new DatagramSocket(port);
			packet = new DatagramPacket(buf, bufLen);
			socket.setSoTimeout(3000);
			socket.receive(packet);
			System.out.println(packet.getData());
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
