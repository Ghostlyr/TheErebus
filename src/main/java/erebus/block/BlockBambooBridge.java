package erebus.block;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import erebus.core.helper.Utils;
import erebus.tileentity.TileEntityBambooBridge;

public class BlockBambooBridge extends BlockContainer {

	public boolean front;
	public boolean back;
	public boolean left;
	public boolean right;

	public BlockBambooBridge() {
		super(Material.wood);
		setBlockTextureName("erebus:bambooBridge");
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityBambooBridge();
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entityLivingBase, ItemStack is) {
		byte meta = 0;
		int rotation = MathHelper.floor_double(entityLivingBase.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
		if (rotation == 0)
			meta = 2;
		if (rotation == 1)
			meta = 5;
		if (rotation == 2)
			meta = 3;
		if (rotation == 3)
			meta = 4;
		world.setBlockMetadataWithNotify(x, y, z, meta, 3);
		onBlockAdded(world, x, y, z);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityBambooBridge te = Utils.getTileEntity(world, x, y, z, TileEntityBambooBridge.class);
		int meta = world.getBlockMetadata(x, y, z);
		front = canConnectBridgeTo(world, x, y, z - 1);
		back = canConnectBridgeTo(world, x, y, z + 1);
		left = canConnectBridgeTo(world, x - 1, y, z);
		right = canConnectBridgeTo(world, x + 1, y, z);
		if (meta == 2) {
			if (!right)
				te.setRenderSide1((byte) 1);
			if (!left)
				te.setRenderSide2((byte) 1);
			if (right)
				te.setRenderSide1((byte) 0);
			if (left)
				te.setRenderSide2((byte) 0);
		}

		if (meta == 3) {
			if (!right)
				te.setRenderSide2((byte) 1);
			if (!left)
				te.setRenderSide1((byte) 1);
			if (right)
				te.setRenderSide2((byte) 0);
			if (left)
				te.setRenderSide1((byte) 0);
		}

		if (meta == 4) {
			if (!back)
				te.setRenderSide1((byte) 1);
			if (!front)
				te.setRenderSide2((byte) 1);
			if (back)
				te.setRenderSide1((byte) 0);
			if (front)
				te.setRenderSide2((byte) 0);
		}

		if (meta == 5) {
			if (!back)
				te.setRenderSide2((byte) 1);
			if (!front)
				te.setRenderSide1((byte) 1);
			if (back)
				te.setRenderSide2((byte) 0);
			if (front)
				te.setRenderSide1((byte) 0);
		}
		world.setBlockMetadataWithNotify(x, y, z, meta, 3);
		world.func_147479_m(x, y, z);
		world.markBlockForUpdate(x, y, z);
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) {
		onBlockAdded(world, x, y, z);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB box, List list, Entity entity) {
		float pixel = 0.0625F; // 1 pixel
		int meta = world.getBlockMetadata(x, y, z);
		front = canConnectBridgeTo(world, x, y, z - 1);
		back = canConnectBridgeTo(world, x, y, z + 1);
		left = canConnectBridgeTo(world, x - 1, y, z);
		right = canConnectBridgeTo(world, x + 1, y, z);

		if (meta == 2 || meta == 3) {
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, pixel * 2, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, box, list, entity);

			if (!right) {
				setBlockBounds(1.0F - pixel * 2, 0.0F, 0.0F, 1.0F, 0.875F, 1.0F);
				super.addCollisionBoxesToList(world, x, y, z, box, list, entity);
			}
			if (!left) {
				setBlockBounds(0.0F, 0.0F, 0.0F, 0.0F + pixel * 2, 0.875F, 1.0F);
				super.addCollisionBoxesToList(world, x, y, z, box, list, entity);
			}
		}

		if (meta == 4 || meta == 5) {
			setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, pixel * 2, 1.0F);
			super.addCollisionBoxesToList(world, x, y, z, box, list, entity);
			if (!back) {
				setBlockBounds(0.0F, 0.0F, 1.0F - pixel * 2, 1.0F, 0.875F, 1.0F);
				super.addCollisionBoxesToList(world, x, y, z, box, list, entity);
			}
			if (!front) {
				setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.875F, 0.0F + pixel * 2);
				super.addCollisionBoxesToList(world, x, y, z, box, list, entity);
			}
		}
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.875F, 1.0F);
	}

	public boolean canConnectBridgeTo(IBlockAccess IblockAccess, int x, int y, int z) {
		Block block = IblockAccess.getBlock(x, y, z);
		if (block != this)
			return block != null && block.renderAsNormalBlock() ? block.getMaterial() != Material.gourd : false;
		else
			return true;
	}
}