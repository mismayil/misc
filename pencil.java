import java.util.Scanner;
import java.util.ArrayList;

public class pencil {
	
	static int[] multiply(int[] num1, int[] num2) {
		
		int[] product = null;
		int carry = 0;
		ArrayList<int[]> terms = new ArrayList<int[]>();
		int[] term = null;
		int count = 0;
		int pLength = num1.length + num2.length;
		int sum = 0;
		
		for(int i=num2.length-1; i>=0; i--) {
			
			carry = 0;
			count = 0;
			term = new int[num1.length+num2.length];
			
			for(int j=num1.length-1; j>=0; j--) {
				int p = num1[j] * num2[i] + carry;
				term[count++] = p % 10;
				carry = p / 10;
			}
			
			if (carry != 0) term[count++] = carry;
			
			terms.add(term);
		}
		
		for(int i=0; i<terms.size(); i++) {
			
			term = terms.get(i);
			
			for(int j=term.length-i-1; j>=0; j--) {
				term[j+i] = term[j];
			}
			
			for(int j=0; j<i; j++) {
				term[j] = 0;
			}
		}
		
		carry = 0;
		count = 0;
		product = new int[pLength+1];
		
		for(int i=0; i<pLength; i++) {
			
			sum = 0;
			
			for(int j=0; j<terms.size(); j++) {
				term = terms.get(j);
				sum += term[i];
			}
			
			sum += carry;
			carry = sum / 10;
			product[count++] = sum % 10;
		}
		
		if (carry != 0) product[count] = carry;
		
		return clean(product);
	}
	
	//clean up an array from leading zeros and reverse it
	static int[] clean(int[] array) {
		int[] newArray = null;
		int pos = 0;
		int count = 0;
		
		for(int i=array.length-1; i>=0; i--) {
			if (array[i] != 0) {
				pos = i;
				break;
			}
		}
		
		newArray = new int[pos+1];
		
		for(int i=pos; i>=0; i--) {
			newArray[count++] = array[i];
		}
		
		return newArray;
	}
	
	public static void main(String[] args) {
		
		Scanner in = new Scanner(System.in);
		ArrayList<int[]> numbers = new ArrayList<int[]>();
		int[] number = null;
		int[] product = null;
		String line = null;
		int count = 0;
		
		while (in.hasNextLine()) {
			
			line = in.nextLine();
			number = new int[line.length()];
			count = 0;
			
			for(int i=0; i<line.length(); i++) {
				number[count++] = line.charAt(i) - 48;
			}
			
			numbers.add(number);
		}
		
		in.close();
		product = numbers.get(0);
		
		for(int i=1; i<numbers.size(); i++) {
			product = multiply(product, numbers.get(i));
		}
		
		for(int i=0; i<product.length; i++) {
			System.out.print(product[i]);
		}
		
		System.out.print("\n");
	}

}
