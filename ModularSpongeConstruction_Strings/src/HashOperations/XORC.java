package HashOperations;

public class XORC extends HashOperation {
	//XOR a constant into the state
	public String id = "XOC";
	@Override
	public
	char[] run(char[] state, String parameters) {
		int n, m;
		char[] c;
		String[] paramSplit = parameters.split(",");
		n = Integer.parseInt(paramSplit[0]);
		m = Integer.parseInt(paramSplit[1]);
		c = paramSplit[2].toCharArray();
		char[] preservedState = state;
		int iterator = 0;
		for(int i = n; i < m; i++) {
			state[i] = main.BSF.xor(preservedState[i], c[iterator]);
			iterator++;
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
