package BabyBaby;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import BabyBaby.Command.commands.Bot.button;
import BabyBaby.Command.commands.Bot.clock;
import BabyBaby.Command.commands.Bot.drawwithFerris;
import BabyBaby.Command.commands.Public.GetUnmute;
import BabyBaby.Command.commands.Public.MuteCMD;
import BabyBaby.data.data;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BabyListener extends ListenerAdapter {


    private final CmdHandler manager;

    private SelfUser botUser;
    private User owner;
    public final JDA bot;
    private static HashMap<String, String> prefix = new HashMap<>();
    private final String ownerID = "223932775474921472";
    private static boolean typing = true;

    public BabyListener(JDA bot) throws IOException {
        this.bot = bot;
        manager = new CmdHandler(bot);
        owner = bot.getUserById(ownerID);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        botUser = event.getJDA().getSelfUser();
        Connection c = null;
        Statement stmt = null;

		ResultSet rs;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT * FROM GUILD;");
            
            while (rs.next()) {
                String id = rs.getString("ID");
                String prefixstr = rs.getString("PREFIX");
                prefix.put(id, prefixstr);
            }
            rs.close();
            stmt.close();
            c.close();
            } catch ( Exception e ) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
                return;
            }
        
        
        c = null;
        stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT * FROM USERS");            
            while(rs.next()){
                Guild called = bot.getGuildById( rs.getString("GUILDUSER"));
                User muteUser = called.getMemberById(rs.getString("ID")).getUser();
                long time = Long.parseLong(rs.getString("MUTETIME"));
                ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);
                List<Role> tmp = called.getRolesByName("STFU", true);
                Role muteR = tmp.get(0);
                GetUnmute muteclass = new GetUnmute(muteUser, called, muteR);
                mute.schedule(muteclass, (time-System.currentTimeMillis())/1000, TimeUnit.SECONDS);
                MuteCMD.userMuted.put(called.getMember(muteUser), mute);
                MuteCMD.variables.put(mute, muteclass);
            }
            stmt.close();
            c.close();

        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }
       

        c = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM ASSIGNROLES;");

            while(rs.next()){
                data.emoteassign.put(rs.getString("EMOTE"), rs.getString("ID"));
            }

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }


        c = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM MSGS;");

            while(rs.next()){
                data.msgid.add(rs.getString("MSGID"));
            }

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }

        System.out.println("Started!");
    }

    @Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		// TODO Auto-generated method stub
		OffsetDateTime time = event.getUser().getTimeCreated();
		String username = event.getUser().getName().toLowerCase();

		if(username.contains("lengler") || username.contains("emo") || username.contains("welzl")){
			event.getGuild().getTextChannelById("747754931905364000").sendMessage("<@&773908766973624340> Account with name Onur joined. Time of creation of the account:" + time).queue();
		}
	}

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        
        User user = event.getAuthor();

        if (user.isBot()) {
            return;
        }

        //main prefix is + in DM
        String prefixstr = "+";
        String raw = event.getMessage().getContentRaw();

        // starts with prefix -> send to command handler
        if (raw.startsWith(prefixstr)) {
            manager.privhandle(event, prefixstr);
        }
    }

    

    @Override
    public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event) {
        if (event.getUser().isBot())
            return;
        if(data.msgid.contains(event.getMessageId())){
            String emote = "";
            try{
                emote += ":" + event.getReactionEmote().getName() + ":" + event.getReactionEmote().getId(); 
            } catch (Exception e) {
                emote += event.getReactionEmote().getName();
            }

            if(data.emoteassign.containsKey(emote)){
                Role assign = event.getGuild().getRoleById(data.emoteassign.get(emote));
                if(event instanceof GuildMessageReactionAddEvent) {
                    //767315361443741717 External
                    //747786383317532823 Student
                    if(assign.getId().equals("747786383317532823")){
                        event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("767315361443741717")).queueAfter(1, TimeUnit.SECONDS);
                    } else if(assign.getId().equals("767315361443741717")){
                        event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("747786383317532823")).queueAfter(1, TimeUnit.SECONDS);
                    }
                    event.getGuild().addRoleToMember(event.getMember(), assign).queue();
                    
                } else if (event instanceof GuildMessageReactionRemoveEvent){
                    //767315361443741717 External
                    //747786383317532823 Student
                    if(assign.getId().equals("747786383317532823")){
                        List<Role> tmp = event.getMember().getRoles();
                        Role external = event.getGuild().getRoleById("767315361443741717");
                        if(tmp.contains(external)){
                            event.getGuild().removeRoleFromMember(event.getMember(), assign).queue();
                        } else{
                            event.getUser().openPrivateChannel().complete().sendMessage("You need at least either the Student or External Role").queue();
                        }
                    } else if(assign.getId().equals("767315361443741717")){
                        List<Role> tmp = event.getMember().getRoles();
                        Role student = event.getGuild().getRoleById("747786383317532823");
                        if(tmp.contains(student)){
                            event.getGuild().removeRoleFromMember(event.getMember(), assign).queue();
                        } else{
                            event.getUser().openPrivateChannel().complete().sendMessage("You need at least either the Student or External Role").queue();
                        }
                    } else {
                        event.getGuild().removeRoleFromMember(event.getMember(), assign).queue();
                    }
                
                } else {
                    System.out.println("Whatever");
                    return;
                }
            }
        }
    }

    

    @Override
    public void onUserTyping(@Nonnull UserTypingEvent event) {
        if(event.getMember().getId().equals("123841216662994944")){ //Hello Elthision :eyes:
            if(typing){
                Random rand = new Random();
                if(rand.nextDouble() < 0.23){
                    typing = false;
                    event.getChannel().sendTyping().queue(response -> {
                        typing = true;
                    });
                }
            }
        }
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        MessageChannel log = event.getGuild().getTextChannelById(data.modlog);

        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
        eb.setColor(1);
        eb.setThumbnail(event.getUser().getAvatarUrl());
        User warned = event.getUser();

        eb.setDescription(":warning: **Warned** " + warned.getAsMention() + "(" + warned.getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + "");

        log.sendMessage(eb.build()).queue();
    }


    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {

        User user = event.getAuthor();
        
        if(user.getId().equals("781949572103536650")){
            String prefixstr = prefix.get(event.getGuild().getId());
            String content = event.getMessage().getContentRaw();
            if(content.startsWith(prefixstr + "clock")){
                clock.clockTick(event);
            }
        } else  if(user.getId().equals("781949572103536650")){
            String content = event.getMessage().getContentRaw();
            if(content.startsWith("PIXELVERIFY") && content.split(" ")[3].equals("SUCCESS")){
                clock.verify(event);
            }
        } else if(user.getId().equals("778731540359675904") && data.antibamboozle){
            String content = event.getMessage().getContentRaw();
            if (content.startsWith("Current value: ")) {
                button.tap(event);
            }
        } else if(user.getId().equals("590453186922545152") || user.getId().equals("223932775474921472")){
            String content = event.getMessage().getContentRaw();
            if(content.contains("781949572103536650")){
                new drawwithFerris().drawing(event);
            }
        }
        /*
        else if(event.getMessage().getContentRaw().equals("?bamboozle")){
            manager.handle(event, "?");
        }
        */
        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }
        


        String prefixstr = prefix.get(event.getGuild().getId());
        if(prefixstr == null || prefixstr.length() == 0)
            prefixstr = "+";
        String raw = event.getMessage().getContentRaw();

        // starts with prefix -> send to command handler
        if (raw.startsWith(prefixstr)) {
            manager.handle(event, prefixstr);
        }
    }

    User getOwner() {
        if (owner == null){
            return bot.getUserById(ownerID);
        }
        return owner;
    }

    void sendDM(User user, String message) {
        user.openPrivateChannel().complete().sendMessage(message).queue();
    }
   
}
