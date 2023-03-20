
package com.openjfx;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

public class FxDialogController {

	@FXML
	private Label totalscreen;
	@FXML
	private TableView<BonItem> itemdisplaytable;
	@FXML
	private StackPane layeredPane;
	@FXML
	private Button euro5_btn;
	@FXML
	private Button euro10_btn;
	@FXML
	private Button euro20_btn;
	@FXML
	private Button euro50_btn;
	@FXML
	private Button other_euro_btn;
	@FXML
	private TextField money_textfield;
	@FXML
	private Label money_paid;
	@FXML
	private Label money_back;

	private Price totalAmount;
	private Price payedAmount;


	@FXML
	private void initialize() {
		euro5_btn.setOnAction( evt -> {
			payedAmount = new Price( 5, 0);
			switchToLayer((byte) 2);
		});
		euro10_btn.setOnAction( evt -> {
			payedAmount = new Price(10, 0);
			switchToLayer((byte) 2);
		});
		euro20_btn.setOnAction( evt -> {
			payedAmount = new Price(20, 0);
			switchToLayer((byte) 2);
		});
		euro50_btn.setOnAction( evt -> {
			payedAmount = new Price(50, 0);
			switchToLayer((byte) 2);
		});
		other_euro_btn.setOnAction( evt -> switchToLayer((byte) 1));
	}
	
	public void setTotal(StringProperty prop, Price total) {
		totalscreen.textProperty().bind(prop);
		totalAmount = total;

		if (totalAmount.toCent() > 500) {
			if (totalAmount.toCent() > 1000) {
				if (totalAmount.toCent() > 2000) {
					if (totalAmount.toCent() > 5000) {
						euro50_btn.setDisable(true); }
					euro20_btn.setDisable(true); }
				euro10_btn.setDisable(true); }
			euro5_btn.setDisable(true);
		}
	}

	public void setTable(TableView<BonItem> pTable) {
		itemdisplaytable.getColumns().addAll(pTable.getColumns());
		itemdisplaytable.setItems(pTable.getItems());
	}
	
	private byte currentLayer = 0;
	private void switchToLayer(byte b) {
		layeredPane.getChildren().get(currentLayer).setVisible(false);
		layeredPane.getChildren().get(b).setVisible(true);
		currentLayer = b;
	}
}
