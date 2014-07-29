package org.jugvale.rfid.ui;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import org.jugvale.rfid.LinuxFTDISerialReader;

public class FTDIReaderPane extends TitledPane {

	LinuxFTDISerialReader reader = new LinuxFTDISerialReader();
	ListView<Path> listDevices;

	/**
	 * Contains the read RFID Tag
	 */
	public StringProperty rfidTagProperty;

	/**
	 * CAny error will be on this message
	 */
	public StringProperty errorWhenReadingProperty;

	private Path selectedDevice;

	public FTDIReaderPane() {
		settings();
		initComponent();
	}

	/**
	 * 
	 * Will show up and read a RFID tag, but it will always ask to select a tag
	 * 
	 * @throws IOException
	 *             Error when it can't read the device
	 */
	public void askForDeviceAndReadTag() {
		try {
			if (selectedDevice == null)
				fillDevices();
			else {
				readDevice();
			}
			setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
			setErrorMessage();
		}
	}

	private void settings() {
		setText("Read RFID Tag");
		setMaxHeight(200);
		setMaxWidth(300);
		setStyle("-fx-stroke-width: 20;");
		setCollapsible(false);
		setVisible(false);
	}

	private void initComponent() {
		StackPane root = new StackPane();
		Label lblPassCard = new Label("Pass RFID Card");
		lblPassCard.setStyle("-fx-font-size: 20px; -fx-font-weight: bold");
		listDevices = new ListView<>();
		listDevices.setOnMouseReleased(this::newDeviceSelectedAction);
		root.getChildren().addAll(listDevices, lblPassCard);
		lblPassCard.visibleProperty().bind(listDevices.visibleProperty().not());
		this.setContent(root);
		rfidTagProperty = new SimpleStringProperty();
		errorWhenReadingProperty = new SimpleStringProperty();
	}

	private void newDeviceSelectedAction(MouseEvent e) {
		Path selectedPath = listDevices.getSelectionModel().getSelectedItem();
		if (selectedPath != null) {
			listDevices.setVisible(false);
			listDevices.getSelectionModel().clearSelection();
			selectedDevice = selectedPath;
			readDevice();
			return;
		}
	}

	private void fillDevices() throws IOException {
		listDevices.setVisible(true);
		listDevices.getItems().setAll(reader.getAvailableDevices());
		listDevices.getSelectionModel().clearSelection();
	}

	private void readDevice() {
		new Thread(new Task<String>() {
			@Override
			protected String call() throws Exception {
				return reader.waitAndRead(selectedDevice);
			}

			@Override
			protected void succeeded() {
				try {
					rfidTagProperty.setValue(this.get());
					errorWhenReadingProperty.setValue("");
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
				// will dispose the pane no matter what
				setVisible(false);
			}

			@Override
			protected void failed() {
				setErrorMessage();
				setVisible(false);
				selectedDevice = null;
			}
		}).start();
	}

	private void setErrorMessage() {
		errorWhenReadingProperty
				.setValue(LinuxFTDISerialReader.DEFAULT_ERROR_MESSAGE);
	}
}