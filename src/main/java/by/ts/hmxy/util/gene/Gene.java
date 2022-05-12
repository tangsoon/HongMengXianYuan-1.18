package by.ts.hmxy.util.gene;

/**
 * 基因
 * 
 * @author tangsoon
 *
 */
public class Gene<T> implements IGene<T> {
	private final IGeneType<T> GENE_TYPE;

	private final T VALUE;

	private Gene(IGeneType<T> geneType, T value) {
		this.GENE_TYPE = geneType;
		this.VALUE = value;
	}

	@Override
	public IGeneType<T> getGenType() {
		return GENE_TYPE;
	}

	@Override
	public T getValue() {
		return VALUE;
	}
	
	public static <T> Gene<T> create(IGeneType<T> type,T value) {
		return new Gene<>(type,value);
	}

}
