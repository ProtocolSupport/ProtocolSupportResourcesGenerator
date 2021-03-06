package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.core.IRegistry;
import net.minecraft.world.entity.EntityTypes;

public class EntityDataGenerator {

	public static void writeData() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (EntityTypes<?> type : IRegistry.Y) {
			rootObject.addProperty(IRegistry.Y.getKey(type).getKey(), IRegistry.Y.getId(type));
		}
		try (FileWriter writer = new FileWriter(new File(DataGeneratorConstants.targetFolder, "entity.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
