package nortantis.util;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import nortantis.util.Logger;
import org.json.simple.parser.ParseException;

/**
 * Loads translations from a JSON file in assets/internal. Each translation file is named translation_<langCode>.json and contains a simple
 * mapping from keys to translated strings.
 */
public class Localization
{
	private static final Map<String, String> messages = new HashMap<>();
	private static String currentLanguage = Locale.getDefault().getLanguage();

	static
	{
		load(currentLanguage);
	}

	public static void load(String langCode) {
		currentLanguage = langCode;
		messages.clear();
		JSONParser parser = new JSONParser();

		String jsonData = Assets.readFileAsString(Paths.get(Assets.getAssetsPath(), "internal", "translation_" + langCode + ".json").toString());

		try {
			JSONObject obj = (JSONObject) parser.parse(jsonData);
			for (Object key : obj.keySet())
			{
				messages.put(key.toString(), obj.get(key).toString());
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	public static String get(String key, Object... formatArgs)
	{
		if (!messages.containsKey(key))
			System.out.println("\"" + key + "\": ,");
		return MessageFormat.format(messages.getOrDefault(key, key), formatArgs);
	}

	public static String getLanguage()
	{
		return currentLanguage;
	}
}
