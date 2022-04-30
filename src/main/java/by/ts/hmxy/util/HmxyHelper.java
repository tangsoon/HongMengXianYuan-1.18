package by.ts.hmxy.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import by.ts.hmxy.HmxyMod;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeI18n;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

/**
 * 玩家境界相关的操作
 * 
 * @author tangsoon
 */
@EventBusSubscriber(bus = Bus.MOD, modid = HmxyMod.MOD_ID)
public class HmxyHelper {
	// ------------------------------拥有境界的生物的属性------------------------------
	/** 真元 */
	public static EntityDataAccessor<Integer> ZHEN_YUAN;
	/** 小境界 */
	public static EntityDataAccessor<Integer> XIAO_JING_JIE;
	/** 灵力 */
	public static EntityDataAccessor<Float> 灵力;
	/** 耐力 */
	public static EntityDataAccessor<Float> STAMINA;
	// ------------------------------------------------------------------------------

	/** 灵力上限修饰符 */
	public static final UUID MAX_LING_LI_UUID = UUID.fromString("da63a858-86bb-4097-878e-6f9fca0fe19c");

	private static final Logger LOGGER = LogManager.getLogger();

	@SubscribeEvent
	public static void onEntityAttributeCreate(EntityAttributeModificationEvent event) {
		addAttributes(event, EntityType.PLAYER, Attrs.MAX_LING_LI.get(), Attrs.LING_LI_RESUME.get(),
				Attrs.STAMINA_RESUME.get(), Attrs.MAX_STAMINA.get(), Attrs.STAMINA_CONSUME.get());
	}

	/** 一个辅助方法用来添加属性 */
	public static void addAttributes(EntityAttributeModificationEvent event,
			EntityType<? extends LivingEntity> entityType, Attribute... attributes) {
		for (Attribute attr : attributes) {
			event.add(entityType, attr);
		}
	}

	public static class DaJingJie implements Comparable<DaJingJie> {
		private int index;
		private String name;
		private String zhName;

		DaJingJie(int index, String name, String zhName) {
			super();
			this.index = index;
			this.name = name;
			this.zhName = zhName;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@OnlyIn(Dist.CLIENT)
		public String getLocalName() {
			return ForgeI18n.getPattern(name);
		}

		@Override
		public int compareTo(DaJingJie o) {
			return this.index > o.index ? 1 : -1;
		}

		public String getZhName() {
			return this.zhName;
		}
	}

	/** 境界 */
	public static List<DaJingJie> JingJies = new ArrayList<DaJingJie>();

