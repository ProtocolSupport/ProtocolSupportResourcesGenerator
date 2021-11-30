package protocolsupportresourcesgenerator.utils.minecraft;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_18_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_18_R1.util.CraftMagicNumbers;

import net.minecraft.core.IRegistry;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.block.Block;

public class MaterialAPI {

	public static Material matchMaterial(String name) {
		Material material = Material.matchMaterial(name);
		if (material == null) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} doesn''t exist", name));
		}
		return material;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static <T extends BlockData> List<T> getBlockDataList(Material material) {
		if (material.isLegacy()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is legacy", material));
		}
		if (!material.isBlock()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is not a block", material));
		}
		return
			(List<T>) CraftMagicNumbers.getBlock(material).m().a().stream()
			.map(CraftBlockData::fromData)
			.collect(Collectors.toList());
	}

	public static int getBlockDataNetworkId(BlockData blockdata) {
		return Block.i(((CraftBlockData) blockdata).getState());
	}

	public static BlockData getBlockDataByNetworkId(int id) {
		return CraftBlockData.fromData(Block.a(id));
	}

	@SuppressWarnings("deprecation")
	public static int getBlockNetworkId(Material material) {
		if (material.isLegacy()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is legacy", material));
		}
		if (!material.isBlock()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is not a block", material));
		}
		return IRegistry.X.a(CraftMagicNumbers.getBlock(material));
	}

	public static Material getBlockByNetworkId(int id) {
		return CraftMagicNumbers.getMaterial(IRegistry.X.a(id));
	}

	@SuppressWarnings("deprecation")
	public static int getItemNetworkId(Material material) {
		if (material.isLegacy()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is legacy", material));
		}
		if (!material.isItem()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is not an item", material));
		}
		return IRegistry.aa.a(CraftMagicNumbers.getItem(material));
	}

	public static Material getItemByNetworkId(int id) {
		return CraftMagicNumbers.getMaterial(IRegistry.aa.a(id));
	}

	public static int getEntityTypeNetworkId(String name) {
		Optional<EntityTypes<?>> type = EntityTypes.a(name);
		if (!type.isPresent()) {
			throw new IllegalArgumentException(MessageFormat.format("Entity type {0} doesn''t exist", name));
		}
		return IRegistry.Z.a(type.get());
	}

}
