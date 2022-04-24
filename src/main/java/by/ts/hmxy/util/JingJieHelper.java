package by.ts.hmxy.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import by.ts.hmxy.HmxyMod;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
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
 *
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
		if(!living.getEntityData().getAll().stream().anyMatch(dataItem->{
			return dataItem.getAccessor()==dataAcce;
		})) {
			living.getEntityData().define(dataAcce, defaultValue);
		}
	}
	
	public static class DaJingJie{
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
	
	
	
	/**根据小境界计算大境界*/
	public static int getDaJingJieByXiao(int xiaoJingJie) {
		if(xiaoJingJie==0) {
			return xiaoJingJie;
		}
		return (xiaoJingJie-1)/4+1;
	}
}
