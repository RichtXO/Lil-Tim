package me.richtxo.command.music;

import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Objects;

public class LeaveCommand extends Command {
    public LeaveCommand(){
        this.name = "leave";
        this.category = "music";
        this.help = "Leave the current voice channel";
        this.aliases = new String[]{"l"};
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel();
        AudioManager audioManager = ctx.getEvent().getGuild().getAudioManager();

        if (!audioManager.isConnected()) {
            channel.sendMessage("I already left... Stop making me feel unwanted, " +
                    ctx.getEvent().getMember().getEffectiveName() + ".").queue();
            return;
        }

        VoiceChannel voiceChannel = audioManager.getConnectedChannel();
        if (Objects.requireNonNull(ctx.getEvent().getMember()).getUser().getId().equals(System.getenv("OWNER_ID"))){
            audioManager.closeAudioConnection();
            assert voiceChannel != null;
            channel.sendMessageFormat("Disconnected from `\uD83d\uDD0A %s`", voiceChannel.getName()).queue();
            return;
        }

        assert voiceChannel != null;
        if (!voiceChannel.getMembers().contains(ctx.getEvent().getMember())) {
            channel.sendMessage("You have to be in the same voice channel as me to kick me, " +
                    ctx.getEvent().getMember().getEffectiveName() + "!").queue();
            return;
        }

        audioManager.closeAudioConnection();
        channel.sendMessageFormat("Disconnected from `\uD83d\uDD0A %s`", voiceChannel.getName()).queue();
    }
}
