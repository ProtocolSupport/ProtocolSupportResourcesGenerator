package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.server.v1_15_R1.IRegistry;
import net.minecraft.server.v1_15_R1.MobEffectList;

public class PotionDataGenerator {

	public static void writeData() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (MobEffectList effect : IRegistry.MOB_EFFECT) {
			rootObject.addProperty(String.valueOf(IRegistry.MOB_EFFECT.a(effect)), IRegistry.MOB_EFFECT.getKey(effect).getKey());
		}
		try (FileWriter writer = new FileWriter(new File(DataGeneratorConstants.targetFolder, "potions.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
