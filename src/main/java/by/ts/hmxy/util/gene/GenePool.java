package by.ts.hmxy.util.gene;

import java.util.ArrayList;

public class GenePool {
	private ArrayList<IGeneType<?>> geneTypes;
	private ArrayList<ArrayList<IGene<?>>> genes;
	
	private GenePool() {
		
	}
	
	public IGeneType<?> getGeneType(int typeIndex){
		return this.geneTypes.get(typeIndex);
	}
	
	public IGene<?> getGene(int typeIndex,int geneIndex){
		return genes.get(typeIndex).get(geneIndex);
	}
	
	public static class Builder{
		private ArrayList<IGeneType<?>> geneTypes=new ArrayList<>();
		private ArrayList<ArrayList<IGene<?>>> genes=new ArrayList<>();
		private ArrayList<IGene<?>> geneClan;
		public static Builder of() {
			return new Builder();
		}
		
		public Builder createGeneType(IGeneType<?> geneType) {
			this.geneTypes.add(geneType);
			geneClan=new ArrayList<>();
			this.genes.add(geneClan);
			return this;
		}
		
		public Builder addGenes(Gene<?> gene) {
			this.geneClan.add(gene);
			return this;
		};
		
		public GenePool build() {
			GenePool dna=new GenePool();
			dna.geneTypes=this.geneTypes;
			dna.genes=this.genes;
			return dna;
		}
	}
	
	
}
