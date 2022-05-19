package by.ts.hmxy.item.gene;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraftforge.registries.RegistryObject;

/**
 * 一个基因库
 * @author tangsoon
 *
 * @param <T> 基因的值类型
 */
public class GeneType<T> {
	public final String NAME;
	public final String NAME_ZH;
	public final Class<T> VALUE_TYPE;
	public final T DEFAULT_VALUE;
	public List<Supplier<GeneItem<T>>> tempGemeItems=new ArrayList<>();
	public final List<RegistryObject<GeneItem<?>>> GENES_REGISTRY=new ArrayList<>();
	public final int INDEX;
	public GeneType(String NAME,String NAME_ZH,Class<T> VALUE_TYPE,T DEFAULT_VALUE,int INDEX) {
		this.NAME=NAME;
		this.NAME_ZH=NAME_ZH;
		this.VALUE_TYPE = VALUE_TYPE;
		this.DEFAULT_VALUE=DEFAULT_VALUE;
		this.INDEX=INDEX;
		this.createGene(DEFAULT_VALUE);
	}
	private int geneCounter=0;
	/**
	 * 通过这里创造一个基因
	 * @param value
	 * @return
	 */
	public void createGene(T value){
		this.tempGemeItems.add(()->new GeneItem<T>(this, value,geneCounter++));
	}
	
	public RegistryObject<GeneItem<?>> getGene(int index){
		return GENES_REGISTRY.get(index);
	}
	
	public int size() {
		return GENES_REGISTRY.size();
	}
}
