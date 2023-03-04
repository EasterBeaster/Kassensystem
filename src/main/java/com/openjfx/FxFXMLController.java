package com.openjfx;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;

import mjson.Json;

import java.util.List;

// import com.openjfx.BonItem;
// import static com.openjfx.TabColumnThemes.applyColumnStyle;

public class FxFXMLController {
	@FXML
	private TabPane tabs;
	@FXML
	private GridPane numpad;
	@FXML
	private Label smallscreen;
	@FXML
	private Label totalscreen;
	@FXML
	private TableView<BonItem> bonview;
	@FXML
	private TableColumn<BonItem, String> bonview_article_col;
	@FXML
	private TableColumn<BonItem, Integer> bonview_amount_col;
	@FXML
	private TableColumn<BonItem, Price> bonview_price_col;


	private String charqueue = "";
	private ObservableList<BonItem> bondata;// = FXCollections.observableArrayList();

	@FXML
	private void initialize() {
		numpad.getChildren().filtered(n -> n != smallscreen).forEach(n -> {
			Button b = (Button) n;
			if(b.getText().length() == 1) {
				b.setOnAction(this::numclickAction);
			}
		});

		initBonview();
		initTabpane();
		totalscreen.setText("0,00 €");
	}

	@FXML
	void clearSmallScreen(ActionEvent event) {
		charqueue = "";
		smallscreen.setText("");
	}

	@FXML
	void dec_SelectedItem(ActionEvent event) {
		bonview.getSelectionModel().getSelectedItems().forEach(item -> item.setAmount(item.getAmount() -1));
		updateTotalscreen();
	}

	@FXML
	void inc_SelectedItem(ActionEvent event) {
		bonview.getSelectionModel().getSelectedItems().forEach(item -> item.setAmount(item.getAmount() +1));
		updateTotalscreen();
	}

	@FXML
	void enter_pressed(ActionEvent event) {
		if (charqueue.isEmpty())
			return;
		double pri = Double.parseDouble(charqueue.replace(',', '.'));
		if (pri >= 0.01)
			bondata.add(new BonItem("Extra Betrag", 1, pri));

		charqueue = "";
		smallscreen.setText("");
	}

	@FXML
	void cancel_pressed(ActionEvent event) {
		bondata.removeAll(bonview.getSelectionModel().getSelectedItems());
	}

	@FXML
	void abort_pressed(ActionEvent event) {
		bondata.clear();

		charqueue = "";
		smallscreen.setText("");
	}

	private void numclickAction(ActionEvent evt) {
		String btnInput = ((Button) evt.getTarget()).getText();

		if (charqueue.isEmpty()) {
			if (btnInput.equals(",")) {
				charqueue = "0,";
				smallscreen.setText("0,");
			} else if (!btnInput.equals("0")) {
				charqueue = btnInput;
				smallscreen.setText(btnInput);
			}
			return;
		}

		int lio = charqueue.lastIndexOf(',');

		// Noch kein Komma; Min eine Zahl != 0 in der Queue
		if (lio == -1) {
			if (btnInput.equals(",")) {
				charqueue += ',';
				smallscreen.setText(charqueue);
			} else {
				if (charqueue.length() < 2) {
					charqueue += btnInput;
					smallscreen.setText(charqueue);
				} else {
					charqueue = "99";
					smallscreen.setText("99");
				}
			}
			return;
		}

		if (!btnInput.equals(","))
		switch (charqueue.length() - lio -1) {
			case 0:
			case 1: // 0 oder 1 Nachkommastelle
				charqueue += btnInput;
				smallscreen.setText(charqueue);
				break;

			default:
				System.out.println("Nicht mehr als 2 Nachkommastellen sind erlaubt!");
		}

	}

	private void addItemToBon(ActionEvent evt) {
		Article a = ((TabPaneButton) evt.getTarget()).getArticle();

		if (a.baseprice.toCent() == 0)
			return;

		int amount = 1;
		if (!charqueue.isEmpty() && !charqueue.contains(","))
			amount = Integer.parseInt(charqueue);

		//Temporär: Add without collapse
		BonItem entry = new BonItem(a, amount);
		bondata.add(entry);

		charqueue = "";
		smallscreen.setText("");
	}

	private void updateTotalscreen() {
		totalscreen.setText(Price.addAll(bondata.stream().map(BonItem::getPrice).toList()).toString());
	}




	//******** Init-Methods ********//

	private void initBonview() {
		bonview_article_col.setCellValueFactory(new PropertyValueFactory<BonItem, String>("name"));
		bonview_amount_col.setCellValueFactory(new PropertyValueFactory<BonItem, Integer>("amount"));
		bonview_price_col.setCellValueFactory(new PropertyValueFactory<BonItem, Price>("price"));
		bonview.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		bondata = FXCollections.observableArrayList(); // bonitem -> new Observable[]{ bonitem.priceProperty() });
		bonview.setItems(bondata);

		bondata.addListener((ListChangeListener<? super BonItem>) c -> {
			while(c.next())
				if(c.wasAdded() || c.wasRemoved() || c.wasUpdated() || c.wasReplaced()) {
					updateTotalscreen();
					c.reset();
					break;
				}
		});
	}

	private void initTabpane() {
		java.net.URL url = getClass().getResource("backwaren_linux.json");
		if (url == null) {
			System.err.println("Json-File not found. Loading default Buttons with no Actions.");
			return;
		}

		Json json = Json.read(url);

		List<Json> columns = json.at("order").asJsonList();
		tabs.getTabs().clear();
		for (Json colName: columns)
		{
			Tab newTab = new Tab(colName.asString());
			AnchorPane anchorp = new AnchorPane();
			GridPane gridp = new GridPane();

			AnchorPane.setTopAnchor(gridp, 0.0);
			AnchorPane.setRightAnchor(gridp, 0.0);
			//AnchorPane.setBottomAnchor(gridp, 0.0);
			AnchorPane.setLeftAnchor(gridp, 0.0);

			for (int i = 0; i < 7; i++) {
				RowConstraints constraint = new RowConstraints(80, 80, 100);
				constraint.setVgrow(Priority.SOMETIMES);
				gridp.getRowConstraints().add(constraint);
			}
			for (int i = 0; i < 5; i++) {
				ColumnConstraints constraint = new ColumnConstraints(100, 100, 125);
				constraint.setHgrow(Priority.SOMETIMES);
				gridp.getColumnConstraints().add(constraint);
			}

			var articlelist = json.at(colName.asString()).at("buttons").asJsonList();
			for(Json article_info: articlelist)
			{
				String artiName = article_info.at("name").asString();
				String artiShortname = article_info.has("shortname") ? article_info.at("shortname").asString() : artiName;
				Article article = new Article(artiName, artiShortname, article_info.at("price").asFloat());

				TabPaneButton tabPaneBtn = new TabPaneButton(artiName, article);	// <- Shortname *****************
				tabPaneBtn.setOnAction(this::addItemToBon);

				gridp.add(tabPaneBtn, article_info.at("y").asInteger(), article_info.at("x").asInteger());

				GridPane.setMargin(tabPaneBtn, new Insets(6.0));
			}

			//applyTabStyleThemes

			anchorp.getChildren().add(gridp);
			newTab.setContent(anchorp);
			tabs.getTabs().add(newTab);
		}
	}

}
