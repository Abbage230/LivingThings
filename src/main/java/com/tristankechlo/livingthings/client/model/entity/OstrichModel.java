package com.tristankechlo.livingthings.client.model.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.tristankechlo.livingthings.client.model.AdvancedEntityModel;
import com.tristankechlo.livingthings.entities.OstrichEntity;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OstrichModel<T extends OstrichEntity> extends AdvancedEntityModel<T> {

	private final ModelRenderer Body;
	private final ModelRenderer Neck;
	private final ModelRenderer NeckTop;
	private final ModelRenderer Head;
	private final ModelRenderer Mouth;
	private final ModelRenderer MouthTop;
	private final ModelRenderer LeftWing;
	private final ModelRenderer RightWing;
	private final ModelRenderer LeftLegTop;
	private final ModelRenderer LeftLegBottom;
	private final ModelRenderer LeftFoot;
	private final ModelRenderer RightLegTop;
	private final ModelRenderer RightLegBottom;
	private final ModelRenderer RightFoot;
	private final ModelRenderer Back;
	private final ModelRenderer add1;
	private final ModelRenderer add1Left;
	private final ModelRenderer add1Right;
	private final ModelRenderer add2;
	private final ModelRenderer add2Left;
	private final ModelRenderer add2Right;
	private final ModelRenderer add3;
	private final ModelRenderer add3Left;
	private final ModelRenderer add3Right;

	private boolean islayingEgg;
	private boolean isbuildingNest;

	public OstrichModel() {
		texWidth = 64;
		texHeight = 64;

		Body = new ModelRenderer(this);
		Body.setPos(0.0F, 8.0F, 0.0F);
		this.setRotationAngle(Body, -0.0436F, 0.0F, 0.0F);
		Body.texOffs(0, 44).addBox(-4.5F, -5.0F, -4.0F, 9.0F, 9.0F, 11.0F, 0.0F, false);
		Body.texOffs(42, 53).addBox(-4.0F, -5.0F, -7.0F, 8.0F, 8.0F, 3.0F, 0.0F, false);

		Neck = new ModelRenderer(this);
		Neck.setPos(0.0F, -1.0F, -7.5F);
		Body.addChild(Neck);
		this.setRotationAngle(Neck, 0.3054F, 0.0F, 0.0F);
		Neck.texOffs(29, 0).addBox(-1.5F, -5.0F, -0.5F, 3.0F, 7.0F, 3.0F, 0.0F, false);

		NeckTop = new ModelRenderer(this);
		NeckTop.setPos(0.0F, -5.0F, 1.0F);
		Neck.addChild(NeckTop);
		this.setRotationAngle(NeckTop, -0.2182F, 0.0F, 0.0F);
		NeckTop.texOffs(29, 11).addBox(-1.0F, -7.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

		Head = new ModelRenderer(this);
		Head.setPos(0.0F, -7.0F, 0.0F);
		NeckTop.addChild(Head);
		this.setRotationAngle(Head, -0.0436F, 0.0F, 0.0F);
		Head.texOffs(29, 22).addBox(-1.5F, -3.0F, -2.75F, 3.0F, 3.0F, 4.0F, 0.0F, false);

		Mouth = new ModelRenderer(this);
		Mouth.setPos(0.0F, 0.0F, -3.0F);
		Head.addChild(Mouth);
		Mouth.texOffs(29, 30).addBox(-1.0F, -1.3F, -1.25F, 2.0F, 1.0F, 2.0F, 0.0F, false);

		MouthTop = new ModelRenderer(this);
		MouthTop.setPos(0.0F, -0.8F, -0.25F);
		Mouth.addChild(MouthTop);
		this.setRotationAngle(MouthTop, 0.2182F, 0.0F, 0.0F);
		MouthTop.texOffs(29, 30).addBox(-1.0F, -0.75F, -1.0F, 2.0F, 1.0F, 2.0F, 0.0F, false);

		LeftWing = new ModelRenderer(this);
		LeftWing.setPos(4.5F, -2.0F, -3.0F);
		Body.addChild(LeftWing);
		this.setRotationAngle(LeftWing, 0.0F, 0.1745F, 0.0F);
		LeftWing.texOffs(44, 22).addBox(-0.5F, -2.0F, 0.0F, 1.0F, 5.0F, 9.0F, 0.0F, false);

		RightWing = new ModelRenderer(this);
		RightWing.setPos(-4.5F, -2.0F, -3.0F);
		Body.addChild(RightWing);
		this.setRotationAngle(RightWing, 0.0F, -0.1745F, 0.0F);
		RightWing.texOffs(44, 37).addBox(-0.5F, -2.0F, 0.0F, 1.0F, 5.0F, 9.0F, 0.0F, false);

		LeftLegTop = new ModelRenderer(this);
		LeftLegTop.setPos(4.0F, 2.0F, 0.0F);
		Body.addChild(LeftLegTop);
		this.setRotationAngle(LeftLegTop, 0.3927F, 0.0F, 0.0F);
		LeftLegTop.texOffs(0, 21).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

		LeftLegBottom = new ModelRenderer(this);
		LeftLegBottom.setPos(0.0F, 7.0F, 0.0F);
		LeftLegTop.addChild(LeftLegBottom);
		this.setRotationAngle(LeftLegBottom, -0.5236F, 0.0F, 0.0F);
		LeftLegBottom.texOffs(9, 21).addBox(-1.0F, -0.6F, -1.15F, 2.0F, 8.0F, 2.0F, 0.0F, false);

		LeftFoot = new ModelRenderer(this);
		LeftFoot.setPos(0.0F, 7.0F, 0.0F);
		LeftLegBottom.addChild(LeftFoot);
		this.setRotationAngle(LeftFoot, 0.1745F, 0.0F, 0.0F);
		LeftFoot.texOffs(18, 27).addBox(-1.0F, -0.5F, -2.1F, 2.0F, 1.0F, 3.0F, 0.0F, false);

		RightLegTop = new ModelRenderer(this);
		RightLegTop.setPos(-4.0F, 2.0F, 0.0F);
		Body.addChild(RightLegTop);
		this.setRotationAngle(RightLegTop, 0.3927F, 0.0F, 0.0F);
		RightLegTop.texOffs(0, 32).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F, false);

		RightLegBottom = new ModelRenderer(this);
		RightLegBottom.setPos(0.0F, 7.0F, 0.0F);
		RightLegTop.addChild(RightLegBottom);
		this.setRotationAngle(RightLegBottom, -0.5236F, 0.0F, 0.0F);
		RightLegBottom.texOffs(9, 32).addBox(-1.0F, -0.6F, -1.15F, 2.0F, 8.0F, 2.0F, 0.0F, false);

		RightFoot = new ModelRenderer(this);
		RightFoot.setPos(0.0F, 7.0F, 0.0F);
		RightLegBottom.addChild(RightFoot);
		this.setRotationAngle(RightFoot, 0.1745F, 0.0F, 0.0F);
		RightFoot.texOffs(18, 38).addBox(-1.0F, -0.5F, -2.1F, 2.0F, 1.0F, 3.0F, 0.0F, false);

		Back = new ModelRenderer(this);
		Back.setPos(0.0F, -4.0F, 7.5F);
		Body.addChild(Back);
		Back.texOffs(46, 10).addBox(-4.0F, -1.0F, -0.5F, 8.0F, 8.0F, 1.0F, 0.0F, false);

		add1 = new ModelRenderer(this);
		add1.setPos(0.0F, 0.0F, 0.0F);
		Back.addChild(add1);
		this.setRotationAngle(add1, -0.6545F, 0.0F, 0.0F);
		add1.texOffs(0, 0).addBox(-1.5F, -5.0F, -0.5F, 3.0F, 5.0F, 1.0F, 0.0F, false);

		add1Left = new ModelRenderer(this);
		add1Left.setPos(1.0F, 0.0F, 2.0F);
		add1.addChild(add1Left);
		this.setRotationAngle(add1Left, 0.0F, 0.0F, 0.6109F);
		add1Left.texOffs(9, 0).addBox(-1.0F, -5.0F, -2.5F, 2.0F, 5.0F, 1.0F, 0.0F, false);

		add1Right = new ModelRenderer(this);
		add1Right.setPos(-1.0F, 0.0F, 2.0F);
		add1.addChild(add1Right);
		this.setRotationAngle(add1Right, 0.0F, 0.0F, -0.6109F);
		add1Right.texOffs(16, 0).addBox(-1.0F, -5.0F, -2.5F, 2.0F, 5.0F, 1.0F, 0.0F, false);

		add2 = new ModelRenderer(this);
		add2.setPos(0.0F, 0.5F, 0.0F);
		Back.addChild(add2);
		this.setRotationAngle(add2, -1.1781F, 0.0F, 0.0F);
		add2.texOffs(0, 7).addBox(-1.5F, -5.0F, -0.5F, 3.0F, 5.0F, 1.0F, 0.0F, false);

		add2Left = new ModelRenderer(this);
		add2Left.setPos(1.0F, 0.0F, 2.0F);
		add2.addChild(add2Left);
		this.setRotationAngle(add2Left, 0.0F, 0.0F, 0.6545F);
		add2Left.texOffs(9, 7).addBox(-1.0F, -5.0F, -2.5F, 2.0F, 5.0F, 1.0F, 0.0F, false);

		add2Right = new ModelRenderer(this);
		add2Right.setPos(-1.0F, 0.0F, 2.0F);
		add2.addChild(add2Right);
		this.setRotationAngle(add2Right, 0.0F, 0.0F, -0.7418F);
		add2Right.texOffs(9, 14).addBox(-1.0F, -5.0F, -2.5F, 2.0F, 5.0F, 1.0F, 0.0F, false);

		add3 = new ModelRenderer(this);
		add3.setPos(0.0F, 1.0F, 0.0F);
		Back.addChild(add3);
		this.setRotationAngle(add3, -1.6581F, 0.0F, 0.0F);
		add3.texOffs(0, 14).addBox(-1.5F, -5.0F, -0.5F, 3.0F, 5.0F, 1.0F, 0.0F, false);

		add3Left = new ModelRenderer(this);
		add3Left.setPos(1.0F, 0.0F, 2.0F);
		add3.addChild(add3Left);
		this.setRotationAngle(add3Left, 0.0F, 0.0F, 0.6981F);
		add3Left.texOffs(16, 7).addBox(-1.0F, -5.0F, -2.5F, 2.0F, 5.0F, 1.0F, 0.0F, false);

		add3Right = new ModelRenderer(this);
		add3Right.setPos(-1.0F, 0.0F, 2.0F);
		add3.addChild(add3Right);
		this.setRotationAngle(add3Right, 0.0F, 0.0F, -0.6981F);
		add3Right.texOffs(16, 14).addBox(-1.0F, -5.0F, -2.5F, 2.0F, 5.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
			float headPitch) {
		this.islayingEgg = entity.isLayingEgg();
		this.isbuildingNest = entity.isBuildingNest();

		this.Head.xRot = headPitch * 0.0174532925F;
		this.Head.yRot = (netHeadYaw / 3.75F) * 0.0174532925F;

		this.NeckTop.xRot = (float) (this.Head.xRot / 1.75F);
		this.Neck.yRot = (float) (this.Head.yRot / 1.75F);

		if (this.islayingEgg) {

			this.LeftLegTop.xRot = 1.17809724375F;
			this.LeftLegBottom.xRot = -2.5743606466F;
			this.LeftFoot.xRot = 0.6108652381F;

			this.RightLegTop.xRot = 1.17809724375F;
			this.RightLegBottom.xRot = -2.5743606466F;
			this.RightFoot.xRot = 0.6108652381F;

		} else if (this.isbuildingNest) {

			this.LeftLegTop.xRot = 0.3926990812F;
			this.LeftLegBottom.xRot = -0.523598775F;
			this.LeftFoot.xRot = 0.174532925F;

			this.RightLegTop.xRot = 0.3926990812F + (MathHelper.cos(ageInTicks * 0.45F));
			this.RightLegBottom.xRot = -0.523598775F;
			this.RightFoot.xRot = 0.174532925F;

		} else {

			this.LeftLegTop.xRot = 0.3926990812F
					+ (MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * limbSwingAmount);
			this.LeftLegBottom.xRot = -0.523598775F;
			this.LeftFoot.xRot = 0.174532925F;

			this.RightLegTop.xRot = 0.3926990812F + (MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount);
			this.RightLegBottom.xRot = -0.523598775F;
			this.RightFoot.xRot = 0.174532925F;

		}
	}

	@Override
	public void renderToBuffer(MatrixStack matrixStack, IVertexBuilder buffer, int packedLight, int packedOverlay,
			float red, float green, float blue, float alpha) {

		if (this.young) {
			matrixStack.scale(0.6F, 0.6F, 0.6F);
			matrixStack.translate(0, 1, 0);
		} else if (this.islayingEgg) {
			matrixStack.translate(0, 0.65, 0);
		}
		Body.render(matrixStack, buffer, packedLight, packedOverlay);
	}

}