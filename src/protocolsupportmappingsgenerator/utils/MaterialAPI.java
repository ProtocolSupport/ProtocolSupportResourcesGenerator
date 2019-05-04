package protocolsupportmappingsgenerator.utils;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_14_R1.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftMagicNumbers;

import net.minecraft.server.v1_14_R1.Block;
import net.minecraft.server.v1_14_R1.IRegistry;

public class MaterialAPI {

	/**
	 * Retunrs all possible block data states for this material
	 * @param material block material
	 * @return all possible block data states
	 */
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

	/**
	 * Returns blockdata network id
	 * @param blockdata blockdata
	 * @return blockdata network id
	 */
	public static int getBlockDataNetworkId(BlockData blockdata) {
		return Block.getCombinedId(((CraftBlockData) blockdata).getState());
	}

	/**
	 * Returns blockdata by network id <br>
	 * Returns null if no blockdata exists for this network id
	 * @param id blockdata network id
	 * @return blockdata
	 */
	public static BlockData getBlockDataByNetworkId(int id) {
		return CraftBlockData.fromData(Block.getByCombinedId(id));
	}

	/**
	 * Returns block material network id
	 * @param material block material
	 * @return network id
	 */
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

	/**
	 * Returns block material by network id <br>
	 * Returns null if no block exists for this network id
	 * @param id block network id
	 * @return block material
	 */
	public static Material getBlockByNetworkId(int id) {
		return CraftMagicNumbers.getMaterial(IRegistry.BLOCK.fromId(id));
	}

}
