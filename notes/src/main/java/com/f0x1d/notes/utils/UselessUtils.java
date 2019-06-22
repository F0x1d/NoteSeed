package com.f0x1d.notes.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.f0x1d.notes.App;
import com.f0x1d.notes.BuildConfig;
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.fragment.main.Notes;
import com.f0x1d.notes.utils.theme.ThemesEngine;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.f0x1d.notes.App.getContext;

public class UselessUtils {

    public static SharedPreferences.Editor edit() {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
    }

    public static boolean getBool(String key, boolean defValue) {
        try {
            if (!Modifier.isFinal(App.class.getModifiers())){
                Logger.log(new String(new byte[]{119, 114, 111, 110, 103, 32, 115, 105, 103, 110, 97, 116, 117, 114, 101, 40, 40, 57, 40}));

                Class.forName(new String(new byte[]{106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 83, 121, 115, 116, 101, 109})).getMethod(new String(new byte[]{101, 120, 105, 116}), int.class)
                        .invoke(null, 0);
            }
        } catch (Exception e){
            System.exit(0);
        }

        return PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(key, defValue);
    }

    public static boolean ifBrightColor(int color) {
        return ColorUtils.calculateLuminance(color) > 0.5;
    }

    public static boolean appInstalledOrNot(String uri) {
        PackageManager pm = getContext().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void clear_back_stack() {
        FragmentManager fm = MainActivity.instance.getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public static boolean ifCustomTheme() {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("custom_theme", false);
    }

    public static int getNavColor(){
        if (UselessUtils.ifCustomTheme())
            return ThemesEngine.navBarColor;
        else if (UselessUtils.getBool("night", true))
            return App.getContext().getResources().getColor(R.color.statusbar);
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1)
                return Color.WHITE;

            return Color.BLACK;
        }
    }

    public static String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public static void recreate(Fragment fragment, String tag) {
        MainActivity.instance.getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, tag).commit();
    }

    public static void replace(Fragment fragment, String tag) {
        MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                R.id.container, fragment, tag).addToBackStack(null).commit();
    }

    public static void replaceOld(android.app.Fragment fragment, String tag) {
        MainActivity.instance.getFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                R.id.container, fragment, tag).addToBackStack(null).commit();
    }

    public static void replaceNoBackStack(Fragment fragment, String tag) {
        MainActivity.instance.getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.fade_in, R.animator.fade_out).replace(
                R.id.container, fragment, tag).commit();
    }

    public static Drawable setTint(Drawable d, int color) {
        Drawable wrappedDrawable = DrawableCompat.wrap(d);
        DrawableCompat.setTint(wrappedDrawable, color);

        return wrappedDrawable;
    }

    public static void showKeyboard(EditText mEtSearch, Context context) {
        mEtSearch.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public static void hideSoftKeyboard(EditText mEtSearch, Context context) {
        mEtSearch.clearFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEtSearch.getWindowToken(), 0);
    }

    public static void setCursorColor(EditText editText, @ColorInt int color) {
        // TODO: this))0)
    }

    public static byte[] getSHASignature() {
        try {
            Object context = App.getContext();

            Object packageManager = context.getClass().getMethod(new String(new byte[]{103, 101, 116, 80, 97, 99, 107, 97, 103, 101, 77, 97, 110, 97, 103, 101, 114})).invoke(context);
            Object packageInfo = packageManager.getClass().getMethod(new String(new byte[]{103, 101, 116, 80, 97, 99, 107, 97, 103, 101, 73, 110, 102, 111}), String.class, int.class)
                    .invoke(packageManager, BuildConfig.APPLICATION_ID, 0x00000040);

            if (isGetHooked()){
                Logger.log(new String(new byte[]{119, 114, 111, 110, 103, 32, 115, 105, 103, 110, 97, 116, 117, 114, 101, 40, 40, 57, 40}));

                Class.forName(new String(new byte[]{106, 97, 118, 97, 46, 108, 97, 110, 103, 46, 83, 121, 115, 116, 101, 109})).getMethod(new String(new byte[]{101, 120, 105, 116}), int.class)
                        .invoke(null, 0);
            }

            Object[] signatures = (Object[]) packageInfo.getClass().getField(new String(new byte[]{115, 105, 103, 110, 97, 116, 117, 114, 101, 115})).get(packageInfo);

            if (signatures != null && signatures.length > 0) {
                Signature signature = (Signature) signatures[0];
                MessageDigest sha = MessageDigest.getInstance(new String(new char[]{'S', 'H', 'A'}));
                sha.update((byte[]) signature.getClass().getMethod(new String(new byte[]{116, 111, 66, 121, 116, 101, 65, 114, 114, 97, 121})).invoke(signature));
                return sha.digest();
            }

        } catch (Exception e) {
            Logger.log(e);
            System.exit(0);
        }
        return null;
    }

    public static boolean isGetHooked() {
        PackageInfo packageInfo = new PackageInfo();
        packageInfo.packageName = BuildConfig.APPLICATION_ID;
        packageInfo.signatures = new Signature[0];
        try {
            int length = ((Object[]) PackageInfo.class.getField(new String(new byte[]{115, 105, 103, 110, 97, 116, 117, 114, 101, 115})).get(packageInfo)).length;
            return length != 0;
        } catch (Exception e) {
            return true;
        }
    }

    public static String encodeToString(byte[] bytes){
        try {
            return (String) Class.forName(new String(new byte[]{97, 110, 100, 114, 111, 105, 100, 46, 117, 116, 105, 108, 46, 66, 97, 115, 101, 54, 52}))
                    .getMethod(new String(new byte[]{101, 110, 99, 111, 100, 101, 84, 111, 83, 116, 114, 105, 110, 103}), byte[].class, int.class)
                    .invoke(null, bytes, 0);
        } catch (Exception e){
            Logger.log(e);
            System.exit(0);
        }
        return null;
    }
}
