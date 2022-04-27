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
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import by.ts.hmxy.HmxyMod;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fmllegacy.ForgeI18n;

/**
 * 玩家境界相关的操作
 * @author tangsoon
 */
@EventBusSubscriber
public class HmxyHelper {
	/**真元*/
	private static final EntityDataAccessor<Integer> ZHEN_YUAN = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
	/**小境界*/
	private static final EntityDataAccessor<Integer> XIAO_JING_JIE=SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.INT);
	/**灵力*/
	private static final EntityDataAccessor<Float> 灵力=SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.FLOAT);
	/**灵力上限*/
	public static final Attribute MAX_LING_LI = new RangedAttribute("attribute.name.generic.max_ling_li", 20.0D, 1.0D, 1024.0D).setSyncable(true);
	/**灵力上限修饰符*/
	public static final UUID uuid=UUID.fromString("da63a858-86bb-4097-878e-6f9fca0fe19c");
	@SubscribeEvent
	public static void onEntityConstructing(EntityConstructing event) {
		Entity entity=event.getEntity();
		if(entity instanceof Player) {
			Player player=(Player) entity;
			player.getEntityData().define(ZHEN_YUAN, Integer.valueOf(0));
			player.getEntityData().define(XIAO_JING_JIE, Integer.valueOf(0));
			player.getEntityData().define(灵力, Float.valueOf(0.0F));
//			HmxyHelper.registerDataIfNull(player,ZHEN_YUAN, Integer.valueOf(0));
//			HmxyHelper.registerDataIfNull(player, XIAO_JING_JIE,Integer.valueOf(0));
		}
	}
	
	@SubscribeEvent
	public static void onEntityAttributeCreate(EntityAttributeCreationEvent event) {
		event.put(EntityType.PLAYER,LivingEntity.createLivingAttributes().add(MAX_LING_LI).build());
	}
	
