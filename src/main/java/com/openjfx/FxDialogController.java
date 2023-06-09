
package com.openjfx;

import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;

public class FxDialogController {

	@FXML
	private DialogPane dialogpane;
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
	private Button layer2_goLeft;
	@FXML
	private Button layer2_goRight;
	@FXML
	private GridPane layer_enterpaid;
	@FXML
	private TextField money_textfield;
	@FXML
	private Button btn_clear;
	@FXML
	private Label money_paid;
	@FXML
	private Label money_back;
	@FXML
	private Button layer3_goLeft;

	private Price totalAmount;
	private Price paidAmount;

	private Dialog<ButtonType> dialog;


	@FXML
	private void initialize() {
		// Layer 1
		euro5_btn.setOnAction(  evt -> updateLabelsOnLayer3(new Price( 5, 0), (byte) 2) );
		euro10_btn.setOnAction( evt -> updateLabelsOnLayer3(new Price(10, 0), (byte) 2) );
		euro20_btn.setOnAction( evt -> updateLabelsOnLayer3(new Price(20, 0), (byte) 2) );
		euro50_btn.setOnAction( evt -> updateLabelsOnLayer3(new Price(50, 0), (byte) 2) );
		other_euro_btn.setOnAction( evt -> switchToLayer((byte) 1));

		// Layer 2
		layer2_goLeft.setOnAction(evt -> {
			money_textfield.setText("");
			switchToLayer((byte) 0);
		});
		layer2_goRight.setOnAction(evt -> updateLabelsOnLayer3(new Price(money_textfield.getText()), (byte) 2) );
		layer2_goRight.disableProperty().bind(money_textfield.textProperty().isEmpty());

		layer_enterpaid.getChildren().filtered(n -> n != btn_clear && n != money_textfield).forEach(n -> {
			Button b = (Button) n;
			if(b.getText().length() == 1) {
				b.setOnAction(this::numclickAction);
			}
		});

		money_textfield.setTextFormatter(new TextFormatter<String>(SceneUtil.numberFormatter));

		btn_clear.setOnAction(evt -> money_textfield.setText(""));

		// Layer 3
		layer3_goLeft.setOnAction(evt -> switchToLayer(lastLayer));

		// Other
		dialogpane.getButtonTypes().setAll(ButtonType.CANCEL/*, ButtonType.PREVIOUS, ButtonType.FINISH*/);

		layeredPane.getChildren().forEach(n -> n.setVisible(false));
		switchToLayer((byte) 0);
		lastLayer = 0;
	}

	@FXML
	void onCompletePayment(ActionEvent event) {
		dialog.setResult(ButtonType.FINISH);
		dialog.close();
	}

	private void updateLabelsOnLayer3(Price p, byte gotoLayer) {
		paidAmount = p;
		money_paid.setText(p.toString());
		money_back.setText(new Price(0, paidAmount.toCent() - totalAmount.toCent()).toString());
		switchToLayer(gotoLayer);
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

	public void setDialog(Dialog<ButtonType> parent) {
		dialog = parent;
	}

	private byte currentLayer = 0;
	private byte lastLayer = 0;
	private void switchToLayer(byte b) {
		layeredPane.getChildren().get(currentLayer).setVisible(false);
		layeredPane.getChildren().get(b).setVisible(true);
		lastLayer = currentLayer;
		currentLayer = b;
	}

	private final javafx.event.EventHandler<? super MouseEvent> GotoLL = MouseEvt -> switchToLayer(lastLayer);
	//private static final byte LAYER_BANKNOTES = 0, LAYER_MAN_PRICE = 1, LAYER_CHANGE = 2;

	private void numclickAction(ActionEvent evt) {
		String btnInput = ((Button) evt.getSource()).getText();

		money_textfield.appendText(btnInput);
		//charqueue = money_textfield.getText();
	}
}
