package by.ts.hmxy.util.gene;
interface IGeneType<T> {
	String getTypeName();
	Class<T> valueType();
}