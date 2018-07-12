package main;
/**
 * 
 * @author Zaine Wilson
 * This class contains ways to treat character arrays and characters as bitstrings and bits, respectively. this class seeks to implement speedy ways
 * to apply bitwise operations to correctly-formatted strings.
 */
public class BSF {
	public static char[] xor(char[] s1_char, char[] s2_char) {
		assert s1_char.length == s2_char.length;
		char[] output = new char[s1_char.length];
		for(int i = 0; i < s1_char.length; i++) {
			output[i]=xor(s1_char[i], s2_char[i]);
		}
		return output;
	}
	public static char[] and(char[] s1_char, char[] s2_char) {
		assert s1_char.length == s2_char.length;
		char[] output = new char[s1_char.length];
		for(int i = 0; i < s1_char.length; i++) {
			output[i]=and(s1_char[i], s2_char[i]);
		}
		return output;
	}
	public static char[] or(char[] s1_char, char[] s2_char) {
		assert s1_char.length == s2_char.length;
		char[] output = new char[s1_char.length];
		for(int i = 0; i < s1_char.length; i++) {
			output[i]=or(s1_char[i], s2_char[i]);
		}
		return output;
	}
	public static char[] not(char[] s1_char) {
		char[] output = new char[s1_char.length];
		for(int i = 0; i < s1_char.length; i++) {
			output[i]=not(s1_char[i]);
		}
		return output;
	}
	public static char[] lRot(char[] c1, int rot) {
		char[] output;
		String s1 = String.copyValueOf(c1);
		String rotateChunk = s1.substring(0, rot%c1.length);
		String preserveChunk = s1.substring(rot%c1.length);
		String outStr = preserveChunk+rotateChunk;
		output = outStr.toCharArray();
		return output;
	}
	
	public static char[] add(char[] c1, char[] c2){
		char[] output;
		char[] c1Operating, c2Operating;
		if(c1.length >= c2.length){
			c1Operating = new char[c1.length];
			c2Operating = new char[c1.length];
			for(int i = 0; i < c1.length; i++){
				int lenDif = c1.length - c2.length;
				if(i < lenDif){
					c2Operating[i]='0';
				}else{
					c2Operating[i] = c2[i-lenDif];
				}
				c1Operating[i]=c1[i];
			}
			output = new char[c1.length];
		}else{
			c1Operating = new char[c2.length];
			c2Operating = new char[c2.length];
			for(int i = 0; i < c2.length; i++){
				int lenDif = c2.length - c1.length;
				if(i < lenDif){
					c1Operating[i]='0';
				}else{
					c1Operating[i] = c1[i-lenDif];
				}
				c2Operating[i]=c2[i];
			}
			output = new char[c2.length];
		}
		char carry = '0';
		char sum = '0';
		for(int i = 0 ; i < output.length; i++){
			sum = xor(c1Operating[i],c2Operating[i]);
			sum = xor(sum,carry);
			carry = and(c1Operating[i],c2Operating[i]);
			output[i]=sum;
		}
		
		return output;
	}
	//////////////////////////////////////////////////////////////////////
	
	
	public static char xor(char c1, char c2) {
		if(c1==c2) {
			return '0';
		}else {
			return '1';
		}
	}
	public static char or(char c1, char c2) {
		if(c1 == '1' || c2 == '1') {
			return '1';
		}else {
			return '0';
		}
	}
	public static char and(char c1, char c2) {
		if(c1 == '1' && c2 == '1') {
			return '1';
		}else {
			return '0';
		}
	}
	public static char not(char c1) {
		if(c1 == '0') {
			return '1';
		}else {
			return '0';
		}
	}
	public static char[] reverse(char[] c1) {
		char[] out = new char[c1.length];
		int iterator = 0;
		for(int i = c1.length-1; i >= 0; i--) {
			out[iterator] = c1[i];
			iterator++;
		}
		return out;
	}
	
	/**
	 * 
	 * @param inByte
	 * @return the character value of an 8-bit bitstring
	 */
	public static char convertByteToCharacter(String inByte) {
		assert inByte.length() == 8;
		return (char)Integer.parseUnsignedInt(inByte, 2);
	}
	/////////////////////////////////////////////////////////
}
