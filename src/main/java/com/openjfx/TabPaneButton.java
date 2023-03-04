package com.openjfx;

public class TabPaneButton extends javafx.scene.control.Button {

	private Article article;

	public TabPaneButton(String text) {
		super(text);
		article = new Article();
	}

	public TabPaneButton(String text, Article a) {
		super(text);
		this.article = a;
	}

	public TabPaneButton(String text, javafx.scene.Node graphic, Article a) {
		super(text, graphic);
		this.article = a;
	}

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}
}
