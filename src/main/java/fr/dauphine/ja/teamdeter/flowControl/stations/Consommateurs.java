package fr.dauphine.ja.teamdeter.flowControl.stations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

import fr.dauphine.ja.teamdeter.flowControl.message.ApplicatifMessage;
import fr.dauphine.ja.teamdeter.flowControl.message.Message;
import fr.dauphine.ja.teamdeter.flowControl.message.RequestEnum;
import fr.dauphine.ja.teamdeter.flowControl.message.Requests;
import fr.dauphine.ja.teamdeter.flowControl.message.Token;
import fr.dauphine.ja.teamdeter.flowControl.stations.MasterT.MasterTListenner;

public class Consommateurs extends Station {
	private State m_myState;
	private int m_lateStation;
	private int m_candidate;
	private final Object m_editState = new Object();
	private ApplicatifMessage[] m_tampon;
	private int m_nbMess;// nbr mess stockés dans tampon pas encore envoyés à un consommateur
	private int m_in;
	private int m_out;
	private final Object m_editTampon = new Object();
	private boolean isEnabled;
	private boolean token;
	private static long m_timeOut = 2000;
	private int m_idMaster;

	public Consommateurs(int tailleTampon, int successeur) {
		this.m_in = 0;
		this.m_out = 0;
		this.m_nbMess = 0;
		this.m_idMaster = successeur;
		m_tampon = new ApplicatifMessage[tailleTampon];
		this.isEnabled = true;
		this.m_myState = State.sleep;
		this.m_lateStation = super.getId();
		this.token = super.getId() > m_idMaster;
		new Thread(new ConsommateurRdvSeeker()).start();
		new Thread(new ConsommateursWorker()).start();
	}

	public void run() {
		while (isEnabled) {
			try {
				Socket clt = this.m_mySocket.accept();
				new Thread(new ConsommateursListenner(clt)).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class ConsommateursListenner implements Runnable {
		private Socket m_myClient;

		public ConsommateursListenner(Socket s) {
			this.m_myClient = s;
		}

		public void run() {
			ObjectInputStream in = null;
			Message monmsg = null;
			try {
				in = new ObjectInputStream(this.m_myClient.getInputStream());
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				try {
					monmsg = (Message) in.readObject();
					if (monmsg instanceof ApplicatifMessage) {
						sur_reception_de(monmsg.getEmit(), (ApplicatifMessage) monmsg);
					} else {
						sur_reception_de(monmsg.getEmit(), (Requests) monmsg);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class ConsommateursWorker implements Runnable {
		public void run() {
			while (isEnabled) {
				synchronized (m_editTampon) {
					while (!(m_nbMess > 0)) {
						try {
							m_editTampon.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					m_out = (m_out + 1) % m_tampon.length;
					m_nbMess--;
					m_editTampon.notifyAll();
				}
			}
		}
	}

	class ConsommateurRdvSeeker implements Runnable {
		public void run() {
			while (isEnabled) {
				synchronized (m_editTampon) {
					while (!(m_nbMess < m_tampon.length)) {
						try {
							m_editTampon.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				synchronized (m_editState) {
					do {
						m_myState = State.process;
						while (!token) {
							try {
								m_editState.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (m_myState == State.process) {
							m_candidate = m_idMaster;
							Requests maReq = new Requests(getId());
							try {
								Thread.sleep(m_timeOut);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							envoyer_a(m_candidate, maReq);
							token = false;
							m_myState = State.waiting;
							while (!(m_myState != State.waiting)) {
								try {
									m_editState.wait();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					} while (!(m_myState == State.success));
					m_myState = State.sleep;
					m_editState.notifyAll();
				}
			}
		}
	}

	public void sur_reception_de(int j, Requests a) {
		synchronized (m_editState) {
			switch (a.getStatus()) {
			case ack:
				m_myState = State.success;
				if (m_lateStation != getId()) {
					a.setStatus(RequestEnum.rej);
					a.setEmit(super.getId());
					envoyer_a(m_lateStation, a);
					m_lateStation = getId();
				}
				break;
			case rej:
				if (m_lateStation == getId()) {
					m_myState = State.process;
				} else {
					m_myState = State.success;
					a.setStatus(RequestEnum.ack);
					a.setEmit(super.getId());
					envoyer_a(m_lateStation, a);
					m_candidate = m_lateStation;
					m_lateStation = getId();
				}
				break;
			case req:
				token = true;
				if (m_myState == State.sleep || j != m_idMaster) {
					a.setStatus(RequestEnum.rej);
					a.setEmit(super.getId());
					envoyer_a(j, a);
				} else if (m_myState == State.process) {
					m_myState = State.success;
					m_candidate = j;
					a.setStatus(RequestEnum.ack);
					a.setEmit(super.getId());
					envoyer_a(j, a);
				} else if (m_lateStation != getId() || j < getId()) {
					a.setStatus(RequestEnum.rej);
					a.setEmit(super.getId());
					envoyer_a(j, a);
				} else {
					m_lateStation = j;
				}
				break;
			default:
				break;
			}
			m_editState.notifyAll();
		}
	}

	public void sur_reception_de(int j, ApplicatifMessage a) {
		if (j == m_idMaster) {
			synchronized (m_editTampon) {
				m_tampon[m_in] = a;
				m_nbMess++;
				m_in = (m_in + 1) % this.m_tampon.length;
				m_editTampon.notifyAll();
			}
		}
	}
}
