package erebus.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import erebus.ModItems;
import erebus.item.ItemErebusMaterial.DATA;

public class EntityBeetle extends EntityAnimal {
	int shagCount;
	public EntityBeetle(World world) {
		super(world);
		setSize(1.6F, 0.9F);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new EntityAIPanic(this, 0.6D));
		tasks.addTask(2, new EntityAIMate(this, 0.5D));
		tasks.addTask(3, new EntityAITempt(this, 0.5D, ModItems.turnip, false));
		tasks.addTask(5, new EntityAIWander(this, 0.5D));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
		tasks.addTask(7, new EntityAILookIdle(this));
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataWatcher.addObject(30, new Integer(rand.nextInt(51)));
		dataWatcher.addObject(31, new Byte((byte) 0));
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(15.0D);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5D);
	}

	@Override
	public boolean getCanSpawnHere() {
		float light = this.getBrightness(1.0F);
		if (light >= 0F) {
			return worldObj.checkNoEntityCollision(boundingBox) && worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty() && !worldObj.isAnyLiquid(this.boundingBox);
		}
		return super.getCanSpawnHere();
	}

	@Override
	public int getMaxSpawnedInChunk() {
		return 3;
	}

	@Override
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	@Override
	public int getTotalArmorValue() {
		return 4;
	}

	@Override
	protected String getLivingSound() {
		return "erebus:beetleSound";
	}

	@Override
	protected String getHurtSound() {
		return "erebus:beetleHurt";
	}

	@Override
	protected String getDeathSound() {
		return "erebus:squish";
	}

	@Override
	protected void func_145780_a(int x, int y, int z, Block block) { // playStepSound
		playSound("mob.spider.step", 0.15F, 1.0F);
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		if(shagCount > 0)
			shagCount--;
	}

	@Override
	public boolean interact(EntityPlayer player) {
		ItemStack is = player.inventory.getCurrentItem();

		if (is != null && is == new ItemStack(Items.bucket, 1) && !player.capabilities.isCreativeMode) {
			if (is.stackSize-- == 1)
				player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(ModItems.bucketOfBeetleJuice));
			else if (!player.inventory.addItemStackToInventory(new ItemStack(ModItems.bucketOfBeetleJuice)))
				player.dropPlayerItemWithRandomChoice(new ItemStack(ModItems.bucketOfBeetleJuice, 1, 0), false);
			return true;
		}
		if (is != null && is == new ItemStack(ModItems.bamBucket) && is.getItemDamage() == 0 && !player.capabilities.isCreativeMode) {
			if (is.stackSize-- == 1)
				player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(ModItems.bamBucket, 1, 2));
			else if (!player.inventory.addItemStackToInventory(new ItemStack(ModItems.bamBucket, 1, 2)))
				player.dropPlayerItemWithRandomChoice(new ItemStack(ModItems.bamBucket, 1, 2), false);
			return true;
		}
		if (is != null && is == new ItemStack(ModItems.turnip) && !shagging()) {
			is.stackSize--;
			setTame((byte) 1);
			shagCount = 600;
			return true;
		} else
			return super.interact(player);
	}
	
    public boolean shagging() {
        return shagCount > 0;
    }

	@Override
	protected void dropFewItems(boolean par1, int par2) {
		int var3 = 1 + rand.nextInt(3) + rand.nextInt(1 + par2);
		for (int a = 0; a < var3; ++a)
			entityDropItem(new ItemStack(ModItems.erebusMaterials, 1, DATA.plateExo.ordinal()), 0.0F);
	}

	@Override
	public boolean isBreedingItem(ItemStack is) {
		return is != null && is == new ItemStack(ModItems.turnip);
	}

	public EntityBeetleLarva spawnBabyAnimal(EntityAgeable entityageable) {
		EntityBeetleLarva entityBeetleLarva = new EntityBeetleLarva(worldObj);
		entityBeetleLarva.setTame((byte) 1);
		return entityBeetleLarva;
	}

	@Override
	protected boolean canDespawn() {
		if (getHasMated() == 1)
			return false;
		else
			return true;
	}

	@Override
	public EntityAgeable createChild(EntityAgeable entityageable) {
		return spawnBabyAnimal(entityageable);
	}

	public void setSkin(int skinType) {
		dataWatcher.updateObject(30, new Integer(skinType));
	}

	public void setTame(byte hasMated) {
		dataWatcher.updateObject(31, Byte.valueOf(hasMated));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("beetleSkin", getSkin());
		nbt.setByte("hasMated", getHasMated());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setSkin(nbt.getInteger("beetleSkin"));
		setTame(nbt.getByte("hasMated"));
	}

	public int getSkin() {
		return dataWatcher.getWatchableObjectInt(30);
	}

	public byte getHasMated() {
		return dataWatcher.getWatchableObjectByte(31);
	}

}