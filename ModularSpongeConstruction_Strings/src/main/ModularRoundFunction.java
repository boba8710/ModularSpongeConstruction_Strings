package main;

import HashOperations.HashOperation;

public class ModularRoundFunction extends RoundFunction {
	String parameterString;
	static HashOperation[] opTable = {new HashOperations.AND(), new HashOperations.LROT(), new HashOperations.NOT(), /*new HashOperations.OR(),*/ new HashOperations.SWAP(), new HashOperations.SWAP0(), new HashOperations.SWAP1(), new HashOperations.SWAP2(), new HashOperations.XOR()/*, new HashOperations.ADD()*/, new HashOperations.XORC()};
	//This opTable must directly correspond to the operations array in RandomFunctionBuilder. It serves as a lookup of available operations.
	ModularRoundFunction(int stateSize, String parameterString) {
		super(stateSize);
		this.parameterString = parameterString; //This parameter string is what is generated in RandomFunctionBuilder. It serves as an encoded round function.
	}
	@Override
	char[] runFunction(char[] state) { //Taking an input of the current state, decodes and applies a round function.
		
		//This block decodes the round function
		for(int round = 0; round < rounds; round++){
			for(String s : parameterString.split("#")){//First, split the function by its delimiters. This results in an array of encoded operations.
				String selectedOp = s.substring(0,3);  //The specific operation will always be identified by the first three characters of the encoded operation.
				for(HashOperation operation : opTable) { //Search linearly through the operation table...
					if(selectedOp.equals(operation.getId())) {//...until we find the operation whose id matches our selectedOperation's encoded ID.
						operation.run(state, s.substring(3));//once this operation is found, we then run the operation. Each operation will have its parameters
															 //fed in as its second argument. Parameters in an encoded operation are everything following the operaton ID,
															 //hence s.substring(3);
					}
				}
			}
		}
		return state;
	}
	@Override
	public String getFunc() { //This method is inherited from the round function class, it serves as a utility to extract a parameterString from an in-use round function
		// TODO Auto-generated method stub
		return parameterString;
	}

}
