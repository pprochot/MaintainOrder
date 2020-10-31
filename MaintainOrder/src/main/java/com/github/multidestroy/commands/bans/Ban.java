package com.github.multidestroy.commands.bans;

import com.github.multidestroy.MainPluginClass;
import com.github.multidestroy.environment.TimeArgument;
import com.github.multidestroy.Utils;
import com.github.multidestroy.commands.assets.MaintainOrderCommand;
import com.github.multidestroy.commands.assets.CommandPermissions;
import com.github.multidestroy.environment.database.Database;
import com.github.multidestroy.exceptions.WrongArgumentException;
import com.github.multidestroy.i18n.Messages;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.SQLException;

public class Ban extends MaintainOrderCommand {

    private final Database database;

    public Ban(Database database, Messages messages) {
        super(messages,"ban", CommandPermissions.ban, 3);
        this.database = database;
    }

    protected Ban(String commandName, String permission, Database database, Messages messages) {
        super(messages, commandName, permission, 3);
        this.database = database;
    }

    @Override
    public final void start(ProxiedPlayer executor, String[] args) throws WrongArgumentException {
        String recipient = getPlayer(args[0]);
        String time = getPlayer(args[1]);
        String reason = getReason(args, 2);
        String serverName = getServerName(executor);

        //Get time argument
        TimeArgument timeArgument = new TimeArgument(time, true);

        MainPluginClass.plugin.getProxy().getScheduler().runAsync(
                MainPluginClass.plugin,
                () -> banPlayerAsync(executor, recipient, serverName, timeArgument, reason)
        );
    }

    @Override
    protected TextComponent createCorrectUsage() {
        return Utils.createHoverEvent(
                messages.getString("NORMAL.COMMAND.BAN.CORRECT_USAGE"),
                messages.getString("NORMAL.COMMAND.BAN.HOVER_EVENT"));
    }

    private void banPlayerAsync(ProxiedPlayer giver, String recipient, String serverName, TimeArgument time, String reason) {
        try {
            database.banPlayer(giver.getName(), recipient, serverName, time, reason);
        } catch (SQLException ex) {
            ex.getErrorCode();
        }
    }

    protected String getServerName(ProxiedPlayer player) {
        return player.getServer().getInfo().getName();
    }

}