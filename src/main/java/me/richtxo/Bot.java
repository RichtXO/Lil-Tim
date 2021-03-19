package me.richtxo;

import me.richtxo.commands.*;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class Bot {
    public static void main(String[] args) throws LoginException {
        EventWaiter waiter = new EventWaiter();

        CommandClientBuilder client = new CommandClientBuilder();
        client.setPrefix(System.getenv("PREFIX"));


        DefaultShardManagerBuilder.createDefault(System.getenv("TOKEN"),
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES
        )
        .disableCache(EnumSet.of(
                CacheFlag.ACTIVITY,
                CacheFlag.EMOTE
        ))
        .enableCache(CacheFlag.VOICE_STATE)
        .addEventListeners(waiter, client.build())
        .setShardsTotal(4)
        .setActivity(Activity.listening("RichtXO"))
        .build();
    }
}