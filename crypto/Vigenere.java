import java.io.File;
import java.util.Scanner;

public class Vigenere {
	
	final static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static void main(String[] args) {
		
		try {
			String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			File cipherFile = new File("ciphertext.txt");
			Scanner in = new Scanner(System.in);
			Scanner cipher = new Scanner(cipherFile);
			int keyLen = Integer.parseInt(in.nextLine());
			int[][] histogram = new int[keyLen][26];
			int[] lens = new int[keyLen];
			String cipherText = "";
			
			while (cipher.hasNext()) {
				cipherText += cipher.next();
			}
			
			for(int i=0; i<cipherText.length(); i++) {
				char letter = cipherText.charAt(i);
				histogram[i % keyLen][letter-65]++;
				lens[i % keyLen]++;
			}
			
			for(int i=0; i<keyLen; i++) {
				System.out.println("Group "+i+": "+lens[i]);
				for(int j=0; j<26; j++) {
					System.out.println(alphabet.charAt(j) + ": " + histogram[i][j]);
				}
			}

			System.out.println("Guess the keyword or type q to quit: ");
			String key = in.next();

			if (!key.equals("q")) decipher(cipherText, key);
			
			in.close();
			cipher.close();
		} catch(Exception e) {
			e.printStackTrace();
		}

	}
	
	static void decipher(String cipherText, String key) {
		String plainText = "";
		
		for(int i=0; i<cipherText.length(); i++) {
			char letter = cipherText.charAt(i);
			char keyLetter = key.charAt(i % key.length());
			int pos = (letter-keyLetter) % 26;
			if (pos < 0) pos = 26 + pos;
			plainText += alphabet.charAt(pos);
		}
		
		System.out.println(plainText);
	}

}
