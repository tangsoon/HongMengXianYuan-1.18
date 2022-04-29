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
public abstract class MixinPlayer extends LivingEntity{
	
	protected MixinPlayer(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
		super(p_20966_, p_20967_);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("RETURN") )
	public void onReadAdditionalSaveData(CompoundTag pCompound,CallbackInfo ci) {
		HmxyHelper.setZhenYuan(this, pCompound.getInt("zhenYuan"));
		HmxyHelper.setXiaoJingJie(this, pCompound.getInt("xiaoJingJie"));
		HmxyHelper.setLingLi(this, pCompound.getFloat("lingLi"));
	}
	
	@Inject(method = "addAdditionalSaveData", at = @At("RETURN") )
	 public void onAddAdditionalSaveData(CompoundTag pCompound,CallbackInfo ci) {
		 pCompound.putInt("zhenYuan", HmxyHelper.getZhenYuan(this));
		 pCompound.putInt("xiaoJingJie",HmxyHelper.getXiaoJingJie(this));
		 pCompound.putFloat("lingLi",HmxyHelper.getLingLi(this));
	 }
}
