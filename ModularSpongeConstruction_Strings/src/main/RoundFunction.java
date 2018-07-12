package main;

public abstract class RoundFunction {
	int stateSize;
	/**
	 * 
	 * @param stateSize
	 * 
	 * Parent class of all encoded round functions. Currently, the statesize is locked to 1600 bits.
	 */
	RoundFunction(int stateSize){
		this.stateSize = stateSize;
		assert stateSize == 1600;
	}
	abstract char[] runFunction(char[] state);
	abstract public String getFunc();
}
