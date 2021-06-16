package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.block.data.BlockData;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;

public class BlockDataGenerator {

	public static void writeData() throws IOException {
		JsonArray rootArray = new JsonArray();
		MinecraftData.getBlocks().forEach(material -> {
			JsonObject blockObject = new JsonObject();
			blockObject.addProperty("name", material.getKey().toString());
			blockObject.addProperty("network_id", MaterialAPI.getBlockNetworkId(material));
			JsonArray dataArray = new JsonArray();
			for (BlockData blockdata : MaterialAPI.getBlockDataList(material)) {
				JsonObject blockdataObject = new JsonObject();
				blockdataObject.addProperty("name", blockdata.getAsString());
				blockdataObject.addProperty("network_id", MaterialAPI.getBlockDataNetworkId(blockdata));
				dataArray.add(blockdataObject);
			}
			blockObject.add("data", dataArray);
			rootArray.add(blockObject);
		});
		try (FileWriter writer = new FileWriter(new File(DataGeneratorConstants.targetFolder, "block.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootArray, writer);
		}
	}

}
