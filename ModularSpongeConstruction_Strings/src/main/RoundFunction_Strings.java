package main;
public class RoundFunction_Strings extends RoundFunction {
	int stateSize;
	/**
	 * 
	 * @param stateSize
	 * 
	 * This is a static testing function, not used in actual runs.
	 */
	public RoundFunction_Strings(int stateSize) {
		super(stateSize);
	}
	
	public char[] runFunction(char[] state) {
		stateSize = state.length; //workaround
		for(int i = 0; i < stateSize; i++) {
			for(int j = 0; j < stateSize; j++) {
				if(i!=j) {
					for(int k = 0; k < 25; k++) {
						state[(i+k)%stateSize]=BSF.xor(state[(i+j)%stateSize], state[(i+k)%stateSize]);
					}
				}
			}
		}
		char[] output = state;
		return output;
	}

	@Override
	public String getFunc() {
		return "Static Function";
	}
	
}
