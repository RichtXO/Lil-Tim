package me.richtxo.command.lilTim;

import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.sharding.ShardManager;

public class AboutCommand extends Command {
    public AboutCommand (){
        this.name = "about";
        this.category = "Lil Tim";
        this.help = "Shows info about me!";
    }

    @Override
    public void handle(CommandContext ctx) {
        ShardManager sm = ctx.getGuild().getJDA().getShardManager();
        GuildMessageReceivedEvent event = ctx.getEvent();

        EmbedBuilder builder = new EmbedBuilder()
            .setTitle(event.getJDA().getSelfUser().getName())
            .setColor(0xad0000)
            .setThumbnail(String.valueOf(event.getJDA().getSelfUser().getAvatarUrl()))
            .setFooter("Lil Tim by RichtXO", String.valueOf(event.getJDA().getSelfUser().getAvatarUrl()))
            .setDescription("Hello, I'm **Lil Tim**, yet another Java Discord Bot!")
            .addField("Stats", sm.getShardsTotal()+ " Shards\n" + sm.getGuildCache().size() +
                    " Servers", true)
            .addField("", sm.getUserCache().size() + " Users\n" +
                    Math.round(sm.getAverageGatewayPing()) + "ms Avg Ping", true)
            .addField("", sm.getTextChannelCache().size() + " Text Channels\n" +
                    sm.getVoiceChannelCache().size() + " Voice Channels", true);

        ctx.getEvent().getChannel().sendMessage(builder.build()).queue();
    }
}
