package by.ts.hmxy.block.model;

import javax.annotation.Nullable;

import net.minecraft.client.resources.model.ModelState;
//Copy from Mcjty and do Some change.
public record ModelKey(boolean generating, boolean collecting, boolean actuallyGenerating, @Nullable ModelState modelState) { }