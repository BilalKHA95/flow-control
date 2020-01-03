package fr.dauphine.ja.teamdeter.flowControl.stations;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public abstract class Station implements Runnable {
	private final Object m_monitorReceipt = new Object();
	private int nbStation = 0;
	private static HashMap<Integer, InetSocketAddress> m_localDnsById = new HashMap<Integer, InetSocketAddress>();
	private static HashMap<InetSocketAddress, Integer> m_localDnsByIdReverse = new HashMap<InetSocketAddress, Integer>();
	private int m_id;
	protected ServerSocket m_mySocket;

	public Station() {
		this.m_id = nbStation;
		for (int i = m_id; i <= 65535; i++) {
			try {
				this.m_mySocket = new ServerSocket(i);
				InetSocketAddress addr = new InetSocketAddress(this.m_mySocket.getInetAddress(), this.m_mySocket.getLocalPort()) ; 
				Station.m_localDnsById.put(m_id,addr);
				m_localDnsByIdReverse.put(addr,m_id) ; 
				break;
			} catch (IOException e) {
				System.out.println("Station id:" + this.m_id + " can't use port :" + i);
			}
		}
		nbStation++;
	}

	public boolean envoyer_a(int j, Object envoi) {
		InetSocketAddress receipterAdress = Station.m_localDnsById.get(j);
		Socket receiver = new Socket();
		try {
			receiver.bind(receipterAdress);
			ObjectOutputStream out = new ObjectOutputStream(receiver.getOutputStream());
			if (envoi instanceof Serializable) {
				out.writeObject(envoi);
				receiver.close();
				return true;
			} else {
				System.out.println(envoi + " can't be send not a serializable Object");
				return false;
			}
		} catch (IOException e) {
			System.out.println("Station :" + j + " is not reachable");
			e.printStackTrace();
			return false;
		}
	}


}
