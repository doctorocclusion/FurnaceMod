package net.drocclusion.furnacemod.jslib;


import net.drocclusion.furnacemod.Furnace;
import net.minecraft.server.MinecraftServer;

/**
 * Created by Sam Sartor on 8/6/2016.
 */
public class FurnaceContext {
	public Furnace furnace;

	public MinecraftServer server;

	public Entities entities;
	public FurnMCUtils furnMCUtils;

	public FurnaceContext(Furnace furnace) {
		this.furnace = furnace;

		furnMCUtils = new FurnMCUtils(this);
		entities = new Entities(this);
	}
}
