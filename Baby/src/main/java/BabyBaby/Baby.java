package BabyBaby;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import BabyBaby.Listeners.BabyListener;
import BabyBaby.Listeners.ButtonSlashListener;
import BabyBaby.Listeners.ModerationListener;
import BabyBaby.Listeners.StartupListener;
import BabyBaby.data.Data;

import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Scanner;

public class Baby {

        public static void main(String[] args) throws IOException {

                String token = "";

                Scanner s = new Scanner(new File(Data.TOKEN));
                token = s.nextLine();
                s.close();

                JDABuilder builder = JDABuilder.createDefault(token);
                builder.enableIntents(
                                GatewayIntent.DIRECT_MESSAGES,
                                GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                                GatewayIntent.DIRECT_MESSAGE_TYPING,
                                GatewayIntent.GUILD_BANS,
                                GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
                                GatewayIntent.GUILD_INVITES,
                                GatewayIntent.GUILD_MEMBERS,
                                GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.MESSAGE_CONTENT,
                                GatewayIntent.GUILD_MESSAGE_REACTIONS,
                                GatewayIntent.GUILD_MESSAGE_TYPING,
                                GatewayIntent.GUILD_PRESENCES,
                                GatewayIntent.GUILD_VOICE_STATES);
                builder.setChunkingFilter(ChunkingFilter.ALL);
                builder.enableCache(CacheFlag.ONLINE_STATUS, CacheFlag.CLIENT_STATUS);
                builder.setMemberCachePolicy(MemberCachePolicy.ALL);

                JDA jda = builder.build();
                jda.addEventListener(new StartupListener(jda), new BabyListener(jda), new ModerationListener(),
                                new ButtonSlashListener());

                Data.startUp = OffsetDateTime.now();

                jda.getPresence().setActivity(Activity.listening(" +help [cmd]"));
        }
}