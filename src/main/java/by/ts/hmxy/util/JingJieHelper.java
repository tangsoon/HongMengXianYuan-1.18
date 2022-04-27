package by.ts.hmxy.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import by.ts.hmxy.HmxyMod;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fmllegacy.ForgeI18n;

/**
 * 玩家境界相关的操作
 * @author tangsoon
 */
@EventBusSubscriber
public class JingJieHelper {
	/**真元*/
	private static final EntityDataAccessor<Integer> ZHEN_YUAN = SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
	/**小境界*/
	private static final EntityDataAccessor<Integer> XIAO_JING_JIE=SynchedEntityData.defineId(Player.class, EntityDataSerializers.INT);
	
	@SubscribeEvent
	public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if(event.getEntity() instanceof Player) {
			Player player=(Player) event.getEntity();
			JingJieHelper.registerDataIfNull(player,ZHEN_YUAN, Integer.valueOf(0));
			JingJieHelper.registerDataIfNull(player, XIAO_JING_JIE,Integer.valueOf(0));
		}
	}
	
	public static <T> void registerDataIfNull(LivingEntity living,EntityDataAccessor<T> dataAcce,T defaultValue) {
		boolean result=living.getEntityData().getAll().stream().anyMatch(dataItem->{
			return dataItem.getAccessor()==dataAcce;
		});
		if(!result) {
			living.getEntityData().define(dataAcce, defaultValue);
		}
	}
	
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
		int zhenYuan= JingJieHelper.getZhenYuan(entity);
		int xiaoJingJie=JingJieHelper.getXiaoJingJie(entity);
		int necessaryZhenYuan=JingJieHelper.getNecessaryZhenYuan(xiaoJingJie+1);
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
					xiaoJingJie=JingJieHelper.getXiaoJingJie(entity);
					necessaryZhenYuan=JingJieHelper.getNecessaryZhenYuan(xiaoJingJie+1);
				}
				else {
					//到达圆满
					break;
				}
			}
		}
		JingJieHelper.setZhenYuan(entity,zhenYuan);
		entity.getEntityData().set(XIAO_JING_JIE,xiaoJingJie);
		return value;
	}
	
	private static void onXiaoJingJieGrow(LivingEntity living) {
		//TODO 在这里执行属性成长
	}
	
	public static boolean isTop(int xiaoJingJie,int zhenYuan) {
		return xiaoJingJie%4==0&&JingJieHelper.getNecessaryZhenYuan(xiaoJingJie+1)==zhenYuan;
	}
}
