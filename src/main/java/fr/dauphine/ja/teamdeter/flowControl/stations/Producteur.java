package fr.dauphine.ja.teamdeter.flowControl.stations;

public class Producteur extends Station {
	private final Object m_monitorReceipt = new Object();

	public void run() {
	}

	class ProducteurWorker implements Runnable {
		public void run() {
		}
	}
	class ProducteurPostMan implements Runnable{

		public void run() {
			
			
		}
		
	}
}
