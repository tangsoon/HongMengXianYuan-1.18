package by.ts.hmxy.block.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import by.ts.hmxy.util.gene.DNA;
import net.minecraft.world.level.block.state.properties.Property;

public class DNAProperty extends Property<DNA>{

	protected DNAProperty(String pName) {
		super(pName, DNA.class);
	}

	@Override
	public Collection<DNA> getPossibleValues() {
		return null;
	}

	@Override
	public String getName(DNA dna) {
		return dna.toString();
	}

	@Override
	public Optional<DNA> getValue(String pValue) {
		return null;
	}

	public static DNAProperty crate(String name) {
		return new DNAProperty(name);
	}
}
