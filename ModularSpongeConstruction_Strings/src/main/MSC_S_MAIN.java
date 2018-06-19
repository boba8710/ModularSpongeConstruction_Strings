package main;

import java.util.Arrays;

public class MSC_S_MAIN {
	public static void main(String[] args) {
		int popSize = 128;
		int messageCount = 8196;
		int messageLenBytes = 64;
		int funcCount = 10;
		int stateSize = 1600;
		int rate = 300;
		int capacity = 1600-rate;
		double populationDieOffPercent = 0.75; //A higher value is more selective and less diverse, a lower value is the opposite
		double mutationChance = 0.25;	//A higher value will increase the chance of random mutation in offspring
		RandomFunctionBuilder functionBuilder = new RandomFunctionBuilder(funcCount);
		String[] functionStringPop = new String[popSize];
		ModularRoundFunction[] functionPop = new ModularRoundFunction[popSize];
		SpongeConstruction_Strings[] spongeArray = new SpongeConstruction_Strings[popSize];
		String[] messages = new String[messageCount];
		String[] messagesFlipped = new String[messageCount];
		double[] scoreTable = new double[popSize];
		for(int i = 0; i < functionPop.length; i++) {
			System.out.println(i+":");
			functionStringPop[i] = functionBuilder.genFuncString();
			functionPop[i] = new ModularRoundFunction(stateSize, functionStringPop[i]);
			spongeArray[i] = new SpongeConstruction_Strings(stateSize,rate,capacity, functionPop[i]);
		}
		GeneticHelperMethods ghm = new GeneticHelperMethods();
		for(int i = 0; i < messages.length; i++) {
			messages[i]=ghm.generateMersenneRandomString(messageLenBytes);
			messagesFlipped[i]=ghm.flipRand(messages[i]);
		}
		
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
			System.out.println(i+"	:	"+scoreTable[i]);
		}
		ghm.sortPopulationArray(spongeArray);
		double bestOfRun0 = spongeArray[popSize-1].geneticScore;
		ghm.runGenerationOnSortedPopulation(spongeArray, populationDieOffPercent, mutationChance);
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
			System.out.println(i+"	:	"+scoreTable[i]);
		}
		ghm.sortPopulationArray(spongeArray);
		double bestOfRun1 = spongeArray[popSize-1].geneticScore;
		
		System.out.println(bestOfRun0+"	:	"+bestOfRun1);
		
		
		
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
