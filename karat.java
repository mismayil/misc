import java.util.ArrayList;
import java.util.Scanner;

public class karat {
	
	static int[] multiply(int[] num1, int[] num2) {
		
		int[] product = null;
		int len1 = num1.length;
		int len2 = num2.length;
		
		//base case
		if (len1 <= 100 && len2 <= 100) {
			product = pencilMultiply(num1, num2);
			return product;
		}
		
		int evenLen = 0;
		int max = len1 < len2 ? len2 : len1;
		
		if ((max % 2) == 0) evenLen = max;
		else evenLen = max + 1;
		
		num1 = pad(num1, evenLen);
		num2 = pad(num2, evenLen);
		
		int[] a = null;
		int[] b = null;
		int[] c = null;
		int[] d = null;
		
		a = alloc(num1, evenLen/2, true);
		b = alloc(num1, evenLen/2, false);
		c = alloc(num2, evenLen/2, true);
		d = alloc(num2, evenLen/2, false);
		
		int[] ac = multiply(a, c);
		int[] bd = multiply(b, d);
		int[] a_b = subtract(a, b);
		int[] d_c = subtract(d, c);
		int[] abdc = multiply(a_b, d_c);
		
		product = add(add(add(shift(ac, evenLen), shift(ac, evenLen/2)), add(shift(abdc, evenLen/2), shift(bd, evenLen/2))), bd);
		
		return clean(product);
	}
	
	static int[] pencilMultiply(int[] num1, int[] num2) {
		
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

		product = reverse(terms.get(0));
		for(int i=1; i<terms.size(); i++) {
			product = add(product, reverse(terms.get(i)));
		}
		
		return clean(product);
	}

	//reverse an array
	static int[] reverse(int[] array) {
		int[] newArray = new int[array.length];

		for(int i=0; i<newArray.length; i++) {
			newArray[i] = array[array.length-i-1];
		}

		return newArray;
	}

	//pad array with zeros to make it of even length len
	static int[] pad(int[] array, int len) {
		int[] newArray = new int[len];
		int count = 0;
		
		for(int i=0; i<newArray.length; i++) {
			if (i < len-array.length) newArray[i] = 0;
			else newArray[i] = array[count++];
		}
		
		return newArray;
	}
	
	//allocate an array of size len as first or second half of the array
	static int[] alloc(int[] array, int len, boolean firstHalf) {
		int[] newArray = new int[len];
		
		if (firstHalf) {
			
			for(int i=0; i<len; i++) {
				newArray[i] = array[i];
			}
			
		} else {
			
			for(int i=0; i<len; i++) {
				newArray[i] = array[i+len];
			}
		}
		
		return newArray;
	}
	
	//subtract arrays v and u
	static int[] subtract(int[] v, int[] u) {
		int[] diff = new int[v.length];
		
		if (v.length < u.length) {
			v = pad(v, u.length);
			diff = new int[u.length];
		}
		else if (v.length > u.length) {
			u = pad(u, v.length);
			diff = new int[v.length];
		}
		
		boolean vPositive = true;
		boolean uPositive = true;
		boolean negate = false;
		boolean same = true;
		int vFirst = 0, uFirst = 0;
		
		for(int i=0; i<v.length; i++) {
			if (v[i] != u[i]) {
				vFirst = v[i];
				same = false;
				break;
			}
		}
		
		for(int i=0; i<v.length; i++) {
			if (v[i] < 0) {
				vPositive = false;
				break;
			}
		}
		
		for(int i=0; i<u.length; i++) {
			if (u[i] != v[i]) {
				uFirst = u[i];
				same = false;
				break;
			}
		}
		
		for(int i=0; i<u.length; i++) {
			if (u[i] < 0) {
				uPositive = false;
				break;
			}
		}
		
		if (same) return diff;
		
		if (vPositive == uPositive) {
			
			if (vPositive || uPositive) {
				if (Math.abs(vFirst) < Math.abs(uFirst)) {
					negate = true;
					int[] tmp = u;
					u = v;
					v = tmp;
				}
			} else {
				if (Math.abs(vFirst) < Math.abs(uFirst)) {
					int[] tmp = negate(u);
					u = negate(v);
					v = tmp;
				} else {
					negate = true;
					v = negate(v);
					u = negate(u);
				}
			}
			int pos = 0;
			boolean seen = false;
			for(int i=diff.length-1; i>=0; i--) {
				if (i != 0) {
					if (v[i] < u[i]) {
						
						for(int j=i-1; j>=0; j--) {
							if (v[j] > 0) {
								pos = j;
								seen = true;
								break;
							}
						}
						
						if (seen) {
							
							for(int j=pos; j<i; j++) {
								if (v[j] == 0) v[j] = 9;
								else v[j] -= 1;
							}
							
							diff[i] = 10 + v[i] - u[i];
						} else diff[i] = v[i] - u[i];
					} else diff[i] = v[i] - u[i];
				} else diff[i] = v[i] - u[i];
			}
		} else {
			diff = add(v, negate(u));
		}
		
		if (negate) diff = negate(diff);
		return diff;
	}
	
