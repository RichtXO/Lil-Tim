package me.richtxo.command.music;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.richtxo.audio.PlayerManager;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PlayCommand extends Command {
    private final YouTube youtube;
    private final EventWaiter waiter;

    public PlayCommand(EventWaiter waiter){
        this.name = "play";
        this.category = "music";
        this.help = "Play any music on YouTube, SoundCloud, & Spotify";
        this.aliases = new String[]{"p"};

        this.waiter = waiter;
        YouTube temp = null;
        try {
            temp = new YouTube.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(),
                    null)
                    .setApplicationName("Discord Bot")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        youtube = temp;
    }

    @Override
    public void handle(CommandContext ctx) {
        final List<String> args = ctx.getArgs();
        final TextChannel channel = ctx.getEvent().getChannel();
        final Member self = ctx.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();


        // If bot isn't in vc already
        assert selfVoiceState != null;
        if (!selfVoiceState.inVoiceChannel()){

            final GuildVoiceState memberVoiceState = Objects.requireNonNull(ctx.getEvent().getMember()).getVoiceState();
            if (!memberVoiceState.inVoiceChannel()){
                channel.sendMessage("You expect me to join to join a channel when you're not in one...").queue();
                return;
            }

            final AudioManager audioManager = ctx.getGuild().getAudioManager();
            final VoiceChannel memberChannel = memberVoiceState.getChannel();

            audioManager.openAudioConnection(memberChannel);
        }

        if (args.isEmpty()){
            channel.sendMessage("Nani? No song to play?").queue();
            return;
        }
        String input = String.join(" ", args);

        if (!isUrl(input)) {
            channel.sendMessage("Searching " + input + "...").queue(message -> {
                searchYoutube(input, ctx);
            });
        }else{
            PlayerManager manager = PlayerManager.getInstance();
            manager.loadAndPlay(channel, input);
        }
    }


    private boolean isUrl(String input) {
        try {
            new URL(input);
            return true;
        } catch (MalformedURLException ignored) {
            return false;
        }
    }

    private void searchYoutube(String input, CommandContext ctx) {
        List<SearchResult> results;
        try {
            results = youtube.search()
                    .list("id,snippet")
                    .setQ(input)
                    .setType("video")
                    .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
                    .setKey(System.getenv("YOUTUBE_KEY"))
                    .execute()
                    .getItems();
        } catch (Exception e) {
            //e.printStackTrace();
            ctx.getEvent().getChannel().sendMessage("I can't find that song...").queue();
            return;
        }
        if (!results.isEmpty()) {

            GuildMessageReceivedEvent event = ctx.getEvent();

            // wait for a response
            waiter.waitForEvent(MessageReceivedEvent.class,
                    // make sure it's by the same user, and in the same channel, and for safety, a different message
                    e -> e.getAuthor().equals(event.getAuthor())
                            && e.getChannel().equals(event.getChannel())
                            && !e.getMessage().equals(event.getMessage()),
                    // respond, inserting the name they listed into the response
                    e -> event.getChannel().sendMessageFormat("Hello, `"+e.getMessage().getContentRaw()+"`! I'm `"+e.getJDA().getSelfUser().getName()+"`!"),
                    // if the user takes more than a minute, time out
                    1, TimeUnit.MINUTES, () -> event.reply("Sorry, you took too long."));




        } else{
            ctx.getChannel().sendMessage("I can't find that song...").queue();
        }
}
