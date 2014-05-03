package erebus.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import erebus.entity.EntityFireResistent;

public class EncrustedDiamond extends Item {

	public EncrustedDiamond(int id) {
		super(id);
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return true;
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack stack) {
		EntityFireResistent entity = new EntityFireResistent(world, location.posX, location.posY, location.posZ, stack);
		entity.motionX = location.motionX;
		entity.motionY = location.motionY;
		entity.motionZ = location.motionZ;
		entity.delayBeforeCanPickup = 10;
		return entity;
	}
}