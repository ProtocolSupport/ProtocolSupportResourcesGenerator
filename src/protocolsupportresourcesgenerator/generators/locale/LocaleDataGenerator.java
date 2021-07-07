package protocolsupportresourcesgenerator.generators.locale;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import protocolsupportresourcesgenerator.utils.JsonUtils;
import protocolsupportresourcesgenerator.utils.ResourceUtils;
import protocolsupportresourcesgenerator.utils.Utils;

public class LocaleDataGenerator {

	public static void writeData() throws JsonSyntaxException, JsonIOException, IOException {
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		JsonObject enusLangJson = ResourceUtils.getAsJson("lang_en_us.json");
		try (Writer writer = LocaleGeneratorConstants.createBufferedWriter("en_us.json")) {
			gson.toJson(enusLangJson, writer);
		}
		for (Map.Entry<String, JsonElement> assetEntry : JsonUtils.getJsonObject(readJsonObject("https://launchermeta.mojang.com/v1/packages/4fefa75bc44abb319a51939cda70094b6c183c5d/1.17.json"), "objects").entrySet()) {
			String name = assetEntry.getKey();
			if (!name.startsWith("minecraft/lang/")) {
				continue;
			}
			String hash = JsonUtils.getString(assetEntry.getValue().getAsJsonObject(), "hash");
			JsonObject langJson = readJsonObject("https://resources.download.minecraft.net/" + hash.substring(0, 2) + "/" + hash);
			langJson.entrySet().removeIf(e -> Objects.equals(e.getValue(), enusLangJson.get(e.getKey())));
			try (Writer writer = LocaleGeneratorConstants.createBufferedWriter(name.split("\\/")[2])) {
				gson.toJson(langJson, writer);
			}
		}
	}

	protected static JsonObject readJsonObject(String url) throws JsonSyntaxException, JsonIOException, IOException {
		return Utils.GSON.fromJson(new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8), JsonObject.class);
	}

}
