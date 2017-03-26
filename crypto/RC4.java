import java.io.File;
import java.util.Scanner;

public class RC4 {

	public static void main(String[] args) {
		
		try {
			
			int[] frequency = new int[256];
			File ciphertext = new File("rc4-ciphertexts.txt");
			Scanner fin = new Scanner(ciphertext);
			String line;
			int max = 0;
			int index = 0;
			
			while (fin.hasNextLine()) {
				
				line = fin.nextLine();
				int c2 = Integer.parseInt(line.substring(2, 4), 16);
				int t = frequency[c2]++;
				if (t > max) {
					max = t;
					index = c2;
				}
				
			}
			
			System.out.println(index);
			fin.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
