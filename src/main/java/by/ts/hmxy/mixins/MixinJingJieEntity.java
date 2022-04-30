package by.ts.hmxy.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
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

	public static final List<Predicate<LivingEntity>> IS_CONSUMMING_LingLi = new ArrayList<>();
	static {
		IS_CONSUMMING_LingLi.add(living -> {
			return living.isSwimming();
		});
		IS_CONSUMMING_LingLi.add(living -> {
			return living.isSprinting();
		});
		IS_CONSUMMING_LingLi.add(living -> {
			return living.isFallFlying();
		});
	}

	protected MixinJingJieEntity(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@Inject(method = "defineSynchedData", at = @At("RETURN"))
	protected void onDefineSynchedData(CallbackInfo ci) {
		this.getEntityData().define(HmxyHelper.ZHEN_YUAN, Integer.valueOf(0));
		this.getEntityData().define(HmxyHelper.XIAO_JING_JIE, Integer.valueOf(0));
		this.getEntityData().define(HmxyHelper.灵力, Float.valueOf(20.0F));
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

	{
		System.out.println("lala");
	}

	@Inject(method = "tick", at = @At("RETURN"))
	public void onTick(CallbackInfo ci) {
		LivingEntity living = this;
		float lingLi = HmxyHelper.getLingLi(this);
		if (living instanceof Player) {
			// 冲刺时消耗灵力
			if (((Player) living).isSprinting()) {
				float consume = (float) HmxyHelper.getLingLiConsumeWhenSprinting(this);
				if (lingLi > consume) {
					HmxyHelper.setLingLi(this, lingLi - consume);
				} else {
					this.setSprinting(false);
				}
			}
		}
		// 灵力恢复，冲刺、在水中、飞行中不能恢复灵力
		float capacity;
		if (!this.isSprinting() && !this.swinging && !this.isFallFlying()
				&& (capacity = ((float) HmxyHelper.getMaxLingLi(this) - lingLi)) > 0) {
			HmxyHelper.setLingLi(this,
					lingLi + Math.min(capacity, (float) HmxyHelper.getLingLiResumeWhenSprinting(this)));
		}
	}

	public boolean isConsummingLingLi() {
		if (IS_CONSUMMING_LingLi.stream().anyMatch(p -> {
			return p.test(this);
		})) {
			return true;
		}
		return false;
	}
}
