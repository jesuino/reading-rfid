package org.jugvale.rfid.ui;

import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FTDIReaderPaneTest extends Application {

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage s) throws Exception {
		FTDIReaderPane rfidPane = new FTDIReaderPane();
		VBox vbTag = new VBox(100);
		vbTag.setAlignment(Pos.CENTER);
		Label lblReadTag = new Label();
		Label lblError = new Label();
		Button btnRead = new Button("Read RFID Tag");
		lblReadTag.setStyle("-fx-font-size: 20px");
		lblError.setStyle("-fx-font-size: 10px; -fx-text-fill: red;");
		lblError.setWrapText(true);
		vbTag.getChildren().addAll(lblReadTag, btnRead, lblError);
		vbTag.visibleProperty().bind(rfidPane.visibleProperty().not());
		s.setScene(new Scene(new StackPane(rfidPane, vbTag)));
		s.show();
		s.setWidth(600);
		s.setHeight(400);
		
		// a little animation for the error
		ScaleTransition t = new ScaleTransition(new Duration(500));
		t.setNode(lblError);
		t.setAutoReverse(true);
		t.setByX(0.1);
		t.setByY(0.1);
		t.setCycleCount(2);

		btnRead.setOnAction(e -> {
			rfidPane.askForDeviceAndReadTag();
		});

		rfidPane.setOnRead(rfid -> {
			lblError.setText("");
			lblReadTag.setText(rfid);
		});
		
		rfidPane.setOnError(error ->{
			lblReadTag.setText("");
			lblError.setText(error);
			t.play();
		});
	}
}