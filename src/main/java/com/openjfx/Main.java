package com.openjfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

	@Override
	public void start(Stage stage) {
		File f = new File(".");
		System.out.println(f.getAbsolutePath());

		// File fxml = new File("/home/philipp/Dokumente/JavaFx_Project/Kassensystem/src/main/java/com/openjfx/Kassesys_gui_besser.fxml");
		Parent root;
		try {
			System.out.println("Base URL: " + getClass().getResource(".").toString());
			root = FXMLLoader.load(getClass().getResource("Kassesys_gui_besser.fxml")); //fxml.toURI().toURL());
		} catch (javafx.fxml.LoadException fxe) {
			fxe.printStackTrace();
			System.err.println("The File has a false Configuration!");
			System.exit(1);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("The Path to the FXML-File could not be resolved!");
			System.exit(1);
			return;
		}

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}

}
