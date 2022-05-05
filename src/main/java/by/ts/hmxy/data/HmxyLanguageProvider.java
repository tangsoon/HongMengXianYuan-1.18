package by.ts.hmxy.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import by.ts.hmxy.HmxyMod;
import by.ts.hmxy.item.Grade;
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
    public static final Map<TransMsg,String> MSG_TEXT=new HashMap<>();
    public static final Map<String,String> ENTITY_NAME=new HashMap<>();
    
    @Override
    protected void addTranslations() {
    	this.add(TAB_NAMES,this::add);
    	this.addRegistryObj(ITEM_NAMES, this::add);
    	this.add(GRADE_NAMES,this::add);
    	this.add(MSG_TEXT, this::add);
    	this.add(ENTITY_NAME, this::add);
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

