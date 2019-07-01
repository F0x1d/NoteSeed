package com.f0x1d.notes.utils.translations;

import android.content.Context;
import android.content.SharedPreferences;

import com.f0x1d.notes.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Translations {

    public static HashMap<String, Integer> defaultStrings = new HashMap<>();
    protected static Context context;
    private static File translationDir;
    private static SharedPreferences preferences;
    private static String currentTranslationName;
    private static HashMap<String, String> currentTranslation;

    public static void init(Context appContext) throws IncorrectTranslationError {
        currentTranslation = new HashMap<>();
        context = appContext;
        preferences = context.getSharedPreferences("customTranslations", Context.MODE_PRIVATE);
        translationDir = new File("data/data/" + context.getPackageName() + "/files/translations");

        if (preferences.getString("path", null) != null)
            setCurrentTranslation(new File(preferences.getString("path", null)));

        try {
            Field[] fields = R.string.class.getFields();
            for (Field field : fields) {
                defaultStrings.put(field.getName(), (Integer) field.get(null));
            }
        } catch (Exception e) {
            throw new IncorrectTranslationError(e);
        }
    }

    public static boolean translationIsOk() {
        return !currentTranslation.isEmpty();
    }

    public static File getCurrentTranslation() {
        if (currentTranslation.isEmpty())
            throw new RuntimeException("now there is no active translation!");

        return new File(preferences.getString("path", null));
    }

    public static void setCurrentTranslation(File translation) throws IncorrectTranslationError {
        if (isImported(translation)) {
            currentTranslation.clear();
            currentTranslationName = translation.getName();
            parseTranslation(translation);
            preferences.edit().putString("path", translation.getAbsolutePath()).apply();
        } else
            throw new RuntimeException("it is not imported!");
    }

    public static List<File> getAvailableTranslations() {
        if (translationDir == null) {
            throw new RuntimeException("init() pls");
        }

        List<File> translations = new ArrayList<>();
        translations.addAll(Arrays.asList(translationDir.listFiles()));
        return translations;
    }

    private static boolean isImported(File translation) {
        for (File importedFile : getAvailableTranslations()) {
            if (importedFile.getName().equals(translation.getName())) {
                return true;
            }
        }

        return false;
    }

    public static boolean deleteTranslation(File translation) {
        if (isImported(translation)) {
            boolean it = currentTranslationName.equals(translation.getName());
            if (it) {
                currentTranslationName = "";
                currentTranslation.clear();
                preferences.edit().putString("path", null).apply();
            }
            translation.delete();

            return it;
        } else
            throw new RuntimeException("it is not imported!");
    }

    public static void setDefaultTranslation() {
        currentTranslationName = "";
        currentTranslation.clear();
        preferences.edit().putString("path", null).apply();
    }

    public static String getString(String key) {
        if (!hasString(key)) {
            try {
                if (defaultStrings.get(key) != null)
                    return context.getResources().getString(defaultStrings.get(key));
                else
                    throw new RuntimeException("no such string");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else
            return currentTranslation.get(key);
    }

    public static String getString(String key, String defaultValue) {
        if (hasString(key))
            return currentTranslation.get(key);
        else
            return defaultValue;
    }

    public static boolean hasString(String key) {
        if (currentTranslation.isEmpty())
            return false;
        return currentTranslation.get(key) != null;
    }

    public static void addTranslation(File translationFile) throws IncorrectTranslationError {
        if (translationDir == null) {
            throw new RuntimeException("init() pls");
        }

        if (!translationDir.exists())
            translationDir.mkdirs();

        try {
            File translationFileNew = new File(translationDir, translationFile.getName());
            copyFileUsingStream(new FileInputStream(translationFile), translationFileNew);
            setCurrentTranslation(translationFileNew);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addTranslation(FileInputStream translationFileIs, String translationName) throws IncorrectTranslationError {
        if (translationDir == null) {
            throw new RuntimeException("init() pls");
        }

        if (!translationDir.exists())
            translationDir.mkdirs();

        try {
            File translationFile = new File(translationDir, translationName);
            copyFileUsingStream(translationFileIs, translationFile);
            setCurrentTranslation(translationFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void parseTranslation(File translationFile) throws IncorrectTranslationError {
        try {
            StringBuilder result = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(translationFile));
            String line;
            while ((line = reader.readLine()) != null) {
                if (result.toString().isEmpty())
                    result.append(line);
                else {
                    result.append("\n");
                    result.append(line);
                }
            }
            reader.close();

            JSONArray array = new JSONArray(result.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject translation = array.getJSONObject(i);
                try {
                    translation.getString("key");
                    translation.getString("value");
                } catch (Exception e) {
                    continue;
                }
                currentTranslation.put(translation.getString("key"), translation.getString("value"));
            }
        } catch (Exception e) {
            preferences.edit().putString("path", null).apply();
            currentTranslation.clear();
            currentTranslationName = "";
            throw new IncorrectTranslationError(e);
        }
    }

    public static HashMap<String, String> parseFileWithTranslation(File file) throws IncorrectTranslationError {
        try {
            StringBuilder result = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (result.toString().isEmpty())
                    result.append(line);
                else {
                    result.append("\n");
                    result.append(line);
                }
            }
            reader.close();

            HashMap<String, String> map = new HashMap<>();

            JSONArray array = new JSONArray(result.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject translation = array.getJSONObject(i);
                try {
                    translation.getString("key");
                    translation.getString("value");
                } catch (Exception e) {
                    continue;
                }
                map.put(translation.getString("key"), translation.getString("value"));
            }

            return map;
        } catch (Exception e) {
            throw new IncorrectTranslationError(e);
        }
    }

    public static String createTranslation(HashMap<String, String> keysAndValues) {
        if (keysAndValues.isEmpty())
            throw new RuntimeException("your translation is empty!");

        JSONArray array = new JSONArray();
        for (Map.Entry<String, String> entry : keysAndValues.entrySet()) {
            JSONObject translation = new JSONObject();
            try {
                translation.put("key", entry.getKey());
                translation.put("value", entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(translation);
        }

        return array.toString();
    }

    private static void copyFileUsingStream(FileInputStream is, File dest) throws IOException {
        OutputStream os = null;
        try {
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }
}
