package ru.ddiribas.Encryption;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

public class FingerPrinter {
	public static byte[] addFingerPrint(byte[] input) {
		SystemInfo systemInfo = new SystemInfo();
		OperatingSystem operatingSystem = systemInfo.getOperatingSystem();
		HardwareAbstractionLayer hardwareAbstractionLayer = systemInfo.getHardware();
		CentralProcessor centralProcessor = hardwareAbstractionLayer.getProcessor();

		String vendor = operatingSystem.getManufacturer();
		String processorID = centralProcessor.getProcessorID();
		String processorIdentifier = centralProcessor.getIdentifier();
		String key = new String(input);
		int processors = centralProcessor.getLogicalProcessorCount();

		StringBuilder result = new StringBuilder();
		result.append(vendor).append(processorID).append(processorIdentifier).append(processors).append(key);
		return StribogBouncy.getByteHash256(result.toString().getBytes());
	}
}
