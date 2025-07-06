package mcjty.theoneprobe.commands;

import mcjty.theoneprobe.ClientForgeEventHandlers;
import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.config.Config;
import mcjty.theoneprobe.setup.GuiProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CommandTopCfg implements ICommand {


    private static Map<String, Consumer<String[]>> SUBCOMMANDS = new HashMap<>();

    static {
        SUBCOMMANDS.put("center", s -> Config.setPos(-1, -1, -1, -1));
        SUBCOMMANDS.put("topleft", s -> Config.setPos(5, 5, -1, -1));
        SUBCOMMANDS.put("topcenter", s -> Config.setPos(-1, 5, -1, -1));
        SUBCOMMANDS.put("topright", s -> Config.setPos(-1, 5, 5, -1));
        SUBCOMMANDS.put("bottomleft", s -> Config.setPos(5, -1, -1, 20));
        SUBCOMMANDS.put("bottomcenter", s -> Config.setPos(-1, -1, -1, 20));
        SUBCOMMANDS.put("bottomright", s -> Config.setPos(-1, -1, 5, 20));
        SUBCOMMANDS.put("centerleft", s -> Config.setPos(5, -1, -1, -1));
        SUBCOMMANDS.put("centerright", s -> Config.setPos(-1, -1, 5, -1));
        SUBCOMMANDS.put("transparent", s -> Config.setBoxStyle(0, 0, 0, 0));
        SUBCOMMANDS.put("setpos", CommandTopCfg::setPos);
        SUBCOMMANDS.put("opaque", s -> Config.setBoxStyle(2, 0xff999999, 0xff003366, 0));
        SUBCOMMANDS.put("default", s -> Config.setBoxStyle(2, 0xff999999, 0x55006699, 0));
        SUBCOMMANDS.put("liquids", s -> Config.setLiquids(true));
        SUBCOMMANDS.put("noliquids", s -> Config.setLiquids(false));
        SUBCOMMANDS.put("compactequalstacks", s -> Config.setCompactEqualStacks(true));
        SUBCOMMANDS.put("dontcompactequalstacks", s -> Config.setCompactEqualStacks(false));
        SUBCOMMANDS.put("extendedinmain", s -> Config.setExtendedInMain(true));
        SUBCOMMANDS.put("defaultinmain", s -> Config.setExtendedInMain(false));
        SUBCOMMANDS.put("reload", s -> Config.reload(Config.mainConfig));
    }

    private static void setPos(String[] args) {
        if (args.length != 5) {
            return;
        }
        try {
            int leftx = Integer.parseInt(args[1]);
            int topy = Integer.parseInt(args[2]);
            int rightx = Integer.parseInt(args[3]);
            int bottomy = Integer.parseInt(args[4]);
            Config.setPos(leftx, topy, rightx, bottomy);
        } catch (NumberFormatException e) {
        }
    }

    @Override
    public String getName() {
        return "topcfg";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        String args = StringUtils.join(SUBCOMMANDS.keySet(), " | ");
        return "topcfg [ " + args + " ]";
    }

    @Override
    public List<String> getAliases() {
        return Collections.emptyList();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 1) {
            ClientForgeEventHandlers.ignoreNextGuiClose = true;
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            player.openGui(TheOneProbe.instance, GuiProxy.GUI_CONFIG, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
            return;
        }
        String cmd = args[0];
        Consumer<String[]> consumer = SUBCOMMANDS.get(cmd);
        if (consumer == null) {
            ((EntityPlayer) sender).sendStatusMessage(new TextComponentString(TextFormatting.RED + "Unknown style option!"), false);
        } else {
            consumer.accept(args);
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }


    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
        return CommandBase.getListOfStringsMatchingLastWord(args, SUBCOMMANDS.keySet());
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    @Override
    public int compareTo(ICommand o) {
        return getName().compareTo(o.getName());
    }
}
