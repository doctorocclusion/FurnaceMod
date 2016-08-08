package net.drocclusion.furnacemod;

import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = ExampleMod.MODID, version = ExampleMod.VERSION)
public class ExampleMod
{
    public static final String MODID = "furnacemod";
    public static final String VERSION = "0.0-SNAPSHOT";
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
}
