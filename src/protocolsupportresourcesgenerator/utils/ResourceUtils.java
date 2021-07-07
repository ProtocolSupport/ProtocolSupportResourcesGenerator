package protocolsupportresourcesgenerator.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import protocolsupportresourcesgenerator.version.ProtocolVersion;

public class ResourceUtils {

	public static InputStream getAsStream(String name) {
		return ResourceUtils.class.getClassLoader().getResourceAsStream(name);
	}

	public static BufferedReader getAsBufferedReader(String name) {
		InputStream resource = getAsStream(name);
		return resource != null ? new BufferedReader(new InputStreamReader(resource, StandardCharsets.UTF_8)) : null;
	}

	public static JsonObject getAsJson(String name) {
		BufferedReader reader = getAsBufferedReader(name);
		return reader != null ? Utils.GSON.fromJson(reader, JsonObject.class) : null;
	}

	public static Iterable<JsonElement> getAsIterableJson(String name) {
		BufferedReader reader = getAsBufferedReader(name);
		return reader != null ? Utils.GSON.fromJson(reader, JsonArray.class) : null;
	}

	public static String getFlatteningResoucePath(ProtocolVersion version, String name) {
		return ResourceUtils.getMappingsPath("flattening/" + version.toString().toLowerCase() + "/" + name);
	}

	public static String getMappingsPath(String name) {
		return "mappings/" + name;
	}

}
