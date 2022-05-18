package by.ts.hmxy.item.gene;

import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class DNA implements IDNA{

	private GeneItem<?>[] genesA = null;
	private GeneItem<?>[] genesB = null;
	
	public DNA(List<GeneType<?>> genTypes) {
		int lenth=genTypes.size();
		GeneItem<?>[] genesA = new GeneItem<?>[lenth];
		GeneItem<?>[] genesB = new GeneItem<?>[lenth];
		Random ran = new Random();
		for (int i = 0; i < lenth; i++) {
			GeneType<?> geneType = genTypes.get(i);
			int size = geneType.size();
			if (size > 0) {
				genesA[i] = geneType.getGene(ran.nextInt(size)).get();
				genesB[i] = geneType.getGene(ran.nextInt(size)).get();
			}
		}
		this.setGenesA(genesA);
		this.setGenesB(genesB);
	}

	@Override
	public GeneItem<?>[] getGenesA() {
		return this.genesA;
	}

	@Override
	public GeneItem<?>[] getGenesB() {
		return this.genesB;
	}

	@Override
	public void setGenesA(GeneItem<?>[] genes) {
		this.genesA=genes;
	}

	@Override
	public void setGenesB(GeneItem<?>[] genes) {
		this.genesB=genes;
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag genes = new CompoundTag();
		ListTag genesA = new ListTag();
		for (GeneItem<?> gene : this.getGenesA()) {
			genesA.add(StringTag.valueOf(gene.getRegistryName().toString()));
		}
		genes.put("genesA", genesA);
		ListTag genesB = new ListTag();
		for (GeneItem<?> gene : this.getGenesB()) {
			genesB.add(StringTag.valueOf(gene.getRegistryName().toString()));
		}
		genes.put("genesB", genesB);
		return genes;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		ListTag genesListA = nbt.getList("genesA", Tag.TAG_STRING);
		for (int i = 0; i < genesA.length; i++) {
			genesA[i] = (GeneItem<?>) ForgeRegistries.ITEMS.getValue(new ResourceLocation(genesListA.getString(i)));
		}
		GeneItem<?>[] genesB = this.getGenesB();
		ListTag genesListB = nbt.getList("genesB", Tag.TAG_STRING);
		for (int i = 0; i < genesB.length; i++) {
			genesB[i] = (GeneItem<?>) ForgeRegistries.ITEMS.getValue(new ResourceLocation(genesListB.getString(i)));
		}
	}
}