package me.richtxo;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Listener extends ListenerAdapter{

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager;
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

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
        if (user.isBot() || event.isWebhookMessage())
           return;

        if (event.getMessage().getContentRaw().startsWith(System.getenv("PREFIX"))) {
            try {
                manager.handle(event);
            } catch (ParseException | IOException | SpotifyWebApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        AudioManager manager;

        try {
            manager = event.getChannelJoined().getGuild().getAudioManager();
        } catch (NullPointerException e){
            manager = event.getChannelLeft().getGuild().getAudioManager();
        }

        // Cancel if bot isn't connected in the first place
        if (!manager.isConnected())
            return;

        if (event.getOldValue() != manager.getConnectedChannel())
            return;

        // If bot is the only one in voice channel
        if (manager.getConnectedChannel().getMembers().size() == 1){
            AudioManager finalManager = manager;
            executor.schedule(() -> {
                finalManager.closeAudioConnection();
                // TODO: Need to find a way to output message to Discord :/
//                System.out.printf("I have left `\uD83D\uDD0A %s` due to inactivity!%n", finalManager.getConnectedChannel().getName());
//                event.getOldValue().getGuild().getDefaultChannel().sendMessageFormat(
//                        "I have left `\uD83d\uDD0A %s` due to inactivity!", finalManager.getConnectedChannel().getName()).queue();
            }, 10, TimeUnit.SECONDS);
        }
    }
}
