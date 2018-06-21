package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;

public class MSC_S_MAIN {
	public static void main(String[] args) {
		
		//CONFIGURATION
		int popSize = 64;
		int messageCount = 8192;
		int messageLenBytes = 16;
		int funcCount = 25;
		int stateSize = 1600;
		int rate = 300;
		int capacity = 1600-rate;
		double populationDieOffPercent = 0.50; //A higher value is more selective and less diverse, a lower value is the opposite
		double mutationChance = 0.75;	//A higher value will increase the chance of random mutation in offspring
		int preserveTopNIndividuals = 8;
		int generationCount = 100;
		//adding -p will enable parameter entry
		try {
			if(args[0].equals("-p")) {
				Scanner s = new Scanner(System.in);
				System.out.println("Parameters:");
				System.out.print("popSize=");
				popSize = s.nextInt();
				System.out.print("\nfuncCount=");
				funcCount = s.nextInt();
				System.out.print("\npopulationDieOffPercent=");
				populationDieOffPercent = s.nextDouble();
				System.out.print("\nmutationChance=");
				mutationChance = s.nextDouble();
				System.out.print("\npreserveTopNIndividuals=");
				preserveTopNIndividuals = s.nextInt();
				System.out.print("\ngenerationCount=");
				generationCount = s.nextInt();
				s.close();
			}
		}catch(Exception e) {
			
		}
		
		
		//RANDOM GENERATION OF INITIAL POPULATION
		RandomFunctionBuilder functionBuilder = new RandomFunctionBuilder(funcCount);
		String[] functionStringPop = new String[popSize];
		ModularRoundFunction[] functionPop = new ModularRoundFunction[popSize];
		SpongeConstruction_Strings[] spongeArray = new SpongeConstruction_Strings[popSize];
		String[] messages = new String[messageCount];
		String[] messagesFlipped = new String[messageCount];
		double[] scoreTable = new double[popSize];
		for(int i = 0; i < functionPop.length; i++) {
			//System.out.println(i+":");
			functionStringPop[i] = functionBuilder.genFuncString();
			functionPop[i] = new ModularRoundFunction(stateSize, functionStringPop[i]);
			spongeArray[i] = new SpongeConstruction_Strings(stateSize,rate,capacity, functionPop[i]);
		}
		
		//GENERATION OF MESSAGE SET
		GeneticHelperMethods ghm = new GeneticHelperMethods();
		for(int i = 0; i < messages.length; i++) {
			messages[i]=ghm.generateMersenneRandomString(messageLenBytes);
			messagesFlipped[i]=ghm.flipRand(messages[i]);
		}
		System.out.println("Population ready, running generations...");
		double[] topScores = new double[generationCount];
		//Run GA
		Date runStart = new Date();
		long runStartTime = runStart.getTime();
		for(int generation= 0; generation < generationCount; generation++) {
			Date dateStart = new Date();
			long startTime = dateStart.getTime();
			for(int i = 0; i < popSize; i++) {
				double score = 0;
				for(int j = 0; j < messages.length; j++) {
					spongeArray[i].spongeAbsorb(messages[j]);
					String h1 = spongeArray[i].spongeSqueeze(1);
					spongeArray[i].spongePurge();
					spongeArray[i].spongeAbsorb(messagesFlipped[j]);
					String h2 = spongeArray[i].spongeSqueeze(1);
					spongeArray[i].spongePurge();
					score+=ghm.bitchange(h1,h2);
				}
				spongeArray[i].geneticScore=score/(double)messageCount;
				scoreTable[i] = score/(double)messageCount;
				System.out.printf("%.3f%s\n",(double)i*100/(double)popSize,"%");
			}
			ghm.sortPopulationArray(spongeArray);
			Date dateEnd = new Date();
			long endTime = dateEnd.getTime();
			System.out.println("Generation	"+generation+"	completed.");
			System.out.println("Generation runtime: "+ghm.millisToTimestamp(endTime-startTime));
			System.out.println("Average individual runtime:	"+ghm.millisToTimestamp((long)((endTime-startTime)/popSize)));
			System.out.println("Best of gen:	"+spongeArray[popSize-1].geneticScore);
			topScores[generation] = spongeArray[popSize-1].geneticScore;
			System.out.println("Projected remaining runtime: "+ghm.millisToTimestamp((long)(endTime-startTime)*(generationCount-generation)));
			ghm.runGenerationOnSortedPopulation(spongeArray, populationDieOffPercent, mutationChance, preserveTopNIndividuals);
			
			
			
		}
		
		
		//Output run data
		Date runEnd = new Date();
		try {
			PrintWriter finalPopulationWriter = new PrintWriter("FinalRunPopulation"+runEnd.getTime()+".log");
			PrintWriter dataWriter = new PrintWriter("RunData"+runEnd.getTime()+".csv");
			finalPopulationWriter.println("Parameters:");
			finalPopulationWriter.println("popSize: "+popSize);
			finalPopulationWriter.println("messageCount: "+messageCount);
			finalPopulationWriter.println("messageLength: "+messageLenBytes);
			finalPopulationWriter.println("funcCount: "+funcCount);
			finalPopulationWriter.println("stateSize: "+stateSize);
			finalPopulationWriter.println("rate: "+rate);
			finalPopulationWriter.println("capacity: "+capacity);
			finalPopulationWriter.println("populationDieOffPercent: "+populationDieOffPercent);
			finalPopulationWriter.println("mutationChance: "+mutationChance);
			finalPopulationWriter.println("preserveTopNIndividuals: "+preserveTopNIndividuals);
			finalPopulationWriter.println("generationCount: "+generationCount);
			for(int i = 0; i < popSize; i++){
				finalPopulationWriter.print(Integer.toString(i)+"	:\n");
				finalPopulationWriter.print("Fitness Score:	"+spongeArray[i].geneticScore+"\n");
				finalPopulationWriter.print("Round Function:\n");
				finalPopulationWriter.print(spongeArray[i].f.getFunc()+"\n");
				finalPopulationWriter.print("=================================================================\n\n");
			}
			for(int i = 0; i < generationCount; i++) {
				dataWriter.print(i+","+topScores[i]+"\n");
			}
			
			System.out.println("File output succeeded, output saved to "+"FinalRunPopulation"+runEnd.getTime()+".log"+" and "+"RunData"+runEnd.getTime()+".csv");
			finalPopulationWriter.close();
			dataWriter.close();
		} catch (IOException e1) {
			System.out.println("File output failed.");
			e1.printStackTrace();
		}
		
		long runEndTime = runEnd.getTime();
		System.out.println("Run completed. Run time elapsed: "+ghm.millisToTimestamp(runEndTime-runStartTime));
		
		
		
		
		
		/*	Random Search Code
		for(int i = 0; i < popSize; i++) {
			double score = 0;
			for(int j = 0; j < messages.length; j++) {
				spongeArray[i].spongeAbsorb(messages[j]);
				String h1 = spongeArray[i].spongeSqueeze(1);
				spongeArray[i].spongePurge();
				spongeArray[i].spongeAbsorb(messagesFlipped[j]);
				String h2 = spongeArray[i].spongeSqueeze(1);
				spongeArray[i].spongePurge();
				score+=ghm.bitchange(h1,h2);
			}
			scoreTable[i]=score/(double)messageCount;
			if(i%32==0){
				System.out.printf("%.3f%s\n",(double)i*100/(double)popSize,"%");
				System.out.println(i+"	:	"+scoreTable[i]);
			}
		}
		for(int i = 0; i < popSize; i++){
			System.out.println(i+"	:	"+scoreTable[i]+"	:	"+functionPop[i].getFunc());
		}
		Arrays.sort(scoreTable);
		System.out.println("Max of run: "+scoreTable[scoreTable.length-1]);
		*/
	}
}
