package protocolsupportresourcesgenerator.generators.minecraftdata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import net.minecraft.core.IRegistry;
import net.minecraft.core.particles.Particle;

public class ParticleDataGenerator {

	public static void writeData() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (Particle<?> type : IRegistry.Z) {
			rootObject.addProperty(IRegistry.Z.b(type).a(), IRegistry.Z.a(type));
		}
		try (FileWriter writer = new FileWriter(new File(DataGeneratorConstants.targetFolder, "particle.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
