package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.core.IRegistry;
import net.minecraft.world.effect.MobEffectList;

public class PotionDataGenerator {

	public static void writeData() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (MobEffectList effect : IRegistry.T) {
			rootObject.addProperty(String.valueOf(IRegistry.T.a(effect)), IRegistry.T.b(effect).a());
		}
		try (FileWriter writer = new FileWriter(new File(DataGeneratorConstants.targetFolder, "potions.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
