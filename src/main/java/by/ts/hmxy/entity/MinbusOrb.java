package by.ts.hmxy.entity;

import java.util.List;

import by.ts.hmxy.util.HmxyHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;

//Copy from vallia and do some change.
public class MinbusOrb extends Entity {
	private int age;
	private int health = 5;
	public int value;
	private int count = 1;
	private Player followingPlayer;

	public MinbusOrb(Level level, double x, double y, double z, int value) {
		this(HmxyEntities.MINBUS_ORB.get(), level);
		this.setPos(x, y, z);
		this.setYRot((float) (this.random.nextDouble() * 360.0D));
		this.setDeltaMovement((this.random.nextDouble() * (double) 0.2F - (double) 0.1F) * 2.0D,
				this.random.nextDouble() * 0.2D * 2.0D,
				(this.random.nextDouble() * (double) 0.2F - (double) 0.1F) * 2.0D);
		this.value = value;
	}

	public MinbusOrb(EntityType<? extends MinbusOrb> type, Level level) {
		super(type, level);
	}

	protected Entity.MovementEmission getMovementEmission() {
		return Entity.MovementEmission.NONE;
	}

	protected void defineSynchedData() {
	}

	public void tick() {
		super.tick();
		// xOld
		this.xo = this.getX();
		this.yo = this.getY();
		this.zo = this.getZ();
		if (this.isEyeInFluid(FluidTags.WATER)) {
			this.setUnderwaterMovement();
		} else if (!this.isNoGravity()) {
			this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
		}

		if (this.level.getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
			this.setDeltaMovement((double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F), (double) 0.2F,
					(double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F));
		}

		if (!this.level.noCollision(this.getBoundingBox())) {
			this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D,
					this.getZ());
		}

		if (this.tickCount % 20 == 1) {
			this.scanForEntities();
		}

		if (this.followingPlayer != null
				&& (this.followingPlayer.isSpectator() || this.followingPlayer.isDeadOrDying())) {
			this.followingPlayer = null;
		}

		if (this.followingPlayer != null) {
			Vec3 vec3 = new Vec3(this.followingPlayer.getX() - this.getX(),
					this.followingPlayer.getY() + (double) this.followingPlayer.getEyeHeight() / 2.0D - this.getY(),
					this.followingPlayer.getZ() - this.getZ());
			double d0 = vec3.lengthSqr();
			if (d0 < 64.0D) {
				double d1 = 1.0D - Math.sqrt(d0) / 8.0D;
				this.setDeltaMovement(this.getDeltaMovement().add(vec3.normalize().scale(d1 * d1 * 0.1D)));
			}
		}

		this.move(MoverType.SELF, this.getDeltaMovement());
		float f = 0.98F;
		if (this.onGround) {
			BlockPos pos = new BlockPos(this.getX(), this.getY() - 1.0D, this.getZ());
			f = this.level.getBlockState(pos).getFriction(this.level, pos, this) * 0.98F;
		}

