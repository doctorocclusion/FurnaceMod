package net.drocclusion.furnacemod.jslib;


import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.V8ObjectUtils;
import net.drocclusion.furnacemod.Furnace;
import net.drocclusion.furnacemod.FurnaceUtils;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.Sys;

import java.util.IllegalFormatException;
import java.util.function.Consumer;

/**
 * Created by Sam Sartor on 8/6/2016.
 */
public class FurnaceContext {
	public Furnace f;
	public V8 v8;

	public MinecraftServer server;

	public EntityContexts entities;
	public MinecraftContext minecraft;

	public TypeAdapters adapters;

	public V8Object context;
	public Consumer<ITextComponent> printTarget;

	public FurnaceContext(Furnace furnace) {
		this.f = furnace;
		v8 = f.runtime;

		server = f.ms;

		//context = new V8Object(v8);
		context = v8;

		adapters = new TypeAdapters(this);
		entities = new EntityContexts(this);
		minecraft = new MinecraftContext(this);

		init();
	}

	public void init() {
		FurnaceUtils.PropBuilder cb = f.utils.beginProperties(context);
		cb.newMember("print").value((receiver, parameters) -> {
			if (printTarget == null) throw new Error("No target for print()");
			String format = parameters.getString(0);
			Object[] args = new Object[parameters.length() - 1];
			for (int i = 0; i < args.length; i++) {
				Object o = parameters.get(i + 1);
				if (o instanceof Number) o = ((Number) o).doubleValue();
				args[i] = o;
			}
			String out = null;
			try {
				out = String.format(format, args);
			} catch (IllegalFormatException e) {
				throw new Error("Illegal print format", e);
			}
			if (out != null) printTarget.accept(new TextComponentString(out));
		});
		cb.build();
	}

	public void release() {
		minecraft.release();
		entities.release();
		adapters.release();

		//context.release();
	}
}
