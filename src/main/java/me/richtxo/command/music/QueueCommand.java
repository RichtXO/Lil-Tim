package me.richtxo.command.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import me.richtxo.audio.GuildMusicManager;
import me.richtxo.audio.PlayerManager;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class QueueCommand extends Command {
    public QueueCommand(){
        this.name = "queue";
        this.category = "music";
        this.help = "Shows queue from music player";
        this.aliases = new String[]{"q"};
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx.getEvent().getGuild());
        BlockingQueue<AudioTrack> queue = musicManager.scheduler.getQueue();

        if (queue.isEmpty()) {
            channel.sendMessage("The queue is empty").queue();
            return;
        }

        int trackCount = Math.min(queue.size(), 10);
        List<AudioTrack> tracks = new ArrayList<>(queue);
        EmbedBuilder builder = new EmbedBuilder()
                .setTitle("Top 10 Tracks in Current Queue (Total: " + queue.size() + ")")
                .setColor(ctx.getEvent().getGuild().getSelfMember().getColor());

        long totalMin = 0;
        long totalSec = 0;

        for (int i = 0; i < trackCount; i++) {
            AudioTrack track = tracks.get(i);
            AudioTrackInfo info = track.getInfo();

            builder.appendDescription(String.format(
                "`%d:` %s - %s - `%02d:%02d`\n",
                i + 1,
                info.title,
                info.author,
                (info.length / 1000) / 60,
                (info.length / 1000) % 60
            ));
        }

        for (int i = 0; i < queue.size(); i++){
            AudioTrack track = tracks.get(i);
            AudioTrackInfo info = track.getInfo();
            totalMin += (info.length / 1000) / 60;
            totalSec += (info.length / 1000) % 60;
        }

        long temp = totalSec / 60;
        totalMin += temp;

        long totalHour = totalMin / 60;
        totalMin = totalMin % 60;
        totalSec = totalSec % 60;

        builder.setFooter(String.format("Lil Tim by RichtXO -- Total Time Remaining = %02d:%02d:%02d",
                totalHour, totalMin, totalSec), String.valueOf(ctx.getEvent().getJDA().getSelfUser().getAvatarUrl()));
        channel.sendMessage(builder.build()).queue();
    }
}
