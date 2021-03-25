package me.richtxo.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.richtxo.audio.GuildMusicManager;
import me.richtxo.audio.PlayerManager;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.concurrent.TimeUnit;

public class NowPlaying extends Command {
    public NowPlaying(){
        this.name = "nowplaying";
        this.category = "music";
        this.help = "Shows the current playing track";
        this.aliases = new String[]{"np", "now", "n"};
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx.getEvent().getGuild());
        AudioPlayer player = musicManager.player;

        if (player.getPlayingTrack() == null) {
            channel.sendMessage("The player is not playing any song.").queue();
            return;
        }

        AudioTrackInfo info = player.getPlayingTrack().getInfo();

        EmbedBuilder builder = new EmbedBuilder()
            .setColor(ctx.getGuild().getSelfMember().getColor())
            .setFooter("Lil Tim by RichtXO", String.valueOf(ctx.getEvent().getJDA().getSelfUser().getAvatarUrl()))
            .setDescription(String.format(
                "**Playing** [%s](%s)\n\n%s `%s` - `%s`\n",
                info.title,
                info.uri,
                player.isPaused() ? "\u23F8" : "▶",
                formatTime(player.getPlayingTrack().getPosition()),
                formatTime(player.getPlayingTrack().getDuration())));


        channel.sendMessage(builder.build()).queue();
    }

    private String formatTime(long timeInMillis) {
        final long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        final long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1) - TimeUnit.HOURS.toMinutes(hours);
        final long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

}
