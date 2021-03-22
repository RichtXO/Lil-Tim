package me.richtxo.command.music;

import me.richtxo.audio.GuildMusicManager;
import me.richtxo.audio.PlayerManager;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

public class ResumeCommand extends Command {
    public ResumeCommand(){
        this.name = "resume";
        this.category = "music";
        this.help = "Resume playing those wonderful tracks!";
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel();
        final GuildVoiceState memberVoiceState = Objects.requireNonNull(ctx.getEvent().getMember()).getVoiceState();
        if (memberVoiceState == null)
            return;
        if (!memberVoiceState.inVoiceChannel()){
            channel.sendMessage("You expect me to resume when you're not in the VC, " +
                    ctx.getEvent().getMember().getEffectiveName() + "?!").queue();
            return;
        }


        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx.getEvent().getGuild());
        if (!musicManager.player.isPaused()){
            channel.sendMessage("Nani? You deaf or something? I'm already playing," +
                    Objects.requireNonNull(ctx.getEvent().getMember()).getEffectiveName() + "!").queue();
            return;
        }

        musicManager.player.setPaused(false);
        channel.sendMessage("Music is now resuming to play, " +
                Objects.requireNonNull(ctx.getEvent().getMember()).getEffectiveName() + "!").queue();
    }
}
