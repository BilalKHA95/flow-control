package fr.dauphine.ja.teamdeter.flowControl.stations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import fr.dauphine.ja.teamdeter.flowControl.message.ApplicatifMessage;
import fr.dauphine.ja.teamdeter.flowControl.message.Message;
import fr.dauphine.ja.teamdeter.flowControl.message.Requests;
import fr.dauphine.ja.teamdeter.flowControl.message.Token;

public class Producteur extends Station {
	private final Object m_editAutorisation = new Object();
	private final Object m_editTampon = new Object();
	private ApplicatifMessage[] m_tampon;
	private int m_in;
	private int m_out;
	private int m_nbMess;
	private int m_nbAut;
	private int m_successeur;
	private int m_idMaster;
	private boolean isEnabled;
	private static long  m_timeOut = Main.timeProcessProducteur ; 

	public Producteur(int tailleTampon, int successeur, int idMaster) {
		this.m_in = 0;
		this.m_out = 0;
		this.m_nbAut = 0;
		this.m_nbMess = 0;
		this.m_successeur = successeur;
		m_tampon = new ApplicatifMessage[tailleTampon];
		this.isEnabled = true;
		this.m_idMaster = idMaster;
		new Thread(new ProducteurPostMan()).start();
	}

	public void run() {
		while (isEnabled) {
			try {
				Socket clt = this.m_mySocket.accept() ; 
				new Thread(new ProducteurListenner(clt)).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class ProducteurWorker implements Runnable {
		private ApplicatifMessage m_message;

		public ProducteurWorker(ApplicatifMessage message) {
			this.m_message = message;
		}

		public void run() {
			synchronized (m_editTampon) {
				while (!(m_nbMess < m_tampon.length)) {
					try {
						m_editTampon.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				m_tampon[m_in] = this.m_message;
				m_in = (m_in + 1) % m_tampon.length;
				m_nbMess++;
			}
		}
	}

	class ProducteurPostMan implements Runnable {
		public void run() {
			while (isEnabled) {
				synchronized (m_editAutorisation) {
					while (!(m_nbAut > 0)) {
						try {
							m_editAutorisation.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					m_nbAut--;
					synchronized (m_editTampon) {
						m_nbMess--;
						m_editTampon.notifyAll();
					}
					envoyer_a(m_idMaster, m_tampon[m_out]);
					m_out = (m_out + 1) % m_tampon.length;
				}
				
			}
		}
	}

	class ProducteurListenner implements Runnable {
		private Socket m_myClient;

		public ProducteurListenner(Socket s) {
			this.m_myClient = s;
		}

		public void run() {
			ObjectInputStream in = null;
			Message myToken = null;
			try {
				in = new ObjectInputStream(this.m_myClient.getInputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				try {
					myToken = (Message) in.readObject();
					if(myToken instanceof Requests) {
						myToken = (Token) in.readObject();
						
					}else {
						myToken = (Token) myToken ; 
						
					}
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			synchronized (m_editAutorisation) {
				int j = myToken.getEmit();
				sur_reception_de(j,(Token) myToken);
				m_editAutorisation.notifyAll();
			}
		}
	}

	public void sur_reception_de(int j, Token a) {
		int temp;
		synchronized (m_editTampon) {
			if (m_nbMess - m_nbAut > a.getVal()) {
				temp = a.getVal();
			} else {
				temp = m_nbMess - m_nbAut;
			}
			m_editTampon.notifyAll();
		}
		m_nbAut += temp;
		a.setVal(a.getVal() - temp);
		a.setEmit(super.getId());
		try {
			Thread.sleep(Producteur.m_timeOut);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.envoyer_a(m_successeur, a);
	}

	public void produire(ApplicatifMessage m) {
		new Thread(new ProducteurWorker(m)).run();
	}


}
