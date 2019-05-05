package protocolsupportresourcesgenerator.generators.mappings.item;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;

import org.bukkit.Material;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.JsonUtils;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;
import protocolsupportresourcesgenerator.utils.minecraft.ResourceUtils;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry.IdRemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;
import protocolsupportresourcesgenerator.version.ProtocolVersion;

public class FlatteningItemMappingsGenerator {

	public static final IdRemappingRegistry<ArrayBasedIdRemappingTable> REGISTRY = new IdRemappingRegistry<ArrayBasedIdRemappingTable>() {
		@Override
		protected ArrayBasedIdRemappingTable createTable() {
			ArrayBasedIdRemappingTable table = new ArrayBasedIdRemappingTable(MinecraftData.ITEM_COUNT);
			for (int i = 0; i < MinecraftData.ITEM_COUNT; i++) {
				table.setRemap(i, -1);
			}
			return table;
		}
	};

	protected static void load(ProtocolVersion version) {
		JsonObject itemsData = ResourceUtils.getAsJson(ResourceUtils.getFlatteningResoucePath(version, "items.json"));
		if (itemsData != null) {
			ArrayBasedIdRemappingTable table = REGISTRY.getTable(version);
			for (Entry<String, JsonElement> entry : itemsData.entrySet()) {
				int legacyId = JsonUtils.getInt(entry.getValue().getAsJsonObject(), "protocol_id");
				table.setRemap(MaterialAPI.getItemNetworkId(Material.matchMaterial(entry.getKey())), legacyId);
			}
		}
	}

	static {
		Arrays.stream(ProtocolVersion.getAllSupported()).forEach(FlatteningItemMappingsGenerator::load);
	}


	public static void writeMappings() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (ProtocolVersion version : ProtocolVersion.getAllSupported()) {
			ArrayBasedIdRemappingTable table = REGISTRY.getTable(version);
			JsonObject versionObject = new JsonObject();
			for (int originalId = 0; originalId < MinecraftData.ITEM_COUNT; originalId++) {
				int remappedId = table.getRemap(originalId);
				if (remappedId != -1) {
					versionObject.addProperty(String.valueOf(originalId), remappedId);
				}
			}
			rootObject.add(version.toString(), versionObject);
		}
		try (FileWriter writer = new FileWriter(new File(MappingsGeneratorConstants.targetFolder, "flatteningitem.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
