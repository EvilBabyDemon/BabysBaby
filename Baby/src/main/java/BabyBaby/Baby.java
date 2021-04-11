package BabyBaby; 

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import javax.security.auth.login.LoginException;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;




public class Baby {
	
    public static void main(String[] args) throws IOException  {
        
        try {

            Scanner s = new Scanner(new File("C:\\Users\\Lukas\\Desktop\\From_Old_to_NEW\\VSCODE WORKSPACE\\BabysBaby\\Baby\\.gitignore\\token.txt"));
            String token = s.nextLine();
            JDABuilder builder = JDABuilder.createDefault(token);
            builder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            builder.setChunkingFilter(ChunkingFilter.ALL);
            //createDefault(token, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES,
           // GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.DIRECT_MESSAGES,
            //GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGE_REACTIONS)
            JDA jda = builder.build();
            jda.addEventListener(new BabyListener(jda));
            //jda.addEventListener(new MyListener());
            jda.getPresence().setActivity(Activity.watching(" some pixels getting slowly placed."));
        } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}


/*
public class Baby {
    private final JDA jda;
    private final BabyListener listener;

    private Baby() throws LoginException, SQLException, IOException {


       // SQLiteDataSource.getConnection();

        WebUtils.setUserAgent("");
        EmbedUtils.setEmbedBuilder(
                () -> new EmbedBuilder()
                        .setColor(0xff00ff) //#ff00ff
                        .setFooter(" ")
        );

        jda =
                JDABuilder.createDefault(
                        Config.get("NzgxOTQ5NTcyMTAzNTM2NjUw.X8FFSQ._pmy-ObcLt91Dt0N70JtwR44c2o"),
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_EMOJIS,
                        GatewayIntent.DIRECT_MESSAGES
                )
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .setActivity(Activity.listening("some Metalcore"))
                        .build();

        listener = new BabyListener(jda);

        jda.addEventListener(listener);
    }

    public static void main(String[] args) throws LoginException, SQLException, IOException, InterruptedException {
        Baby bot = new Baby();
        User owner = null;

        while (owner == null) {
            Thread.sleep(1000);
            owner = bot.listener.getOwner();
        }

        owner.openPrivateChannel().complete().sendMessage("found you").queue();
    }
}
*/


