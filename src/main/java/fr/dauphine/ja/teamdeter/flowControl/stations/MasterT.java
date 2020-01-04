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

public class MasterT extends Station {
	private ApplicatifMessage[] m_tampon;
	private int m_nbcell;// nbr de mess envoyés à un consommateur entre 2 passages du jeton
	private int m_nbMess;// nbr mess stockés dans tampon pas encore envoyés à un consommateur
	private int m_in;
	private int m_out;
	private int m_successeur;
	private boolean isEnabled;
	private static long m_timeOut = 50;
	private final Object m_editToken = new Object();
	private final Object m_editTampon = new Object();
	private ArrayList<Integer> m_possibleMeeting;
	private State m_myState;
	private int m_lateStation;
	private Boolean[] m_canSendRequest;
	private int m_candidate;
	private final Object m_editState = new Object();
	private boolean m_present;
	private int m_prochain;
	private final static int m_seuil = 5;

	public MasterT(int tailleTampon, int successeur, ArrayList<Integer> meet) {
		this.m_in = 0;
		this.m_out = 0;
		this.m_nbMess = 0;
		this.m_successeur = successeur;
		m_tampon = new ApplicatifMessage[tailleTampon];
		this.isEnabled = true;
		this.m_possibleMeeting = meet;
		this.m_myState = State.sleep;
		this.m_lateStation = super.getId();
		m_canSendRequest = new Boolean[meet.size()];
		for (int i = 0; i < meet.size(); i++) {
			m_canSendRequest[i] = meet.get(i) < super.getId();
		}
		new Thread(new MasterTWorker()).start();
	}

	public void run() {
		Token init = new Token(getId());
		init.setVal(m_tampon.length);
		envoyer_a(m_successeur, init);
		while (isEnabled) {
			try {
				Socket clt = this.m_mySocket.accept();
				new Thread(new MasterTListenner(clt)).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	class MasterTListenner implements Runnable {
		private Socket m_myClient;

		public MasterTListenner(Socket s) {
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
					if (monmsg instanceof Token) {
						sur_reception_de(monmsg.getEmit(), (Token) monmsg);
					} else if (monmsg instanceof ApplicatifMessage) {
						sur_reception_de(monmsg.getEmit(), (ApplicatifMessage) monmsg);
					} else if (monmsg instanceof Requests) {
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

	class MasterTWorker implements Runnable {
		public void run() {
			while (isEnabled) {
				synchronized (m_editTampon) {
					while (!(m_nbMess > 0)) {
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
						while (searchToken() == -1) {
							try {
								m_editState.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (m_myState == State.process) {
							m_candidate = searchToken();
							Requests maReq = new Requests(getId());
							try {
								Thread.sleep(m_timeOut);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							envoyer_a(m_candidate, maReq);
							m_canSendRequest[m_possibleMeeting.indexOf(m_candidate)] = false;
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
					new Thread(new MasterTConsommateur(m_candidate)).run();
					m_myState = State.sleep;
					m_editState.notifyAll();
				}
			}
		}
	}

	class MasterTConsommateur implements Runnable {
		private int m_j;

		public MasterTConsommateur(int j) {
			this.m_j = j;
		}

		public void run() {
			synchronized (m_editTampon) {
				try {
					Thread.sleep(m_timeOut);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				m_tampon[m_out].setEmit(getId());
				envoyer_a(this.m_j, m_tampon[m_out]);
				m_out = (m_out + 1) % m_tampon.length;
				m_nbMess--;
				m_nbcell++;
				if (m_present && m_nbcell > m_seuil) {
					Token token = new Token(getId());
					token.setVal(m_nbcell);
					envoyer_a(m_prochain, token);
					m_nbcell = 0;
					m_present = false;
				}
				m_editTampon.notifyAll();
			}
		}
	}

	public void sur_reception_de(int j, Token a) {
		synchronized (m_editToken) {
			this.m_prochain = (j + 1) % (m_canSendRequest.length);
			this.m_nbcell += a.getVal();
			if (this.m_nbcell > m_seuil) {
				a.setVal(m_nbcell);
				this.envoyer_a(this.m_prochain, a);
				this.m_nbcell = 0;
			} else {
				this.m_present = true;
			}
		}
		try {
			Thread.sleep(MasterT.m_timeOut);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.envoyer_a(m_successeur, a);
	}

	public void sur_reception_de(int j, ApplicatifMessage a) {
		synchronized (m_editTampon) {
			m_tampon[m_in] = a;
			m_nbMess++;
			m_in = (m_in + 1) % this.m_tampon.length;
			m_editTampon.notifyAll();
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
				int index = m_possibleMeeting.indexOf(j);
				m_canSendRequest[index] = true;
				if (m_myState == State.sleep || m_possibleMeeting.indexOf(j) == -1) {
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

	public int searchToken() {
		for (int i = 0; i < m_canSendRequest.length; i++) {
			if (m_canSendRequest[i]) {
				return m_possibleMeeting.get(i);
			}
		}
		return -1;
	}
}
