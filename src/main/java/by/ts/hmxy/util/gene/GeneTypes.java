package by.ts.hmxy.util.gene;

public class GeneTypes {
	/** 寿命上限 */
	public static final IGeneType<Integer> MAX_AGE = GeneType.create("max_age", Integer.class);

	public static final Gene<Integer> MAX_AGE_GENE_1 = Gene.create(MAX_AGE, 24000);
	public static final Gene<Integer> MAX_AGE_GENE_2 = Gene.create(MAX_AGE, 24000 * 2);

	/** 生长速度 */
	public static final IGeneType<Float> GROW_SPEED = GeneType.create("grow_speed", Float.class);
	public static final Gene<Float> GROW_SPEED_GENE_1 = Gene.create(GROW_SPEED, 0.01F);
	public static final Gene<Float> GROW_SPEED_GENE_2 = Gene.create(GROW_SPEED, 0.02F);

	/** 种子成熟年龄 */
	public static final IGeneType<Integer> SEED_AGE = GeneType.create("seed_age", Integer.class);
	public static final Gene<Integer> SEED_AGE_GENE_1 = Gene.create(SEED_AGE, 24000);
	public static final Gene<Integer> SEED_AGE_GENE_2 = Gene.create(SEED_AGE, 24000 * 2);
}
