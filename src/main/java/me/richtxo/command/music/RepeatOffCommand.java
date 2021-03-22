package me.richtxo.command.music;

import me.richtxo.audio.PlayerManager;
import me.richtxo.audio.TrackScheduler;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

public class RepeatOffCommand extends Command {
    public RepeatOffCommand(){
        this.name = "repeatoff";
        this.category = "music";
        this.help= "Stops repeating the current song that's playing";
        this.aliases = new String[]{"roff"};
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel();
        final GuildVoiceState memberVoiceState = Objects.requireNonNull(ctx.getEvent().getMember()).getVoiceState();
        if (memberVoiceState == null)
            return;

        if (!memberVoiceState.inVoiceChannel()){
            channel.sendMessage("You expect me to stop repeating a song that you're not even listening to, " +
                    ctx.getEvent().getMember().getEffectiveName() + "?!").queue();
            return;
        }

        TrackScheduler scheduler = PlayerManager.getInstance().getGuildMusicManager(ctx.getEvent().getGuild()).scheduler;
        if (!scheduler.isRepeating()){
            ctx.getEvent().getChannel().sendMessage("This song must really suck... I'm already not repeating this" +
                    " mate.").queue();
            return;
        }

        scheduler.setRepeating(false);
        ctx.getEvent().getChannel().sendMessage("That's enough of this song ig... " +
                "Gonna stop repeating mate!").queue();
    }
}
