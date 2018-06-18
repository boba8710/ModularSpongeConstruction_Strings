package main;
import org.apache.commons.math3.random.MersenneTwister;
import java.util.Random;
public class GeneticHelperMethods {
	static Random rand = new Random();
	public String generateMersenneRandomString(int len, int seed) {
		MersenneTwister bustaRhymes = new MersenneTwister(seed);
		byte[] output = new byte[len];
		bustaRhymes.nextBytes(output, 0, len);
		String outputString = "";
		for(byte b : output) {
			String temp = Integer.toBinaryString(b+128);
			while(temp.length()<8) {
				temp='0'+temp;
			}
			outputString=outputString+temp;
		}
		return outputString;
	}
	public String generateMersenneRandomString(int len) {
		MersenneTwister bustaRhymes = new MersenneTwister();
		byte[] output = new byte[len];
		bustaRhymes.nextBytes(output, 0, len);
		String outputString = "";
		for(byte b : output) {
			String temp = Integer.toBinaryString(b+128);
			while(temp.length()<8) {
				temp='0'+temp;
				
			}
			outputString=outputString+temp;
		}
		return outputString;
	}
	
	public String flipRand(String in) {
		int index = rand.nextInt(in.length());
		char[] out = in.toCharArray();
		out[index]=BSF.not(out[index]);
		return String.copyValueOf(out);
	}
	public double bitchange(String h1, String h2) {
		assert h1.length() == h2.length();
		double total = 0;
		double iterations = h1.length();
		for(int i = 0; i < h1.length(); i++) {
			if(h1.charAt(i)!=h2.charAt(i)) {
				total++;
			}
		}
		return total/iterations;
	}
	public String mutateRoundFunction(String roundFunction, double mutationChance) {
		String[] operationArray = roundFunction.split("#");
		Random mutationRandomGenerator = new Random();
		RandomFunctionBuilder rfb = new RandomFunctionBuilder();
		int randInt = mutationRandomGenerator.nextInt(100);
		for(int i =0 ;i < operationArray.length; i++) {
			if(randInt < mutationChance) {
				operationArray[i]=rfb.genRandOperation();
			}
		}
		
		String retString = "";
		for(String s : operationArray) {
			retString+=s+"#";
		}
		
		return retString;
	}
	public String crossoverRoundFunction(String parent1, String parent2, double crossoverChance) {
		
		return null;
	}
	
}
