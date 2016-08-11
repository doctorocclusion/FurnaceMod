package net.drocclusion.furnacemod;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = FurnaceMod.MODID, version = FurnaceMod.VERSION)
public class FurnaceMod
{
    public static final String MODID = "furnacemod";
    public static final String VERSION = "0.0-SNAPSHOT";

    @Mod.Instance(MODID)
    public static FurnaceMod instance;

    public Furnace furnace;

	private CommandSmelt commandSmelt;

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @EventHandler
    public void onServerPreStart(FMLServerAboutToStartEvent event)
    {
        if (furnace != null && !furnace.v8.isReleased()) furnace.release();
        furnace = new Furnace(event.getServer());
    }

    @EventHandler
    public void onServerStart(FMLServerStartingEvent event)
    {
        event.registerServerCommand(commandSmelt = new CommandSmelt(furnace));
    }

    @EventHandler
    public void onServerStopping(FMLServerStoppingEvent event)
    {
        furnace.release();
        furnace = null;
    }
}