		this.setDeltaMovement(this.getDeltaMovement().multiply((double) f, 0.98D, (double) f));
		if (this.onGround) {
			this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, -0.9D, 1.0D));
		}

		++this.age;
		if (this.age >= 6000) {
			this.discard();
		}

	}

	private void scanForEntities() {
		if (this.followingPlayer == null || this.followingPlayer.distanceToSqr(this) > 64.0D) {
			this.followingPlayer = this.level.getNearestPlayer(this, 8.0D);
		}

		if (this.level instanceof ServerLevel) {
			for (MinbusOrb experienceorb : this.level.getEntities(EntityTypeTest.forClass(MinbusOrb.class),
					this.getBoundingBox().inflate(0.5D), this::canMerge)) {
				this.merge(experienceorb);
			}
		}

	}

	public static void award(ServerLevel level, Vec3 vec, int value) {
		while (value > 0) {
			int i = getExperienceValue(value);
			value -= i;
			if (!tryMergeToExisting(level, vec, i)) {
				level.addFreshEntity(new MinbusOrb(level, vec.x(), vec.y(), vec.z(), i));
			}
		}

	}

	private static boolean tryMergeToExisting(ServerLevel p_147097_, Vec3 p_147098_, int p_147099_) {
		AABB aabb = AABB.ofSize(p_147098_, 1.0D, 1.0D, 1.0D);
		int i = p_147097_.getRandom().nextInt(40);
		List<MinbusOrb> list = p_147097_.getEntities(EntityTypeTest.forClass(MinbusOrb.class), aabb, (p_147081_) -> {
			return canMerge(p_147081_, i, p_147099_);
		});
		if (!list.isEmpty()) {
			MinbusOrb experienceorb = list.get(0);
			++experienceorb.count;
			experienceorb.age = 0;
			return true;
		} else {
			return false;
		}
	}

	private boolean canMerge(MinbusOrb p_147087_) {
		return p_147087_ != this && canMerge(p_147087_, this.getId(), this.value);
	}

	private static boolean canMerge(MinbusOrb p_147089_, int p_147090_, int p_147091_) {
		return !p_147089_.isRemoved() && (p_147089_.getId() - p_147090_) % 40 == 0 && p_147089_.value == p_147091_;
	}

	private void merge(MinbusOrb p_147101_) {
		this.count += p_147101_.count;
		this.age = Math.min(this.age, p_147101_.age);
		p_147101_.discard();
	}

	private void setUnderwaterMovement() {
		Vec3 vec3 = this.getDeltaMovement();
		this.setDeltaMovement(vec3.x * (double) 0.99F, Math.min(vec3.y + (double) 5.0E-4F, (double) 0.06F),
				vec3.z * (double) 0.99F);
	}

	@Override
	public boolean hurt(DamageSource pSource, float pAmount) {
		if (this.level.isClientSide || this.isRemoved())
			return false;
		if (this.isInvulnerableTo(pSource)) {
			return false;
		} else {
			this.markHurt();
			this.health = (int) ((float) this.health - pAmount);
			if (this.health <= 0) {
				this.discard();
			}
			return true;
		}
	}

	public void addAdditionalSaveData(CompoundTag pCompound) {
		pCompound.putShort("Health", (short) this.health);
		pCompound.putShort("Age", (short) this.age);
		pCompound.putShort("Value", (short) this.value);
		pCompound.putInt("Count", this.count);
	}

	public void readAdditionalSaveData(CompoundTag pCompound) {
		this.health = pCompound.getShort("Health");
		this.age = pCompound.getShort("Age");
		this.value = pCompound.getShort("Value");
		this.count = Math.max(pCompound.getInt("Count"), 1);
	}

	/**
	 * Called by a player entity when they collide with an entity
	 */
	public void playerTouch(Player pEntity) {
		if (!this.level.isClientSide) {
			int xiaoJingJie, zhenYuan;
			if ((zhenYuan = HmxyHelper.getZhenYuan(pEntity)) <= HmxyHelper
					.getNecessaryZhenYuan(xiaoJingJie = HmxyHelper.getXiaoJingJie(pEntity) + 1)
					&& !HmxyHelper.isTop(xiaoJingJie, zhenYuan)) {
				if (pEntity.takeXpDelay == 0) {
					pEntity.takeXpDelay = 2;
				}
				int rest=HmxyHelper.onGetZhenYuan(pEntity, this.value);
				if(rest<this.value) {
					--this.count;
					if (this.count == 0) {
						this.discard();
					}
					if(rest!=0) {
						 MinbusOrb.award((ServerLevel)this.level, this.position(), rest);
					}
				}
				this.level.playSound((Player) null, this.getX(), this.getY(), this.getZ(),
						SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 0.5F,
						this.level.getRandom().nextFloat() * 0.4F + 0.8F);
			}
		}
	}

	public int getValue() {
		return this.value;
	}

	public int getIcon() {
		if (this.value >= 2477) {
			return 10;
		} else if (this.value >= 1237) {
			return 9;
		} else if (this.value >= 617) {
			return 8;
		} else if (this.value >= 307) {
			return 7;
		} else if (this.value >= 149) {
			return 6;
		} else if (this.value >= 73) {
			return 5;
		} else if (this.value >= 37) {
			return 4;
		} else if (this.value >= 17) {
			return 3;
		} else if (this.value >= 7) {
			return 2;
		} else {
			return this.value >= 3 ? 1 : 0;
		}
	}

	public static int getExperienceValue(int pExpValue) {
		if (pExpValue >= 2477) {
			return 2477;
		} else if (pExpValue >= 1237) {
			return 1237;
		} else if (pExpValue >= 617) {
			return 617;
		} else if (pExpValue >= 307) {
			return 307;
		} else if (pExpValue >= 149) {
			return 149;
		} else if (pExpValue >= 73) {
			return 73;
		} else if (pExpValue >= 37) {
			return 37;
		} else if (pExpValue >= 17) {
			return 17;
		} else if (pExpValue >= 7) {
			return 7;
		} else {
			return pExpValue >= 3 ? 3 : 1;
		}
	}

	public boolean isAttackable() {
		return false;
	}

	@Override
	public Packet<?> getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	public SoundSource getSoundSource() {
		return SoundSource.AMBIENT;
	}
}
