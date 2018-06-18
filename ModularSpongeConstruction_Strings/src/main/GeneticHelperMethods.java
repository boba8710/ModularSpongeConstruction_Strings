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
		assert parent1.length() == parent2.length();
		Random crossoverRoundFunctionGenerator = new Random();
		//This assumes parent1 dominant for crossoverChance > 50
		String child = "";
		String[] parent1OpArray = parent1.split("#");
		String[] parent2OpArray = parent2.split("#");
		for(int i = 0 ; i < parent1OpArray.length; i++) {
			int randInt = crossoverRoundFunctionGenerator.nextInt(100);
			if(randInt < crossoverChance) {
				child+=parent1OpArray[i]+"#";
			}else {
				child+=parent2OpArray[i]+"#";
			}
		}
		return child;
	}
	public void runGenerationOnSortedPopulation(SpongeConstruction_Strings[] population, double crossoverChance, double mutationChance) {
		for(int i = population.length-1; i > population.length/2; i--) {
			population[i]=null; //dead
		}
		int iterator = population.length/2;
		for(int i = 0; i < (population.length/2)-1; i+=2) {
			population[iterator] = new SpongeConstruction_Strings(SpongeConstruction_Strings.stateSize, SpongeConstruction_Strings.rate, SpongeConstruction_Strings.capacity, crossoverRoundFunction(population[i].f.getFunc(),population[i+1].f.getFunc(),crossoverChance));
			iterator++;
		}
		iterator = 0;
		for(int i = population.length * 3/4; i < population.length; i++) {
			population[i] = new SpongeConstruction_Strings(SpongeConstruction_Strings.stateSize, SpongeConstruction_Strings.rate, SpongeConstruction_Strings.capacity, mutateRoundFunction(population[iterator].f.getFunc(), mutationChance));
		}
	}
}
