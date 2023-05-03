
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
		Price l_euro = parsePrice(value);
		this.euro = l_euro.euro;
		this.cent = l_euro.cent;
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

	public int euro() {
		return euro;
	}

	public int cent() {
		return cent;
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

	public static Price parsePrice(String value) {
		if(value.isBlank())
			return new Price();
		value = value.trim();

		int sep = value.indexOf(',');
		if (sep == -1)
			sep = value.indexOf('.');
		if (sep == -1) {
			return new Price(parseInnerInt(value), 0);
		}

		return new Price(parseInnerInt(value.substring(0, sep)),
						 parseInnerInt(value.substring(sep+1)));
	}

	public static Price parsePrice(double value) {
		int l_euro = (int) value;
		return new Price(l_euro, (int) Math.round((value - l_euro) * 100));
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
		if(s.isBlank())
			return 0;
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
