package com.openjfx;

import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.control.cell.PropertyValueFactory;

import mjson.Json;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

// import com.openjfx.BonItem;
// import static com.openjfx.TabColumnThemes.applyColumnStyle;

public class FxFXMLController {
	@FXML
	private TabPane tabs;
	@FXML
	private GridPane numpad;
	@FXML
	private TextField smallscreen;
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
	private Price currentTotalAsPrice;


	@FXML
	private void initialize() {
		numpad.getChildren().filtered(n -> n != smallscreen).forEach(n -> {
			Button b = (Button) n;
			if(b.getText().length() == 1) {
				b.setOnAction(this::numclickAction);
			}
		});

		smallscreen.setTextFormatter(new TextFormatter<String>(numberFormatter));

		initBonview();
		initTabpane();
		totalscreen.setText(" 0,00 \u20AC");
		currentTotalAsPrice = new Price();
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
		if (pri < 0.01)
			return;
		if (pri >= 99.99)
			pri = 99.99;
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
		if (bondata.isEmpty())
			return;

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Warning!");
		alert.setHeaderText("Wirklich alles löschen ?");

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.OK) {
			bondata.clear();

			charqueue = "";
			smallscreen.setText("");
		}
	}

	@FXML
	void open_payment_dialog(ActionEvent event) {
		if (new Price(0.0).equals(currentTotalAsPrice))
			return;

		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("BezahlDialog.fxml"));
			DialogPane pane = loader.load();

			FxDialogController diCon = loader.getController();

			diCon.setTotal(totalscreen.textProperty(), currentTotalAsPrice);
			diCon.setTable(bonview);

			Dialog<ButtonType> dialog = new Dialog<>();
			dialog.setDialogPane(pane);
			dialog.setTitle("Bezahlvorgang");

			dialog.showAndWait();
		} catch (IOException ex) {
			System.err.println("Dialog konnte nicht geöffnet werden!");
			ex.printStackTrace();
			return;
		}

	}

	private void updateTotalscreen() {
		currentTotalAsPrice = Price.addAll(bondata.stream().map(BonItem::getPrice).toList());
		totalscreen.setText(currentTotalAsPrice.toString());
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
		if (url == null)
			System.err.println("Json-File not found. Loading default Buttons with no Actions.");
		else {

			Json json = Json.read(url);

			List<Json> columns = json.at("order").asJsonList();
			tabs.getTabs().clear();
			for (Json colName : columns) {
				Tab newTab = new Tab(colName.asString());
				AnchorPane anchorp = new AnchorPane();
				GridPane gridp = new GridPane();

				AnchorPane.setTopAnchor(gridp, 0.0);
				AnchorPane.setRightAnchor(gridp, 0.0);
				//AnchorPane.setBottomAnchor(gridp, 0.0);
				AnchorPane.setLeftAnchor(gridp, 0.0);

				byte anzrows = 7, anzcolumns = 5;
				if (json.has("rows"))
					anzrows = json.at("rows").asByte();
				if (json.has("columns"))
					anzrows = json.at("columns").asByte();

				for (byte i = 0; i < anzrows; i++) {
					RowConstraints constraint = new RowConstraints(100, 120, 200);
					constraint.setVgrow(Priority.SOMETIMES);
					gridp.getRowConstraints().add(constraint);
				}
				for (byte i = 0; i < anzcolumns; i++) {
					ColumnConstraints constraint = new ColumnConstraints(110, 150, 225);
					constraint.setHgrow(Priority.SOMETIMES);
					gridp.getColumnConstraints().add(constraint);
				}

				var articlelist = json.at(colName.asString()).at("buttons").asJsonList();
				for (Json article_info : articlelist) {
					String artiName = article_info.at("name").asString();
					String artiShortname = article_info.has("shortname") ? article_info.at("shortname").asString() : artiName;
					Article article = new Article(artiName, artiShortname, article_info.at("price").asFloat());

					TabPaneButton tabPaneBtn = new TabPaneButton(artiShortname, article);
					tabPaneBtn.setOnAction(this::addItemToBon);

					gridp.add(tabPaneBtn, article_info.at("x").asInteger(), article_info.at("y").asInteger());

					if (article_info.has("rowspan"))
						GridPane.setRowSpan(tabPaneBtn, article_info.at("rowspan").asInteger());
					if (article_info.has("columnspan"))
						GridPane.setColumnSpan(tabPaneBtn, article_info.at("columnspan").asInteger());

					GridPane.setMargin(tabPaneBtn, new Insets(6.0));
				}

				//applyTabStyleThemes

				anchorp.getChildren().add(gridp);
				newTab.setContent(anchorp);
				tabs.getTabs().add(newTab);
			}
		}

		// tabs.tabMinWidthProperty().bind(Bindings.min(150, tabs.widthProperty().divide(tabs.getTabs().size()*0.77)));

		// Style adjacent Tabs with round Corners
		tabs.getSelectionModel().selectedItemProperty().addListener(this::changeCSSofTab);
		if(tabs.getTabs().size() > 1)
			tabs.getTabs().get(1).getStyleClass().add("child-after-selected");
	}



	//******** Lambda-Methods ********//

	private void numclickAction(ActionEvent evt) {
		String btnInput = ((Button) evt.getSource()).getText();

		smallscreen.appendText(btnInput);
		charqueue = smallscreen.getText();

		/*
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
				if (charqueue.length() < 3) {
					charqueue += btnInput;
					smallscreen.setText(charqueue);
				} else {
					charqueue = "999";
					smallscreen.setText("999");
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
			}*/

	}

	private final UnaryOperator<TextFormatter.Change> numberFormatter = change -> {
		String potentialNewVal = change.getControlNewText();
		if (!potentialNewVal.matches("\\d{0,3}(,\\d{0,2})?")) {
			System.out.println("\"" + potentialNewVal + "\" hat das falsche Format.");
			return null;
		}
		if (potentialNewVal.startsWith(",")) {
			change.setText('0' + change.getText());
			// System.out.println("Caret-Position:" + change.getCaretPosition());
			// System.out.println("Anchor-Position:" + change.getAnchor() + "\n");
			change.setCaretPosition(change.getCaretPosition()+1);
			change.setAnchor(change.getAnchor()+1);
			// change.setRange(change.getRangeStart(), change.getRangeEnd()+1);
			return change;
		}

		// Entferne führende Nullen
		if (potentialNewVal.startsWith("0") && !potentialNewVal.startsWith("0,")) {
			int s = potentialNewVal.length();
			int i = 1;
			while(i < s && potentialNewVal.charAt(i) == '0')
				i++;
			if (i >= s)
				change.setText("0");
			else
				change.setText(potentialNewVal.substring(i)); // Erster Index ohne Null 

			change.setRange(0, change.getControlText().length());
		}

		return change;
	};

	private void addItemToBon(ActionEvent evt) {
		Article a = ((TabPaneButton) evt.getSource()).getArticle();

		if (a.baseprice().toCent() == 0)
			return;

		int amount = 1;
		if (!charqueue.isEmpty() && !charqueue.contains(","))
			amount = Integer.parseInt(charqueue);

		//Try BonItem Collapse
		boolean success = false; int index = 0;
		for (BonItem bi: bondata) {
			if (bi.getArticle().equals(a)) {
				bi.setAmount(bi.getAmount() + amount);
				updateTotalscreen();
				bonview.getSelectionModel().clearAndSelect(index);
				success = true;
				break;
			}
			index++;
		}

		if(!success) {
			BonItem entry = new BonItem(a, amount);
			bondata.add(entry);
			bonview.getSelectionModel().clearAndSelect(index);
		}

		charqueue = "";
		smallscreen.setText("");
	}

	private void changeCSSofTab(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
		final var TAB_COLLEC = tabs.getTabs();
		final int SIZE = tabs.getTabs().size();
		int pos;

		pos = TAB_COLLEC.indexOf(oldValue);
		if (pos > 0)
			TAB_COLLEC.get(pos-1).getStyleClass().remove("child-before-selected");
		if (pos < SIZE-1)
			TAB_COLLEC.get(pos+1).getStyleClass().remove("child-after-selected");

		pos = TAB_COLLEC.indexOf(newValue);
		if (pos > 0)
			TAB_COLLEC.get(pos-1).getStyleClass().add("child-before-selected");
		if (pos < SIZE-1)
			TAB_COLLEC.get(pos+1).getStyleClass().add("child-after-selected");
	}


	//******** Private-Static-Methods - Please Ignore ********//

	/* private static void addPseudoClassIfAbsent(javafx.css.Styleable styleable, String styleClass) {
		ObservableList<String> styleClasses = styleable.getStyleClass();
		if (!styleClasses.contains(styleClass)) {
			styleClasses.add(styleClass);
		}
	} */
}
