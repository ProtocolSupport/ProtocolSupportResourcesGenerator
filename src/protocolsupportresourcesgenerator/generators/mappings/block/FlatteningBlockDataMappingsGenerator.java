package protocolsupportresourcesgenerator.generators.mappings.block;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.JsonUtils;
import protocolsupportresourcesgenerator.utils.ResourceUtils;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;
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

	protected static Map.Entry<String, Map<String, String>> upgradeBlockData(ProtocolVersion version, String name, Map<String, String> properties) {
		if (version.isBeforeOrEq(ProtocolVersion.MINECRAFT_1_16_4)) {
			switch (name) {
				case "minecraft:cauldron": {
					if (Integer.parseInt(properties.get("level")) >= 1) {
						return new AbstractMap.SimpleEntry<>("minecraft:water_cauldron", properties);
					} else {
						return new AbstractMap.SimpleEntry<>("minecraft:cauldron", Collections.emptyMap());
					}
				}
				case "minecraft:grass_path": {
					return new AbstractMap.SimpleEntry<>("minecraft:dirt_path", properties);
				}
			}
		}
		if (version.isBeforeOrEq(ProtocolVersion.MINECRAFT_1_15_2)) {
			switch (name) {
				case "minecraft:cobblestone_wall":
				case "minecraft:mossy_cobblestone_wall":
				case "minecraft:brick_wall":
				case "minecraft:prismarine_wall":
				case "minecraft:red_sandstone_wall":
				case "minecraft:mossy_stone_brick_wall":
				case "minecraft:granite_wall":
				case "minecraft:stone_brick_wall":
				case "minecraft:nether_brick_wall":
				case "minecraft:andesite_wall":
				case "minecraft:red_nether_brick_wall":
				case "minecraft:sandstone_wall":
				case "minecraft:end_stone_brick_wall":
				case "minecraft:diorite_wall": {
					Map<String, String> newProperties = new HashMap<>();
					for (Map.Entry<String, String> propertyEntry : properties.entrySet()) {
						String propertyName = propertyEntry.getKey();
						String propertyValue = propertyEntry.getValue();
						switch (propertyName) {
							case "east":
							case "west":
							case "north":
							case "south": {
								newProperties.put(propertyName, propertyValue.equals("false") ? "none" : "low");
								break;
							}
							default: {
								newProperties.put(propertyName, propertyValue);
								break;
							}
						}
					}
					return new AbstractMap.SimpleEntry<>(name, newProperties);
				}
				case "minecraft:jigsaw": {
					Map<String, String> newProperties = new HashMap<>();
					for (Map.Entry<String, String> propertyEntry : properties.entrySet()) {
						String propertyName = propertyEntry.getKey();
						String propertyValue = propertyEntry.getValue();
						if (propertyName.equals("facing")) {
							switch (propertyValue) {
								case "north":
								case "south":
								case "east":
								case "west": {
									newProperties.put("orientation", propertyValue + "_up");
									break;
								}
								case "up":
								case "down": {
									newProperties.put("orientation", propertyValue + "_north");
									break;
								}
							}
						} else {
							newProperties.put(propertyName, propertyValue);
						}
					}
					return new AbstractMap.SimpleEntry<>(name, newProperties);
				}
			}
		}
		return new AbstractMap.SimpleEntry<>(name, properties);
	}

	protected static void load(ProtocolVersion version) {
		JsonObject blockdataidJson = ResourceUtils.getAsJson(ResourceUtils.getFlatteningResoucePath(version, "blockdataid.json"));
		if (blockdataidJson != null) {
			JsonObject blockidJson = ResourceUtils.getAsJson(ResourceUtils.getFlatteningResoucePath(version, "blockid.json"));

			FlatteningBlockDataTable table = REGISTRY.getTable(version);
			for (Entry<String, JsonElement> blockDefinitionEntry : blockdataidJson.entrySet()) {
				String blockName = blockDefinitionEntry.getKey();
				int blockId = JsonUtils.getInt(JsonUtils.getJsonObject(blockidJson, blockName), "protocol_id");
				for (JsonElement blockdataElement : JsonUtils.getJsonArray(blockDefinitionEntry.getValue().getAsJsonObject(), "states")) {
					JsonObject blockdataObject = blockdataElement.getAsJsonObject();
					Entry<String, Map<String, String>> blockdata = upgradeBlockData(
						version,
						blockName,
						blockdataObject.has("properties") ?
						blockdataObject.getAsJsonObject("properties").entrySet().stream()
						.collect(Collectors.toMap(Map.Entry::getKey, pEntry -> JsonUtils.getAsString(pEntry.getValue(), "block property value"))) :
						Collections.emptyMap()
					);
					String blockdataString =
						blockdata.getKey() +
						"[" +
						blockdata.getValue().entrySet().stream()
						.map(pEntry -> pEntry.getKey() + "=" + pEntry.getValue())
						.collect(Collectors.joining(",")) +
						"]";
					int blockdataId = JsonUtils.getInt(blockdataObject, "id");
					table.setRemap(MaterialAPI.getBlockDataNetworkId(Bukkit.createBlockData(blockdataString)), new FlatteningBlockDataEntry(blockdataId, blockId));
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
