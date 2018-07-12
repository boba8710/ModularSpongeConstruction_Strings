package main;
/**
 * 
 * @author Zaine Wilson
 * This class contains the sponge construction, as well as variables to store values important to genetic algorithm functionality
 */
class SpongeConstruction_Strings{
	
	char[] state; 				//The state is treated as a char[], an array of character 1s and 0s
	static int rate; 			//The rate is set statically, not changing between individuals
	static int capacity;		//same with the capacity
	static int stateSize;		//and the state size.
	static char[] iv;			//a variable for initialization vector is included for completeness, but not used in general algorithm runs.
	public double geneticScore;	//stores the geneticScore of the algorithm, see GeneticHelperMethods
	public double bitchangeScore;//stores the bitchangeScore of the algorithm, see GeneticHelperMethods
	RoundFunction f;			//This is the internal round function of the construction.
	@SuppressWarnings("static-access")
	
	/**
	 * 
	 * @param stateSize
	 * @param rate
	 * @param capacity
	 * 
	 * A testing function using a static round function (RoundFunction_Strings)
	 */
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
	/**
	 * 
	 * @param stateSize
	 * @param rate
	 * @param capacity
	 * @param f
	 * 
	 * This function is used for the GA, it has the ability to use a drop-in round function f.
	 */
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
		for(int i = 0; i < stateSize; i++) { //Without an IV, state is initialized to all 0s.
			state[i] = '0';
		}
		this.rate = rate;
		this.capacity = capacity;
	}
	@SuppressWarnings("static-access")
	/**
	 * 
	 * @param stateSize
	 * @param rate
	 * @param capacity
	 * @param iv
	 * 
	 * A testing function using a static round function and a custom IV. Included for completeness, not used in the GA.
	 */
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
	/**
	 * 
	 * @param messageRateChunk
	 * 
	 * Exclusive ors the message into a rate-sized chunk of the state, which is really simply the rate.
	 */
	private void xorIntoState(char[] messageRateChunk) {
		for(int i = 0; i < rate; i++) {
			state[i] = BSF.xor(state[i],messageRateChunk[i]);
		}
	}
	/**
	 * 
	 * @param message
	 * 
	 * Accepts a bitstring message and absorbs it into the state.
	 */
	public void spongeAbsorb(String message) {
		String messageBits = message;
		//System.out.println("Recieved message with bit length: "+messageBits.length());
		int iterator = 0;
		while(messageBits!="") {
			if(messageBits.length() < rate) {
				while(messageBits.length()!=rate) {
					messageBits+='0';				//Pads the message with zeroes until it fits in the rate
				}
				xorIntoState(messageBits.toCharArray());
				messageBits = ""; //If this chunk of message needed padding, it was the last chunk. Set message bits to empty string.
			}else {
				String messageRateChunk = messageBits.substring(0, rate); //If the message is longer than the rate, take the first rateLength many bits of message
				xorIntoState(messageRateChunk.toCharArray()); //Absorb them
				messageBits = messageBits.substring(rate);//And then remove them from the message
				//System.out.println("Sponge absorbing...Remaining bits: "+messageBits.length());
			}
			state = f.runFunction(state); //each time we absorb some part of the message, run the round function on it (as is the sponge construction)
			iterator++;
		}
		//System.out.println("Sponge absorption completed.");
	}
	
	/**
	 * 
	 * @param iterations
	 * @return the digest of the function, squeezed iterations times.
	 */
	public String spongeSqueeze(int iterations){
		//System.out.println("Squeezing state for " + iterations +" iterations");
		String retStr = "";
		for(int i = 0; i < iterations; i++) {
			retStr+=String.copyValueOf(state).substring(0, rate) + "\n";
			state = f.runFunction(state); //After each iteration, run the function again.
			//System.out.println("Squeeze iteration "+i+" completed.");
		}
		return retStr;
	}
	
	/**
	 * This function zeroes out the state of the sponge, and is used as a utility between generations.
	 */
	public void spongePurge() {
		//System.out.println("Purging Sponge at: "+this.toString());
		for(int i = 0; i < stateSize; i++) {
			state[i] = '0';
		}
		
	}
	
}
