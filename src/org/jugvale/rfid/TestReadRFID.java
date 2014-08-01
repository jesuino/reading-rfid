package org.jugvale.rfid;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class TestReadRFID {

	static LinuxFTDISerialReader reader = new LinuxFTDISerialReader();

	public static void main(String[] args) throws IOException,
			InterruptedException {
		System.out
				.println("\n--- Welcome for the Simplest RFID scanner in Java! -- \n");
		System.out.println("Please, make sure you installed the FTDI drivers.");
		System.out.println("This program will only work with Linux!\n");

		List<Path> deviceList = scan();
		Path chosen = menu(deviceList);
		read(chosen);
	}

	private static List<Path> scan() {
		List<Path> deviceList = Collections.emptyList();
		System.out
				.println("We are scanning for FTDI devices connected to this machine...");
		System.out.println("(press Ctrl + C to stop...)");
		do {
			try {
				deviceList = reader.getAvailableDevices();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		} while (deviceList.size() == 0);
		return deviceList;
	}

	private static Path menu(List<Path> deviceList) {
		int input;
		Scanner sc = new Scanner(System.in);
		int size = deviceList.size();
		do {
			System.out.println("Choose a device:");
			for (int i = 0; i < size; i++) {
				System.out.printf("%d: %s\n", i, deviceList.get(i));
			}
			System.out.printf("\nEnter the selected device number: ");
			input = sc.nextInt();
			System.out.println();
		} while (input < 0 || input > deviceList.size());
		sc.close();
		return deviceList.get(input);
	}

	private static void read(Path p) throws IOException {
		System.out.println("Pass the RFID card...");
		System.out.println(reader.waitAndRead(p));

	}
}