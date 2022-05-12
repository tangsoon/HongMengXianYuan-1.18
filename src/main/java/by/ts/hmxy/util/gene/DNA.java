package by.ts.hmxy.util.gene;

import java.util.ArrayList;

public class DNA implements Comparable<DNA> {

	private final ArrayList<Integer> geneIndex;

	public DNA(ArrayList<Integer> geneIndex) {
		this.geneIndex = geneIndex;
	}

	@Override
	public int compareTo(DNA o) {
		for (int i = 0; i < geneIndex.size(); i++) {
			int index = geneIndex.get(i);
			int oIndex = o.getGene(i);
			if (index > oIndex) {
				return 1;
			} else if (index < oIndex) {
				return -1;
			}
		}
		return 0;
	}

	public int getGene(int geneType) {
		return this.geneIndex.get(geneType);
	}
	
	public String getName() {
		StringBuilder sb=new StringBuilder();
		for(Integer i:this.geneIndex) {
			sb.append(i);
		}
		return sb.toString();
	}
}
