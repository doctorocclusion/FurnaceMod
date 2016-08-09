package net.drocclusion.furnacemod;

import com.eclipsesource.v8.V8Value;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandSmelt extends CommandBase {
	public Furnace furnace;

	public CommandSmelt(Furnace furnace) {
		this.furnace = furnace;
	}

	@Override
	public String getCommandName() {
		return "smelt";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 2;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.smelt.usage";
	}

	@Override
	public void execute(MinecraftServer minecraftServer, ICommandSender sender, String[] strings) throws CommandException {
		Object result = null;
		try {
			furnace.context.printTarget = sender::addChatMessage;
			result = furnace.execute(String.join(" ", strings));
		} catch (Exception e) {
			e.printStackTrace();
			TextComponentString tc = new TextComponentString(e.getLocalizedMessage());
			Style s = new Style();
			s.setColor(TextFormatting.RED);
			sender.addChatMessage(tc.setStyle(s));
		}
		if (result != null) {
			boolean flag = true;
			if (result instanceof V8Value) flag &= !((V8Value) result).isUndefined();
			if (flag) sender.addChatMessage(new TextComponentString(String.valueOf(result)));
		}
		furnace.context.printTarget = null;
	}
}
