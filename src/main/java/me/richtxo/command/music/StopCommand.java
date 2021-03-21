package me.richtxo.command.music;

import me.richtxo.audio.GuildMusicManager;
import me.richtxo.audio.PlayerManager;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Objects;

public class StopCommand extends Command {
    public StopCommand(){
        this.name = "stop";
        this.category = "music";
        this.help = "Stops the music player and clears the queue";
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel();
        final GuildVoiceState memberVoiceState = Objects.requireNonNull(ctx.getEvent().getMember()).getVoiceState();
        if (!memberVoiceState.inVoiceChannel()){
            channel.sendMessage("You expect me to stop the music when you're not in the VC, " +
                    ctx.getEvent().getMember().getEffectiveName() + "?!").queue();
            return;
        }

        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx.getEvent().getGuild());

        musicManager.scheduler.getQueue().clear();
        musicManager.player.stopTrack();
        musicManager.player.setPaused(false);

        channel.sendMessage("Stopping the player and clearing the queue").queue();
    }
}
