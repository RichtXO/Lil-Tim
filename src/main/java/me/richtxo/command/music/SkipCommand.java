package me.richtxo.command.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import me.richtxo.audio.GuildMusicManager;
import me.richtxo.audio.PlayerManager;
import me.richtxo.audio.TrackScheduler;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;

public class SkipCommand extends Command {
    public SkipCommand(){
        this.name = "skip";
        this.category = "music";
        this.help = "Skips current crappy song that no one likes\n" +
                "Can also remove songs in queue with their relative position!";
        this.aliases = new String[]{"s"};
    }

    @Override
    public void handle(CommandContext ctx) {
        TextChannel channel = ctx.getEvent().getChannel();
        final GuildVoiceState memberVoiceState = Objects.requireNonNull(ctx.getEvent().getMember()).getVoiceState();
        if (!memberVoiceState.inVoiceChannel()){
            channel.sendMessage("You expect me to skip a song when you're not in the VC, " +
                    ctx.getEvent().getMember().getEffectiveName() + "?!").queue();
            return;
        }

        List<String> args = ctx.getArgs();
        PlayerManager playerManager = PlayerManager.getInstance();
        GuildMusicManager musicManager = playerManager.getGuildMusicManager(ctx.getEvent().getGuild());
        AudioPlayer player = musicManager.player;
        TrackScheduler scheduler = musicManager.scheduler;

        if (player.getPlayingTrack() == null) {
            channel.sendMessage("The music player isn't playing jack, " +
                    ctx.getEvent().getMember().getEffectiveName()).queue();
            return;
        }

        if (args.isEmpty()){
            scheduler.nextTrack();
            channel.sendMessage("Skipping the current track").queue();
        }
        else{
            try{
                BlockingQueue<AudioTrack> queue = scheduler.getQueue();
                int numToRemove = Integer.parseInt(args.get(0)) - 1;
                if (numToRemove >= queue.size() || numToRemove < 0){
                    channel.sendMessage("Invalid number," + ctx.getEvent().getMember().getEffectiveName()
                            + "!").queue();
                    return;
                }
                Iterator<AudioTrack> it = queue.iterator();
                int current = 0;
                while (it.hasNext()){
                    if (current == numToRemove){
                        AudioTrack info = it.next();
                        String trackName = info.getInfo().title;
                        queue.remove(info);
                        channel.sendMessage(String.format("*Skipping* (%s)[%s]",
                                info.getInfo().title,
                                info.getInfo().uri)
                        ).queue();
                        return;
                    }
                    current++;
                }
            } catch (NumberFormatException nfe){
                channel.sendMessage("Invalid number, " + ctx.getEvent().getMember().getEffectiveName()
                        + "!").queue();
            }


        }
    }
}