	//add arrays v and u
	static int[] add(int[] v, int[] u) {
		int[] sum = new int[v.length];
		
		if (v.length < u.length) {
			v = pad(v, u.length);
			sum = new int[u.length];
		}
		else if (v.length > u.length) {
			u = pad(u, v.length);
			sum = new int[v.length];
		}
		
		boolean vPositive = true;
		boolean uPositive = true;
		boolean same = true;
		int vFirst = 0, uFirst = 0;
		
		for(int i=0; i<v.length; i++) {
			if (v[i] != u[i]) {
				vFirst = v[i];
				same = false;
				break;
			}
		}
		
		for(int i=0; i<v.length; i++) {
			if (v[i] < 0) {
				vPositive = false;
				break;
			}
		}
		
		for(int i=0; i<u.length; i++) {
			if (u[i] != v[i]) {
				uFirst = u[i];
				same = false;
				break;
			}
		}
		
		for(int i=0; i<u.length; i++) {
			if (u[i] < 0) {
				uPositive = false;
				break;
			}
		}
		
		if (same || (vPositive == uPositive)) {
			
			int carry = 0;
			
			for(int i=sum.length-1; i>=0; i--) {
				int s = v[i] + u[i] + carry;
				sum[i] = s % 10;
				carry = s / 10;
			}
			
			if (carry != 0) {
				sum = pad(sum, sum.length+1);
				sum[0] = carry;
			}
			
		} else {
			if (vPositive) {
				if (Math.abs(vFirst) <= Math.abs(uFirst)) sum = negate(subtract(negate(u), v));
				else sum = subtract(v, negate(u));
			} else {
				if (Math.abs(vFirst) <= Math.abs(uFirst)) sum = subtract(u, negate(v));
				else sum = negate(subtract(negate(v), u));
			}
		}
		
		return sum;
	}
	
	//negate the array
	static int[] negate(int[] array) {
		
		for(int i=0; i<array.length; i++) {
			array[i] *= -1;
		}
		
		return array;
	}
	
	//shift the array elements to the left by the amount
	static int[] shift(int[] array, int amount) {
		int[] newArray = new int[array.length+amount];
		int count = 0;
		
		for(int i=0; i<array.length; i++) {
			newArray[count++] = array[i];
		}
		
		return newArray;
	}
	
	//clean up the array from leading zeros
	static int[] clean(int[] array) {
		int[] newArray = null;
		int pos = 0;
		int count = 0;
		boolean zero = true;
		
		for(int i=0; i<array.length; i++) {
			if (array[i] != 0) {
				pos = i;
				zero = false;
				break;
			}
		}
		
		if (!zero) {
			newArray = new int[array.length-pos];
			
			for(int i=pos; i<array.length; i++) {
				newArray[count++] = array[i];
			}
		} else {
			newArray = new int[1];
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
