package pl.marhaj.ubiqt.device.exception;

public class DeviceNotFoundException extends RuntimeException{
    public DeviceNotFoundException(String macAddress) {
        super("No device with address " + macAddress + " found");
    }
}
