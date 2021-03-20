package me.richtxo.command.lilTim;

import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
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
        EmbedBuilder embed = new EmbedBuilder();


        ctx.getEvent().getChannel().sendMessage(embed.build()).queue();
    }
}
