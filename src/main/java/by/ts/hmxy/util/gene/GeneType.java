package by.ts.hmxy.util.gene;

public class GeneType<T> implements IGeneType<T> {

	private final String TYPE_NAME;
	private final Class<T> VALUE_TYPE;

	private GeneType(String tYPE_NAME, Class<T> vALUE_TYPE) {
		TYPE_NAME = tYPE_NAME;
		VALUE_TYPE = vALUE_TYPE;
	}

	@Override
	public String getTypeName() {
		return TYPE_NAME;
	}

	@Override
	public Class<T> valueType() {
		return VALUE_TYPE;
	}

	public static <T> GeneType<T> create(String tYPE_NAME, Class<T> vALUE_TYPE) {
		return new GeneType<>(tYPE_NAME, vALUE_TYPE);
	}
}