package protocolsupportmappingsgenerator.utils;

import com.google.gson.Gson;

public class Utils {

	public static final Gson GSON = new Gson();

	public static <T> T getFromArrayOrNull(T[] array, int index) {
		if ((index >= 0) && (index < array.length)) {
			return array[index];
		} else {
			return null;
		}
	}

}