//	public static <T> void registerDataIfNull(LivingEntity living,EntityDataAccessor<T> dataAcce,T defaultValue) {
//		boolean result=living.getEntityData().getAll().stream().anyMatch(dataItem->{
//			return dataItem.getAccessor()==dataAcce;
//		});
//		if(!result) {
//			living.getEntityData().define(dataAcce, defaultValue);
//		}
//	}
	
	public static class DaJingJie implements Comparable<DaJingJie>{
		private int index;
		private String name;
		
		DaJingJie(int index, String name) {
			super();
			this.index = index;
			this.name = name;
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
			return this.index>o.index?1:-1;
		}
	}
	
	/**境界*/
	public static List<DaJingJie> JingJies=new ArrayList<DaJingJie>();
	/**初始化境界*/
	public static void initJingJies() {
		
		File file=new File("config\\"+HmxyMod.MOD_ID+"_da_jing_jie.json");
        if(file.exists()&&!file.isDirectory()) {
        	try {
				FileReader fr=new FileReader(file);
				try (JsonReader jr = new JsonReader(fr)) {
					jr.beginArray();
					while(jr.hasNext()) {
						jr.beginObject();
						int index=-1;
						String jingName=null;
						if("index".equals(jr.nextName())) {
							index=jr.nextInt();
						}
						if("name".equals(jr.nextName())) {
							jingName=jr.nextString();
						}
						if(index!=-1&&jingName!=null) {
							DaJingJie daJingJie=new DaJingJie(index,jingName);	
							JingJies.add(daJingJie);
						}
						else {
							throw new Exception(HmxyMod.MOD_ID+"_da_jing_jie.json错误!!!");
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
        }
        else {
        	JingJies.add(new DaJingJie(0, "da_jing_jie.fan_ti.name"));
        	JingJies.add(new DaJingJie(1, "da_jing_jie.lian_qi.name"));
        	JingJies.add(new DaJingJie(2, "da_jing_jie.zhu_ji.name"));
        	JingJies.add(new DaJingJie(3, "da_jing_jie.jie_dan.name"));
        	JingJies.add(new DaJingJie(4, "da_jing_jie.jin_dan.name"));
        	JingJies.add(new DaJingJie(5, "da_jing_jie.yuan_ying.name"));
        	JingJies.add(new DaJingJie(6, "da_jing_jie.hua_shen.name"));
        	JingJies.add(new DaJingJie(7, "da_jing_jie.lian_xu.name"));
        	JingJies.add(new DaJingJie(8, "da_jing_jie.he_ti.name"));
        	JingJies.add(new DaJingJie(9, "da_jing_jie.da_cheng.name"));
        	Gson gson=new Gson();
        	String str=gson.toJson(JingJies);
        	try {
				FileWriter fw=new FileWriter(file);
				fw.write(str);
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
	
	public static void initPlayerData() {
		
	}
	
	
	/**根据小境界计算大境界*/
	public static int getDaJingJieByXiao(int xiaoJingJie) {
		if(xiaoJingJie==0) {
			return xiaoJingJie;
		}
		return (xiaoJingJie-1)/4+1;
	}
	
	public static Integer getZhenYuan(LivingEntity living) {
		return living.getEntityData().get(ZHEN_YUAN);
	}
	
	public static void setZhenYuan(LivingEntity living,Integer zhenYuan){
		living.getEntityData().set(ZHEN_YUAN, zhenYuan);
	}
	
	public static int getNecessaryZhenYuan(int nextXiaoJingJie) {
		 return (1*(1+nextXiaoJingJie)*nextXiaoJingJie/2+20)*30*nextXiaoJingJie;
	}
	
	public static int getXiaoJingJie(LivingEntity entity) {
		return entity.getEntityData().get(XIAO_JING_JIE);
	}
	
	public static int onGetZhenYuan(LivingEntity entity, int value) {
		int zhenYuan= HmxyHelper.getZhenYuan(entity);
		int xiaoJingJie=HmxyHelper.getXiaoJingJie(entity);
		int necessaryZhenYuan=HmxyHelper.getNecessaryZhenYuan(xiaoJingJie+1);
		while(value>0) {
			int capacity=necessaryZhenYuan-zhenYuan;
			int consume=Math.min(value, capacity);
			value-=consume;
			zhenYuan+=consume;
			if(zhenYuan==necessaryZhenYuan) {
				if(xiaoJingJie==0||xiaoJingJie%4!=0) {
					zhenYuan=0;
					xiaoJingJie+=1;
					onXiaoJingJieGrow(entity);
					entity.level.playSound((Player) null, entity.getX(), entity.getY(), entity.getZ(),
							SoundEvents.PLAYER_LEVELUP, SoundSource.NEUTRAL, 0.5F,
							entity.level.getRandom().nextFloat() * 0.4F + 0.6F);
					xiaoJingJie=HmxyHelper.getXiaoJingJie(entity);
					necessaryZhenYuan=HmxyHelper.getNecessaryZhenYuan(xiaoJingJie+1);
				}
				else {
					//到达圆满
					break;
				}
			}
		}
		HmxyHelper.setZhenYuan(entity,zhenYuan);
		entity.getEntityData().set(XIAO_JING_JIE,xiaoJingJie);
		return value;
	}
	
	private static void onXiaoJingJieGrow(LivingEntity living) {
		//TODO 在这里执行属性成长
	}
	
	public static boolean isTop(int xiaoJingJie,int zhenYuan) {
		return xiaoJingJie%4==0&&HmxyHelper.getNecessaryZhenYuan(xiaoJingJie+1)==zhenYuan;
	}
	/**获取灵力*/
	public static float getLingLi(LivingEntity entity) {
		return entity.getEntityData().get(灵力);
	}
	/**设置灵力*/
	public static void setLingLi(LivingEntity entity) {
		entity.getEntityData().get(灵力);
	}
	/**设置最大灵力*/
	public static void setMaxLingLi(LivingEntity living,double value) {
		changeAttr(living, MAX_LING_LI, uuid, "更改最大灵力", value, AttributeModifier.Operation.ADDITION);
	}
	
	public static double getMaxLingLi(LivingEntity living, Attribute attr) {
		return living.getAttributeValue(attr);
	}
	
	public static void changeAttr(LivingEntity living,Attribute attr,UUID uuid,String name,double value,AttributeModifier.Operation op) {
		AttributeInstance attrIns= living.getAttribute(attr);
		AttributeModifier modifer=new AttributeModifier(uuid,name,value,op);
		if(attrIns.hasModifier(modifer)) {
			attrIns.removeModifier(uuid);
		}
		attrIns.addPermanentModifier(modifer);
	}
}
