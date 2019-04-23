package com.f0x1d.notes.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
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
import com.f0x1d.notes.R;
import com.f0x1d.notes.activity.MainActivity;
import com.f0x1d.notes.utils.theme.ThemesEngine;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.f0x1d.notes.App.getContext;

public class UselessUtils {

    public static SharedPreferences.Editor edit() {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
    }

    public static boolean getBool(String key, boolean defValue) {
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

    public static int getThemeColor(int themeColor){
        if (UselessUtils.ifCustomTheme())
            return themeColor;
        else if (UselessUtils.getBool("night", true))
            return Color.WHITE;
        else
            return Color.BLACK;
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

    public static boolean ifPMSHook() {
        try {
            PackageManager pm = getContext().getPackageManager();
            Field mPmField = pm.getClass().getDeclaredField("mPM");
            mPmField.setAccessible(true);
            Object mPm = mPmField.get(pm);
            return Proxy.isProxyClass(App.getInstance().getClass());
        } catch (Exception e) {
            return true;
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

    public static void setCursorColor(EditText view, @ColorInt int color) {
        try {
            Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
            field.setAccessible(true);
            int drawableResId = field.getInt(view);

            field = TextView.class.getDeclaredField("mEditor");
            field.setAccessible(true);
            Object editor = field.get(view);

            Drawable drawable = ContextCompat.getDrawable(view.getContext(), drawableResId);
            drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            Drawable[] drawables = {drawable, drawable};

            field = editor.getClass().getDeclaredField("mCursorDrawable");
            field.setAccessible(true);
            field.set(editor, drawables);
        } catch (Exception ignored) {
        }
    }

    public static byte[] getSHASignature() {
        try {
            PackageInfo info = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), PackageManager.GET_SIGNATURES);
            if (info.signatures != null && info.signatures.length > 0) {
                Signature signature = info.signatures[0];
                MessageDigest sha;

                try {
                    sha = MessageDigest.getInstance("SHA");
                    sha.update(signature.toByteArray());

                    return sha.digest();

                } catch (NoSuchAlgorithmException e) {
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
        }
        return null;
    }

}
