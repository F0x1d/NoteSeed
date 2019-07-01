package com.f0x1d.notes.utils.translations;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.f0x1d.notes.R;
import com.f0x1d.notes.utils.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;

public class CustomResources extends Resources {

    public HashMap<Integer, String> defaultStrings;

    public CustomResources(AssetManager assets, DisplayMetrics metrics, Configuration config) {
        super(assets, metrics, config);

        defaultStrings = new HashMap<>();
        try {
            Field[] fields = R.string.class.getFields();
            for (Field field : fields) {
                defaultStrings.put((Integer) field.get(null), field.getName());
            }
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        return Translations.getString(defaultStrings.get(id), super.getString(id));
    }

    @NonNull
    @Override
    public String getString(int id, Object... formatArgs) throws NotFoundException {
        return Translations.getString(defaultStrings.get(id), super.getString(id, formatArgs));
    }

    @NonNull
    @Override
    public CharSequence getText(int id) throws NotFoundException {
        return Translations.getString(defaultStrings.get(id), super.getText(id).toString());
    }

    @Override
    public CharSequence getText(int id, CharSequence def) {
        return Translations.getString(defaultStrings.get(id), super.getText(id, def).toString());
    }
}
