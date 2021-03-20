package me.richtxo.command.utility;

import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class UptimeCommand extends Command {
    public UptimeCommand(){
        this.name = "uptime";
        this.category = "Utility";
        this.help = "Shows current uptime of me";
    }

    @Override
    public void handle(CommandContext ctx) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeMXBean.getUptime();

        long uptimeInSeconds = uptime / 1000;
        long numDays = uptimeInSeconds / 86400;
        long numHours = (uptimeInSeconds / 3600) - (numDays * 24);
        long numMinutes = (uptimeInSeconds / 60) - (numDays * 1440) - (numHours * 60);

        ctx.getEvent().getChannel().sendMessageFormat(
                "Been up for `%s days, %s hours, %s minutes`",
                numDays, numHours, numMinutes
        ).queue();
    }
}
