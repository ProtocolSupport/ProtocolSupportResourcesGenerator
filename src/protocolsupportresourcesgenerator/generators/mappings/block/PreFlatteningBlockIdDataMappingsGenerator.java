package protocolsupportresourcesgenerator.generators.mappings.block;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.Rotatable;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import protocolsupportresourcesgenerator.generators.mappings.MappingsGeneratorConstants;
import protocolsupportresourcesgenerator.utils.JsonUtils;
import protocolsupportresourcesgenerator.utils.ResourceUtils;
import protocolsupportresourcesgenerator.utils.minecraft.MaterialAPI;
import protocolsupportresourcesgenerator.utils.minecraft.MinecraftData;

@SuppressWarnings("deprecation")
public class PreFlatteningBlockIdDataMappingsGenerator {

	protected static final int[] toLegacyId = new int[MinecraftData.BLOCKDATA_COUNT];

	protected static void register(String modernBlockState, int legacyId, int legacyData) {
		toLegacyId[MaterialAPI.getBlockDataNetworkId(Bukkit.createBlockData(modernBlockState))] = ((legacyId << 4) | legacyData);
	}

	protected static byte getLegacyRotatableId(BlockFace face) {
		switch (face) {
			case SOUTH: return 0;
			case SOUTH_SOUTH_WEST: return 1;
			case SOUTH_WEST: return 2;
			case WEST_SOUTH_WEST: return 3;
			case WEST: return 4;
			case WEST_NORTH_WEST: return 5;
			case NORTH_WEST: return 6;
			case NORTH_NORTH_WEST: return 7;
			case NORTH: return 8;
			case NORTH_NORTH_EAST: return 9;
			case NORTH_EAST: return 10;
			case EAST_NORTH_EAST: return 11;
			case EAST: return 12;
			case EAST_SOUTH_EAST: return 13;
			case SOUTH_EAST: return 14;
			case SOUTH_SOUTH_EAST: return 15;
			default: return 0;
		}
	}

	protected static byte getLegacyDirectionalId(BlockFace face) {
		switch (face) {
			case NORTH: return 2;
			case SOUTH: return 3;
			case WEST: return 4;
			case EAST: return 5;
			default: return 1;
		}
	}

	protected static byte getBannerLegacyData(BlockData banner) {
		if (banner instanceof Rotatable) {
			return getLegacyRotatableId(((Rotatable) banner).getRotation());
		} else if (banner instanceof Directional) {
			return getLegacyDirectionalId(((Directional) banner).getFacing());
		} else {
			return 1;
		}
	}

	protected static byte getSkullLegacyData(BlockData skull) {
		if (skull instanceof Directional) {
			return getLegacyDirectionalId(((Directional) skull).getFacing());
		} else {
			return 1;
		}
	}

	static {
		Arrays.fill(toLegacyId, -1);
		for (JsonElement element : ResourceUtils.getAsIterableJson(ResourceUtils.getMappingsPath("preflatteningblockiddata.json"))) {
			JsonObject object = element.getAsJsonObject();
			register(JsonUtils.getString(object, "blockdata"), JsonUtils.getInt(object, "legacyid"), JsonUtils.getInt(object, "legacydata"));
		}
		for (Material material : Arrays.asList(
			Material.CREEPER_HEAD, Material.CREEPER_WALL_HEAD,
			Material.ZOMBIE_HEAD, Material.ZOMBIE_WALL_HEAD,
			Material.SKELETON_SKULL, Material.SKELETON_WALL_SKULL,
			Material.WITHER_SKELETON_SKULL, Material.WITHER_SKELETON_WALL_SKULL,
			Material.PLAYER_HEAD, Material.PLAYER_WALL_HEAD,
			Material.DRAGON_HEAD, Material.DRAGON_WALL_HEAD
		)) {
			for (BlockData blockdata : MaterialAPI.getBlockDataList(material)) {
				register(blockdata.getAsString(), Material.LEGACY_SKULL.getId(), getSkullLegacyData(blockdata));
			}
		}
		for (Material material : Arrays.asList(
			Material.BLACK_BANNER, Material.BLUE_BANNER, Material.BROWN_BANNER, Material.CYAN_BANNER,
			Material.GRAY_BANNER, Material.GREEN_BANNER, Material.LIGHT_BLUE_BANNER, Material.LIGHT_GRAY_BANNER,
			Material.LIME_BANNER, Material.MAGENTA_BANNER, Material.ORANGE_BANNER, Material.PINK_BANNER,
			Material.PURPLE_BANNER, Material.RED_BANNER, Material.WHITE_BANNER, Material.YELLOW_BANNER
		)) {
			for (BlockData blockdata : MaterialAPI.getBlockDataList(material)) {
				register(blockdata.getAsString(), Material.LEGACY_STANDING_BANNER.getId(), getBannerLegacyData(blockdata));
			}
		}
		for (Material material : Arrays.asList(
			Material.BLACK_WALL_BANNER, Material.BLUE_WALL_BANNER, Material.BROWN_WALL_BANNER, Material.CYAN_WALL_BANNER,
			Material.GRAY_WALL_BANNER, Material.GREEN_WALL_BANNER, Material.LIGHT_BLUE_WALL_BANNER, Material.LIGHT_GRAY_WALL_BANNER,
			Material.LIME_WALL_BANNER, Material.MAGENTA_WALL_BANNER, Material.ORANGE_WALL_BANNER, Material.PINK_WALL_BANNER,
			Material.PURPLE_WALL_BANNER, Material.RED_WALL_BANNER, Material.WHITE_WALL_BANNER, Material.YELLOW_WALL_BANNER
		)) {
			for (BlockData blockdata : MaterialAPI.getBlockDataList(material)) {
				register(blockdata.getAsString(), Material.LEGACY_WALL_BANNER.getId(), getBannerLegacyData(blockdata));
			}
		}
	}

	public static int getCombinedId(int modernId) {
		return toLegacyId[modernId];
	}

	public static boolean exists(int modernId) {
		return getCombinedId(modernId) != -1;
	}


	public static void writeMappings() throws IOException {
		JsonObject rootObject = new JsonObject();
		for (int originalId = 0; originalId < MinecraftData.BLOCKDATA_COUNT; originalId++) {
			int legacyId = getCombinedId(originalId);
			if (legacyId != -1) {
				rootObject.addProperty(String.valueOf(originalId), legacyId);
			}
		}
		try (FileWriter writer = new FileWriter(new File(MappingsGeneratorConstants.targetFolder, "preflatteningblockdataid.json"))) {
			new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(rootObject, writer);
		}
	}

}
