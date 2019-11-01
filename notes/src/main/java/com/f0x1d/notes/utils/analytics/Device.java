package com.f0x1d.notes.utils.analytics;

public class Device {

    private String serialId;
    private String androidId;
    private String wifiMac;
    private String bluetoothMac;
    private int sdkVersion;
    private String firmwareId;
    private String firmwareDisplay;
    private String productName;
    private String deviceName;
    private String boardName;
    private String cpuAbi;
    private String manufacturerName;
    private String brandName;
    private String modelName;


    public Device withSerialId(String serialId) {
        this.serialId = serialId;
        return this;
    }

    public Device withAndroidId(String androidId) {
        this.androidId = androidId;
        return this;
    }

    public Device withWifiMac(String wifiMac) {
        this.wifiMac = wifiMac;
        return this;
    }

    public Device withBluetoothMac(String bluetoothMac) {
        this.bluetoothMac = bluetoothMac;
        return this;
    }

    public Device withSdkVersion(int sdkVersion) {
        this.sdkVersion = sdkVersion;
        return this;
    }

    public Device withFirmwareId(String firmwareId) {
        this.firmwareId = firmwareId;
        return this;
    }

    public Device withFirmwareDisplay(String firmwareDisplay) {
        this.firmwareDisplay = firmwareDisplay;
        return this;
    }

    public Device withProductName(String productName) {
        this.productName = productName;
        return this;
    }

    public Device withDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return this;
    }

    public Device withBoardName(String boardName) {
        this.boardName = boardName;
        return this;
    }

    public Device withCpuAbi(String cpuAbi) {
        this.cpuAbi = cpuAbi;
        return this;
    }

    public Device withManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
        return this;
    }

    public Device withBrandName(String brandName) {
        this.brandName = brandName;
        return this;
    }

    public Device withModelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    @Override
    public String toString() {
        return "Device{" +
                "serialId='" + serialId + '\'' +
                ", androidId='" + androidId + '\'' +
                ", wifiMac='" + wifiMac + '\'' +
                ", bluetoothMac='" + bluetoothMac + '\'' +
                '}';
    }
}
