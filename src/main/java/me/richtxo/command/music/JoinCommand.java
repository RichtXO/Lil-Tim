package me.richtxo.command.music;

import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinCommand extends Command {
    public JoinCommand(){
        this.name = "join";
        this.category = "music";
        this.help = "Joins the current voice channel";
        this.aliases = new String[]{"j"};
    }

    @Override
    public void handle(CommandContext ctx) {
        final TextChannel channel = ctx.getEvent().getChannel();
        final Member self = ctx.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();

        assert selfVoiceState != null;
        if (selfVoiceState.inVoiceChannel()){
            channel.sendMessageFormat("I'm already in `\uD83d\uDD0A %s`", selfVoiceState.getChannel().getName()).queue();
            return;
        }

        final Member member = ctx.getEvent().getMember();
        assert member != null;
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        assert memberVoiceState != null;
        if (!memberVoiceState.inVoiceChannel()){
            channel.sendMessage("You need to be in a voice channel!").queue();
            return;
        }

        final AudioManager audioManager = ctx.getGuild().getAudioManager();
        final VoiceChannel memberChannel = memberVoiceState.getChannel();

        if (!self.hasPermission(Permission.VOICE_CONNECT)){
            channel.sendMessage("I don't have perms to speak!").queue();
            return;
        }

        audioManager.openAudioConnection(memberChannel);
        channel.sendMessageFormat("Connecting to `\uD83d\uDD0A %s`", memberChannel.getName()).queue();
    }
}
