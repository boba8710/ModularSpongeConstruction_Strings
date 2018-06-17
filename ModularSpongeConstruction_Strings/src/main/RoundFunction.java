package main;

public abstract class RoundFunction {
	int stateSize;
	RoundFunction(int stateSize){
		this.stateSize = stateSize;
		assert stateSize == 1600;
	}
	abstract char[] runFunction(char[] state);
	abstract public String getFunc();
}
