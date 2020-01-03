package fr.dauphine.ja.teamdeter.flowControl.stations;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public abstract class Station implements Runnable {
	private static int nbStation = 0;
	public static HashMap<Integer, InetSocketAddress> m_localDnsById = new HashMap<Integer, InetSocketAddress>();
	public static HashMap<InetSocketAddress, Integer> m_localDnsByIdReverse = new HashMap<InetSocketAddress, Integer>();
	private int m_id;
	protected ServerSocket m_mySocket;

	public Station() {
		this.m_id = nbStation;
		for (int i = m_id; i <= 65535; i++) {
			try {
				this.m_mySocket = new ServerSocket(i);
				this.m_mySocket.setReuseAddress(true);
				InetSocketAddress addr = new InetSocketAddress(InetAddress.getLocalHost(),
						this.m_mySocket.getLocalPort());
				Station.m_localDnsById.put(m_id, addr);
				m_localDnsByIdReverse.put(addr, m_id);
				break;
			} catch (IOException e) {
				System.out.println("Station id:" + this.m_id + " can't use port :" + i);
			}
		}
		nbStation++;
	}

	public boolean envoyer_a(int j, Object envoi) {
		try {
			Socket receiver = new Socket();
			receiver.connect( Station.m_localDnsById.get(j));
			ObjectOutputStream out = new ObjectOutputStream(receiver.getOutputStream());
			if (envoi instanceof Serializable) {
				out.writeObject(envoi);
				receiver.close();
				return true;
			} else {
				System.out.println(envoi + " can't be send not a serializable Object");
				receiver.close();
				return false;
			}
		} catch (IOException e) {
			System.out.println("Station :" + j + " is not reachable");
			e.printStackTrace();
			return false;
		}
	}

	public int getId() {
		return this.m_id;
	}
}
