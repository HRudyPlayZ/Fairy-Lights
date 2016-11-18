package com.pau101.fairylights.client.renderer.block.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

import com.pau101.fairylights.client.renderer.FastenerRenderer;
import com.pau101.fairylights.server.block.entity.BlockEntityFastener;
import com.pau101.fairylights.server.capability.CapabilityHandler;

public final class BlockEntityFastenerRenderer extends TileEntitySpecialRenderer<BlockEntityFastener> {
	@Override
	public boolean isGlobalRenderer(BlockEntityFastener fastener) {
		return true;
	}

	@Override
	public void renderTileEntityAt(BlockEntityFastener fastener, double x, double y, double z, float delta, int destroyStage) {
		bindTexture(FastenerRenderer.TEXTURE);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		FastenerRenderer.render(fastener.getCapability(CapabilityHandler.FASTENER_CAP, null), delta);
		GlStateManager.popMatrix();
	}
}
