package main;
import HashOperations.HashOperation;
import java.util.Random;

public class RandomFunctionBuilder {
	int funcCount;
	static HashOperation[] operations = {new HashOperations.AND(), new HashOperations.LROT(), new HashOperations.NOT(), /*new HashOperations.OR(),*/ new HashOperations.SWAP(), new HashOperations.SWAP0(), new HashOperations.SWAP1(), new HashOperations.SWAP2(), new HashOperations.XOR(), /*new HashOperations.ADD(),*/ new HashOperations.XORC()};
	RandomFunctionBuilder(int funcCount){
		this.funcCount = funcCount;
	}
	public RandomFunctionBuilder() {
	}
	public String genRandOperation() { //used during mutations
		Random rand = new Random();
		HashOperation selected = operations[rand.nextInt(operations.length)];
		String paramString = selected.getId();
		if(selected.getId() == "NOP"){
			
		}else if(selected.getId() == "ADD"){
			int n,m;
			while(true) {
				n = rand.nextInt(200)*8;
				m = (rand.nextInt(200)+1)*8;
				if(n<m) {
					break;
				}
			}
			int pInt = rand.nextInt((int) Math.pow(2, m-n));
			String p = Integer.toBinaryString(pInt);
			paramString+=n+","+m+","+p;
		}else if(selected.getId() == "LRO" || selected.getId() == "NOT") {
			int r, s;
			while(true) {
				r = (rand.nextInt(200)-1)*8;
				s = (rand.nextInt(200))*8;
				if(s>r) {
					break;
				}
			}
				paramString+=r+","+s;
				if(selected.getId() == "LRO") {
					paramString+=","+rand.nextInt(s-r);
				}
			}else if(selected.getId() == "XOC"){
				int n,m;
				while(true) {
					n = (rand.nextInt(200)-1)*8;
					m = (rand.nextInt(200))*8;
					if(n<m) {
						break;
					}
				}
				int pInt = rand.nextInt((int) Math.pow(2, m-n));
				String p = Integer.toBinaryString(pInt);
				while(p.length() != m-n){
					p="0"+p;
				}
				paramString+=n+","+m+","+p;
			}else{
				int n,m;
				int offset = rand.nextInt(100)*8;
				while(true) {
					n = (rand.nextInt(100)-1)*8;
					m = (rand.nextInt(100))*8;
					if(n<m) {
						break;
					}
				}
				int p = n+offset;
				int q = m+offset;
				paramString+=n+","+m+","+p+","+q;
		}
		//No hash here, the GenRandOperation works on elements of arrays already delimited with hashes
		return paramString;
		
	}
	public String genFuncString() { //used during population instantiation, operations delimited with #
		Random rand = new Random(); //This random number generator will be used throughout the method
		String retString = "";      //this return string is to store the total generated round function encoded as [OPRn,m,p,q#] where OPR is the operation's 
									//3 letter identifier, and n, m, p and q are parameters. Not all parameters are needed for each operation.
		for(int functionCount = 0; functionCount < funcCount; functionCount++) { //This loop runs until we reach functionCount of functions
			HashOperation selected = operations[rand.nextInt(operations.length)]; //select a random operation from our operations array (static)
			String paramString = selected.getId(); //start the paramString (string storing one operation) with the three letter identifier
			if(selected.getId() == "NOP"){ //If it's a NOP, we don't have to do anything
			}else if(selected.getId() == "ADD"){ //ADD requires a n and m (the start and end of a state chunk) and a p, which is the random integer to add to
												 //the chunk
				int n,m;
				while(true) {
					n = (rand.nextInt(200)-1)*8;
					m = (rand.nextInt(200))*8;
					if(n<m) {
						break;
					}
				}
				int pInt = rand.nextInt((int) Math.pow(2, m-n)); //This /should/ ensure the random integer's bitstring isn't longer than the chunk
				String p = Integer.toBinaryString(pInt);//convert the integer to a bitstring
				paramString+=n+","+m+","+p; //assemble the operation
			}else if(selected.getId() == "LRO" || selected.getId() == "NOT") { //LRO and NOT both act on one hash chunk. 
				int r, s;
				while(true) {
					r = (rand.nextInt(200)-1)*8;
					s = (rand.nextInt(200))*8;
					if(s>r) {
						break;
					}
				}
					paramString+=r+","+s;
					if(selected.getId() == "LRO") {
						paramString+=","+rand.nextInt(s-r);
					}
				}else if(selected.getId() == "XOC"){
					int n,m;
					while(true) {
						n = (rand.nextInt(200)-1)*8;
						m = (rand.nextInt(200))*8;
						if(n<m) {
							break;
						}
					}
					int pInt = rand.nextInt((int) Math.pow(2, m-n));
					String p = Integer.toBinaryString(pInt);
					while(p.length() != m-n){
						p="0"+p;
					}
					paramString+=n+","+m+","+p;
				}else{
					int n,m;
					int offset = rand.nextInt(100)*8;
					while(true) {
						n = (rand.nextInt(100)-1)*8;
						m = (rand.nextInt(100))*8;
						if(n<m) {
							break;
						}
					}
					int p = n+offset;
					int q = m+offset;
					paramString+=n+","+m+","+p+","+q;
			}
			paramString+="#"; //Here's the hash delimiter; it signifies the end of a single operation
			retString+=paramString;
		}
		return retString;
	}
	
}
