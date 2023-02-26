package me.richtxo.command.music;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.richtxo.audio.LinkConverter;
import me.richtxo.audio.PlayerManager;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
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
    public void handle(CommandContext ctx) throws ParseException, IOException, SpotifyWebApiException {
        final List<String> args = ctx.getArgs();
        final TextChannel channel = ctx.getEvent().getChannel();
        final Member self = ctx.getGuild().getSelfMember();
        final GuildVoiceState selfVoiceState = self.getVoiceState();


        // If bot isn't in vc already
        assert selfVoiceState != null;
        if (!selfVoiceState.inVoiceChannel()){

            final GuildVoiceState memberVoiceState = Objects.requireNonNull(ctx.getEvent().getMember()).getVoiceState();
            if (!memberVoiceState.inVoiceChannel()){
                channel.sendMessage("You expect me to join a channel when you're not in one...").queue();
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
            channel.sendMessage("Searching " + input + "...").queue(message -> displayResults(input, ctx));
        }else{
            if (input.contains("open.spotify.com")){
                searchSpotify(input, channel);
            }else{
                PlayerManager manager = PlayerManager.getInstance();
                channel.sendMessage("Adding youtube link to queue!").queue();
                manager.loadAndPlay(channel, input);
            }
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


    private void searchSpotify(String input, TextChannel channel) throws ParseException, IOException, SpotifyWebApiException {
        LinkConverter spotify = new LinkConverter();
        ArrayList<String> result = spotify.convert(input);

        if (result.isEmpty()){
            channel.sendMessage("I can't find anything with that Spotify link...").queue();
            return;
        }
        for (String song : result){
            PlayerManager manager = PlayerManager.getInstance();
            String vidId = String.valueOf(searchYoutube(song).get(0).getId().getVideoId());
            String youtubeLink = "https://www.youtube.com/watch?v=" + vidId;
            manager.loadAndPlay(channel, youtubeLink);
        }
        channel.sendMessage("Added Spotify Playlist!").queue();
    }


    private List<SearchResult> searchYoutube(String search) {
        List<SearchResult> results = new ArrayList<>();
        try {
            results = youtube.search()
                    .list("id,snippet")
                    .setQ(search)
                    .setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
                    .setKey(System.getenv("YOUTUBE_KEY"))
                    .execute()
                    .getItems();
        } catch (Exception e) {
            return results;
        }
        return results;
    }


    private void displayResults(String input, CommandContext ctx) {
        List<SearchResult> results = searchYoutube(input);
        assert results != null;
        if (results.isEmpty()){
            //e.printStackTrace();
            ctx.getEvent().getChannel().sendMessage("I can't find `" + input + "` song...").queue();
            return;
        }

        final GuildMessageReceivedEvent event = ctx.getEvent();
        final TextChannel channel = event.getChannel();

        EmbedBuilder builder = new EmbedBuilder()
                .setTitle(event.getJDA().getSelfUser().getName() + " Music Selection")
                .setColor(0xad0000)
                .setThumbnail(String.valueOf(event.getAuthor().getAvatarUrl()))
                .setFooter("Type `cancel` to cancel the selection, auto-cancels in 15 seconds")
                .setDescription("Select the following songs by their respected numbers.\n");

        for (int i = 1; i < 6; i++){
            SearchResult temp = results.get(i - 1);
            builder.appendDescription(String.format(
                    "`" + i + "`\t "+ "%s\n",
                    temp.getSnippet().getTitle()
            ));
        }

        channel.sendMessageEmbeds(builder.build()).queue((message) -> {
            this.waiter.waitForEvent(
                GuildMessageReceivedEvent.class,
                e -> {
                    if (e.getChannel().getId().equals(channel.getId()) &&
                            e.getAuthor().getId().equals(event.getAuthor().getId())){
                        if (e.getMessage().getContentRaw().equals("cancel"))
                            return true;

                        else{
                            try {
                                int temp = Integer.parseInt(e.getMessage().getContentRaw());
                                if (temp >= 1 && temp <= 5)
                                    return true;

                                channel.sendMessage("Select between `1 - 5` " +
                                        event.getMember().getEffectiveName() + "!").queue();
                            } catch (NumberFormatException nfe){
                                channel.sendMessage("Select between `1 - 5` " +
                                        event.getMember().getEffectiveName() + "!").queue();
                            }
                        }
                    }
                    return false;
                },
                e -> {
                    if (e.getMessage().getContentRaw().equals("cancel")){
                        channel.sendMessage("Cancel Search for `" + event.getMember().getEffectiveName() + "`").queue();
                        return;
                    }

                    int selection = Integer.parseInt(e.getMessage().getContentRaw());
                    PlayerManager manager = PlayerManager.getInstance();
                    channel.sendMessageFormat("Adding `%s` to queue", input).queue();
                    manager.loadAndPlay(channel, "https://www.youtube.com/watch?v=" +
                            results.get(selection - 1).getId().getVideoId());
                    }, 15, TimeUnit.SECONDS,
                    () ->  channel.sendMessage("Time out, " + event.getMember().getEffectiveName() + "!").queue()
            );
        });
    }
}
