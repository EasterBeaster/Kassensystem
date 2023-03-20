
package com.openjfx;

import java.util.Collection;

public class Price {
	private final int euro;
	private final int cent;

	public Price() {
		this.euro = 0;
		this.cent = 0;
	}

	public Price(String value) {
		value = value.trim();

		int sep = value.indexOf(',');
		if (sep == -1)
			sep = value.indexOf('.');
		if (sep == -1) {
			this.euro = parseInnerInt(value);
			this.cent = 0;
			return;
		}

		this.euro = parseInnerInt(value.substring(0, sep));
		this.cent = parseInnerInt(value.substring(sep+1));
	}

	public Price(int euro, int cent) {
		int carry = cent / 100;
		this.euro = euro + carry;
		this.cent = cent % 100;
	}

	public Price(double value) {
		this.euro = (int) value;
		this.cent = (int) Math.round((value - euro) * 100);
	}

	public Price multiply(int factor) {
		return new Price( this.euro*factor, this.cent*factor);
	}

	public Price add(Price pr) {
		return new Price(this.euro + pr.euro, this.cent + pr.cent);
	}

	public int toCent() { return euro*100 + cent; }

	@Override
	public String toString() {
		return String.format("%2d,%02d \u20AC", euro, cent);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Price) {
			Price p = (Price) o;
			return this.euro == p.euro && this.cent == p.cent;
		}
		if (o instanceof CharSequence) {
			Price p = new Price(o.toString());
			return this.euro == p.euro && this.cent == p.cent;
		}
		return this.toString().equals(o.toString());
	}

	@Override
	public int hashCode() {
		return toCent();
	}

	public static Price addAll(Collection<Price> list) {
		int e= 0, c=0;

		for (Price p: list) {
			e+=p.euro;
			c+=p.cent;
		}
		return new Price(e, c);
	}
	private static int parseInnerInt(String s) {
		final int LEN = s.length();
		int i = 0;
		while (!Character.isDigit(s.charAt(i)) && ++i < LEN);
		if (i == LEN)
			return 0;

		int j = s.length()-1;
		while (!Character.isDigit(s.charAt(j)) && --j > -1);

		return Integer.parseInt(s.substring(i, j+1));
	}

}
