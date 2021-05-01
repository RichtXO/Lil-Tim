package me.richtxo;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.richtxo.audio.GuildMusicManager;
import me.richtxo.audio.PlayerManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Listener extends ListenerAdapter{

    private static final Logger LOGGER = LoggerFactory.getLogger(Listener.class);
    private final CommandManager manager;
    private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

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

        if (event.getMessage().getContentRaw().startsWith(System.getenv("PREFIX")))
            manager.handle(event);
    }

    @Override
    public void onGuildVoiceUpdate(@NotNull GuildVoiceUpdateEvent event) {
        AudioManager manager = event.getOldValue().getGuild().getAudioManager();
        if (event.getOldValue() != manager.getConnectedChannel() ||
        event.getOldValue() != event.getNewValue())
            return;

        if (event.getOldValue().getMembers().size() != 1){
            executor.scheduleAtFixedRate(() -> {
                manager.closeAudioConnection();
                event.getOldValue().getGuild().getDefaultChannel().sendMessageFormat(
                        "I have left `\uD83d\uDD0A %s` due to inactivity!", manager.getConnectedChannel().getName()).queue();
            }, 0, 5, TimeUnit.SECONDS);
        }
    }
}
