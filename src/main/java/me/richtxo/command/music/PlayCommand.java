package me.richtxo.command.music;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import me.richtxo.audio.PlayerManager;
import me.richtxo.command.Command;
import me.richtxo.command.CommandContext;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
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
            channel.sendMessage("Searching " + input + "...").queue(message -> searchYoutube(input, ctx));
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


            event.getChannel().sendMessage(builder.build()).queue((message) -> {
                this.waiter.waitForEvent(
                        MessageReceivedEvent.class,
                        (e) -> !e.getMessage().isWebhookMessage(),
                        (e) -> {
                            MessageChannel channel = e.getChannel();
                            String content = e.getMessage().getContentRaw();

                            if (e.getAuthor().getIdLong() == message.getAuthor().getIdLong()){
                                if (content.equals("cancel")){
                                    ctx.getEvent().getChannel().sendMessage("Cancel Search for `" +
                                            message.getMember().getEffectiveName() + "`").queue();
                                    message.getJDA().removeEventListener(this); // stop listening
                                }
                                else {
                                    try {
                                        int selection = Integer.parseInt(content);
                                        if (selection >= 1 && selection <= 5) {
                                            PlayerManager manager = PlayerManager.getInstance();

                                            manager.loadAndPlay(message.getTextChannel(),
                                                    "https://www.youtube.com/watch?v=" +
                                                            results.get(selection - 1).getId().getVideoId());
                                            event.getJDA().removeEventListener(this);
                                        } else
                                            channel.sendMessage("Select between `1 - 5` " + event.getMember().
                                                    getEffectiveName() + "!").queue();
                                    } catch (NumberFormatException nfe) {
                                        channel.sendMessage("Select between `1 - 5` " +
                                                event.getMember().getEffectiveName() + "!").queue();
                                    }
                                }
                            }
                        }, 15, TimeUnit.SECONDS,
                        () ->  event.getChannel().sendMessage("Time out! " + event.getMember().getEffectiveName() +
                                " too slow!").queue()
                );

            });

        } else {
            ctx.getEvent().getChannel().sendMessage("I can't find that song...").queue();
        }
    }
}
