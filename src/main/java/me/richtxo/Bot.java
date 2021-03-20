package me.richtxo;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

public class Bot {
    public static void main(String[] args) throws LoginException {

        System.out.println("Token: " + System.getenv("TOKEN"));

        EventWaiter waiter = new EventWaiter();

        DefaultShardManagerBuilder.createDefault(System.getenv("TOKEN"),
                GatewayIntent.GUILD_PRESENCES,
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.GUILD_VOICE_STATES
        )
        .disableCache(EnumSet.of(
                CacheFlag.ACTIVITY,
                CacheFlag.EMOTE
        ))
        .enableCache(CacheFlag.VOICE_STATE)
        .addEventListeners(new Listener(waiter), waiter)
        .setActivity(Activity.listening("RichtXO"))
        .setShardsTotal(4)
        .build();
    }
}