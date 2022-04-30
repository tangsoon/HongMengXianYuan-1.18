package by.ts.hmxy.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import by.ts.hmxy.util.HmxyHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class MixinJingJieEntity extends LivingEntity {
	//不要在Mixin类中添加费静态代码块

	protected MixinJingJieEntity(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
		super(p_20966_, p_20967_);
	}

	@Inject(method = "defineSynchedData", at = @At("RETURN"))
	protected void onDefineSynchedData(CallbackInfo ci) {
		this.getEntityData().define(HmxyHelper.ZHEN_YUAN, Integer.valueOf(0));
		this.getEntityData().define(HmxyHelper.XIAO_JING_JIE, Integer.valueOf(0));
		this.getEntityData().define(HmxyHelper.灵力, Float.valueOf(20.0F));
		this.getEntityData().define(HmxyHelper.STAMINA, Float.valueOf(20.0F));
	}

	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	public void onReadAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
		HmxyHelper.setZhenYuan(this, pCompound.getInt("zhenYuan"));
		HmxyHelper.setXiaoJingJie(this, pCompound.getInt("xiaoJingJie"));
		HmxyHelper.setLingLi(this, pCompound.getFloat("lingLi"));
		
	}

	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	public void onAddAdditionalSaveData(CompoundTag pCompound, CallbackInfo ci) {
		pCompound.putInt("zhenYuan", HmxyHelper.getZhenYuan(this));
		pCompound.putInt("xiaoJingJie", HmxyHelper.getXiaoJingJie(this));
		pCompound.putFloat("lingLi", HmxyHelper.getLingLi(this));
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void onTick(CallbackInfo ci) {
		LivingEntity living = this;
		float stamina = HmxyHelper.getStamina(this);

		// 冲刺时消耗体力
		if (HmxyHelper.isConsummingStamina(living)) {
			float consume = (float) HmxyHelper.getStaminaConsume(this);
			if (stamina > consume) {
				HmxyHelper.setStamina(living, stamina);
			} else {
				this.setSprinting(false);
			}
		}

		// 灵力恢复，冲刺、在水中、飞行中不能恢复灵力
		float capacity;
		if (!HmxyHelper.isConsummingStamina(living)
				&& (capacity = ((float) HmxyHelper.getMaxStamina(this) - stamina)) > 0) {
			HmxyHelper.setLingLi(this,
					stamina + Math.min(capacity, (float) HmxyHelper.getStaminaResume(this)));
		}
	}
}
