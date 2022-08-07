package protocolsupportresourcesgenerator.utils;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CollectionsUtils {

	private CollectionsUtils() {
	}

	public static @Nullable <T> T getFromArrayOrNull(@Nonnull T[] array, @Nonnegative int index) {
		if ((index >= 0) && (index < array.length)) {
			return array[index];
		} else {
			return null;
		}
	}

}
