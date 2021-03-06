package com.vnator.adminshop.blocks.seller;

import com.vnator.adminshop.AdminShop;
import com.vnator.adminshop.ModBlocks;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

public class GuiSeller extends GuiContainer {

	private static final ResourceLocation BG_TEXTURE = 	new ResourceLocation(AdminShop.MODID, "textures/gui/seller.png");
	private InventoryPlayer playerInv;
	private String owner;

	public GuiSeller(Container inventorySlotsIn, InventoryPlayer playerInv, TileEntitySeller te) {
		super(inventorySlotsIn);
		this.playerInv = playerInv;
		this.owner = "Insert or pipe in items to sell";
		//this.owner = "Owned by: "+Minecraft.getMinecraft().world.getPlayerEntityByUUID(((ContainerSeller)inventorySlotsIn).owner).getName();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(BG_TEXTURE);
		int x = (width-xSize) / 2;
		int y = (height-ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		String name = I18n.format(ModBlocks.itemSeller.getUnlocalizedName()+".name");
		fontRenderer.drawString(name, xSize/2 - fontRenderer.getStringWidth(name)/2, 6, 0x404040);
		fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, ySize-94, 0x404040);
		fontRenderer.drawString(owner, xSize/2 - fontRenderer.getStringWidth(owner)/2, 20, 0x404040);
	}
}
