package protocolsupportresourcesgenerator.generators.mappings.block;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.JsonUtils;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;
import protocolsupportresourcesgenerator.utils.minecraft.ResourceUtils;
import protocolsupportresourcesgenerator.utils.registry.RemappingRegistry;
import protocolsupportresourcesgenerator.utils.registry.RemappingTable;
import protocolsupportresourcesgenerator.version.ProtocolVersion;

public class FlatteningBlockDataMappingsGenerator {

	public static final RemappingRegistry<FlatteningBlockDataTable> REGISTRY = new RemappingRegistry<FlatteningBlockDataTable>() {
		@Override
		protected FlatteningBlockDataTable createTable() {
			return new FlatteningBlockDataTable();
		}
	};

	protected static void load(ProtocolVersion version) {
		JsonObject blockdataidJson = ResourceUtils.getAsJson(ResourceUtils.getFlatteningResoucePath(version, "blockdataid.json"));
		if (blockdataidJson != null) {
			JsonObject blockidJson = ResourceUtils.getAsJson(ResourceUtils.getFlatteningResoucePath(version, "blockid.json"));

			FlatteningBlockDataTable table = REGISTRY.getTable(version);
			for (Entry<String, JsonElement> entry : blockdataidJson.entrySet()) {
				String name = entry.getKey();
				JsonElement blockIdObject = blockidJson.get(name);
				if (blockIdObject == null) {
					throw new IllegalStateException(MessageFormat.format("Missing blockdata {0} block id mapping", name));
				}
				int blockId = JsonUtils.getInt(blockIdObject.getAsJsonObject(), "protocol_id");
				for (JsonElement blockdataElement : JsonUtils.getJsonArray(entry.getValue().getAsJsonObject(), "states")) {
					JsonObject blockdataObject = blockdataElement.getAsJsonObject();
					String blockdata = name;
					if (blockdataObject.has("properties")) {
						blockdata +=
							"[" +
							blockdataObject.getAsJsonObject("properties").entrySet().stream()
							.map(bdEntry -> bdEntry.getKey() + "=" + bdEntry.getValue().getAsString())
							.collect(Collectors.joining(",")) +
							"]";
					}
					int blockdataId = JsonUtils.getInt(blockdataObject, "id");
					table.setRemap(MaterialAPI.getBlockDataNetworkId(Bukkit.createBlockData(blockdata)), new FlatteningBlockDataEntry(blockdataId, blockId));
				}
			}
		}
	}

	static {
		Arrays.stream(ProtocolVersion.getAllSupported()).forEach(FlatteningBlockDataMappingsGenerator::load);
	}

	public static class FlatteningBlockDataTable extends RemappingTable {
		protected final FlatteningBlockDataEntry[] table = new FlatteningBlockDataEntry[MinecraftData.BLOCKDATA_COUNT];
		public FlatteningBlockDataEntry getRemap(int blockdataId) {
			return table[blockdataId];
		}
		public void setRemap(int blockdataId, FlatteningBlockDataEntry entry) {
			table[blockdataId] = entry;
		}
	}

	public static final class FlatteningBlockDataEntry {
		protected final int blockdataId;
		protected final int blockId;
		public FlatteningBlockDataEntry(int blockdataId, int blockId) {
			this.blockdataId = blockdataId;
			this.blockId = blockId;
		}
		public int getBlockDataId() {
			return blockdataId;
		}
		public int getBlockId() {
			return blockId;
		}
	}

	public static void writeMappings() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (ProtocolVersion version : ProtocolVersion.getAllSupported()) {
			FlatteningBlockDataTable table = REGISTRY.getTable(version);
			JsonObject versionObject = new JsonObject();
			for (int originalId = 0; originalId < MinecraftData.BLOCKDATA_COUNT; originalId++) {
				FlatteningBlockDataEntry remappedId = table.getRemap(originalId);
				if (remappedId != null) {
					JsonObject entryObject = new JsonObject();
					entryObject.addProperty("bdId", remappedId.getBlockDataId());
					entryObject.addProperty("bId", remappedId.getBlockId());
					versionObject.add(String.valueOf(originalId), entryObject);
				}
			}
			rootObject.add(version.toString(), versionObject);
		}
		try (FileWriter writer = new FileWriter(new File(MappingsGeneratorConstants.targetFolder, "flatteningblockdata.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
