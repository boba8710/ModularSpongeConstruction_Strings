package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Stream;

public class MSC_S_MAIN {
	public static void main(String[] args) {
		
		//CONFIGURATION
		int _popSize = 64;
		int _funcCount = 25;
		double _populationDieOffPercent = 0.50; //A higher value is more selective and less diverse, a lower value is the opposite
		double _mutationChance = 0.75;	//A higher value will increase the chance of random mutation in offspring
		int _preserveTopNIndividuals = 8;
		int _generationCount = 100;
		//adding -p will enable parameter entry
		try {
			if(args[0].equals("-p")) {
				Scanner s = new Scanner(System.in);
				System.out.println("Parameters:");
				System.out.print("popSize=");
				_popSize = s.nextInt();
				System.out.print("\nfuncCount=");
				_funcCount = s.nextInt();
				System.out.print("\npopulationDieOffPercent=");
				_populationDieOffPercent = s.nextDouble();
				System.out.print("\nmutationChance=");
				_mutationChance = s.nextDouble();
				System.out.print("\npreserveTopNIndividuals=");
				_preserveTopNIndividuals = s.nextInt();
				System.out.print("\ngenerationCount=");
				_generationCount = s.nextInt();
				s.close();
			}
		}catch(Exception e) {
			
		}
		final double bitchangeLowerBoundAutostop = 0.49;
		final double bitchangeUpperBoundAutostop = 0.54;
		final int popSize = _popSize;
		final int aggressiveThreshold = 35;
		final int messageCount = 8192;
		final int messageLenBytes = 16;
		final int funcCount = _funcCount;
		final int stateSize = 1600;
		final int rate = 300;
		final int capacity = 1600-rate;
		final double populationDieOffPercent = _populationDieOffPercent; //A higher value is more selective and less diverse, a lower value is the opposite
		final double mutationChance = _mutationChance;	//A higher value will increase the chance of random mutation in offspring
		final int preserveTopNIndividuals = _preserveTopNIndividuals;
		final int generationCount = _generationCount;
		boolean aggressiveMode = false;
		double[] lastScores = new double[aggressiveThreshold];
		int lastTenIterator=0;
		
		//RANDOM GENERATION OF INITIAL POPULATION
		RandomFunctionBuilder functionBuilder = new RandomFunctionBuilder(funcCount);
		String[] functionStringPop = new String[popSize];
		ModularRoundFunction[] functionPop = new ModularRoundFunction[popSize];
		SpongeConstruction_Strings[] spongeArray = new SpongeConstruction_Strings[popSize];
		SpongeConstruction_Strings[] spongeArrayReserve = new SpongeConstruction_Strings[popSize];
		String[] messages = new String[messageCount];
		String[] messagesFlipped = new String[messageCount];
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
		double[] topBitchange = new double[generationCount];
		
		
		
		
		Date runStart = new Date();
		long runStartTime = runStart.getTime();
		//Hook exit to runtime end
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				
				Date runEnd = new Date();
				try {
					System.out.println("Run Ended.");
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
						finalPopulationWriter.print("Fitness Score:	"+spongeArrayReserve[i].geneticScore+"\n");
						finalPopulationWriter.print("Bitchange:	"+(spongeArrayReserve[i].bitchangeScore)+"\n");
						finalPopulationWriter.print("Round Function:\n");
						finalPopulationWriter.print(spongeArrayReserve[i].f.getFunc()+"\n");
						finalPopulationWriter.print("=================================================================\n\n");
					}
					for(int i = 0; i < generationCount; i++) {
						dataWriter.print(i+","+topScores[i]+","+topBitchange[i]+"\n");
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
			}
		});

		
		//Run GA
		for(int generation= 0; generation < generationCount-1; generation++) {
			Date dateStart = new Date();
			long startTime = dateStart.getTime();
			ghm.multithreadScore(spongeArray, messages, messagesFlipped, popSize, messageCount);
			ghm.sortPopulationArray(spongeArray);
			for(int i = 0 ; i < popSize; i++) {
				spongeArrayReserve[i] = spongeArray[i];
			}
			lastScores[generation%9] = (0.50-(1/spongeArray[popSize-1].geneticScore));
			if(generation >= aggressiveThreshold) {
				double scoreProd = 1;
				for(double score : lastScores) {
					scoreProd*=score;
				}
				if(lastScores[0]*aggressiveThreshold==scoreProd) {
					aggressiveMode = true;
					System.out.println("!!!AGGRESSIVE GROWTH ENGAGED!!!");
				}else {
					aggressiveMode = false;
					System.out.println("!!!AGGRESSIVE GROWTH DISENGAGED!!!");
				}
			}
			Date dateEnd = new Date();
			long endTime = dateEnd.getTime();
			System.out.println("Generation	"+generation+"	completed.");
			System.out.println("Generation runtime: "+ghm.millisToTimestamp(endTime-startTime));
			System.out.println("Average individual runtime:	"+ghm.millisToTimestamp((long)((endTime-startTime)/popSize)));
			System.out.println("Best of gen:	"+spongeArray[popSize-1].geneticScore);
			System.out.println("Max bitchange(+/-):	"+(0.50-(1/spongeArray[popSize-1].geneticScore)));
			topScores[generation] = spongeArray[popSize-1].geneticScore;
			topBitchange[generation] = spongeArray[popSize-1].bitchangeScore;
			System.out.println("Projected remaining runtime: "+ghm.millisToTimestamp((long)(endTime-startTime)*(generationCount-generation)));
			if(aggressiveMode) {
				ghm.runGenerationOnSortedPopulation(spongeArray, populationDieOffPercent, 0.90, 1);
				
			}else {
				ghm.runGenerationOnSortedPopulation(spongeArray, populationDieOffPercent, mutationChance, preserveTopNIndividuals);
			}
			ghm.runGenerationOnSortedPopulation(spongeArray, populationDieOffPercent, mutationChance, preserveTopNIndividuals);
			
			if(topBitchange[generation]<bitchangeUpperBoundAutostop&&topBitchange[generation]>bitchangeLowerBoundAutostop) {
				System.out.println("Target bitchange detected! Autostop!");
				break;
			}
			
		}
		//Score the last generation
		ghm.multithreadScore(spongeArray, messages, messagesFlipped, popSize, messageCount);
		ghm.sortPopulationArray(spongeArray);
		System.out.println("Generation	"+(generationCount-1)+"	completed.");
		System.out.println("Best of gen:	"+spongeArray[popSize-1].geneticScore);
		System.out.println("Max bitchange:	"+(spongeArray[popSize-1].bitchangeScore));
		topScores[generationCount-1] = spongeArray[popSize-1].geneticScore;
		topBitchange[generationCount-1] = spongeArray[popSize-1].bitchangeScore;
		System.out.println(topBitchange[generationCount-1]);
		for(int i = 0 ; i < popSize; i++) {
			spongeArrayReserve[i] = spongeArray[i];
		}
		
		
		
		
		
		
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
	static void testFunction(String function) {
		SpongeConstruction_Strings testingFunc = new SpongeConstruction_Strings(1600, 300, 1300, new ModularRoundFunction(1600, function));
		GeneticHelperMethods ghm = new GeneticHelperMethods();
		String[] messages = new String[8196];
		String[] messagesFlipped = new String[8196];
		
		for(int i = 0; i < messages.length; i++) {
			messages[i]=ghm.generateMersenneRandomString(16);
			messagesFlipped[i]=ghm.flipRand(messages[i]);
		}
		
			double score = 0;
			for(int j = 0; j < messages.length; j++) {
				testingFunc.spongeAbsorb(messages[j]);
				String h1 = testingFunc.spongeSqueeze(1);
				testingFunc.spongePurge();
				testingFunc.spongeAbsorb(messagesFlipped[j]);
				String h2 = testingFunc.spongeSqueeze(1);
				testingFunc.spongePurge();
				score+=ghm.bitchange(h1,h2);
			}
			testingFunc.bitchangeScore = score/(double)8196;
			testingFunc.geneticScore=1/Math.abs(0.5-(score/(double)8196));
			System.out.println("Function under test scored a bitchange of: "+testingFunc.bitchangeScore);
			System.out.println("And a cooresponding fitness score of     : "+testingFunc.geneticScore);
		
	}
}
