package protocolsupportresourcesgenerator.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {

	public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

	public static <T> T getFromArrayOrNull(T[] array, int index) {
		if ((index >= 0) && (index < array.length)) {
			return array[index];
		} else {
			return null;
		}
	}

}