	/** 初始化境界 */
	public static void initJingJies() {
		LOGGER.info("开始读取大境界。。。");
		File file = new File("config\\" + HmxyMod.MOD_ID + "_da_jing_jie.json");
		if (file.exists() && !file.isDirectory()) {
			try {
				FileReader fr = new FileReader(file);
				try (JsonReader jr = new JsonReader(fr)) {
					jr.beginArray();
					while (jr.hasNext()) {
						jr.beginObject();
						int index = -1;
						String jingName = null;
						String nameZh = null;
						if ("index".equals(jr.nextName())) {
							index = jr.nextInt();
						}
						if ("name".equals(jr.nextName())) {
							jingName = jr.nextString();
						}
						if ("zhName".equals(jr.nextName())) {
							nameZh = jr.nextString();
						}
						if (index != -1 && jingName != null) {
							DaJingJie daJingJie = new DaJingJie(index, jingName, nameZh);
							JingJies.add(daJingJie);
						} else {
							throw new Exception(HmxyMod.MOD_ID + "_da_jing_jie.json错误!!!");
						}
						jr.endObject();
					}
					jr.endArray();
					jr.close();
				}
				fr.close();
				Collections.sort(JingJies);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			JingJies.add(new DaJingJie(0, "da_jing_jie.fan_ti.name", "凡体"));
			JingJies.add(new DaJingJie(1, "da_jing_jie.lian_qi.name", "练气"));
			JingJies.add(new DaJingJie(2, "da_jing_jie.zhu_ji.name", "筑基"));
			JingJies.add(new DaJingJie(3, "da_jing_jie.jie_dan.name", "结丹"));
			JingJies.add(new DaJingJie(4, "da_jing_jie.jin_dan.name", "金丹"));
			JingJies.add(new DaJingJie(5, "da_jing_jie.yuan_ying.name", "元婴"));
			JingJies.add(new DaJingJie(6, "da_jing_jie.hua_shen.name", "化神"));
			JingJies.add(new DaJingJie(7, "da_jing_jie.lian_xu.name", "炼虚"));
			JingJies.add(new DaJingJie(8, "da_jing_jie.he_ti.name", "合体"));
			JingJies.add(new DaJingJie(9, "da_jing_jie.da_cheng.name", "大乘"));
			Gson gson = new Gson();
			String str = gson.toJson(JingJies);
			try {
				FileWriter fw = new FileWriter(file);
				fw.write(str);
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		LOGGER.info("开始读取大境界完成");
	}

	public static void initPlayerData() {

	}

	/** 根据小境界计算大境界 */
	public static int getDaJingJieByXiao(int xiaoJingJie) {
		if (xiaoJingJie == 0) {
			return xiaoJingJie;
		}
		return (xiaoJingJie - 1) / 4 + 1;
	}

	public static Integer getZhenYuan(LivingEntity living) {
		return living.getEntityData().get(ZHEN_YUAN);
	}

	public static void setZhenYuan(LivingEntity living, Integer zhenYuan) {
		living.getEntityData().set(ZHEN_YUAN, zhenYuan);
	}

	public static float getStamina(LivingEntity living) {
		return living.getEntityData().get(STAMINA);
	}

	public static void setStamina(LivingEntity living, float stamina) {
		living.getEntityData().set(STAMINA, stamina);
	}

	public static int getNecessaryZhenYuan(int nextXiaoJingJie) {
		return (1 * (1 + nextXiaoJingJie) * nextXiaoJingJie / 2 + 20) * 30 * nextXiaoJingJie;
	}

	public static int getXiaoJingJie(LivingEntity entity) {
		return entity.getEntityData().get(XIAO_JING_JIE);
	}

	public static void setXiaoJingJie(LivingEntity entity, int xiaoJingJie) {
		entity.getEntityData().set(XIAO_JING_JIE, xiaoJingJie);
	}

	public static int onGetZhenYuan(LivingEntity entity, int value) {
		int zhenYuan = HmxyHelper.getZhenYuan(entity);
		int xiaoJingJie = HmxyHelper.getXiaoJingJie(entity);
		int necessaryZhenYuan = HmxyHelper.getNecessaryZhenYuan(xiaoJingJie + 1);
		while (value > 0) {
			int capacity = necessaryZhenYuan - zhenYuan;
			int consume = Math.min(value, capacity);
			value -= consume;
			zhenYuan += consume;
			if (zhenYuan == necessaryZhenYuan) {
				if (xiaoJingJie == 0 || xiaoJingJie % 4 != 0) {
					zhenYuan = 0;
					xiaoJingJie += 1;
					onXiaoJingJieGrow(entity);
					entity.level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(),
							SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.5F,
							entity.level.getRandom().nextFloat() * 0.4F + 0.6F);
					xiaoJingJie = HmxyHelper.getXiaoJingJie(entity);
					necessaryZhenYuan = HmxyHelper.getNecessaryZhenYuan(xiaoJingJie + 1);
				} else {
					// 到达圆满
					break;
				}
			}
		}
		HmxyHelper.setZhenYuan(entity, zhenYuan);
		entity.getEntityData().set(XIAO_JING_JIE, xiaoJingJie);
		return value;
	}

	private static void onXiaoJingJieGrow(LivingEntity living) {
		// TODO 在这里执行属性成长
	}

	public static boolean isTop(int xiaoJingJie, int zhenYuan) {
		return xiaoJingJie % 4 == 0 && HmxyHelper.getNecessaryZhenYuan(xiaoJingJie + 1) == zhenYuan;
	}

	/** 获取灵力 */
	public static float getLingLi(LivingEntity entity) {
		return entity.getEntityData().get(灵力);
	}

	/** 设置灵力 */
	public static void setLingLi(LivingEntity entity, float lingLi) {
		entity.getEntityData().set(灵力, lingLi);
	}

	public static double getMaxLingLi(LivingEntity living) {
		return living.getAttributeValue(Attrs.MAX_LING_LI.get());
	}

	public static double getStaminaConsume(LivingEntity living) {
		return living.getAttributeValue(Attrs.STAMINA_CONSUME.get());
	}

	public static double getStaminaResume(LivingEntity living) {
		return living.getAttributeValue(Attrs.STAMINA_RESUME.get());
	}
	
	public static double getMaxStamina(LivingEntity living) {
		return living.getAttributeValue(Attrs.MAX_STAMINA.get());
	}

	private static final List<Predicate<LivingEntity>> IS_CONSUMMING_STAMINA = new ArrayList<>();

	static {
		IS_CONSUMMING_STAMINA.add(living -> {
			return living.isSwimming();
		});
		IS_CONSUMMING_STAMINA.add(living -> {
			return living.isSprinting();
		});
		IS_CONSUMMING_STAMINA.add(living -> {
			return living.isFallFlying();
		});
	}

	public static boolean isConsummingStamina(LivingEntity living) {
		if (IS_CONSUMMING_STAMINA.stream().anyMatch(p -> {
			return p.test(living);
		})) {
			return true;
		}
		return false;
	}
}
