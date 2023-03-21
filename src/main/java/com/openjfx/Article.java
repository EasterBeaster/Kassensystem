package com.openjfx;

public record Article (String name,
					   String shortname,
					   Price baseprice) {

	public Article() {
		this("Unbekanter Artikel", "Unbekannt", new Price());
	}

	public Article(String name, String shortname, String baseprice) {
		this(name, shortname, new Price(baseprice));
	}

	public Article(String name, String shortname, double baseprice) {
		this(name, shortname, new Price(baseprice));
	}

	public Article(String name, String baseprice) {
		this(name, name, baseprice);
	}

	public Article(String name, double baseprice) {
		this(name, name, baseprice);
	}
}
