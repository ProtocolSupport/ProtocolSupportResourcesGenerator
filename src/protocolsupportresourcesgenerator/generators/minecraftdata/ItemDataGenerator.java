package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.core.IRegistry;
import net.minecraft.world.item.Item;

public class ItemDataGenerator {

	public static void writeData() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (Item type : IRegistry.X) {
			rootObject.addProperty(IRegistry.X.b(type).a(), IRegistry.X.a(type));
		}
		try (FileWriter writer = new FileWriter(new File(DataGeneratorConstants.targetFolder, "item.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
