package by.ts.hmxy.item.gene;

import by.ts.hmxy.item.Tabs;
import net.minecraft.world.item.Item;

public class GeneItem<T> extends Item{
	public final T VALUE;
	public final GeneType<T> GENE_TYPE;
	/**该基因在GeneType中的序号*/
	public final int INDEX;
	public GeneItem(GeneType<T> geneType,T value,int INDEX) {
		super(new Item.Properties().stacksTo(64).tab(Tabs.GENE));
		this.VALUE=value;
		this.GENE_TYPE=geneType;
		this.INDEX=INDEX;
	}
}
