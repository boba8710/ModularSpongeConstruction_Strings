package HashOperations;

public class NOP extends HashOperation {
	public String id = "NOP";
	@Override
	public
	char[] run(char[] state, String parameters) {
		return state;
	}
	@Override
	public
	String getId() {
		// TODO Auto-generated method stub
		return id;
	}
}
