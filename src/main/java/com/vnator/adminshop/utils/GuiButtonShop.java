package com.vnator.adminshop.utils;

import com.vnator.adminshop.AdminShop;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.logging.log4j.Level;

import java.util.LinkedList;
import java.util.List;

public class GuiButtonShop extends GuiButton {

	private ItemStack item;
	private ItemStack item1, item16, item64;//Amounts of 1, 16, and 64
	public short category;
	public int index;
	private float price;
	private boolean isBuying;
	private RenderItem itemRender;

	private LinkedList<String> ttl1, ttl16, ttl64;

	/**
	 * A GuiButton that can be clicked to initiate a buy or sell action.
	 * @param buttonId ID of the button
	 * @param x x coordinate to draw the button
	 * @param y y coordinate to draw the button
	 * @param item ItemStack representing the item to be bought/sold
	 * @param price Price to buy/sell item for
	 * @param isBuying Whether the button is to initiate buying or selling. True = buying, False = selling
	 * @param itemRender RenderItem instance from GuiContainer to render item
	 */
	public GuiButtonShop(int buttonId, int x, int y, ItemStack item, float price, boolean isBuying, RenderItem itemRender) {
		super(buttonId, x, y, 16, 16, "");
		this.price = price;
		this.item = item;
		this.isBuying = isBuying;
		this.itemRender = itemRender;

		ttl1 = new LinkedList<String>();
		ttl1.add(item.getDisplayName());
		ttl1.add("$"+getPrice(1));

		ttl16 = new LinkedList<String>();
		ttl16.add(item.getDisplayName());
		ttl16.add("$"+getPrice(16));

		ttl64 = new LinkedList<String>();
		ttl64.add(item.getDisplayName());
		ttl64.add("$"+getPrice(64));

		item1 = item;
		item16 = ItemHandlerHelper.copyStackWithSize(item, 16);
		item64 = ItemHandlerHelper.copyStackWithSize(item, 64);

		//Initialize ItemStacks from item
		/*
		String [] itemParts = itemString.split(":");
		if(itemParts.length == 2){
			constructItemStacks(itemString, 0);
		}else if(itemParts.length == 3){
			int meta = 0;
			try {
				meta = Integer.parseInt(itemParts[2]);
			}catch (NumberFormatException e){
				AdminShop.logger.log(Level.ERROR, "Incorrectly formatted item: metadata must be int, not "+itemParts[2]);
			}
			constructItemStacks(itemParts[0]+":"+itemParts[1], meta);
		}else{
			constructItemStacks("", 0);
		}
		*/
	}

	private void constructItemStacks(String itemName, int meta){
		Item baseItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
		item = new ItemStack(baseItem, 1, meta);
	}


	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks){
		if(!visible)
			return;
		RenderHelper.enableGUIStandardItemLighting();
		itemRender.renderItemAndEffectIntoGUI(item, x, y);
		if(GuiScreen.isShiftKeyDown()){
			drawString(mc.fontRenderer, "64", x+17-mc.fontRenderer.getStringWidth("64"), y+9, 0xFFFFFF);
		}else if(GuiScreen.isCtrlKeyDown()){
			drawString(mc.fontRenderer, "16", x+17-mc.fontRenderer.getStringWidth("16"), y+9, 0xFFFFFF);
		}

	}

	/*
	@Override
	public void drawButtonForegroundLayer(int mouseX, int mouseY){

	}
	*/

	public List<String> getTooltipStrings(EntityPlayer player){

		int quant = 1;
		if(GuiScreen.isShiftKeyDown()){
			quant = 64;
		}else if(GuiScreen.isCtrlKeyDown()){
			quant = 16;
		}
		List<String> toret = item.getTooltip(player, ITooltipFlag.TooltipFlags.NORMAL);
		toret.add("");
		toret.add("$"+quant*price);
		return toret;
	}

	/**
	 * Calculates the total cost/gain of a transaction
	 * @return Amount of money to be exchanged by transaction
	 */
	public float getPrice(int quant){
		return price*quant;
	}

	/**
	 *
	 * @return ItemStack to be added/removed from player inventory
	 */
	public ItemStack getItemStack(){
		int quant = 1;
		if(GuiScreen.isShiftKeyDown()){
			quant = 64;
			return item64;
		}else if(GuiScreen.isCtrlKeyDown()){
			quant = 16;
			return item16;
		}else{
			return item;
		}

		//System.out.println(item.getItem().getUnlocalizedName()+" : "+item.getMetadata());
		//return new ItemStack(item.getItem(), quant, item.getMetadata());
	}

	public boolean isBuying(){
		return isBuying;
	}

}
