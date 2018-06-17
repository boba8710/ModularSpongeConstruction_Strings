package main;
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
	/////////////////////////////////////////////////////////
}
