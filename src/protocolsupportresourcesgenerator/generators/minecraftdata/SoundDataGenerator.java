package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.server.v1_16_R3.IRegistry;
import net.minecraft.server.v1_16_R3.SoundEffect;

public class SoundDataGenerator {

	public static void writeData() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (SoundEffect soundeffect : IRegistry.SOUND_EVENT) {
			rootObject.addProperty(String.valueOf(IRegistry.SOUND_EVENT.a(soundeffect)), IRegistry.SOUND_EVENT.getKey(soundeffect).getKey());
		}
		try (FileWriter writer = new FileWriter(new File(DataGeneratorConstants.targetFolder, "sounds.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
