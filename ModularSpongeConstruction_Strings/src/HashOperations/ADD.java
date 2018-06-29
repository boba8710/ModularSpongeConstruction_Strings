package HashOperations;

import main.BSF;

public class ADD extends HashOperation {
	//Add a given constant to a chunk of the state
	String id = "ADD";
	@Override
	public String getId() {
		return id;
	}

	@Override
	public char[] run(char[] state, String parameters) {
		String[] paramArray = parameters.split(",");
		char[] b1 = new char[Integer.parseInt(paramArray[1])-Integer.parseInt(paramArray[0])];
		int iterator = 0;
		for(int i = Integer.parseInt(paramArray[0]); i < Integer.parseInt(paramArray[1]); i++){
			b1[iterator++]=state[i];
		}
		char[] b2 = paramArray[2].toCharArray();
		char[] output = BSF.add(b1, b2);
		return output;
	}

}
