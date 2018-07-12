package main;
import org.apache.commons.math3.random.MersenneTwister;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
/**
 * 
 * @author Zaine Wilson
 * This class contains a slew of methods to help with genetic algorithm implementation, including random number generation, generation of testing strings,
 * flipping random bits, genetic crossover, handling of generations, scoring, and some utility methods for display of data (millisToTimeStamp)
 * This class holds the meat of the genetic algorithm.
 */

public class GeneticHelperMethods {
	/**
	 * Always useful to have a random number generator we can access from any method as needed!
	 */
	static Random rand = new Random();
	
	/**
	 * 
	 * @param len
	 * @param seed
	 * @return string of random bits of length len from MersenneTwister, seeded with seed
	 * 
	 * This one isn't really used much in practice, with the other twister method being favored
	 */
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
	/**
	 * 
	 * @param len
	 * @return string of random bits of length len from MersenneTwister seeded with a random value.
	 */
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
	
	/**
	 * 
	 * @param in
	 * @return a string with one character complimented from the input string in
	 */
	public String flipRand(String in) {
		int index = rand.nextInt(in.length());
		char[] out = in.toCharArray();
		out[index]=BSF.not(out[index]);
		return String.copyValueOf(out);
	}
	/**
	 * 
	 * @param h1
	 * @param h2
	 * @return the difference, in percent, between h1 and h2
	 */
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
	/**
	 * 
	 * @param roundFunction
	 * @param mutationChance
	 * @return the roundFunction, with each operation having a mutationChance chance of being replaced with a random operation
	 */
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
	/**
	 * 
	 * @param parent1
	 * @param parent2
	 * @param mutationChance
	 * @return a child round function, created by first crossing over individuals from parent1 and parent2 at a 50% rate, then by passing it through a mutation round with mutationChance.
	 */
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
	/**
	 * 
	 * @param population
	 * @param populationDieOffPercent
	 * @param mutationChance
	 * @param preserveTopNIndividuals
	 * 
	 * This function IS the genetic algorithm, it takes in a sorted population, kills off the bottom populationDieOffPercent of the population, and then spins up breeding
	 * for the remaining individuals. The top N individuals are preserved, and after breeding will be returned to their original indexes, overwriting N offspring.
	 */
	public void runGenerationOnSortedPopulation(SpongeConstruction_Strings[] population, double populationDieOffPercent, double mutationChance, double preserveTopNIndividuals) {
		SpongeConstruction_Strings[] newPopulation = new SpongeConstruction_Strings[population.length];
		SpongeConstruction_Strings[] topIndividuals = new SpongeConstruction_Strings[(int) (population.length-(population.length*(populationDieOffPercent)))]; //The population preserved will be 1-dieoff
		for(int i = population.length-1; i >= (int) (population.length*populationDieOffPercent);i--) { //populate top individuals array
			topIndividuals[i-((int) (population.length*populationDieOffPercent))] = population[i];
		}
		int iterator = 0; 	//breed enough times to fill the population
							//each iteration of breeding, the second parent shifts up one more index, i.e. 1+2,3+4,5+6 one generation becomes 1+3, 3+5 etc etc
		for(int breedingIteration = 0 ; iterator < population.length; breedingIteration++) {
			for(int i = topIndividuals.length-2; i >= 0; i-=2) {
				
				newPopulation[iterator] = new SpongeConstruction_Strings(SpongeConstruction_Strings.stateSize, SpongeConstruction_Strings.rate, SpongeConstruction_Strings.capacity, new ModularRoundFunction(SpongeConstruction_Strings.stateSize, crossoverRoundFunction(topIndividuals[i].f.getFunc(),topIndividuals[(i+1+breedingIteration)%topIndividuals.length].f.getFunc(), mutationChance)));
				iterator++;
			}
		}
		for(int i = 0; i <  population.length; i++){ //Return top N individuals to their original indexes, overwriting offspring.
			if(!(i > population.length-preserveTopNIndividuals)){
				population[i]=newPopulation[i];
			}
		}
	}
	
