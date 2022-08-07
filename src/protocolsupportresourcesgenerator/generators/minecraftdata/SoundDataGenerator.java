package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.core.IRegistry;
import net.minecraft.sounds.SoundEffect;

public class SoundDataGenerator {

	public static void writeData() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (SoundEffect soundeffect : IRegistry.S) {
			rootObject.addProperty(String.valueOf(IRegistry.S.a(soundeffect)), IRegistry.S.b(soundeffect).a());
		}
		try (FileWriter writer = new FileWriter(new File(DataGeneratorConstants.targetFolder, "sounds.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
