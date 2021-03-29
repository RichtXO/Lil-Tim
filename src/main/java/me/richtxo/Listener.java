package me.richtxo;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.richtxo.audio.GuildMusicManager;
import me.richtxo.audio.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceDeafenEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;


public class Listener extends ListenerAdapter{

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager;

    public Listener(EventWaiter waiter){
        manager = new CommandManager(waiter);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        User user = event.getAuthor();
        if (user.isBot() || event.isWebhookMessage()) {
           return;
        }

        if (event.getMessage().getContentRaw().startsWith(System.getenv("PREFIX"))){
            manager.handle(event);
        }
    }

    @Override
    public void onGuildVoiceLeave(@NotNull GuildVoiceLeaveEvent event) {
        // Ignore if bot event.
        if (event.getMember().getUser().isBot()) {
            return;
        }

        VoiceChannel channel = event.getGuild().getAudioManager().getConnectedChannel();

        // Ignore if not connected to any voice channel
        if (channel == null) {
            return;
        }
        // Ignore if the left/join event is from another voice channel.
        if (!Objects.requireNonNull(event.getChannelJoined()).getId().equals(channel.getId()) &&
                !event.getChannelLeft().getId().equals(channel.getId())) {
            return;
        }

        // If there aren't any users in voice channel, bot will leave
        if (!ifAnyMembers(channel)){
            GuildMusicManager manager = PlayerManager.getInstance().getGuildMusicManager(event.getGuild());
            manager.scheduler.getQueue().clear();
            manager.player.stopTrack();
            manager.player.setPaused(false);

            AudioManager audioManager = event.getGuild().getAudioManager();
            audioManager.closeAudioConnection();
        }
        super.onGuildVoiceLeave(event);
    }

    private boolean ifAnyMembers(VoiceChannel channel){
        for (Member member : channel.getMembers()){
            if (!member.getUser().isBot()){
                return true;
            }
        }
        return false;
    }
}