	public void runAggressiveChangeGeneration(SpongeConstruction_Strings[] population, double populationDieOffPercent) {
		SpongeConstruction_Strings[] newPopulation = new SpongeConstruction_Strings[population.length];
		SpongeConstruction_Strings topIndividual = population[population.length-1];
		for(int i = newPopulation.length-2; i >= 0; i--) {
			newPopulation[i] = new SpongeConstruction_Strings(SpongeConstruction_Strings.stateSize, SpongeConstruction_Strings.rate, SpongeConstruction_Strings.capacity, new ModularRoundFunction(SpongeConstruction_Strings.stateSize, mutateRoundFunction(topIndividual.f.getFunc(),0.50)));
		}
		for(int i = 0; i <  population.length; i++){
			if(!(i > population.length-2)){
				population[i]=newPopulation[i];
			}
		}
	}
	/**
	 * 
	 * @param population
	 * Runs a quicksort on a population, resulting in that population becoming sorted. For credit, see the quicksort class
	 */
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
	
	/**
	 * 
	 * @param spongeArray
	 * @param messages
	 * @param messagesFlipped
	 * @param popSize
	 * @param messageCount
	 * 
	 * This method updates the genetic and bitchange score associated with each individual, which are stored in the individual's SpongeConstruction_Strings class. The scoring
	 * works similarly for the next two methods, but the method that is used for GA runs is multithreadScoring, which results in a speed boost of nearly 4x.
	 * 
	 * messages should be an array of bitstrings generated by MersenneTwister. The messagesFlipped array contains the same messages, except that in each message one bit is complimented.
	 */
	public void score(SpongeConstruction_Strings[] spongeArray, String[] messages, String[] messagesFlipped, int popSize, int messageCount) {
		for(int i = 0; i < popSize; i++) {
			double score = 0;
			for(int j = 0; j < messages.length; j++) {
				spongeArray[i].spongeAbsorb(messages[j]);
				String h1 = spongeArray[i].spongeSqueeze(1);
				spongeArray[i].spongePurge();
				spongeArray[i].spongeAbsorb(messagesFlipped[j]);
				String h2 = spongeArray[i].spongeSqueeze(1);
				spongeArray[i].spongePurge();
				score+=bitchange(h1,h2);
			}
			spongeArray[i].bitchangeScore = score/(double)messageCount; //Associate the bitchange score directly with the bitchange
			spongeArray[i].geneticScore=1/Math.abs(0.5-(score/(double)messageCount)); //Associate a genetic score with the reciprocal of the distance between 0.5 and bitchange. This score will increase as fitness increases, so the algorithm seeks to maximize this value.
			System.out.printf("%.3f%s\n",(double)i*100/(double)popSize,"%");
		}
	}
	/**
	 * 
	 * @param sponge
	 * @param messages
	 * @param messagesFlipped
	 * @param popSize
	 * @param messageCount
	 * Score single is used during multithreaded score, when each individual is scored asynchronously.
	 */
	public void scoreSingle(SpongeConstruction_Strings sponge, String[] messages, String[] messagesFlipped, int popSize, int messageCount) {
			double score = 0;
			for(int j = 0; j < messages.length; j++) {
				sponge.spongeAbsorb(messages[j]);
				String h1 = sponge.spongeSqueeze(1);
				sponge.spongePurge();
				sponge.spongeAbsorb(messagesFlipped[j]);
				String h2 = sponge.spongeSqueeze(1);
				sponge.spongePurge();
				score+=bitchange(h1,h2);
				sponge.bitchangeScore = score/(double)messageCount;
				sponge.geneticScore=1/Math.abs(0.5-(score/(double)messageCount));
			}
			System.out.print("|"); //This | serves as a single pixel on a progress bar; each individual will print this as it finishes scoring
	}
	/**
	 * 
	 * @param spongeArray
	 * @param messages
	 * @param messagesFlipped
	 * @param popSize
	 * @param messageCount
	 * Multithreaded score is used to quickly score individuals within GA runs.
	 */
	public void multithreadScore(SpongeConstruction_Strings[] spongeArray, String[] messages, String[] messagesFlipped, int popSize, int messageCount) {
		final ExecutorService executor = Executors.newCachedThreadPool();
		final List<Future<?>> futures = new ArrayList<>();
		for(SpongeConstruction_Strings sponge : spongeArray) {
			Future<?> future = executor.submit(() -> {
				scoreSingle(sponge, messages, messagesFlipped, popSize, messageCount);
			});
			futures.add(future);
		}
		try {
	        for (Future<?> future : futures) {
	            future.get();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		System.out.println();
	}
}
