package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class MSC_S_MAIN {
	public static void main(String[] args) {
		
		//CONFIGURATION
		int popSize = 128;
		int messageCount = 4096;
		int messageLenBytes = 16;
		int funcCount = 25;
		int stateSize = 1600;
		int rate = 300;
		int capacity = 1600-rate;
		double populationDieOffPercent = 0.50; //A higher value is more selective and less diverse, a lower value is the opposite
		double mutationChance = 0.25;	//A higher value will increase the chance of random mutation in offspring
		int generationCount = 200;
		
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
				//System.out.printf("%.3f%s\n",(double)i*100/(double)popSize,"%");
				//System.out.println(i+"	:	"+scoreTable[i]);
			}
			ghm.sortPopulationArray(spongeArray);
			Date dateEnd = new Date();
			long endTime = dateEnd.getTime();
			System.out.println("Generation	"+generation+"	completed.");
			System.out.println("Generation runtime: "+ghm.millisToTimestamp(endTime-startTime));
			System.out.println("Average individual runtime:	"+ghm.millisToTimestamp((long)((endTime-startTime)/popSize)));
			System.out.println("Best of run:	"+spongeArray[popSize-1].geneticScore);
			System.out.println("Projected remaining runtime: "+ghm.millisToTimestamp((long)(endTime-startTime)*(generationCount-generation)));
			ghm.runGenerationOnSortedPopulation(spongeArray, populationDieOffPercent, mutationChance);
			
			
			
		}
		
		Date runEnd = new Date();
		
		FileWriter finalPopulationData;
		try {
			File outputFile = new File("FinalRunData"+runEnd.toString()+".log");
			outputFile.createNewFile();
			 finalPopulationData = new FileWriter(outputFile);
			for(int i = 0; i < popSize; i++){
				finalPopulationData.write(Integer.toString(i)+"	:\n");
				finalPopulationData.write("Fitness Score:	"+spongeArray[i].geneticScore+"\n");
				finalPopulationData.write("Round Function:\n");
				finalPopulationData.write(spongeArray[i].f.getFunc()+"\n");
				finalPopulationData.write("=================================================================\n");
				
			}
			System.out.println("File output succeeded, output saved to "+"FinalRunData"+runEnd.toString()+".log");
			finalPopulationData.close();
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
