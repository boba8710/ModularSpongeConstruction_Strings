package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;

public class MSC_S_MAIN {
	final static int rate = 320;
	final static int stateSize = 1600;
	final static int capacity = stateSize - rate;
	public static void main(String[] args) {
		
		//CONFIGURATION
		int _popSize = 256;
		int _funcCount = 40;
		double _populationDieOffPercent = 0.50; //A higher value is more selective and less diverse, a lower value is the opposite
		double _mutationChance = 0.35;	//A higher value will increase the chance of random mutation in offspring
		int _preserveTopNIndividuals = 16;
		int _generationCount = 400;
		int _aggressiveThreshold = 15;
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
				System.out.print("\naggressiveThreshold=");
				_aggressiveThreshold = s.nextInt();
				s.close();
			}else if(args[0].equals("-t")){
				try {
					testFunction(args[1]);
				}catch (Exception e){
					System.out.println("Error: usage of -t flag is Configurable_GA.jar -t functionString");
				}
				
				return;
			}else if(args[0].equals("-rs")){
			try {
				generateTestRandDataSqueeze(args[1], Integer.parseInt(args[2]));
			}catch (Exception e){
				System.out.println("Error: usage of -rs flag is Configurable_GA.jar -rs functionString iterations");
			}
				return;
			}else if(args[0].equals("-rh")) {
				try {
					generateTestRandDataHash(args[1], Integer.parseInt(args[2]));
				}catch (Exception e){
					System.out.println("Error: usage of -rh flag is Configurable_GA.jar -rh functionString iterations");
				}
				return;
			}else if(args[0].equals("-rhb")) {
				try {
					generateTestRandDataHashBinary(args[1], Integer.parseInt(args[2]));
				}catch (Exception e){
					System.out.println("Error: usage of -rhb flag is Configurable_GA.jar -rh functionString iterations");
					e.printStackTrace();
				}
				return;
			}else if(args[0].equals("-h")) {
				System.out.println("-p : start a parameterized run of the GA");
				System.out.println("-t functionString : test the function described by functionString");
				System.out.println("-rs functionString iterations : generate pseudorandom data from function described by functionString by XOF, squeezing [iterations] times");
				System.out.println("-rh functionString iterations : generate pseudorandom data from function described by functionString using hashing of low entropy inputs, outputs [iterations] hashes");
				System.out.println("-rhb functionString iterations : generate pseudorandom data from function described by functionString using hashing of low entropy inputs, outputs [iterations] hashes. Output stored as binary data.");
				
			}
		}catch(Exception e) {
			
		}
		final double bitchangeLowerBoundAutostop = 0.4995;
		final double bitchangeUpperBoundAutostop = 0.54;
		final int popSize = _popSize;
		final int aggressiveThreshold = _aggressiveThreshold;
		final int messageCount = 8192;
		final int messageLenBytes = 16;
		final int funcCount = _funcCount;
		final int stateSize = 1600;
		final int rate = 320;
		final int capacity = 1600-rate;
		final double populationDieOffPercent = _populationDieOffPercent; //A higher value is more selective and less diverse, a lower value is the opposite
		final double mutationChance = _mutationChance;	//A higher value will increase the chance of random mutation in offspring
		final int preserveTopNIndividuals = _preserveTopNIndividuals;
		final int generationCount = _generationCount;
		boolean aggressiveMode = false;
		double[] lastScores = new double[aggressiveThreshold];
		
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
		double incrementedMutationChance = mutationChance;
		
		//Run GA
		int lastScoresIterator = 0;
		for(int generation= 0; generation < generationCount-1; generation++) {
			Date dateStart = new Date();
			long startTime = dateStart.getTime();
			ghm.multithreadScore(spongeArray, messages, messagesFlipped, popSize, messageCount);
			ghm.sortPopulationArray(spongeArray);
			for(int i = 0 ; i < popSize; i++) {
				spongeArrayReserve[i] = spongeArray[i];
			}
			lastScores[lastScoresIterator] = spongeArray[popSize-1].geneticScore;
			lastScoresIterator++;
			if(lastScoresIterator == aggressiveThreshold){
				lastScoresIterator = 0;
			}
				double scoreTotal = 0;
				for(double d:lastScores) {
					scoreTotal+=d;
				}
				if(lastScores[0]*aggressiveThreshold==scoreTotal) {
					aggressiveMode = true;
					System.out.println("Stagnant run detected, breaking");
					System.exit(1);
				}else {
					if(aggressiveMode) {
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
				incrementedMutationChance+=0.01;
				if(incrementedMutationChance == 1) {
					incrementedMutationChance = mutationChance;
				}
				ghm.runGenerationOnSortedPopulation(spongeArray, populationDieOffPercent,incrementedMutationChance, preserveTopNIndividuals);
				
			}else {
				incrementedMutationChance = mutationChance;
				ghm.runGenerationOnSortedPopulation(spongeArray, populationDieOffPercent, mutationChance, preserveTopNIndividuals);
			}
			
			
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
		SpongeConstruction_Strings testingFunc = new SpongeConstruction_Strings(stateSize, rate, capacity, new ModularRoundFunction(1600, function));
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
	
	static void generateTestRandDataSqueeze(String function, int iterations){
		SpongeConstruction_Strings testingFunc = new SpongeConstruction_Strings(stateSize, rate, capacity, new ModularRoundFunction(stateSize, function));
		testingFunc.spongeAbsorb("00000000111111110000111100110011010101010");
		System.out.println(testingFunc.spongeSqueeze(iterations));
	}
	static void generateTestRandDataHash(String function, int iterations) {
		SpongeConstruction_Strings testingFunc = new SpongeConstruction_Strings(stateSize, rate, capacity, new ModularRoundFunction(stateSize, function));
		for(int i = 0 ; i < iterations; i++) {
			String hashString = "";
			for(int j = 0; j < i; j++) {
				hashString+="01";
			}
			testingFunc.spongeAbsorb(hashString);
			System.out.println(testingFunc.spongeSqueeze(iterations));
			testingFunc.spongePurge();
		}
	}
	static void generateTestRandDataHashBinary(String function, int iterations) {
		SpongeConstruction_Strings testingFunc = new SpongeConstruction_Strings(stateSize, rate, capacity, new ModularRoundFunction(stateSize, function));
		for(int i = 0 ; i < iterations; i++) {
			String hashString = "";
			for(int j = 0; j < i; j++) {
				hashString+="01";
			}
			testingFunc.spongeAbsorb(hashString);
			String outString = "";
			for(int k = 0 ; k < rate/8;k=k+8) {
				outString += BSF.convertByteToCharacter(testingFunc.spongeSqueeze(1).substring(k, k+8));
			}
			testingFunc.spongePurge();
			System.out.print(outString);
		}
	}
}
