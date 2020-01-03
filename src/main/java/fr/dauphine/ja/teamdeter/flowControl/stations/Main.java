package fr.dauphine.ja.teamdeter.flowControl.stations;

import fr.dauphine.ja.teamdeter.flowControl.message.ApplicatifMessage;
import fr.dauphine.ja.teamdeter.flowControl.message.Token;

public class Main {
	public static void main(String[] args) {
		
		Producteur t = new Producteur(5, 1, 0);
		Producteur t2 = new Producteur(15, 0, 0);
		Thread aa = new Thread(t) ; 
		Thread aaa = new Thread(t2);
		aa.start();
		aaa.start();
		Token t1 = new Token(1) ; 
		t1.setVal(5);
		t2.envoyer_a(0, t1) ; 
		for (int i = 0; i < 100; i++) {
			t.produire(new ApplicatifMessage(i, Integer.toString(i)));
		}
		
	
		System.out.println(""); 
		
	}
}
