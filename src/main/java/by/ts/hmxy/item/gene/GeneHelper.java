package by.ts.hmxy.item.gene;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import by.ts.hmxy.item.HmxyItems;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

/**
 * 封装基因相关的操作
 * 
 * @author tangsoon
 *
 * @param <T> 存放基因的类
 */
public class GeneHelper<T extends IDNA> {

	private final List<GeneType<?>> GENE_TYPES;

	public GeneHelper() {
		GENE_TYPES = new ArrayList<>();
	}

	public <V> GeneType<V> createGeneType(String name,String nameZh, Class<V> clazz, V value) {
		GeneType<V> geneType = new GeneType<V>(name,nameZh, clazz, value, GENE_TYPES.size());
		GENE_TYPES.add(geneType);
		return geneType;
	}

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

	public T createChild(T containerA, T containerB, T containerChild) {
		Random ran = new Random();
		GeneItem<?>[] geneA1 = Arrays.copyOf(containerChild.getGenesA(), this.getGeneTypes().size());
		GeneItem<?>[] geneA2 = Arrays.copyOf(containerChild.getGenesB(), this.getGeneTypes().size());
		cross(geneA1, geneA2);
		GeneItem<?>[] geneB1 = Arrays.copyOf(containerChild.getGenesA(), this.getGeneTypes().size());
		GeneItem<?>[] geneB2 = Arrays.copyOf(containerChild.getGenesB(), this.getGeneTypes().size());
		this.cross(geneB1, geneB2);
		containerChild.setGenesA(ran.nextBoolean() ? geneA1 : geneA2);
		containerChild.setGenesB(ran.nextBoolean() ? geneB1 : geneB2);
		this.mutate(containerChild.getGenesA());
		this.mutate(containerChild.getGenesB());
		return containerChild;
	}

	protected void cross(GeneItem<?>[] gene1, GeneItem<?>[] gene2) {
		Random ran = new Random();
		int crossTimes = ran.nextInt(gene1.length / 4 + 1);
		while (crossTimes-- > 0) {
			int index = ran.nextInt(gene1.length);
			GeneItem<?> temp = gene1[index];
			gene1[index] = gene2[index];
			gene2[index] = temp;
		}
	}

	protected void mutate(GeneItem<?>[] genes) {
		if (genes.length > 0) {
			Random ran = new Random();
			int times = ran.nextInt(genes.length / 4 + 1);
			GeneType<?> type = genes[0].GENE_TYPE;
			while (times-- > 0) {
				int index = ran.nextInt(genes.length);
				genes[index] = type.getGene(Math.min(Math.max(0, ran.nextInt(3) - 1), type.size())).get();
			}
		}
	}

	/**
	 * 在物品注册阶段调用，注册基因对应的物品
	 */
	@SuppressWarnings("unchecked")
	public void registerGeneItems() {
		for (GeneType<?> type : this.getGeneTypes()) {
			for (int i = 0; i < type.tempGemeItems.size(); i++) {
				String postfix=type.postfixs.get(i);
				RegistryObject<?> g= HmxyItems.register(type.NAME+"_"+postfix, type.NAME_ZH+"_"+postfix, type.tempGemeItems.get(i), (a,b)->{}, (c,d,e)->{});
				type.GENES_REGISTRY.add((RegistryObject<GeneItem<?>>) g);
			}
			type.tempGemeItems=null;
			type.postfixs=null;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(DNA dna, @Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		pTooltip.add(TransMsg.GENES_A.get());
		this.appendHoverText(dna.getGenesA(), pLevel, pTooltip, pFlag);
		pTooltip.add(TransMsg.GENES_B.get());
		this.appendHoverText(dna.getGenesB(), pLevel, pTooltip, pFlag);
	}
	@OnlyIn(Dist.CLIENT)
	private void  appendHoverText(GeneItem<?>[] genes,@Nullable BlockGetter pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
		for (GeneItem<?> gene : genes) {
			pTooltip.add(TransMsg.geneDetail(gene));
		}
	}
}
