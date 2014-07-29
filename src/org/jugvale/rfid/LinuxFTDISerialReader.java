package org.jugvale.rfid;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Will read the file created by FTDI when a compatible chip is connected to a
 * Linux machine using the USB port
 * 
 * @author william
 *
 */
public class LinuxFTDISerialReader {
	private static final String DEFAULT_DEVICE_BASE_PATH = "/dev/serial/by-id";
	private String deviceBasePath;
	public static final String DEFAULT_ERROR_MESSAGE = "Device not found. Check if a device is connected, the driver FTDI was correctly installed and if the device base Path is right(default is "
			+ DEFAULT_DEVICE_BASE_PATH + ")";

	public LinuxFTDISerialReader() {
		deviceBasePath = DEFAULT_DEVICE_BASE_PATH;
	}

	public LinuxFTDISerialReader(String deviceBasePath) {
		this.deviceBasePath = deviceBasePath;
	}

	public List<Path> getAvailableDevices() throws IOException {
		List<Path> deviceList = Collections.emptyList();
		try {
			deviceList = Files.list(Paths.get(deviceBasePath)).collect(
					Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException(DEFAULT_ERROR_MESSAGE, e);
		}
		return deviceList;
	}

	/**
	 * Will lock the thread reading the device. Should be executed in a
	 * separated thread!
	 * 
	 * @param device
	 * @return
	 * @throws IOException
	 */
	public String waitAndRead(Path device) throws IOException {
		if(device == null || !Files.exists(device)){
			throw new IOException("Device should not be null!");
		}
		while (true) {
			try {
				String value = Files.lines(device).filter(s -> s.length() > 0)
						.map(s -> s.substring(1)).findFirst()
						.orElseThrow(() -> new IOException(DEFAULT_ERROR_MESSAGE));

				if (value != null && !value.trim().isEmpty())
					return value;
			} catch (IOException e) {
				throw new IOException(DEFAULT_ERROR_MESSAGE, e);
			}
		}
	}
}