package HashOperations;

public abstract class HashOperation {
	//Base HashOperation class
	public String id;
	abstract public String getId();
	abstract public char[] run(char[] state, String parameters);
}
