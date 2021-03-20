package me.richtxo.command.utility;

import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.JDA;

public class PingCommand extends Command {
    public PingCommand(){
        this.name = "ping";
        this.category = "Utility";
        this.help = "Shows current ping from me to Discord server";
    }

    @Override
    public void handle(CommandContext ctx) {
        JDA jda = ctx.getEvent().getJDA();

        jda.getRestPing().queue(
                (ping) -> ctx.getEvent().getChannel().sendMessageFormat("Gateway Ping: `%s ms`\n" +
                        "Discord API Ping `%d ms`", ping, jda.getGatewayPing()).queue()
        );
    }
}