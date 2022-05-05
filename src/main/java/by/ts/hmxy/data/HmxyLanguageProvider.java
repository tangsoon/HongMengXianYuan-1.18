package by.ts.hmxy.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.entity.HmxyEntities;
import by.ts.hmxy.item.Grade;
import by.ts.hmxy.item.fulu.XunLingFuItem;
import by.ts.hmxy.util.TransMsg;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.registries.RegistryObject;

public class HmxyLanguageProvider extends LanguageProvider {

    public HmxyLanguageProvider(DataGenerator gen, String locale) {
        super(gen, HmxyMod.MOD_ID, locale);
    }

    public static final  Map<CreativeModeTab,String> TAB_NAMES=new HashMap<>();
    public static final  Map<RegistryObject<Item>,String> ITEM_NAMES=new HashMap<>();
    public static final Map<Grade,String> GRADE_NAMES=new HashMap<>();
    
    @Override
    protected void addTranslations() {
    	this.add(TAB_NAMES,this::add);
    	this.addRegistryObj(ITEM_NAMES, this::add);
    	this.add(GRADE_NAMES,this::add);
    
//    	//---------------------------------品质---------------------------------
//    	this.add(ElixirItem.GradeEnum.TIAN.grade, "天");
//    	this.add(ElixirItem.GradeEnum.DI.grade, "地");
//    	this.add(ElixirItem.GradeEnum.XUAN.grade, "玄");
//    	this.add(ElixirItem.GradeEnum.HUANG.grade, "黄");
//    	this.add(ElixirItem.GradeEnum.YU.grade, "宇");
//    	this.add(ElixirItem.GradeEnum.ZHOU.grade, "宙");
//    	this.add(ElixirItem.GradeEnum.HONG.grade, "洪");
//    	this.add(ElixirItem.GradeEnum.FANG.grade, "荒");
//    	this.add(ReikiStoneItem.GradeEnum.LOW_GRADE.grade, "下品");
//    	this.add(ReikiStoneItem.GradeEnum.MEDIUM_GRADE.grade, "中品");
//    	this.add(ReikiStoneItem.GradeEnum.HIGHT_GRADE.grade, "上品");
//    	this.add(ReikiStoneItem.GradeEnum.TOP_GRADE.grade, "极品");

    	
    	//---------------------------------实体---------------------------------
    	this.addEntityType(HmxyEntities.MINBUS_ORB, "灵气");
    	//---------------------------------------------------------------------   	
    	
    	//---------------------------------消息---------------------------------
    	this.add(XunLingFuItem.MSG,"区块 (%d,%d) 灵气: %.2f");
    	//---------------------------------------------------------------------  
    }
    
    public void add(Grade grade, String name) {
        add(grade.getName(), name);
    }
    
    public void add(CreativeModeTab tab, String name) {
        add("itemGroup."+tab.getRecipeFolderName(), name);
    }
    
    public void add(ConfigValue<?> config, String name) {
    	List<String> paths=config.getPath();
        add("configured.gui."+paths.get(paths.size()-1), name);
    }
    
    public void add(TransMsg msg,String str) {
    	this.add(msg.getKey(),str);
    }
    
//    public void add(TransMsg msg,String str) {
//    	this.add(msg.getKey(),str);
//    }
    
    public <T> void add(Map<T,String> map,BiConsumer<T, String> con) {
    	for(Map.Entry<T, String> entry: map.entrySet()) {
    		con.accept(entry.getKey(), entry.getValue());
    	}
    	map.clear();
    }
    
    public <T> void addRegistryObj(Map<RegistryObject<T>,String> map,BiConsumer<T, String> con) {
    	for(Map.Entry<RegistryObject<T>, String> entry:map.entrySet()) {
    		con.accept(entry.getKey().get(), entry.getValue());
    	}
    	map.clear();
    }
}

