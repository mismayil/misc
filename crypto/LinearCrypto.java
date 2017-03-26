import java.util.Scanner;
import java.io.File;

public class LinearCrypto {
	
	private static double[] biases;
	private final static int[] SBOX = {14, 3, 4, 8, 1, 12, 10, 15, 7, 13, 9, 6, 11, 2, 0, 5}; 
	private final static int fileSize = 20000;
	private final static int numKeys = 256;
	private final static int size = 16;
	private final static String cipherfile = "ciphertext19.txt";
	private final static String plainfile = "plaintexts.txt";
	private static int partKey;
	
	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		System.out.println("Enter A = solve part A or B = solve part B or D = solve part D: ");
		String part = in.nextLine();
		double bias = 0;
		
		if (part.equalsIgnoreCase("A")) {
			int[] keyBits = {0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0};
			bias = run(keyBits, "A");;
			System.out.println("magnitude of bias = " + Math.abs(bias));
			in.close();
			return;
		}
		
		int[] keyBits = new int[size];
		int[] subKey;
		int[] tmpBits;
		
		biases = new double[numKeys];
			
		for(int i = 0; i < numKeys; i++) {
			
			subKey = toBinary(i);
			
			for(int j = 4; j < 8; j++) {
				keyBits[j] = subKey[j+4];
			}
			
			for(int j = 12; j < 16; j++) {
				keyBits[j] = subKey[j];
			}
			
			biases[i] = run(keyBits, "B");
		}
		
		double maxBias = getMax(biases);
		subKey = toBinary(partKey);

        if (part.equalsIgnoreCase("D")) {
    		
        	biases = new double[numKeys];
    		keyBits = new int[size];
    		
        	for(int i = 4; i < 8; i++) {
        		keyBits[i] = subKey[i+4];
        	}
        	
        	for(int i = 12; i < 16; i++) {
        		keyBits[i] = subKey[i];
        	}
        	
    		for(int i = 0; i < numKeys; i++) {
    			
    			tmpBits = toBinary(i);
    			
    			for(int j = 0; j < 4; j++) {
    				keyBits[j] = tmpBits[j+8];
    			}
    			
    			for(int j = 8; j < 12; j++) {
    				keyBits[j] = tmpBits[j+4];
    			}
    			
    			biases[i] = run(keyBits, "D");
    		}
    		
    		maxBias = getMax(biases);
    		subKey = toBinary(partKey);
    		
    		System.out.print("subkey5 = ");
    		
    		for(int i = 0; i < 4; i++) {
    			System.out.print(subKey[i+8]);
    		}
    		
    		for(int i = 4; i < 8; i++) {
    			System.out.print(keyBits[i]);
    		}
    		
    		for(int i = 8; i < 12; i++) {
    			System.out.print(subKey[i+4]);
    		}
    		
    		for(int i = 12; i < 16; i++) {
    			System.out.print(keyBits[i]);
    		}
			
        } else {
        	
			System.out.print("partial subkey5 = ");
			
			System.out.print("____");
			
			for(int i = 8; i < 12; i++) {
				System.out.print(subKey[i]);
			}
			
			System.out.print("____");
			
			for(int i = 12; i < 16; i++) {
				System.out.print(subKey[i]);
			}
        }
		
		System.out.print("\n");
		
		in.close();
	}
	
	// run for specific key on all ciphertext-plaintext pairs and get the bias
	public static double run(int[] keyBits, String formulaType) {
		
		String plaintext, ciphertext;
		int[] uBits = new int[size]; // u bits
		int[] vBits = new int[size]; // v bits
		int[] pBits = new int[size]; // plaintext bits
		int[] cBits = new int[size]; // ciphertext bits
		int[] result = new int[fileSize];
		int formula = 0;
		int numZeros = 0;
		
		try {
			
			File cipherFile = new File(cipherfile);
			File plainFile = new File(plainfile);
			Scanner pin = new Scanner(plainFile);
			Scanner cin = new Scanner(cipherFile);
			
			for(int pc = 0; pc < fileSize; pc++) {
				
				plaintext = pin.nextLine();
				ciphertext = cin.nextLine();
				
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
				
				if (formulaType.equalsIgnoreCase("A") || formulaType.equalsIgnoreCase("B")) {
					formula = uBits[5] ^ uBits[7] ^ uBits[13] ^ uBits[15] ^ pBits[4] ^ pBits[6] ^ pBits[7];
				} else {
					formula = uBits[1] ^ uBits[5] ^ uBits[9] ^ uBits[13] ^ pBits[0] ^ pBits[3] ^ pBits[8] ^ pBits[11];
				}
				
				result[pc] = formula;
				
			}
			
			numZeros = 0;
			
			for(int i = 0; i < result.length; i++) {
				if (result[i] == 0) numZeros++;
			}
			
			pin.close();
			cin.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return (double) numZeros / result.length - 0.5;
	}
	
	// get U bits from V bits through SBOX
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
				uBits[k] = buf[12 - 4 * i + k];
			}
		}
		
		return uBits;
	}
	
	// convert decimal to binary
	private static int[] toBinary(int number) {
		int[] bitArray = new int[size];
		
		for(int i = 15; i >=0; i--) {
			bitArray[15-i] = (number >> i) & 1;
		}
		
		return bitArray;
	}
	
	// get max of array
	private static double getMax(double[] array) {
		double max = 0;
		
		for(int i = 0; i < array.length; i++) {
			if (Math.abs(array[i]) > Math.abs(max)) {
				max = array[i];
				partKey = i;
			}
		}
		
		return max;
	}

}
