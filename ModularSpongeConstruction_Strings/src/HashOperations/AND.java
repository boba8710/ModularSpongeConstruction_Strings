package HashOperations;

public class AND extends HashOperation {
	//AND two chunks of the state, replacing the first
	public String id = "AND";
	@Override
	public
	char[] run(char[] state, String parameters) {
		int n, m, p, q;
		String[] paramSplit = parameters.split(",");
		n = Integer.parseInt(paramSplit[0]);
		m = Integer.parseInt(paramSplit[1]);
		p = Integer.parseInt(paramSplit[2]);
		q = Integer.parseInt(paramSplit[3]);
		char[] preservedState = state;
		int iterator = 0;
		for(int i = n; i < m; i++) {
			state[i] = main.BSF.and(preservedState[i], preservedState[p+iterator]);
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
