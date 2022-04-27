package by.ts.hmxy.data;

import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.world.entity.HmxyEntities;
import by.ts.hmxy.world.item.Grade;
import by.ts.hmxy.world.item.HmxyItems;
import by.ts.hmxy.world.item.ReikiStoneItem;
import by.ts.hmxy.world.item.Tabs;
import by.ts.hmxy.world.item.food.elixir.ElixirItem;
import by.ts.hmxy.world.item.level.block.HmxyBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.CreativeModeTab;

public class HmxyLanguageProvider extends LanguageProvider {

    public HmxyLanguageProvider(DataGenerator gen, String locale) {
        super(gen, HmxyMod.MOD_ID, locale);
    }

    @Override
    protected void addTranslations() {
    	this.add(Tabs.ELIXIR,"丹药(鸿蒙仙缘)");
    	this.add(Tabs.SUNDRY,"杂物(鸿蒙仙缘)");
    	this.add(Tabs.ORE,"矿石(鸿蒙仙缘)");
    	
    	
    	this.add(HmxyItems.NATURE_REIKI_STONE.get(), "天然灵石");
    	this.add(HmxyItems.LOW_GRADE_REIKI_STONE.get(), "下品灵石");
    	this.add(HmxyItems.MEDIUM_GRADE_REIKI_STONE.get(), "中品灵石");
    	this.add(HmxyItems.HIGH_GRADE_REIKI_STONE.get(), "上品灵石");
    	this.add(HmxyItems.TOP_GRADE_REIKI_STONE.get(), "极品灵石");
//    	this.add(HmxyItems.PREVIOUS_LIFE_STONE.get(), "往生石");
    	this.add(HmxyItems.PREVIOUS_LIFE_WATER_BUCKET.get(), "桶装往生泉");
    	
    	this.add(ElixirItem.GradeEnum.TIAN.grade, "天");
    	this.add(ElixirItem.GradeEnum.DI.grade, "地");
    	this.add(ElixirItem.GradeEnum.XUAN.grade, "玄");
    	this.add(ElixirItem.GradeEnum.HUANG.grade, "黄");
    	this.add(ElixirItem.GradeEnum.YU.grade, "宇");
    	this.add(ElixirItem.GradeEnum.ZHOU.grade, "宙");
    	this.add(ElixirItem.GradeEnum.HONG.grade, "洪");
    	this.add(ElixirItem.GradeEnum.FANG.grade, "荒");
    	this.add(ReikiStoneItem.GradeEnum.LOW_GRADE.grade, "下品");
    	this.add(ReikiStoneItem.GradeEnum.MEDIUM_GRADE.grade, "中品");
    	this.add(ReikiStoneItem.GradeEnum.HIGHT_GRADE.grade, "上品");
    	this.add(ReikiStoneItem.GradeEnum.TOP_GRADE.grade, "极品");
    	this.add(HmxyBlocks.REIKI_STONE_ORE.get(), "灵石矿");
    	this.add(HmxyBlocks.REIKI_STONE_ORE_FLICKER.get(), "闪耀灵石矿");
    	this.add(HmxyBlocks.PREVIOUS_LIFE_WATER.get(), "往生泉");
    	
    	this.addEntityType(HmxyEntities.MINBUS_ORB, "灵气");
    }
    
    public void add(Grade grade, String name) {
        add(grade.getName(), name);
    }
    
    public void add(CreativeModeTab tab, String name) {
        add("itemGroup."+tab.getRecipeFolderName(), name);
    }
}

