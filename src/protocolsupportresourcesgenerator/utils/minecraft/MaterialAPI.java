package protocolsupportresourcesgenerator.utils.minecraft;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_15_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_15_R1.util.CraftMagicNumbers;

import net.minecraft.server.v1_15_R1.Block;
import net.minecraft.server.v1_15_R1.EntityTypes;
import net.minecraft.server.v1_15_R1.IRegistry;

public class MaterialAPI {

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static <T extends BlockData> List<T> getBlockDataList(Material material) {
		if (material.isLegacy()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is legacy", material));
		}
		if (!material.isBlock()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is not a block", material));
		}
		return
			(List<T>) CraftMagicNumbers.getBlock(material).getStates().a().stream()
			.map(CraftBlockData::fromData)
			.collect(Collectors.toList());
	}

	public static int getBlockDataNetworkId(BlockData blockdata) {
		return Block.getCombinedId(((CraftBlockData) blockdata).getState());
	}

	public static BlockData getBlockDataByNetworkId(int id) {
		return CraftBlockData.fromData(Block.getByCombinedId(id));
	}

	@SuppressWarnings("deprecation")
	public static int getBlockNetworkId(Material material) {
		if (material.isLegacy()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is legacy", material));
		}
		if (!material.isBlock()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is not a block", material));
		}
		return IRegistry.BLOCK.a(CraftMagicNumbers.getBlock(material));
	}

	public static Material getBlockByNetworkId(int id) {
		return CraftMagicNumbers.getMaterial(IRegistry.BLOCK.fromId(id));
	}

	@SuppressWarnings("deprecation")
	public static int getItemNetworkId(Material material) {
		if (material.isLegacy()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is legacy", material));
		}
		if (!material.isItem()) {
			throw new IllegalArgumentException(MessageFormat.format("Material {0} is not an item", material));
		}
		return IRegistry.ITEM.a(CraftMagicNumbers.getItem(material));
	}

	public static Material getItemByNetworkId(int id) {
		return CraftMagicNumbers.getMaterial(IRegistry.ITEM.fromId(id));
	}

	public static int getEntityTypeNetworkId(String name) {
		return IRegistry.ENTITY_TYPE.a(EntityTypes.a(name).get());
	}

}
