package fr.dauphine.ja.teamdeter.flowControl.stations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

import fr.dauphine.ja.teamdeter.flowControl.message.ApplicatifMessage;
import fr.dauphine.ja.teamdeter.flowControl.message.Message;
import fr.dauphine.ja.teamdeter.flowControl.message.Token;

public class Producteur extends Station {
	private final Object m_monitorReceipt = new Object();
	private ApplicatifMessage[] m_tampon;
	private int m_in;
	private int m_out;
	private int m_nbMess;
	private int m_nbAut;
	private int m_successeur;
	private boolean isEnabled;
	private ProducteurWorker m_myWorker;
	private ProducteurPostMan m_myPostMan;

	public Producteur(int tailleTampon, int successeur) {
		this.m_in = 0;
		this.m_out = 0;
		this.m_nbAut = 0;
		this.m_nbMess = 0;
		this.m_successeur = successeur;
		m_tampon = new ApplicatifMessage[tailleTampon];
		this.isEnabled = true;
	}

	public void run() {
		while (isEnabled) {
			try {
				Socket clt = this.m_mySocket.accept();
				ObjectInputStream in = new ObjectInputStream(clt.getInputStream());
				Token myToken = null;
				try {
					myToken = (Token) in.readObject();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				int j = myToken.getEmit();
				synchronized (m_monitorReceipt) {
					this.sur_reception_de(j, myToken);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class ProducteurWorker implements Runnable {
		
		private String m_message ; 
		private int m_id ; 
		
		public ProducteurWorker(String message , int id ) {
			this.m_message = message ; 
			this.m_id = id ; 
		}
		public void run() {
			synchronized (m_monitorReceipt) {
				
					while (!(m_nbMess < m_tampon.length)) {
						try {
							m_monitorReceipt.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}}
					m_tampon[m_in] = new ApplicatifMessage(this.m_id,this.m_message); 
					m_nbMess++ ;
				
				
			}
		}
	}

	class ProducteurPostMan implements Runnable {
		public void run() {
		}
	}

	public void sur_reception_de(int j, Token a) {
		int temp;
		if (m_nbMess - m_nbAut > a.getVal()) {
			temp = a.getVal();
		} else {
			temp = m_nbMess - m_nbAut;
		}
		m_nbAut += temp;
		a.setVal(a.getVal() - temp);
		this.envoyer_a(m_successeur, a);
		m_myPostMan.notify();
	}
}
