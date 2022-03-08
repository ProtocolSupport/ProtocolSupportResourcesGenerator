package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.core.IRegistry;
import net.minecraft.world.level.block.entity.TileEntityTypes;

public class TileEntityDataGenerator {

	public static void writeData() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (TileEntityTypes<?> type : IRegistry.aa) {
			rootObject.addProperty(IRegistry.aa.b(type).a(), IRegistry.aa.a(type));
		}
		try (FileWriter writer = new FileWriter(new File(DataGeneratorConstants.targetFolder, "tile.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
