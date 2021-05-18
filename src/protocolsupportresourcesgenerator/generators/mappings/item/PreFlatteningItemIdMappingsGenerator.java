package protocolsupportresourcesgenerator.generators.mappings.item;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.JsonUtils;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;
import protocolsupportresourcesgenerator.utils.minecraft.ResourceUtils;

public class PreFlatteningItemIdMappingsGenerator {

	protected static final int[] toLegacyId = new int[MinecraftData.ITEM_COUNT];

	protected static void register(String modernKey, int legacyMainId, int legacyData) {
		toLegacyId[MaterialAPI.getItemNetworkId(MaterialAPI.matchMaterial(modernKey))] = ((legacyMainId << 16) | legacyData);
	}

	static {
		Arrays.fill(toLegacyId, -1);
		for (JsonElement element : ResourceUtils.getAsIterableJson(ResourceUtils.getMappingsPath("preflatteningitemiddata.json"))) {
			JsonObject object = element.getAsJsonObject();
			register(JsonUtils.getString(object, "itemkey"), JsonUtils.getInt(object, "legacyid"), JsonUtils.getInt(object, "legacydata"));
		}
	}

	public static int getCombinedId(int modernId) {
		return toLegacyId[modernId];
	}

	public static void writeMappings() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (int originalId = 0; originalId < MinecraftData.ITEM_COUNT; originalId++) {
			int legacyId = getCombinedId(originalId);
			if (legacyId != -1) {
				rootObject.addProperty(String.valueOf(originalId), legacyId);
			}
		}
		try (FileWriter writer = new FileWriter(new File(MappingsGeneratorConstants.targetFolder, "preflatteningitemid.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
