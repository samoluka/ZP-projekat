package etf.openpgp.sl180053kf180285.util;

public class Pair<T1, T2> {
	private T1 first;
	private T2 second;

	/**
	 * dohvatanje prvog atributa
	 */
	public T1 getFirst() {
		return first;
	}

	/**
	 * postavljanje prvog atributa
	 */
	public void setFirst(T1 first) {
		this.first = first;
	}

	/**
	 * dohvatanje drugog atributa
	 */
	public T2 getSecond() {
		return second;
	}

	/**
	 * postavljanje drugog atributa
	 */
	public void setSecond(T2 second) {
		this.second = second;
	}

	public Pair(T1 first, T2 second) {
		super();
		this.first = first;
		this.second = second;
	}

	/**
	 * ispis para
	 */
	@Override
	public String toString() {
		return "Pair [first=" + first.toString() + ", second=" + second.toString() + "]";
	}

}
