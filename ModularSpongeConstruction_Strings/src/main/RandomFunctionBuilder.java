package main;
import HashOperations.HashOperation;
import java.util.Random;

public class RandomFunctionBuilder {
	int funcCount;
	static HashOperation[] operations = {/*new HashOperations.AND(),*/ new HashOperations.LROT(), new HashOperations.NOT(), /*new HashOperations.OR(),*/ new HashOperations.SWAP(), new HashOperations.SWAP0(), new HashOperations.SWAP1(), new HashOperations.SWAP2(), new HashOperations.XOR(), new HashOperations.ADD()};
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
			return paramString;
		}
		else if(selected.getId() == "ADD"){
			int n,m;
			int offset = rand.nextInt(800);
			while(true) {
				n = rand.nextInt(799);
				m = rand.nextInt(800);
				if(n<m) {
					break;
				}
			}
			int p = n+offset;
			int q = m+offset;
			paramString+=n+","+m+","+p+","+q;
		}
		else if(selected.getId() == "LRO" || selected.getId() == "NOT") {
			int r, s;
			while(true) {
				r = rand.nextInt(1598);
				s = rand.nextInt(1599);
				if(s>r) {
					break;
				}
			}
				paramString+=r+","+s;
				if(selected.getId() == "LRO") {
					paramString+=","+rand.nextInt(200);
				}
			}else{
				int n,m;
				int offset = rand.nextInt(800);
				while(true) {
					n = rand.nextInt(799);
					m = rand.nextInt(800);
					if(n<m) {
						break;
					}
				}
				int p = n+offset;
				int q = m+offset;
				paramString+=n+","+m+","+p+","+q;
		}
		//No hash here, the GeneticHelperMethods works on arrays
		return paramString;
		
	}
	public String genFuncString() { //used during population instantiation
		Random rand = new Random();
		String retString = "";
		for(int functionCount = 0; functionCount < funcCount; functionCount++) {
			HashOperation selected = operations[rand.nextInt(operations.length)];
			String paramString = selected.getId();
			if(selected.getId() == "NOP"){
				return paramString;
			}
			if(selected.getId() == "LRO" || selected.getId() == "NOT") {
				int r, s;
				while(true) {
					r = rand.nextInt(1598);
					s = rand.nextInt(1599);
					if(s>r) {
						break;
					}
				}
					paramString+=r+","+s;
					if(selected.getId() == "LRO") {
						paramString+=","+rand.nextInt(200);
					}
				}else{
					int n,m;
					int offset = rand.nextInt(800);
					while(true) {
						n = rand.nextInt(799);
						m = rand.nextInt(800);
						if(n<m) {
							break;
						}
					}
					int p = n+offset;
					int q = m+offset;
					paramString+=n+","+m+","+p+","+q;
			}
			paramString+="#";
			retString+=paramString;
		}
		return retString;
	}
	
}
