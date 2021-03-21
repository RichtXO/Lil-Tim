package me.richtxo.command.music;

import me.richtxo.audio.PlayerManager;
import me.richtxo.audio.TrackScheduler;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

public class RepeatCommand extends Command {
    public RepeatCommand(){
        this.name = "repeat";
        this.category = "music";
        this.help= "Repeat the current song that's playing";
        this.aliases = new String[]{"ron", "repeaton"};
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel();
        final GuildVoiceState memberVoiceState = Objects.requireNonNull(ctx.getEvent().getMember()).getVoiceState();
        if (!memberVoiceState.inVoiceChannel()){
            channel.sendMessage("You expect me to repeat a song wthat you're not even listening to, " +
                    ctx.getEvent().getMember().getEffectiveName() + "?!").queue();
            return;
        }

        TrackScheduler scheduler = PlayerManager.getInstance().getGuildMusicManager(ctx.getEvent().getGuild()).scheduler;
        if (scheduler.isRepeating()){
            ctx.getEvent().getChannel().sendMessage("I'm already repeating! You really must like this song! " +
                    "That's amazing!!!").queue();
            return;
        }

        scheduler.setRepeating(true);
        channel.sendMessage("This song must be amazing! I'm gonna repeat this song!").queue();
    }
}
