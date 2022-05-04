package by.ts.hmxy.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import by.ts.hmxy.util.HmxyHelper;
import net.minecraft.world.level.chunk.LevelChunk;

@Mixin(LevelChunk.class)
public class MixinLevelChunk{
	@Inject(method = "tick", at = @At(value=MixinHelper.AT.HEAD.AT))
	protected void onTick(CallbackInfo ci) {
		this.getEntityData().define(HmxyHelper.ZHEN_YUAN, Integer.valueOf(0));
		this.getEntityData().define(HmxyHelper.XIAO_JING_JIE, Integer.valueOf(0));
		this.getEntityData().define(HmxyHelper.灵力, Float.valueOf(20.0F));
		this.getEntityData().define(HmxyHelper.STAMINA, Float.valueOf(20.0F));
	}
}
