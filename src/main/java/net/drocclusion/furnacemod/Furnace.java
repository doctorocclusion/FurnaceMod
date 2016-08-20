package net.drocclusion.furnacemod;

import com.eclipsesource.v8.*;
import net.drocclusion.furnacemod.adapters.AdapterManager;
import net.drocclusion.furnacemod.adapters.Adapters;
import net.drocclusion.furnacemod.adapters.converters.Converter;
import net.drocclusion.furnacemod.api.EntityConnector;
import net.drocclusion.furnacemod.api.UUIDConverter;
import net.drocclusion.furnacemod.components.Component;
import net.drocclusion.furnacemod.utils.FurnaceUtils;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

/**
 * Created by Sam Sartor on 8/6/2016.
 */
public class Furnace {
	public V8 v8;
	public FurnaceUtils utils;
	public Component lib;
	public Adapters adapters;
	public MinecraftServer server;

	public Furnace(MinecraftServer server) {
		this.server = server;
		v8 = V8.createV8Runtime();
		utils = new FurnaceUtils(this);
		lib = new Component(this);

		AdapterManager.build(this);
	}


	public void addAdapters() {
		AdapterManager.addAdapter("minecraft.uuid", a -> {
			return new UUIDConverter();
		});

		AdapterManager.addAdapter("minecraft.entity", a -> {
			return new EntityConnector(server::getEntityFromUuid, this, a.getAdapter("minecraft.uuid"));
		}, "minecraft.uuid");
	}

	public void addComponents() {

	}

	public void release() {
		utils.release();
		lib.releaseDown();
		v8.release();
	}
}

