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
	public final Class<T> VALUE_TYPE;
	public final T DEFAULT_VALUE;
	public final List<Supplier<GeneItem<T>>> GENES=new ArrayList<>();
	public final List<RegistryObject<GeneItem<?>>> GENES_REGISTRY=new ArrayList<>();
	public final int INDEX;
	public GeneType(String NAME,Class<T> VALUE_TYPE,T DEFAULT_VALUE,int INDEX) {
		this.NAME=NAME;
		this.VALUE_TYPE = VALUE_TYPE;
		this.DEFAULT_VALUE=DEFAULT_VALUE;
		this.INDEX=INDEX;
		this.createGene(DEFAULT_VALUE);
	}
	/**
	 * 通过这里创造一个基因
	 * @param value
	 * @return
	 */
	public Supplier<GeneItem<T>> createGene(T value){
		Supplier<GeneItem<T>> gene;
		this.GENES.add(gene=()->new GeneItem<T>(this, value,this.GENES.size()));
		return gene;
	}
	
	public RegistryObject<GeneItem<?>> getGene(int index){
		return GENES_REGISTRY.get(index);
	}
	
	public int size() {
		return GENES_REGISTRY.size();
	}
}