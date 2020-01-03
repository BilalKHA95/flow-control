package fr.dauphine.ja.teamdeter.flowControl.stations;

import fr.dauphine.ja.teamdeter.flowControl.message.ApplicatifMessage;
import fr.dauphine.ja.teamdeter.flowControl.message.Token;

public class Main {
	public static void main(String[] args) {
		
		Producteur t = new Producteur(10, 1, 0);
		Producteur t2 = new Producteur(10, 0, 0);
		new Thread(t).start();
		new Thread(t2).start();
		Token t1 = new Token(1) ; 
		t1.setVal(1000);
		t2.envoyer_a(0, t1) ; 
		for (int i = 0; i < 100; i++) {
			t.produire(new ApplicatifMessage(i, ""));
		}
		
		System.out.println("") ; 
	}
}
