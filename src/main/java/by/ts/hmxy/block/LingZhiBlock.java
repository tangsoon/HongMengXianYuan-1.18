package by.ts.hmxy.block;

import by.ts.hmxy.block.property.DNAProperty;
import by.ts.hmxy.util.gene.GenePool;
import by.ts.hmxy.util.gene.GeneTypes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class LingZhiBlock extends Block {

	public static final GenePool GENE_POOL = new GenePool.Builder().createGeneType(GeneTypes.MAX_AGE)
			.addGenes(GeneTypes.MAX_AGE_GENE_1).addGenes(GeneTypes.MAX_AGE_GENE_2).createGeneType(GeneTypes.GROW_SPEED)
			.addGenes(GeneTypes.GROW_SPEED_GENE_1).addGenes(GeneTypes.GROW_SPEED_GENE_2)
			.createGeneType(GeneTypes.SEED_AGE).addGenes(GeneTypes.SEED_AGE_GENE_1).addGenes(GeneTypes.SEED_AGE_GENE_2)
			.build();


	DNAProperty DNA=DNAProperty.crate("ling_zhi");
	
	public LingZhiBlock(Properties pro) {
		super(pro);
		this.registerDefaultState(this.stateDefinition.any().setValue(DNA, null));
	}
}
