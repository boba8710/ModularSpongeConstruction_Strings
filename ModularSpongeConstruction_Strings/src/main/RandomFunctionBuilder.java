package main;
import HashOperations.HashOperation;
import java.util.Random;

public class RandomFunctionBuilder {
	int funcCount, wordSize;
	static HashOperation[] operations = {new HashOperations.AND(), new HashOperations.LROT(), new HashOperations.NOT(), /*new HashOperations.OR(),*/ new HashOperations.SWAP(), new HashOperations.SWAP0(), new HashOperations.SWAP1(), new HashOperations.SWAP2(), new HashOperations.XOR()/*, new HashOperations.ADD()*/, new HashOperations.XORC()};
	//This operations array must be the same in both RandomFunctionBuilder and ModularRoundFunction. Operations can be enabled and disabled by commenting them out.
	RandomFunctionBuilder(int stateSize, int funcCount, int wordSize){
		this.funcCount = funcCount; //The functionCount is the amount of functions that each round function will contain. Because this includes
									//NOPS, this is effectively the maximum amount of operations in a single round function.
		this.wordSize = wordSize;   //the wordSize variable is not currently used, but in the future it will be used to set the wordsize that the function operates on
	}
	public RandomFunctionBuilder() {
	}
	static GeneticHelperMethods ghm = new GeneticHelperMethods();
	public String genRandOperation() { //used during mutations, see the below method for more complete documentation, as most of the functionality is shared
		Random rand = new Random();
		HashOperation selected = operations[rand.nextInt(operations.length)];
		String paramString = selected.getId();
		if(selected.getId() == "NOP"){
			
		}else if(selected.getId() == "ADD"){
			int n,m;

			n = rand.nextInt(25)*64;
			m = n+64;
			

			String p = ghm.generateMersenneRandomString(64);
			paramString+=n+","+m+","+p;
		}else if(selected.getId() == "LRO" || selected.getId() == "NOT") {
			int r, s;

			r = rand.nextInt(25)*64;
			s = r+64;

			paramString+=r+","+s;
			if(selected.getId() == "LRO") {
				paramString+=","+rand.nextInt(64);
			}
			}else if(selected.getId() == "XOC"){ 
				int n,m;
				n = rand.nextInt(25)*64;
				m = n+64;
				String p = ghm.generateMersenneRandomString(64);
				paramString+=n+","+m+","+p;
			}else{	 
				int n,m;
				int offset = rand.nextInt(14)*64; 

				n = rand.nextInt(13)*64;
				m = n+64;

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
			if(selected.getId() == "NOP"){
				
			}else if(selected.getId() == "ADD"){
				int n,m;

				n = rand.nextInt(25)*64;
				m = n+64;
				

				String p = ghm.generateMersenneRandomString(64);
				paramString+=n+","+m+","+p;
			}else if(selected.getId() == "LRO" || selected.getId() == "NOT") {
				int r, s;

				r = rand.nextInt(25)*64;
				s = r+64;

				paramString+=r+","+s;
				if(selected.getId() == "LRO") {
					paramString+=","+rand.nextInt(64);
				}
				}else if(selected.getId() == "XOC"){ 
					int n,m;
					n = rand.nextInt(25)*64;
					m = n+64;
					String p = ghm.generateMersenneRandomString(64);
					paramString+=n+","+m+","+p;
				}else{	 
					int n,m;
					int offset = rand.nextInt(14)*64; 

					n = rand.nextInt(13)*64;
					m = n+64;

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
