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
	private List<GeneType<?>> genTypes;
	
	public DNA(List<GeneType<?>> genTypes) {
		int lenth=genTypes.size();
		GeneItem<?>[] genesA = new GeneItem<?>[lenth];
		GeneItem<?>[] genesB = new GeneItem<?>[lenth];
		this.genTypes=genTypes;
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
		genes.put("genesA", serializeNBT(this.genesA));
		genes.put("genesB", serializeNBT(this.genesB));
		return genes;
	}
	
	private ListTag serializeNBT(GeneItem<?>[] genes) {
		ListTag genesTag = new ListTag();
		for (GeneItem<?> gene : genes) {
			genesTag.add(StringTag.valueOf(gene.getRegistryName().toString()));
		}
		return genesTag;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.deserializeNBT(nbt.getList("genesA", Tag.TAG_STRING),this.genesA);
		this.deserializeNBT(nbt.getList("genesB", Tag.TAG_STRING),this.genesB);
	}
	
	private void deserializeNBT(ListTag genesList,GeneItem<?>[] genes) {
		for (int i = 0; i < genTypes.size();i++) {			
			if(ForgeRegistries.ITEMS.getValue(new ResourceLocation(genesList.getString(i))) instanceof GeneItem<?> gene) {
				genes[i] = gene;	
			}
			else {
				genes[i]=genTypes.get(i).getGene(0).get();
			}
		}
	}
	
}