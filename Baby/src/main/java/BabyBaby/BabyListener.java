package BabyBaby;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.audit.ActionType;
import net.dv8tion.jda.api.audit.AuditLogEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.IPermissionHolder;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
//import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GenericGuildMessageReactionEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;
import net.dv8tion.jda.internal.managers.ChannelManagerImpl;

import org.jetbrains.annotations.NotNull;

import BabyBaby.Command.commands.Admin.GetUnmutePerson;
import BabyBaby.Command.commands.Admin.MutePersonCMD;
import BabyBaby.Command.commands.Bot.button;
import BabyBaby.Command.commands.Bot.clock;
//import BabyBaby.Command.commands.Bot.drawwithFerris;
import BabyBaby.Command.commands.Public.GetReminder;
import BabyBaby.Command.commands.Public.GetUnmute;
import BabyBaby.Command.commands.Public.MuteCMD;
import BabyBaby.data.data;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class BabyListener extends ListenerAdapter {


    private final CmdHandler manager;

    //private SelfUser botUser;
    private User owner;
    public final JDA bot;
    private static HashMap<String, String> prefix = new HashMap<>();
    private final String ownerID = "223932775474921472";
    //private static boolean typing = true;

    public BabyListener(JDA bot) throws IOException {
        this.bot = bot;
        manager = new CmdHandler(bot);
        owner = bot.getUserById(ownerID);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        long timestopper = System.currentTimeMillis();

        //TODO Put each in a own Thread and make it go brrrr

        //botUser = event.getJDA().getSelfUser();
        Connection c = null;
        Statement stmt = null;

		ResultSet rs;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
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
        }
        
        
        c = null;
        stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT * FROM USERS");            
            while(rs.next()){
                try{
                Guild called = bot.getGuildById( rs.getString("GUILDUSER"));
                User muteUser = called.getMemberById(rs.getString("ID")).getUser();
                long time = Long.parseLong(rs.getString("MUTETIME"));
                ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);
                List<Role> tmp = called.getRolesByName("STFU", true);
                Role muteR = tmp.get(0);
                GetUnmute muteclass = new GetUnmute(muteUser, called, muteR);
                mute.schedule(muteclass, (time-System.currentTimeMillis())/1000, TimeUnit.SECONDS);
                MuteCMD.userMuted.put(called.getMember(muteUser), mute);
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Probably a User that left the server while being muted.");
                }

            }
            stmt.close();
            c.close();

        } catch ( Exception e ) {
            e.printStackTrace(); 
        }
       

        c = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM ASSIGNROLES;");

            while(rs.next()){
                data.emoteassign.put(rs.getString("EMOTE"), rs.getString("ID"));
            }

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
        }


        c = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM MSGS;");

            while(rs.next()){
                data.msgid.add(rs.getString("MSGID"));
            }

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
        }


        List<Invite> inv = event.getJDA().getGuildById(data.ethid).retrieveInvites().complete();
        HashMap<String, Invite> urls = new HashMap<>();
        for (Invite var : inv) {
            urls.put(var.getUrl(), var);
        }

        c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            pstmt = c.prepareStatement("DELETE FROM INVITES;");
            pstmt.execute();
            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        c = null;
        pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            for (String var : urls.keySet()) {
                pstmt = c.prepareStatement("INSERT INTO INVITES (URL, AMOUNT) VALUES (?, ?);");
                pstmt.setString(1, var);
                pstmt.setInt(2, urls.get(var).getUses());
                pstmt.executeUpdate();
                pstmt.close();
            }
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } 

        AuditLogPaginationAction logs = event.getJDA().getGuildById(data.ethid).retrieveAuditLogs();
        for (AuditLogEntry entry : logs) {
            if(entry.getType().equals(ActionType.KICK)){
                data.kick = entry.getTimeCreated();
                break;
            }   
        }
        for (AuditLogEntry entry : logs) {
            if(entry.getType().equals(ActionType.BAN)){
                data.ban = entry.getTimeCreated();
                break;
            }   
        }


        c = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM REMINDERS;");

            while(rs.next()){
                try {
                ScheduledExecutorService remind = Executors.newScheduledThreadPool(1);
                remind.schedule(new GetReminder(event.getJDA().getUserById(rs.getString("USERID")), event.getJDA().getGuildById(rs.getString("GUILDID")), rs.getString("TEXTS"), rs.getString("CHANNELID"), rs.getString("pk")), rs.getInt("TIME") , TimeUnit.SECONDS);
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println("Probably a User that left the server while being reminded.");
                }
            }
            
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
        }


        //Put admin mutes in cache
        c = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT * FROM ADMINMUTE;");

            while(rs.next()){
                try{
                    Guild muteG = event.getJDA().getGuildById(rs.getString("GUILDID"));
                    User mutedPerson = event.getJDA().getUserById(rs.getString("USERID"));
                    int time = rs.getInt("TIME");
                    if(time == 0){
                        MutePersonCMD.userMuted.put(muteG.getMember(mutedPerson), null);
                    } else {
                        ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);
                        mute.schedule(new GetUnmutePerson(mutedPerson, muteG), time , TimeUnit.SECONDS);
                        MutePersonCMD.userMuted.put(muteG.getMember(mutedPerson), mute);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("Probably a User that left the server while being adminmuted.");
                } 
            }
            
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
        }


        //put assign message ids in cache
        c = null;
        stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            stmt = c.createStatement();
            rs = stmt.executeQuery("SELECT MSGID FROM MSGS;");

            while(rs.next()){
                data.msgid.add(rs.getString("MSGID"));
            }
            
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
        }

        
        System.out.println("Started!" + (System.currentTimeMillis()-timestopper));
    }

    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if(!event.getGuild().getId().equals(data.ethid))
            return;
        
        List<Role> removed = event.getRoles();
        if(!removed.contains(event.getGuild().getRoleById(data.stfuID)))
            return;
        

        if(event.getUser().getId().equals("177498563637542921")){
            Connection c = null;
            PreparedStatement stmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(data.db);
                
                stmt = c.prepareStatement("DELETE FROM USERS WHERE ID = ? AND GUILDID = ?;");
                stmt.setString(1, event.getUser().getId());
                stmt.setString(2, event.getGuild().getId());
                stmt.execute();
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                e.printStackTrace(); 
            }
            return;
        }
        
        if(!MutePersonCMD.userMuted.containsKey(event.getMember()))
            return;
        
        MessageChannel log = event.getGuild().getTextChannelById(data.modlog);
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Unmute through Role removal.");
        eb.setColor(0);
        Member warned = event.getMember();
        eb.setThumbnail(warned.getUser().getAvatarUrl());

        eb.setDescription(":loud_sound: **Unmuted ** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** Manually unmuted with Role Removal.");

        log.sendMessage(eb.build()).queue();


        Role muteR = event.getGuild().getRoleById(data.stfuID);

        event.getGuild().removeRoleFromMember(warned, muteR).queue();

        Connection c = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.prepareStatement("DELETE FROM ADMINMUTE WHERE USERID = ? AND GUILDID = ?;");
            stmt.setString(1, event.getMember().getId());
            stmt.setString(2, event.getGuild().getId());
            stmt.execute();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }
        
        

        if(MutePersonCMD.userMuted.get(warned)==null){
            MutePersonCMD.userMuted.remove(warned);
        } else {
            ScheduledExecutorService stopper = MutePersonCMD.userMuted.remove(warned);
            stopper.shutdown();
        }

    }


    @Override
	public void onGuildMemberJoin(GuildMemberJoinEvent event) {
		if(!event.getGuild().getId().equals(data.ethid) || event.getUser().isBot())
            return;

        OffsetDateTime time = event.getUser().getTimeCreated();
		String username = event.getUser().getName().toLowerCase();

		if(username.contains("lengler") || username.contains("welzl")){
			event.getGuild().getTextChannelById("747754931905364000").sendMessage("<@&773908766973624340> Account with Prof name joined. Time of creation of the account:" + time).queue();
		}

        
        List<Invite> inv = event.getGuild().retrieveInvites().complete();
        HashMap<String, Invite> urls = new HashMap<>();
        for (Invite var : inv) {
            urls.put(var.getUrl(), var);
        }

        String url = "";
        int amount = 0;
        boolean found = false;

        Connection c = null;
        Statement stmt = null;
		
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM INVITES;");
            while (rs.next()) {

                url = rs.getString("URL");
                amount = rs.getInt("AMOUNT");
                
                try{
                    Invite temp = urls.get(url);
                    if(temp.getUses() > amount){
                        found = true;
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }

        if(!found){
            event.getGuild().getTextChannelById("747768907992924192").sendMessage("Smth went wrong with the invite link stuff. Couldnt find the invite link... <@!223932775474921472>").queue();
            return;
        }

        MessageChannel log = event.getGuild().getTextChannelById(data.adminlog);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", event.getUser().getAvatarUrl(), event.getUser().getAvatarUrl());
        eb.setColor(1);
        eb.setThumbnail(event.getUser().getAvatarUrl());

        eb.setDescription("Used Link: " + url + "\n Creator: " + urls.get(url).getInviter().getAsMention() + "\n Uses:" + ++amount + "\n Created at: " + urls.get(url).getTimeCreated().toLocalTime());
        Message temp = log.sendMessage(".").complete();
        temp.editMessage(urls.get(url).getInviter().getAsMention()).complete().delete().queue();
        log.sendMessage(eb.build()).queue();


        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            pstmt = c.prepareStatement("UPDATE INVITES SET AMOUNT = ? where URL = ? ;");
            pstmt.setInt(1, amount);
            pstmt.setString(2, url);
            pstmt.executeUpdate();
            
            pstmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return;
        }


	}

    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event) {
        if(!event.getGuild().getId().equals(data.ethid))
            return;
        
        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.createStatement();
            stmt.executeUpdate("INSERT INTO INVITES (URL, AMOUNT) VALUES ('" + event.getUrl() + "', 0) ;");
            
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
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
        //This should be switched with a HashMap instead of a HashSet such that other servers could also at least technically use it. 
        if(!event.getGuild().getId().equals(data.ethid))
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
                    event.getGuild().addRoleToMember(event.getMember(), assign).complete();
                    if(assign.getId().equals("747786383317532823")){
                        event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("767315361443741717")).complete();
                    } else if(assign.getId().equals("767315361443741717")){
                        event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById("747786383317532823")).complete();
                    }
                    
                    
                } else if (event instanceof GuildMessageReactionRemoveEvent){
                    //767315361443741717 External
                    //747786383317532823 Student
                    if(assign.getId().equals("747786383317532823")){
                        List<Role> tmp = event.getMember().getRoles();
                        Role external = event.getGuild().getRoleById("767315361443741717");
                        if(tmp.contains(external)){
                            event.getGuild().addRoleToMember(event.getMember(), external).complete();
                            event.getGuild().removeRoleFromMember(event.getMember(), assign).complete();
                        } else{
                            event.getUser().openPrivateChannel().complete().sendMessage("You need at least either the Student or External Role").queue();
                        }
                    } else if(assign.getId().equals("767315361443741717")){
                        List<Role> tmp = event.getMember().getRoles();
                        Role student = event.getGuild().getRoleById("747786383317532823");
                        if(tmp.contains(student)){
                            event.getGuild().addRoleToMember(event.getMember(), student).complete();
                            event.getGuild().removeRoleFromMember(event.getMember(), assign).complete();
                        } else{
                            event.getUser().openPrivateChannel().complete().sendMessage("You need at least either the Student or External Role").queue();
                        }
                    } else {
                        event.getGuild().removeRoleFromMember(event.getMember(), assign).complete();
                    }
                
                } else {
                    System.out.println("Whatever");
                    return;
                }
            }
        }
    }


    @Override
    public void onTextChannelCreate(TextChannelCreateEvent event) {
        if(!event.getGuild().getId().equals("747752542741725244"))
            return;
        ChannelManager test = new ChannelManagerImpl(event.getChannel());
        Collection<Permission> deny = new LinkedList<>();
        deny.add(Permission.MESSAGE_WRITE);
		IPermissionHolder permHolder = event.getGuild().getRoleById("765542118701400134");
        test.putPermissionOverride(permHolder, null, deny).queue();
    }

    @Override
    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
        if(!event.getGuild().getId().equals("747752542741725244"))
            return;
        AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs();
        for (AuditLogEntry entry : logs) {
            if(entry.getType().equals(ActionType.CHANNEL_CREATE)){
                if(entry.getUser().getId().equals(data.dcvd))
                    return;
                else
                    break;
            }
        }
        
        ChannelManager channelMan = new ChannelManagerImpl(event.getChannel());
        Collection<Permission> deny = new LinkedList<>();
        deny.add(Permission.VOICE_SPEAK );
		IPermissionHolder permHolder = event.getGuild().getRoleById("765542118701400134");
        channelMan.putPermissionOverride(permHolder, null, deny).queue();
    }


    


    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs();
        for (AuditLogEntry entry : logs) {
            if(entry.getType().equals(ActionType.KICK)){
                if(!data.kick.equals(entry.getTimeCreated())){

                    data.kick = entry.getTimeCreated();
                    MessageChannel log = event.getGuild().getTextChannelById(data.modlog);

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setAuthor(entry.getUser().getAsTag() + " (" + entry.getUser().getId() + ")", entry.getUser().getAvatarUrl(), entry.getUser().getAvatarUrl());
                    eb.setColor(0);
                    eb.setThumbnail(entry.getUser().getAvatarUrl());
                    Member warned = event.getMember();
                    
                    eb.setDescription(":warning: **Kicked** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + entry.getReason());

                    log.sendMessage(eb.build()).queue();

                }
                break;
            }  
        }
        for (AuditLogEntry entry : logs) {
            if(entry.getType().equals(ActionType.BAN)){
                if(!data.ban.equals(entry.getTimeCreated())){

                    data.ban = entry.getTimeCreated();
                    MessageChannel log = event.getGuild().getTextChannelById(data.modlog);

                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setAuthor(entry.getUser().getAsTag() + " (" + entry.getUser().getId() + ")", entry.getUser().getAvatarUrl(), entry.getUser().getAvatarUrl());
                    eb.setColor(0);
                    eb.setThumbnail(entry.getUser().getAvatarUrl());
                    Member warned = event.getMember();
                    
                    eb.setDescription(":warning: **Banned** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + entry.getReason());
                    log.sendMessage(eb.build()).queue();

                }
                break;
            }  
        }
    }
    


    


    
    /*
    @Override
    public void onUserTyping(@Nonnull UserTypingEvent event) {
        
        if(event.getMember().getId().equals("1083057361314406401")){ //Hello Ollie :eyes:
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
    */



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
        } /*else if(user.getId().equals("590453186922545152") || user.getId().equals("223932775474921472")){
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
