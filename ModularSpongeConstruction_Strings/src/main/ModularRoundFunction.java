package main;

import HashOperations.HashOperation;

public class ModularRoundFunction extends RoundFunction {
	String parameterString;
	static HashOperation[] opTable = {new HashOperations.AND(), new HashOperations.LROT(), new HashOperations.NOT(), new HashOperations.OR(), new HashOperations.SWAP(), new HashOperations.SWAP0(), new HashOperations.SWAP1(), new HashOperations.SWAP2(), new HashOperations.XOR()};
	ModularRoundFunction(int stateSize, String parameterString) {
		super(stateSize);
		this.parameterString = parameterString;
	}
	@Override
	char[] runFunction(char[] state) {
		for(String s : parameterString.split("#")){
			String selectedOp = s.substring(0,3);
			for(HashOperation operation : opTable) {
				if(selectedOp.equals(operation.getId())) {
					operation.run(state, s.substring(3));
				}
			}
		}
		return state;
	}
	@Override
	public String getFunc() {
		// TODO Auto-generated method stub
		return parameterString;
	}

}
