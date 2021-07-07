package com.lukecreator.BonziBot;

/**
 * Holds two arbitrary values.
 * @author Lukec
 *
 * @param <A>
 * @param <B>
 */
public class Tuple<A, B> {
	
	A value1;
	B value2;
	
	public Tuple(A value1, B value2) {
		this.value1 = value1;
		this.value2 = value2;
	}
	
	public A getA() {
		return value1;
	}
	public B getB() {
		return value2;
	}
}
