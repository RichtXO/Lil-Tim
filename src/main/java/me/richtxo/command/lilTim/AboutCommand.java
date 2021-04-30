package me.richtxo.command.lilTim;

import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class AboutCommand extends Command {
    public AboutCommand() {
        this.name = "about";
        this.category = "Lil Tim";
        this.help = "Shows info about me!";
        this.aliases = new String[]{"info", "aboutme"};
    }

    @Override
    public void handle(CommandContext ctx) {
        ShardManager sm = ctx.getGuild().getJDA().getShardManager();
        GuildMessageReceivedEvent event = ctx.getEvent();

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long uptime = runtimeMXBean.getUptime();
        long uptimeInSeconds = uptime / 1000;
        long days = uptimeInSeconds / 86400;
        long hours = (uptimeInSeconds / 3600) - (days * 24);
        long minutes = (uptimeInSeconds / 60) - (days * 1440) - (hours * 60);

        if (sm != null){
            EmbedBuilder builder = new EmbedBuilder()
                .setTitle(event.getJDA().getSelfUser().getName())
                .setColor(0xad0000)
                .setThumbnail(String.valueOf(event.getJDA().getSelfUser().getAvatarUrl()))
                .setFooter("Lil Tim by RichtXO", String.valueOf(event.getJDA().getSelfUser().getAvatarUrl()))
                .setDescription("Hello, I'm **Lil Tim**, yet another Java Music Discord Bot but with more features! " +
                        "Currently being hosted on a RPi 4!")
                .addField("Discord Library", "[`JDA`](https://github.com/DV8FromTheWorld/JDA)", true)
                .addField("Total Shards", String.valueOf(sm.getShardsTotal()), true)
                .addField("Servers", String.valueOf(sm.getGuildCache().size()), true)
                .addField("GitHub", "[`Lil Tim's Code`]" +
                        "(https://github.com/RichtXO/Lil-Tim-Discord-Bot)", true)
                .addField("Avg Ping (ms)", String.valueOf(Math.round(sm.getAverageGatewayPing())), true)
                .addField("Users", String.valueOf(sm.getUserCache().size()), true)
                .addField("Developer", "RichtXO#0000", true)
                .addField("Updated Since", String.format("`%s days, %s hours, %s minutes`", days, hours, minutes), true)
                .addBlankField(false)
                .addField("*Useful links:*",
                        "[Invite Me](https://discord.com/oauth2/authorize?client_id=736748544706478080&scope=bot)" ,
                        false);

            ctx.getEvent().getChannel().sendMessage(builder.build()).queue();
        }

    }
}
