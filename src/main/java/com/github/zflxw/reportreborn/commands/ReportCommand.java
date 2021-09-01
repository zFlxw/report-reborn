package com.github.zflxw.reportreborn.commands;

import com.github.zflxw.reportreborn.ReportReborn;
import com.github.zflxw.reportreborn.reflections.commands.Command;
import com.github.zflxw.reportreborn.reflections.commands.CommandType;
import com.github.zflxw.reportreborn.reflections.commands.LoadCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

@LoadCommand
public class ReportCommand extends Command {
    private final Permission reportPermission = new Permission("reportreborn.report", PermissionDefault.OP);
    private final Permission helpPermission = new Permission("reportreborn.report.help", PermissionDefault.OP);
    private final Permission reloadPermission = new Permission("reportreborn.report.reload", PermissionDefault.OP);

    public ReportCommand() {
        super("report", new Permission("reportreborn.report", PermissionDefault.OP), CommandType.BOTH, "respo");
    }

    @Override
    public LiteralCommandNode<CommandListenerWrapper> createCommand(LiteralArgumentBuilder<CommandListenerWrapper> rootNodeBuilder) {
        LiteralCommandNode<CommandListenerWrapper> rootNode = rootNodeBuilder
            .executes(commandContext -> {
                commandContext.getSource().getBukkitSender().sendMessage(ReportReborn.getInstance().getTranslator().get("messages.report_command.invalid_command"));
                return 1;
            })
            .build();

        LiteralCommandNode<CommandListenerWrapper> helpNode = super.createLiteralNode("help", helpPermission)
            .executes(commandContext -> {
                try {
                    sendHelp(commandContext.getSource().getBukkitSender(), 1);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return 1;
            }).build();

        LiteralCommandNode<CommandListenerWrapper> reloadNode = super.createLiteralNode("reload", reloadPermission)
            .executes(commandContext -> {

                ReportReborn.getInstance().getConfiguration().reloadConfig();
                return 1;
            }).build();

        rootNode.addChild(helpNode);
        rootNode.addChild(reloadNode);

        return rootNode;
    }

    /**
     * send a help message for the command source. If the command sender is a player, a book with all commands will open
     * @param commandSender the command sender
     */
    private void sendHelp(CommandSender commandSender, int page) {
        commandSender.sendMessage("§8§m                          §r §7[ §9Page §a%o §8| §a%o §7] §8§m                          ".formatted(page, 1));

        if (super.hasPermission(commandSender, reportPermission)) {
            commandSender.sendMessage("§7- §c/report <player> <reason> §8| §7Report a player for a specific reason");
        }

        if (super.hasPermission(commandSender, reloadPermission)) {
            commandSender.sendMessage("§7- §c/report reload §8| §7Report a player for a specific reason");
        }

        commandSender.sendMessage("§8§m                          §r §7[ §9Page §a%o §8| §a%o §7] §8§m                          ".formatted(page, 1));
    }
}
