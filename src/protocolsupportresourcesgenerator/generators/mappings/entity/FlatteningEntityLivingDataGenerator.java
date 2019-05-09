package protocolsupportresourcesgenerator.generators.mappings.entity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map.Entry;

import org.bukkit.entity.EntityType;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.JsonUtils;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.ResourceUtils;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry.IdRemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable.ArrayBasedIdRemappingTable;
import protocolsupportresourcesgenerator.version.ProtocolVersion;

public class FlatteningEntityLivingDataGenerator {

	protected static final int table_size = 256;

	public static final IdRemappingRegistry<ArrayBasedIdRemappingTable> REGISTRY = new IdRemappingRegistry<ArrayBasedIdRemappingTable>() {
		@Override
		protected ArrayBasedIdRemappingTable createTable() {
			ArrayBasedIdRemappingTable table = new ArrayBasedIdRemappingTable(table_size);
			for (int i = 0; i < table_size; i++) {
				table.setRemap(i, -1);
			}
			return table;
		}
	};

	protected static void load(ProtocolVersion version) {
		JsonObject rootObject = ResourceUtils.getAsJson(ResourceUtils.getFlatteningResoucePath(version, "entityl.json"));
		if (rootObject != null) {
			ArrayBasedIdRemappingTable table = REGISTRY.getTable(version);
			for (Entry<String, JsonElement> entry : rootObject.entrySet()) {
				@SuppressWarnings("deprecation")
				EntityType type = EntityType.fromName(entry.getKey());
				table.setRemap(MaterialAPI.getEntityTypeNetworkId(type), JsonUtils.getInt(entry.getValue().getAsJsonObject(), "protocol_id"));
			}
		}
	}

	static {
		Arrays.stream(ProtocolVersion.getAllSupported()).forEach(FlatteningEntityLivingDataGenerator::load);
	}


	public static void writeMappings() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (ProtocolVersion version : ProtocolVersion.getAllSupported()) {
			ArrayBasedIdRemappingTable table = REGISTRY.getTable(version);
			JsonObject versionObject = new JsonObject();
			for (int originalId = 0; originalId < table_size; originalId++) {
				int remappedId = table.getRemap(originalId);
				if (remappedId != -1) {
					versionObject.addProperty(String.valueOf(originalId), remappedId);
				}
			}
			rootObject.add(version.toString(), versionObject);
		}
		try (FileWriter writer = new FileWriter(new File(MappingsGeneratorConstants.targetFolder, "flatteningentityl.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
