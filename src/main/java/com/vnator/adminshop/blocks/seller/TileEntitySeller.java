package com.vnator.adminshop.blocks.seller;

import com.vnator.adminshop.ConfigHandler;
import com.vnator.adminshop.blocks.shop.ShopStock;
import com.vnator.adminshop.capabilities.BalanceAdapter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileEntitySeller extends TileEntity implements ITickable, IFluidHandler{

	private String player;

	private SellerBattery battery = new SellerBattery(1000000000, this);
	private FluidTank tank = new FluidTank(1000000);
	private ItemStackHandler inventory = new ItemStackHandler(1){
		@Override
		public ItemStack insertItem(int slot, ItemStack stack, boolean simulate){
			String id = stack.getItem().getRegistryName() + ":" + stack.getMetadata();
			if(ShopStock.sellItemMap.containsKey(id))
				return super.insertItem(slot, stack, simulate);
			else
				return stack;
		}
	};

	@Override
	public void update() {
		if(!world.isRemote && player != null) {
			if (!inventory.getStackInSlot(0).isEmpty()) {
				//Sell the item
				ItemStack item = inventory.getStackInSlot(0);
				String name = item.getItem().getRegistryName() + ":" + item.getMetadata();
				if (item.getTagCompound() != null)
					name += " " + item.getTagCompound().toString();
				float money = ShopStock.sellItemMap.get(name) * item.getCount();
				BalanceAdapter.deposit(world, player, money);
				//world.getCapability(LedgerProvider.LEDGER_CAPABILITY, null).deposit(player, money);
				inventory.setStackInSlot(0, ItemStack.EMPTY);
				markDirty();
			}

			if(tank.getFluidAmount() >= ConfigHandler.GENERAL_CONFIGS.liquidSellPacketSize){
				//Sell fluid in tank
				String name = tank.getFluid().getFluid().getName();
				if(tank.getFluid().tag != null)
					name += " "+tank.getFluid().tag.toString();
				float money = ShopStock.sellFluidMap.get(name)*tank.getFluidAmount();
				BalanceAdapter.deposit(world, player, money);
				tank.drain(tank.getCapacity(), true);
				markDirty();
			}

			if(battery.getEnergyStored() >= ConfigHandler.GENERAL_CONFIGS.powerSellPacketSize){
				int cap = battery.deleteEnergy(); //This method marks this TileEntity as dirty
				float money = cap * ConfigHandler.Sellable_Items.forgeEnergyPrice;
				BalanceAdapter.deposit(world, player, money);
			}
		}
	}

	/*Fluid Tank Methods*/

	@Override
	public IFluidTankProperties[] getTankProperties() {
		return tank.getTankProperties();
	}

	@Override
	public int fill(FluidStack resource, boolean doFill) {
		String name = resource.getFluid().getName();
		if(resource.tag != null)
			name += " "+resource.tag.toString();

		//Fluid can be sold
		if(ShopStock.sellFluidMap.containsKey(name)){
			markDirty();
			return tank.fill(resource, doFill);
		}else
			return 0;
	}

	@Nullable
	@Override
	public FluidStack drain(FluidStack resource, boolean doDrain) {
		return null;
	}

	@Nullable
	@Override
	public FluidStack drain(int maxDrain, boolean doDrain) {
		return null;
	}

	/*NBT I/O*/

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		compound.setTag("inventory", inventory.serializeNBT());
		compound.setString("player", player.toString());
		tank.writeToNBT(compound);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound){
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		player = compound.getString("player");
		if(player == null || player.equals("")){
			player = null;
		}
		tank.readFromNBT(compound);
		super.readFromNBT(compound);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ||
				capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ||
				capability == CapabilityEnergy.ENERGY ||
				super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T)inventory;
		else if(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T)this;
		else if(capability == CapabilityEnergy.ENERGY)
			return (T) battery;
		else
			return super.getCapability(capability, facing);
	}

	public void setPlayer(String player){
		this.player = player;
	}

	public String getPlayer(){
		return player;
	}


}