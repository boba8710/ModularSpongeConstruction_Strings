package main;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.Arrays;
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
		double rand = mutationRandomGenerator.nextDouble();
		for(int i =0 ;i < operationArray.length; i++) {
			if(rand < mutationChance) {
				operationArray[i]=rfb.genRandOperation();
			}
		}
		
		String retString = "";
		for(String s : operationArray) {
			retString+=s+"#";
		}
		
		return retString;
	}
	public String crossoverRoundFunction(String parent1, String parent2, double mutationChance) {
		assert parent1.length() == parent2.length();
		Random crossoverRoundFunctionGenerator = new Random();
		String child = "";
		String[] parent1OpArray = parent1.split("#");
		String[] parent2OpArray = parent2.split("#");
		for(int i = 0 ; i < parent1OpArray.length; i++) {
			double rand = crossoverRoundFunctionGenerator.nextDouble();
				if(rand >= 0.5) {
					child+=parent1OpArray[i]+"#";
				}else {
					child+=parent2OpArray[i]+"#";
				}
		}
		child = mutateRoundFunction(child, mutationChance);
		return child;
	}
	public void runGenerationOnSortedPopulation(SpongeConstruction_Strings[] population, double populationDieOffPercent, double mutationChance) {
		SpongeConstruction_Strings[] newPopulation = new SpongeConstruction_Strings[population.length];
		SpongeConstruction_Strings[] topIndividuals = new SpongeConstruction_Strings[(int) (population.length-(population.length*(populationDieOffPercent)))]; //The population preserved will be 1-dieoff
		for(int i = population.length-1; i >= (int) (population.length*populationDieOffPercent);i--) { //populate top individuals array
			topIndividuals[i-((int) (population.length*populationDieOffPercent))] = population[i];
		}
		int iterator = 0; 	//breed enough times to fill the population
							//each iteration of breeding, the second parent shifts up one more index, i.e. 1+2,3+4,5+6 one generation becomes 1+3, 3+5 etc etc
		for(int breedingIteration = 0 ; breedingIteration < 1/1-(populationDieOffPercent); breedingIteration++) {
			for(int i = topIndividuals.length-2; i >= 0; i-=2) {
				//System.out.println(iterator+":");
				newPopulation[iterator] = new SpongeConstruction_Strings(SpongeConstruction_Strings.stateSize, SpongeConstruction_Strings.rate, SpongeConstruction_Strings.capacity, new ModularRoundFunction(SpongeConstruction_Strings.stateSize, crossoverRoundFunction(topIndividuals[i].f.getFunc(),topIndividuals[(i+1+breedingIteration)%topIndividuals.length].f.getFunc(), mutationChance)));
				iterator++;
			}
		}
	}
	public void sortPopulationArray(SpongeConstruction_Strings[] population) {
		Quicksort.quickSort(population, 0, population.length-1);
	}
	
	//Credit for the majority of this method goes to mkyong (www.mkyong.com)
	public String millisToTimestamp(long milliseconds){
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = milliseconds / daysInMilli;
		milliseconds = milliseconds % daysInMilli;
		
		long elapsedHours = milliseconds / hoursInMilli;
		milliseconds = milliseconds % hoursInMilli;
		
		long elapsedMinutes = milliseconds / minutesInMilli;
		milliseconds = milliseconds % minutesInMilli;
		
		long elapsedSeconds = milliseconds / secondsInMilli;
		
		milliseconds = milliseconds - elapsedSeconds*secondsInMilli;
		return elapsedDays+":"+elapsedHours+":"+elapsedMinutes+":"+elapsedSeconds+"."+milliseconds;
	}
}
