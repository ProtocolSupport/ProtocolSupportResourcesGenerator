package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.block.data.BlockData;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.server.v1_14_R1.Block;
import net.minecraft.server.v1_14_R1.IBlockData;
import net.minecraft.server.v1_14_R1.IRegistry;
import net.minecraft.server.v1_14_R1.SoundEffectType;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;

public class BlockDataGenerator {

	public static void writeData() throws IOException {
		JsonObject rootObject = new JsonObject();
		MinecraftData.getBlocks().forEach(material -> {
			for (BlockData blockdata : MaterialAPI.getBlockDataList(material)) {
				JsonObject blockdataObject = new JsonObject();

				int id = MaterialAPI.getBlockDataNetworkId(blockdata);
				IBlockData nmsBlockData = Block.getByCombinedId(id);

				JsonObject soundsObject = new JsonObject();
				SoundEffectType sound = nmsBlockData.getBlock().getStepSound(nmsBlockData);
				soundsObject.addProperty("break", IRegistry.SOUND_EVENT.a(sound.e()));
				soundsObject.addProperty("volume", sound.a());
				soundsObject.addProperty("pitch", sound.b());

				blockdataObject.add("sounds", soundsObject);

				rootObject.add(String.valueOf(id), blockdataObject);
			}
		});
		try (FileWriter writer = new FileWriter(new File(DataGeneratorConstants.targetFolder, "blocks.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
