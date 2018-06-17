package main;
public class MSC_S_MAIN {
	public static void main(String[] args) {
		
		//Testing code for round functions and the construction
		/*SpongeConstruction_Strings sponge = new SpongeConstruction_Strings(1600, 300, 1300);
		SpongeConstruction_Strings spongeFlipped = new SpongeConstruction_Strings(1600, 300, 1300);
		Mersenne_Random_Strings rss = new Mersenne_Random_Strings();
		sponge.spongeAbsorb(rss.generateMersenneRandomString(64,123456789));
		String hashBits = sponge.spongeSqueeze(1);
		String hashBitsFlipped = spongeFlipped.spongeSqueeze(1);
		for(char c:hashBits.toCharArray()) {
			if(c!='\n') {
				System.out.print(" "+c);
			}else {
				System.out.print(c);
			}
		}
		for(char c:hashBitsFlipped.toCharArray()) {
			if(c!='\n') {
				System.out.print(" "+c);
			}else {
				System.out.print(c);
			}
		}
		int totalBitchange = 0;
		for(int i = 0; i < hashBits.length(); i++) {
			if(hashBits.toCharArray()[i]!=hashBitsFlipped.toCharArray()[i]) {
				totalBitchange+=1;
			}
		}
		System.out.println("Approximate Bitchange Percentage: "+(double)totalBitchange*100.0/(double)hashBits.length()+"%");*/
		int popSize = 1024;
		int messageCount = 8196;
		int messageLenBytes = 64;
		int funcCount = 20;
		int stateSize = 1600;
		int rate = 300;
		int capacity = 1600-rate;
		RandomFunctionBuilder functionBuilder = new RandomFunctionBuilder(funcCount);
		String[] functionStringPop = new String[popSize];
		ModularRoundFunction[] functionPop = new ModularRoundFunction[popSize];
		SpongeConstruction_Strings[] spongeArray = new SpongeConstruction_Strings[popSize];
		String[] messages = new String[messageCount];
		String[] messagesFlipped = new String[messageCount];
		double[] scoreTable = new double[popSize];
		for(int i = 0; i < functionPop.length; i++) {
			functionStringPop[i] = functionBuilder.genFuncString();
			functionPop[i] = new ModularRoundFunction(stateSize, functionStringPop[i]);
			spongeArray[i] = new SpongeConstruction_Strings(stateSize,rate,capacity, functionPop[i]);
		}
		Mersenne_Random_Strings mrs = new Mersenne_Random_Strings();
		for(int i = 0; i < messages.length; i++) {
			messages[i]=mrs.generateMersenneRandomString(messageLenBytes);
			messagesFlipped[i]=mrs.flipRand(messages[i]);
		}
		for(int i = 0; i < popSize; i++) {
			double score = 0;
			for(int j = 0; j < messages.length; j++) {
				//System.out.println("Candidate	"+i+"	is processing string	"+j);
				
				spongeArray[i].spongeAbsorb(messages[j]);
				String h1 = spongeArray[i].spongeSqueeze(1);
				spongeArray[i].spongePurge();
				spongeArray[i].spongeAbsorb(messagesFlipped[j]);
				String h2 = spongeArray[i].spongeSqueeze(1);
				spongeArray[i].spongePurge();
				score+=mrs.bitchange(h1,h2);
			}
			scoreTable[i]=score/(double)messageCount;
			System.out.println(i+"	:	"+scoreTable[i]);
		}
	}
}
