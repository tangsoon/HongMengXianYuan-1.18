package by.ts.hmxy.mixins;

import org.spongepowered.asm.mixin.Mixin;
import by.ts.hmxy.util.HmxyHelper;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.LivingEntity;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {
	/*
	 * EntityDataAccessor的创建必须在参数指明的类中进行，因为这个构建机制依赖于类的加载顺序，错误使用会导致其ID重复。
	 */
	static {
		HmxyHelper.ZHEN_YUAN = SynchedEntityData.defineId(LivingEntity.class,
				EntityDataSerializers.INT);
		/** 小境界 */
		HmxyHelper.XIAO_JING_JIE = SynchedEntityData.defineId(LivingEntity.class,
				EntityDataSerializers.INT);
		/** 灵力 */
		HmxyHelper.灵力 = SynchedEntityData.defineId(LivingEntity.class,
				EntityDataSerializers.FLOAT);
	}
}
