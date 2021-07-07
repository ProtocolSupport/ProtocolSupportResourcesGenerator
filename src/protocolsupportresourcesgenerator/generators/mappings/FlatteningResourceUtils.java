package protocolsupportresourcesgenerator.generators.mappings;

import java.util.Map.Entry;
import java.util.function.ToIntBiFunction;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import protocolsupportresourcesgenerator.utils.JsonUtils;
import protocolsupportresourcesgenerator.utils.ResourceUtils;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry.IdRemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;
import protocolsupportresourcesgenerator.version.ProtocolVersion;

public class FlatteningResourceUtils {

	public static void loadMappingToRegistry(String filename, ToIntBiFunction<ProtocolVersion, String> toIntFunc, IdRemappingRegistry<ArrayBasedIdRemappingTable> registry) {
		for (ProtocolVersion version : ProtocolVersion.getAllSupported()) {
			JsonObject rootObject = ResourceUtils.getAsJson(ResourceUtils.getFlatteningResoucePath(version, filename));
			if (rootObject != null) {
				ArrayBasedIdRemappingTable table = registry.getTable(version);
				for (Entry<String, JsonElement> entry : rootObject.entrySet()) {
					table.setRemap(toIntFunc.applyAsInt(version, entry.getKey()), JsonUtils.getInt(entry.getValue().getAsJsonObject(), "protocol_id"));
				}
			}
		}
	}

	public static JsonObject generateJsonMappingsFromRegistry(IdRemappingRegistry<ArrayBasedIdRemappingTable> registry, int tableSize) {
		JsonObject rootObject = new JsonObject();
		for (ProtocolVersion version : ProtocolVersion.getAllSupported()) {
			ArrayBasedIdRemappingTable table = registry.getTable(version);
			JsonObject versionObject = new JsonObject();
			for (int originalId = 0; originalId < tableSize; originalId++) {
				int remappedId = table.getRemap(originalId);
				if (remappedId != -1) {
					versionObject.addProperty(String.valueOf(originalId), remappedId);
				}
			}
			rootObject.add(version.toString(), versionObject);
		}
		return rootObject;
	}

}
