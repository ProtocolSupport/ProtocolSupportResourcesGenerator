package protocolsupportresourcesgenerator.utils.minecraft;

import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;

public class MinecraftRegistryUtils {

	public static <T> int getIdByKey(IRegistry<T> registry, String key) {
		T t = registry.a(MinecraftKey.a(key));
		if (t == null) {
			throw new IllegalArgumentException("Key " + key + " missing");
		}
		return registry.a(t);
	}

}
