package com.pau101.fairylights.client.model;

import java.util.List;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.pau101.fairylights.util.MathUtils;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AdvancedModelRenderer extends ModelRenderer {
	protected int textureOffsetX;

	protected int textureOffsetY;

	protected boolean compiled;

	protected int displayList;

	protected ModelBase modelBase;

	protected float scaleX;

	protected float scaleY;

	protected float scaleZ;

	protected float aftMoveX;

	protected float aftMoveY;

	protected float aftMoveZ;

	protected RotationOrder rotationOrder;

	protected float secondaryRotateAngleX;

	protected float secondaryRotateAngleY;

	protected float secondaryRotateAngleZ;

	protected RotationOrder secondaryRotationOrder;

	public boolean isGlowing = false;

	public List<AdvancedModelRenderer> childModels;

	public AdvancedModelRenderer(ModelBase modelBase) {
		this(modelBase, (String) null);
	}

	public AdvancedModelRenderer(ModelBase modelBase, int textureOffsetX, int textureOffsetY) {
		this(modelBase);
		setTextureOffset(textureOffsetX, textureOffsetY);
	}

	public AdvancedModelRenderer(ModelBase modelBase, String name) {
		super(modelBase, name);
		this.modelBase = modelBase;
		setTextureSize(modelBase.textureWidth, modelBase.textureHeight);
		scaleX = scaleY = scaleZ = 1;
		rotationOrder = RotationOrder.ZYX;
		secondaryRotationOrder = RotationOrder.ZYX;
	}

	public void addChild(AdvancedModelRenderer modelRenderer) {
		if (childModels == null) {
			childModels = Lists.newArrayList();
		}

		childModels.add(modelRenderer);
	}

	@Override
	public AdvancedModelRenderer addBox(float posX, float posY, float posZ, int width, int height, int depth) {
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, 0));
		return this;
	}

	@Override
	public void addBox(float posX, float posY, float posZ, int width, int height, int depth, float scale) {
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, scale));
	}

	@Override
	public AdvancedModelRenderer addBox(String name, float posX, float posY, float posZ, int width, int height, int depth) {
		name = boxName + "." + name;
		TextureOffset textureoffset = modelBase.getTextureOffset(name);
		setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
		cubeList.add(new ModelBox(this, textureOffsetX, textureOffsetY, posX, posY, posZ, width, height, depth, 0.0F).func_78244_a(name));
		return this;
	}

	@SideOnly(Side.CLIENT)
	protected void compileDisplayList(float scale) {
		displayList = GLAllocation.generateDisplayLists(1);
		GL11.glNewList(displayList, GL11.GL_COMPILE);

		for (int i = 0; i < cubeList.size(); i++) {
			((ModelBox) cubeList.get(i)).render(Tessellator.instance, scale);
		}

		GL11.glEndList();
		compiled = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void postRender(float scale) {
		if (!isHidden) {
			if (showModel) {
				if (!compiled) {
					compileDisplayList(scale);
				}

				GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
				rotationOrder.rotate(rotateAngleX * MathUtils.RAD_TO_DEG, rotateAngleY * MathUtils.RAD_TO_DEG, rotateAngleZ * MathUtils.RAD_TO_DEG);
				secondaryRotationOrder.rotate(secondaryRotateAngleX * MathUtils.RAD_TO_DEG, secondaryRotateAngleY * MathUtils.RAD_TO_DEG, secondaryRotateAngleZ
					* MathUtils.RAD_TO_DEG);
				GL11.glTranslatef(aftMoveX, aftMoveY, aftMoveZ);
				GL11.glScalef(scaleX, scaleY, scaleZ);
			}
		}
	}

	private void baseRender(float scale) {
		if (isGlowing) {
			List<ModelBox> boxModels = cubeList;
			for (ModelBox box : boxModels) {
				int bri = 1;
				float expand = 0.45F;
				for (int i = 0; i < bri; i++) {
					float width = box.posX2 - box.posX1, height = box.posY2 - box.posY1, depth = box.posZ2 - box.posZ1;
					float localExpand = expand * (i + 1);
					float newWidth = width + 2 * localExpand;
					float newHeight = height + 2 * localExpand;
					float newDepth = depth + 2 * localExpand;
					float scaleX = newWidth / width;
					float scaleY = newHeight / height;
					float scaleZ = newDepth / depth;
					GL11.glPushMatrix();
					GL11.glTranslatef((box.posX1 - expand - scaleX * box.posX1) / 16, (box.posY1 - expand - scaleY * box.posY1) / 16,
						(box.posZ1 - expand - scaleZ * box.posZ1) / 16);
					GL11.glScalef(scaleX, scaleY, scaleZ);
					box.render(Tessellator.instance, scale);
					GL11.glPopMatrix();
				}
			}
		} else {
			GL11.glCallList(displayList);
		}

		if (childModels != null) {
			for (int i = 0; i < childModels.size(); i++) {
				AdvancedModelRenderer modelRenderer = childModels.get(i);
				modelRenderer.isGlowing = isGlowing;
				modelRenderer.render(scale);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(float scale) {
		if (!isHidden) {
			if (showModel) {
				if (!compiled) {
					compileDisplayList(scale);
				}

				GL11.glTranslatef(offsetX, offsetY, offsetZ);
				int i;

				if (rotateAngleX == 0 && rotateAngleY == 0 && rotateAngleZ == 0) {
					if (rotationPointX == 0 && rotationPointY == 0 && rotationPointZ == 0) {
						if (scaleX == 1 && scaleY == 1 && scaleZ == 1) {
							GL11.glPushMatrix();
							secondaryRotationOrder.rotate(secondaryRotateAngleX * MathUtils.RAD_TO_DEG, secondaryRotateAngleY * MathUtils.RAD_TO_DEG,
								secondaryRotateAngleZ * MathUtils.RAD_TO_DEG);
							GL11.glTranslatef(aftMoveX, aftMoveY, aftMoveZ);
							baseRender(scale);
							GL11.glPopMatrix();
						} else {
							GL11.glPushMatrix();
							secondaryRotationOrder.rotate(secondaryRotateAngleX * MathUtils.RAD_TO_DEG, secondaryRotateAngleY * MathUtils.RAD_TO_DEG,
								secondaryRotateAngleZ * MathUtils.RAD_TO_DEG);
							GL11.glTranslatef(aftMoveX, aftMoveY, aftMoveZ);
							GL11.glScalef(scaleX, scaleY, scaleZ);

							baseRender(scale);

							GL11.glPopMatrix();
						}
					} else {
						GL11.glPushMatrix();
						GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);
						secondaryRotationOrder.rotate(secondaryRotateAngleX * MathUtils.RAD_TO_DEG, secondaryRotateAngleY * MathUtils.RAD_TO_DEG,
							secondaryRotateAngleZ * MathUtils.RAD_TO_DEG);
						GL11.glTranslatef(aftMoveX, aftMoveY, aftMoveZ);
						GL11.glScalef(scaleX, scaleY, scaleZ);

						baseRender(scale);

						GL11.glPopMatrix();
					}
				} else {
					GL11.glPushMatrix();
					GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

					rotationOrder.rotate(rotateAngleX * MathUtils.RAD_TO_DEG, rotateAngleY * MathUtils.RAD_TO_DEG, rotateAngleZ * MathUtils.RAD_TO_DEG);
					secondaryRotationOrder.rotate(secondaryRotateAngleX * MathUtils.RAD_TO_DEG, secondaryRotateAngleY * MathUtils.RAD_TO_DEG,
						secondaryRotateAngleZ * MathUtils.RAD_TO_DEG);

					GL11.glTranslatef(aftMoveX, aftMoveY, aftMoveZ);

					GL11.glScalef(scaleX, scaleY, scaleZ);

					baseRender(scale);

					GL11.glPopMatrix();
				}

				GL11.glTranslatef(-offsetX, -offsetY, -offsetZ);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void renderWithRotation(float scale) {
		if (!isHidden) {
			if (showModel) {
				if (!compiled) {
					compileDisplayList(scale);
				}

				GL11.glPushMatrix();
				GL11.glTranslatef(rotationPointX * scale, rotationPointY * scale, rotationPointZ * scale);

				rotationOrder.rotate(rotateAngleX * MathUtils.RAD_TO_DEG, rotateAngleY * MathUtils.RAD_TO_DEG, rotateAngleZ * MathUtils.RAD_TO_DEG);

				GL11.glScalef(scaleX, scaleY, scaleZ);

				baseRender(scale);
				GL11.glPopMatrix();
			}
		}
	}

	@Override
	public void setRotationPoint(float rotationPointX, float rotationPointY, float rotationPointZ) {
		this.rotationPointX = rotationPointX;
		this.rotationPointY = rotationPointY;
		this.rotationPointZ = rotationPointZ;
	}

	public void setRotationAngles(float rotateAngleX, float rotateAngleY, float rotateAngleZ) {
		this.rotateAngleX = rotateAngleX;
		this.rotateAngleY = rotateAngleY;
		this.rotateAngleZ = rotateAngleZ;
	}

	@Override
	public AdvancedModelRenderer setTextureOffset(int textureOffsetX, int textureOffsetY) {
		this.textureOffsetX = textureOffsetX;
		this.textureOffsetY = textureOffsetY;
		return this;
	}

	@Override
	public AdvancedModelRenderer setTextureSize(int textureWidth, int textureHeight) {
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		return this;
	}

	public void setRotationOrder(RotationOrder rotationOrder) {
		this.rotationOrder = rotationOrder;
	}

	public RotationOrder getRotationOrder() {
		return rotationOrder;
	}
}
