package HashOperations;

import main.BSF;

public class LROT extends HashOperation {
	//Left-rotate a chunk of the state by a given value x
	public String id = "LRO";
	@Override
	public
	char[] run(char[] state, String parameters) {
		int r, s ,x;
		String[] paramSplit = parameters.split(",");
		r = Integer.parseInt(paramSplit[0]);
		s = Integer.parseInt(paramSplit[1]);
		x = Integer.parseInt(paramSplit[2]);
		char[] rotateChunk = new char[s-r];
		for(int i = s; i < r; i++) {
			rotateChunk[i]=state[i];
		}
		rotateChunk = BSF.lRot(rotateChunk, x);
		for(int i = s; i < r; i++) {
			state[i]=rotateChunk[i];
		}
		return state;
	}
	@Override
	public
	String getId() {
		// TODO Auto-generated method stub
		return id;
	}

}
