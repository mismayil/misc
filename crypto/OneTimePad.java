import java.io.File;
import java.util.Scanner;

public class OneTimePad {

	final static String alphabet = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static void main(String[] args) {
		
		try {
			
			File cipher1 = new File("cipher1.txt");
			File cipher2 = new File("cipher2.txt");
			Scanner cin1 = new Scanner(cipher1);
			Scanner cin2 = new Scanner(cipher2);
			
			String ciphertext1 = "";
			String ciphertext2 = "";
			
			cin1 = cin1.useDelimiter("\n");
			cin2 = cin2.useDelimiter("\n");
			
			while (cin1.hasNext()) {
				ciphertext1 += cin1.next();
			}

			while (cin2.hasNext()) {
				ciphertext2 += cin2.next();
			}
			
			cin1.close();
			cin2.close();
			
			String dif = "";
			
			for(int i=0; i<ciphertext1.length(); i++) {
				char c1 = ciphertext1.charAt(i);
				char c2 = ciphertext2.charAt(i);
				
				dif += subtract(c1, c2);
			}
			
			System.out.println(dif);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static char subtract(char c1, char c2) {

		if (c1 == ' ' || !alphabet.contains(Character.toString(c1))) c1 = 64;
		if (c2 == ' ' || !alphabet.contains(Character.toString(c2))) c2 = 64;
		
		int pos = (c1-c2) % 27;
		if (pos < 0) pos = 27 + pos;
		return alphabet.charAt(pos);
	}

}
