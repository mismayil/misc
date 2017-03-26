import java.io.File;
import java.util.Scanner;

public class DifferentialCrypto {

	private final static int[] SBOX = {14, 3, 4, 8, 1, 12, 10, 15, 7, 13, 9, 6, 11, 2, 0, 5}; 
	private final static int fileSize = 5000;
	private final static int numKeys = 256;
	private final static int size = 16;
	
	public static void main(String[] args) {
		
		try {
			
			int[] keyBits = new int[size];
			String line;
			String[] buf;
			File difftext = new File("difftext19.txt");
			int[] u1Bits = new int[size];
			int[] u2Bits = new int[size];
			int[] duBits = new int[size];
			int count = 0;
			boolean match = false;
			int[] result = new int[numKeys];
			int maxCount = 0;
			int subKey = 0;
			int[] partKey;
			
			for(int i = 0; i < numKeys; i++) {
				
				int[] key = toBinary(i);
				
				for(int j = 4; j < 8; j++) {
					keyBits[j] = key[j+4];
				}
				
				for(int j = 12; j < 16; j++) {
					keyBits[j] = key[j];
				}
				
				count = 0;
				Scanner din = new Scanner(difftext);
				
				for(int j = 0; j < fileSize; j++) {
					
					line = din.nextLine();
					buf = line.split(",");
					
					u1Bits = getUbits(keyBits, buf[0], buf[2]);
					u2Bits = getUbits(keyBits, buf[1], buf[3]);
					
					for(int k = 0; k < size; k++) {
						duBits[k] = u1Bits[k] ^ u2Bits[k];
					}
					
					match = isSatisfied(duBits);
					
					if (match) count++;
					
				}
				
				result[i] = count;
				
				din.close();
				
			}
			
			for(int i = 0; i < result.length; i++) {
				if (result[i] > maxCount) {
					maxCount = result[i];
					subKey = i;
				}
			}
			
			partKey = toBinary(subKey);
			System.out.print("partial subkey5 = ");
			System.out.print("____");
			
			for(int i = 8; i < 12; i++) {
				System.out.print(partKey[i]);
			}
			
			System.out.print("____");
			
			for(int i = 12; i < 16; i++) {
				System.out.print(partKey[i]);
			}
			
			System.out.print("\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static int[] getUbits(int[] keyBits, String plaintext, String ciphertext) {
			
		int[] uBits = new int[size]; // u bits
		int[] vBits = new int[size]; // v bits
		int[] pBits = new int[size]; // plaintext bits
		int[] cBits = new int[size]; // ciphertext bits

			
		for(int i = 0; i < size; i++) {
			pBits[i] = plaintext.charAt(i) - 48;
		}
		
		for(int i = 0; i < size; i++) {
			cBits[i] = ciphertext.charAt(i) - 48;
		}
		
		for(int i = 0; i < size; i++) {
			vBits[i] = cBits[i] ^ keyBits[i];
		}
		
		uBits = revertSBOX(vBits);
		
		return uBits;
				
	}
	
	public static int[] revertSBOX(int[] vbits) {
		int[] uBits = new int[size];
		int num = 0;
		int[] buf;
		
		for(int i = 0; i < 4; i++) {
			
			num = 0;
			
			for(int j = 0; j < 4; j++) {
				num += vbits[4*i+j] << (3 - j);
			}
			
			num = SBOX[num];
			
			buf = toBinary(num);
			
			for(int k = 4 * i; k < 4 * i + 4; k++) {
				uBits[k] = buf[12-4*i+k];
			}
		}
		
		return uBits;
	}
	
	public static boolean isSatisfied(int[] bits) {
		
		int[] du4 = {0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 1, 0};
		
		for(int i = 4; i < 8; i++) {
			if (du4[i] != bits[i]) return false;
		}
		
		for(int i = 12; i < 16; i++) {
			if (du4[i] != bits[i]) return false;
		}
		
		return true;
	}
	
	private static int[] toBinary(int number) {
		int[] bitArray = new int[size];
		
		for(int i = 15; i >=0; i--) {
			bitArray[15-i] = (number >> i) & 1;
		}
		
		return bitArray;
	}

}
