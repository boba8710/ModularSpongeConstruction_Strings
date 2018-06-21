package main;
class SpongeConstruction_Strings{
	
	static char[] state;
	static int rate;
	static int capacity;
	static int stateSize;
	static char[] iv;
	public double geneticScore;
	public double bitchangeScore;
	RoundFunction f;
	@SuppressWarnings("static-access")
	SpongeConstruction_Strings(int stateSize, int rate, int capacity){
		assert rate+capacity==stateSize;
		this.f= new RoundFunction_Strings(stateSize);
		System.out.println("Instanitating sponge function with:");
		System.out.println("	StateSize=	"+stateSize);
		System.out.println("	     Rate=	"+rate);
		System.out.println("	 Capacity=	"+capacity);
		System.out.println("Using Round Function : ");
		System.out.println("	"+f.getFunc());
		this.stateSize = stateSize;
		state = new char[stateSize];
		for(int i = 0; i < stateSize; i++) {
			state[i] = '0';
		}
		this.iv = state;
		this.rate = rate;
		this.capacity = capacity;
	}
	@SuppressWarnings("static-access")
	SpongeConstruction_Strings(int stateSize, int rate, int capacity, RoundFunction f){
		assert rate+capacity==stateSize;
		/*
		System.out.println("Instanitating sponge function with:");
		System.out.println("	StateSize=	"+stateSize);
		System.out.println("	     Rate=	"+rate);
		System.out.println("	 Capacity=	"+capacity);
		System.out.println("Using Round Function : ");
		System.out.println("	"+f.getFunc());
		*/
		this.stateSize = stateSize;
		this.f= f;
		state = new char[stateSize];
		for(int i = 0; i < stateSize; i++) {
			state[i] = '0';
		}
		this.rate = rate;
		this.capacity = capacity;
	}
	@SuppressWarnings("static-access")
	SpongeConstruction_Strings(int stateSize, int rate, int capacity, String iv){
		assert rate+capacity==stateSize;
		System.out.println("Instanitating sponge function with:");
		System.out.println("	StateSize=	"+stateSize);
		System.out.println("	     Rate=	"+rate);
		System.out.println("	 Capacity=	"+capacity);
		System.out.println("Using Round Function : ");
		System.out.println("	"+f.getFunc());
		System.out.println("A custom IV was recieved: ");
		System.out.println(iv);
		this.iv = iv.toCharArray();
		state = new char[stateSize];
		char[] ivArr = iv.toCharArray();
		for(int i = 0; i < stateSize; i++) {
			state[i]=ivArr[i];
		}
		this.rate = rate;
		this.capacity = capacity;
	}
	private static void xorIntoState(char[] messageRateChunk) {
		for(int i = 0; i < rate; i++) {
			state[i] = BSF.xor(state[i],messageRateChunk[i]);
		}
	}
	public void spongeAbsorb(String message) {
		String messageBits = message;
		//System.out.println("Recieved message with bit length: "+messageBits.length());
		int iterator = 0;
		while(messageBits!="") {
			if(messageBits.length() < rate) {
				while(messageBits.length()!=rate) {
					messageBits+='0';
				}
				xorIntoState(messageBits.toCharArray());
				messageBits = "";
			}else {
				String messageRateChunk = messageBits.substring(0, rate);
				xorIntoState(messageRateChunk.toCharArray());
				messageBits = messageBits.substring(rate);
				//System.out.println("Sponge absorbing...Remaining bits: "+messageBits.length());
			}
			state = f.runFunction(state);
			iterator++;
		}
		//System.out.println("Sponge absorbtion completed.");
	}
	
	public String spongeSqueeze(int iterations){
		//System.out.println("Squeezing state for " + iterations +" iterations");
		String retStr = "";
		for(int i = 0; i < iterations; i++) {
			retStr+=String.copyValueOf(state).substring(0, rate) + "\n";
			state = f.runFunction(state);
			//System.out.println("Squeeze iteration "+i+" completed.");
		}
		return retStr;
	}
	public void spongePurge() {
		//System.out.println("Purging Sponge at: "+this.toString());
		for(int i = 0; i < stateSize; i++) {
			state[i] = '0';
		}
		
	}
	
}
