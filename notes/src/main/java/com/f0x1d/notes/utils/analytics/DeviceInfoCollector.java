package com.f0x1d.notes.utils.analytics;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import static android.text.TextUtils.isEmpty;


@SuppressLint({"HardwareIds", "PrivateApi"})
public class DeviceInfoCollector {

    private static final String TAG = "DeviceInfoCollector";

    public Device collect(Context context) {
        return new Device()
                .withSerialId(serialId())
                .withAndroidId(androidId(context))
                .withWifiMac(wifiMac(context))
                .withBluetoothMac(bluetoothMac(context))
                .withSdkVersion(Build.VERSION.SDK_INT)
                .withFirmwareId(Build.ID)
                .withFirmwareDisplay(Build.DISPLAY)
                .withProductName(Build.PRODUCT)
                .withDeviceName(Build.DEVICE)
                .withBoardName(Build.BOARD)
                .withCpuAbi(Build.CPU_ABI)
                .withManufacturerName(Build.MANUFACTURER)
                .withBrandName(Build.BRAND)
                .withModelName(Build.MODEL);
    }

    private String serialId() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");

            if (isInvalidSerial(serialNumber))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");

            if (isInvalidSerial(serialNumber))
                serialNumber = (String) get.invoke(c, "ro.serialno");

            if (isInvalidSerial(serialNumber))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");

            if (isInvalidSerial(serialNumber))
                serialNumber = Build.SERIAL;

            if (isInvalidSerial(serialNumber))
                serialNumber = null;

        } catch (Exception e) {
            Log.d(TAG, "Serial ID obtaining failed: ", e);
            serialNumber = null;
        }

        return serialNumber != null ? serialNumber.toLowerCase() : "";
    }

    private String androidId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID).toLowerCase();
    }

    private String wifiMac(Context context) {
        String wifiMac = wifiMacFromManager(context);

        if (isInvalidMac(wifiMac))
            wifiMac = wifiMacFromNetworkInterfaces();

        if (isInvalidMac(wifiMac))
            wifiMac = wifiMacFromFileSystem();

        if (isInvalidMac(wifiMac))
            wifiMac = null;

        return wifiMac != null ? wifiMac.toLowerCase() : "";
    }

    private String wifiMacFromManager(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm == null)
                return "";
            WifiInfo connectionInfo = wm.getConnectionInfo();
            if (connectionInfo == null)
                return "";
            return connectionInfo.getMacAddress();
        } catch (Exception e) {
            Log.d(TAG, "WiFi MAC obtaining from manager failed: ", e);
            return "";
        }
    }

    private String wifiMacFromNetworkInterfaces() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface anInterface : interfaces) {
                if (!"wlan0".equalsIgnoreCase(anInterface.getName()))
                    continue;

                byte[] mac = anInterface.getHardwareAddress();
                if (mac == null)
                    return "";

                StringBuilder builder = new StringBuilder();
                for (byte b : mac)
                    builder.append(String.format("%02X:", b));
                if (builder.length() > 0)
                    builder.deleteCharAt(builder.length() - 1);
                return builder.toString();
            }
            return "";
        } catch (Exception e) {
            Log.d(TAG, "WiFi MAC obtaining from network interfaces failed: ", e);
            return "";
        }
    }

    private String wifiMacFromFileSystem() {
        try (FileReader fileReader = new FileReader("/sys/class/net/wlan0/address");
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            return bufferedReader.readLine();
        } catch (Exception e) {
            Log.d(TAG, "WiFi MAC obtaining from file system failed: ", e);
            return "";
        }
    }

    private String bluetoothMac(Context context) {
        String bluetoothMac = bluetoothMacFromAdapter();

        if (isInvalidMac(bluetoothMac))
            bluetoothMac = bluetoothMacFromContentResolver(context);

        if (isInvalidMac(bluetoothMac))
            bluetoothMac = null;

        return bluetoothMac != null ? bluetoothMac.toLowerCase() : "";
    }

    @SuppressLint("MissingPermission")
    @SuppressWarnings("JavaReflectionMemberAccess")
    private String bluetoothMacFromAdapter() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (bluetoothAdapter == null)
                return "";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Field mServiceField = bluetoothAdapter.getClass().getDeclaredField("mService");
                mServiceField.setAccessible(true);

                Object btManagerService = mServiceField.get(bluetoothAdapter);

                if (btManagerService != null)
                    return (String) btManagerService.getClass().getMethod("getAddress").invoke(btManagerService);
                else
                    return "";
            } else
                return bluetoothAdapter.getAddress();
        } catch (Exception e) {
            Log.d(TAG, "BT MAC obtaining from adapter failed: ", e);
            return "";
        }
    }

    private String bluetoothMacFromContentResolver(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), "bluetooth_address");
        } catch (Exception e) {
            Log.d(TAG, "BT MAC obtaining from content resolver failed: ", e);
            return "";
        }
    }

    private boolean isInvalidSerial(String serialId) {
        return isEmpty(serialId) || Build.UNKNOWN.equalsIgnoreCase(serialId);
    }

    private boolean isInvalidMac(String wifiMac) {
        return isEmpty(wifiMac)
                || "02:00:00:00:00:00".equalsIgnoreCase(wifiMac)
                || "00:00:00:00:00:00".equalsIgnoreCase(wifiMac);
    }
}

