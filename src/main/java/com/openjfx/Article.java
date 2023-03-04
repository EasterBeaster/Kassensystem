package com.openjfx;

public class Article {
	String name;
	String shortname;
	Price baseprice;

	public Article() {
		name = "Unbekanter Artikel";
		shortname = "Unbekannt";
		baseprice = new Price();
	}

	public Article(String name, String shortname, String baseprice) {
		this.name = name;
		this.shortname = shortname;
		this.baseprice = new Price(baseprice);
	}

	public Article(String name, String shortname, double baseprice) {
		this.name = name;
		this.shortname = shortname;
		this.baseprice = new Price(baseprice);
	}

	public Article(String name, String baseprice) {
		this(name, name, baseprice);
	}

	public Article(String name, double baseprice) {
		this(name, name, baseprice);
	}
}
