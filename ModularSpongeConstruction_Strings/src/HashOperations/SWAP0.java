package HashOperations;

import main.BSF;

public class SWAP0 extends HashOperation {
	public String id = "SW0";
	@Override
	public
	char[] run(char[] state, String parameters) {
		int n, m, p, q;
		String[] paramSplit = parameters.split(",");
		n = Integer.parseInt(paramSplit[0]);
		m = Integer.parseInt(paramSplit[1]);
		p = Integer.parseInt(paramSplit[2]);
		q = Integer.parseInt(paramSplit[3]);
		char[] swap1 = new char[m-n];
		char[] swap2 = new char[q-p];
		int iterator = 0;
		for(int i = n; i < m; i++) {
			swap1[iterator]=state[i];
			iterator++;
		}
		iterator = 0;
		for(int i = p; i < q; i++) {
			swap2[iterator]=state[i];
			iterator++;
		}
		swap1=BSF.reverse(swap1);
		iterator = 0;
		for(int i = n; i < m; i++) {
			state[i]=swap2[iterator];
			iterator++;
		}
		iterator = 0;
		for(int i = p; i < q; i++) {
			state[i]=swap1[iterator];
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
