package erebus.block.glowshroom;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import erebus.ModBlocks;

public class BlockGlowshroomStalkS1 extends Block {

	public BlockGlowshroomStalkS1() {
		super(Material.wood);
		setTickRandomly(true);
		setBlockBounds(0.3125F, 0.3125F, 0F, 0.6875F, 0.6875F, 0.6875F);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if (world.isRemote)
			return;
		if (world.getBlock(x, y, z - 1) == ModBlocks.glowshroomStalkMain)
			world.setBlock(x, y, z, ModBlocks.glowshroomStalkNS2);
		if (rand.nextInt(2) == 0)
			if (world.getBlock(x, y, z - 1) == ModBlocks.glowshroomStalkNS2 && world.isAirBlock(x, y + 1, z)) {
				world.setBlock(x, y, z, ModBlocks.glowshroomStalkS3);
				world.setBlock(x, y + 1, z, ModBlocks.glowshroom);
			} else {
				world.setBlock(x, y, z, ModBlocks.glowshroomStalkNS2);
				world.setBlock(x, y, z - 1, ModBlocks.glowshroomStalkMain);
			}
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 0;
	}

	@Override
	public Item getItemDropped(int id, Random random, int fortune) {
		return null;
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return isValidBlock(world.getBlock(x, y, z - 1));
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return isValidBlock(world.getBlock(x, y, z - 1));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		if (world.isRemote)
			return;

		if (!isValidBlock(world.getBlock(x, y, z - 1)))
			world.setBlockToAir(x, y, z);
	}

	private boolean isValidBlock(Block block) {
		return block == ModBlocks.glowshroomStalkMain || block == ModBlocks.glowshroomStalkNS2;
	}
}