import java.io.File;
import java.util.Scanner;

public class Convert {
	
	final static String alphabet = " ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			
	public static void main(String[] args) {
		
		try {
			
			File difference = new File("difference.txt");
			File plaintext = new File("m1.txt");
			Scanner din = new Scanner(difference);
			Scanner pin = new Scanner(plaintext);
			String m1 = "";
			String m2 = "";
			String d = "";
			
			din.useDelimiter("\n");
			pin.useDelimiter("\n");
			
			while (pin.hasNext()) {
				m1 += pin.next();
			}
			
			while (din.hasNext()) {
				d += din.next();
			}
			
			m1 = m1.toUpperCase();
			
			for(int i=0; i<d.length(); i++) {
				char c1 = m1.charAt(i);
				char c2 = d.charAt(i);
				
				m2 += OneTimePad.subtract(c1, c2);
			}
			
			System.out.println(m2);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
