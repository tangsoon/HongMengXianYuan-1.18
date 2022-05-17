package by.ts.hmxy.item.gene;

public interface IGeneContanier {
	GeneItem<?>[] getGenesA();
	GeneItem<?>[] getGenesB();
	void setGenesA(GeneItem<?>[] genes);
	void setGenesB(GeneItem<?>[] genes);
}
