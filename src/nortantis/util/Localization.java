package nortantis.util;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Loads translations from a JSON file in assets/internal.
 * Each translation file is named translation_<langCode>.json and contains
 * a simple mapping from keys to translated strings.
 */
public class Localization {
    private static Map<String, String> messages = new HashMap<>();
    private static String currentLanguage = Locale.getDefault().getLanguage();

    static {
        load(currentLanguage);
    }

    public static void load(String langCode) {
        currentLanguage = langCode;
        messages.clear();
        JSONParser parser = new JSONParser();
        Path path = Paths.get(Assets.getAssetsPath(), "internal", "translation_" + langCode + ".json");
        try (FileReader reader = new FileReader(path.toFile())) {
            JSONObject obj = (JSONObject) parser.parse(reader);
            for (Object key : obj.keySet()) {
                messages.put(key.toString(), obj.get(key).toString());
            }
        } catch (Exception e) {
            // If loading fails, leave messages map empty; keys will be returned as-is.
        }
    }

    public static String get(String key) {
        return messages.getOrDefault(key, key);
    }

    public static String getLanguage() {
        return currentLanguage;
    }
}
