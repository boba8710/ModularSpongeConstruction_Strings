package HashOperations;

public class NOT extends HashOperation {

	public String id = "NOT";
	@Override
	public
	char[] run(char[] state, String parameters) {

		int r, s;
		String[] paramSplit = parameters.split(",");
		r = Integer.parseInt(paramSplit[0]);
		s = Integer.parseInt(paramSplit[1]);
		for(int i = r; i < s; i++) {
			state[i]=main.BSF.not(state[i]);
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
