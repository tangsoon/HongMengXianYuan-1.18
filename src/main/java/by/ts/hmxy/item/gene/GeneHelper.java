package by.ts.hmxy.item.gene;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import com.google.common.collect.ImmutableList;

import by.ts.hmxy.item.HmxyItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * 封装基因相关的操作
 * 
 * @author tangsoon
 *
 * @param <T> 存放基因的类
 */
public abstract class GeneHelper<T extends IGeneContanier> {

	private final ImmutableList<GeneType<?>> GENE_TYPES;
	private ImmutableList.Builder<GeneType<?>> builder = ImmutableList.builder();
	private int geneTypeCounter = 0;

	public GeneHelper() {
		init();
		GENE_TYPES = builder.build();
		builder = null;// gc时释放内存
	}

	protected <V> GeneType<V> createGeneType(String name,Class<V> clazz, V value) {
		if (this.builder == null) {
			LogManager.getLogger().info("GeneHelper#createGeneType(Class<V> clazz,V value)只能在GeneHleper#init()中调用");
		} else {
			GeneType<V> geneType = new GeneType<V>(name,clazz, value, geneTypeCounter++);
			builder.add(geneType);
			return geneType;
		}
		return null;
	}

	/**
	 * 生成随机基因序列
	 * 
	 * @param t
	 * @return
	 */
//	public T initContainer(T t) {
////		GeneItem<?>[] genesA = new GeneItem<?>[this.geneTypeCounter];
////		GeneItem<?>[] genesB = new GeneItem<?>[this.geneTypeCounter];
////		Random ran = new Random();
////		for (int i = 0; i < this.GENE_TYPES.size(); i++) {
////			GeneType<?> geneType = GENE_TYPES.get(i);
////			int size = geneType.size();
////			if (size > 0) {
////				genesA[i] = geneType.getGene(ran.nextInt(size)).get();
////				genesB[i] = geneType.getGene(ran.nextInt(size)).get();
////			}
////		}
////		t.setGenesA(genesA);
////		t.setGenesB(genesB);
////		return t;
//	}

	protected abstract void init();

	public List<GeneType<?>> getGeneTypes() {
		return this.GENE_TYPES;
	}

	@SuppressWarnings("unchecked")
	public <V> V getValue(GeneType<V> geneType, T t) {
		int geneTypeIndex = geneType.INDEX;
		GeneItem<?> geneA = t.getGenesA()[geneTypeIndex];
		GeneItem<?> geneB = t.getGenesB()[geneTypeIndex];
		return (V) (geneA.INDEX > geneB.INDEX ? geneA.VALUE : geneB.VALUE);
	}

	public void createChild(T containerA, T containerB, T containerChild) {
		Random ran = new Random();
		GeneItem<?>[] geneA1 = Arrays.copyOf(containerChild.getGenesA(), this.getGeneTypes().size());
		GeneItem<?>[] geneA2 = Arrays.copyOf(containerChild.getGenesB(), this.getGeneTypes().size());
		cross(geneA1, geneA2);
		GeneItem<?>[] geneB1 = Arrays.copyOf(containerChild.getGenesA(), this.getGeneTypes().size());
		GeneItem<?>[] geneB2 = Arrays.copyOf(containerChild.getGenesB(), this.getGeneTypes().size());
		this.cross(geneB1, geneB2);
		containerChild.setGenesA(ran.nextBoolean() ? geneA1 : geneA2);
		containerChild.setGenesB(ran.nextBoolean() ? geneB1 : geneB2);
		//TODO 变异
	}

	protected void cross(GeneItem<?>[] gene1, GeneItem<?>[] gene2) {
		Random ran = new Random();
		int crossTimes = ran.nextInt(gene1.length / 4 + 1);
		while (crossTimes-- > 1) {
			int index = ran.nextInt(gene1.length);
			GeneItem<?> temp = gene1[index];
			gene1[index] = gene2[index];
			gene2[index] = temp;
		}
	}

	/**
	 * 
	 * @param t
	 * @return
	 */
	public CompoundTag serialize(T t) {
		CompoundTag genes = new CompoundTag();
		ListTag genesA = new ListTag();
		for (GeneItem<?> gene : t.getGenesA()) {
			genesA.add(StringTag.valueOf(gene.getRegistryName().toString()));
		}
		genes.put("genesA", genesA);
		ListTag genesB = new ListTag();
		for (GeneItem<?> gene : t.getGenesB()) {
			genesB.add(StringTag.valueOf(gene.getRegistryName().toString()));
		}
		genes.put("genesB", genesB);
		return genes;
	}

	/**
	 * 尽量少调用这个方法，它会消耗一些性能
	 */
	public void deserialize(CompoundTag tag, T t) {
		GeneItem<?>[] genesA = t.getGenesA();
		ListTag genesListA = tag.getList("genesA", Tag.TAG_STRING);
		for (int i = 0; i < genesA.length; i++) {
			genesA[i] = (GeneItem<?>) ForgeRegistries.ITEMS.getValue(new ResourceLocation(genesListA.getString(i)));
		}
		GeneItem<?>[] genesB = t.getGenesB();
		ListTag genesListB = tag.getList("genesB", Tag.TAG_STRING);
		for (int i = 0; i < genesB.length; i++) {
			genesB[i] = (GeneItem<?>) ForgeRegistries.ITEMS.getValue(new ResourceLocation(genesListB.getString(i)));
		}
	}
	
	public void onRegisterItem() {
		
		HmxyItems.register(null, null, null, null, null);
	}
}
