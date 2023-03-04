
package com.openjfx;

import javafx.beans.property.*;

public class BonItem
{
	private final Article article_ref;

	public BonItem (Article a, int amount) {
		this.name = new SimpleStringProperty(a.name);
		this.amount = new SimpleIntegerProperty(amount);
		this.price = new SimpleObjectProperty<>(a.baseprice.multiply(amount));
		this.article_ref = a;
	}

	public BonItem (String name, int amount, String price) {
		this.article_ref = new Article(name, name, price);
		this.name = new SimpleStringProperty(name);
		this.amount = new SimpleIntegerProperty(amount);
		this.price = new SimpleObjectProperty<>(article_ref.baseprice.multiply(amount));
	}

	public BonItem (String name, int amount, double price) {
		this.article_ref = new Article(name, name, price);
		this.name = new SimpleStringProperty(name);
		this.amount = new SimpleIntegerProperty(amount);
		this.price = new SimpleObjectProperty<>(article_ref.baseprice.multiply(amount));
	}


	private final SimpleStringProperty name;
	public StringProperty nameProperty() { return this.name; }
	/*public String getName() {
		return this.name.get();
	}
	public void setName(String name) { this.name.set(name);	}*/


	private final SimpleIntegerProperty amount;
	public IntegerProperty amountProperty() { return this.amount; }
	public Integer getAmount() {
		return amount.get();
	}
	public void setAmount(Integer amount) {
		if(amount > 0) {
			this.amount.set(amount);
			this.price.set(article_ref.baseprice.multiply(amount));
		}
	}


	private final SimpleObjectProperty<Price> price;
	public ObjectProperty<Price> priceProperty() { return this.price; }
	public Price getPrice() {
		return price.get();
	}
	/*public void setPrice(Price price) { this.price.set(price); }
	public void setPrice(String price) { this.price.set(new Price(price)); }*/

}
