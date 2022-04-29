package by.ts.hmxy.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import by.ts.hmxy.HmxyConfig;
import by.ts.hmxy.util.HmxyHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

@Mixin(Player.class)
public abstract class MixinPlayer extends LivingEntity{
	
	protected MixinPlayer(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}

	@Inject(method = "tick", at = @At("HEAD"))
	public void onTick(CallbackInfo ci) {
		if(isSprinting()) {
			float lingLi=HmxyHelper.getLingLi(this);
			float consume= HmxyConfig.lingLiConsumePerTickWhenSpringting();
			if(lingLi>consume) {
				HmxyHelper.setLingLi(this, lingLi-consume);
			}
		}
	}
	
	@Shadow
	public abstract boolean isSprinting();
	
	public abstract void setSprinting(boolean pSprinting);
}
