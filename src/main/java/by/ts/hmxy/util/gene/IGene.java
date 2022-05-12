package by.ts.hmxy.util.gene;

public interface IGene<T> {
	IGeneType<T> getGenType();
	T getValue();
}
