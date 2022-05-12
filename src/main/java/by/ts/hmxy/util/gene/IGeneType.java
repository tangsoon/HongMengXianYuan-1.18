package by.ts.hmxy.util.gene;

import net.minecraft.world.level.block.state.properties.Property;

interface IGeneType<T> {
	String getTypeName();
	Class<T> valueType();
	Property<?> getProperty();
}