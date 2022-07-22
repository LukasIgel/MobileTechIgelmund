package com.example.mobiletechigelmund.models;

public class BluetoothDeviceInfo {
    private String deviceType;
    private String deviceName;
    private String deviceAddress;
    private String signalStrength;
    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }
    public String getSignalStrength() {
        return signalStrength;
    }

    public void setSignalStrength(String signalStrength) {
        this.signalStrength = signalStrength;
    }
    public boolean isEqual(Object object)
    {
        boolean isEqual= true;

        //if (object != null && object instanceof BluetoothDeviceInfo)
        {
            if (!this.getSignalStrength().equals(((BluetoothDeviceInfo) object).getSignalStrength())) {
                isEqual = false;
            }
            if (!this.getDeviceType().equals(((BluetoothDeviceInfo) object).getDeviceType())) {
                isEqual = false;
            }
            if (!this.getDeviceName().equals(((BluetoothDeviceInfo) object).getDeviceName())) {
                isEqual = false;
            }
            if (!this.getDeviceAddress().equals(((BluetoothDeviceInfo) object).getDeviceAddress())) {
                isEqual = false;
            }
        }

        return isEqual;
    }
/*
    @Override
    public int hashCode() {
        return this.deviceAddress.hashCode();
    }

 */
}
