package BabyBaby.Listeners;


import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.audit.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.*;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;

import org.jetbrains.annotations.NotNull;

import BabyBaby.CmdHandler;
import BabyBaby.Command.commands.Admin.*;
import BabyBaby.Command.commands.Bot.*;
//import BabyBaby.Command.commands.Bot.drawwithFerris;
import BabyBaby.Command.commands.Public.*;
import BabyBaby.data.Data;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;


public class BabyListener extends ListenerAdapter {


    private final CmdHandler manager;
    //private SelfUser botUser;
    private User owner;
    public final JDA bot;
    public static HashMap<String, String> prefix = new HashMap<>();
    private final String ownerID = "223932775474921472";
    //private static boolean typing = true;
    public static HashSet<String> slash = new HashSet<>(); 

    public BabyListener(JDA bot) throws IOException {
        this.bot = bot;
        manager = new CmdHandler(bot);
        owner = bot.getUserById(ownerID);
    }



    // Role Removal
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if(!event.getGuild().getId().equals(Data.ethid))
            return;

        
        List<Role> removed = event.getRoles();
        if(!removed.contains(event.getGuild().getRoleById(Data.stfuID))){
            if(!removed.contains(event.getGuild().getRoleById(Data.blindID))){
                return;
            }


            if(!BlindCMD.blind.containsKey(event.getMember())){
                return;
            }

            AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs();
            for (AuditLogEntry entry : logs) {
                if(entry.getType().equals(ActionType.MEMBER_ROLE_UPDATE)){
                    if(entry.getUser().getId().equals(event.getJDA().getSelfUser().getId()))
                        return;
                    else
                        break;
                }
            }
            

            Guild blindServ = event.getGuild();
            Member blinded = event.getMember();
            String roles = "";

            Connection c = null;
            PreparedStatement stmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                
                stmt = c.prepareStatement("SELECT ROLES FROM ROLEREMOVAL WHERE USERID = ? AND GUILDID = ?;");
                stmt.setString(1, blinded.getId());
                stmt.setString(2, blindServ.getId());
                ResultSet rs = stmt.executeQuery();

                roles = rs.getString("ROLES");

                stmt.close();
                c.close();
            } catch ( Exception e ) {
                e.printStackTrace(); 
                return;
            }

            LinkedList<Role> addRole = new LinkedList<>();
            LinkedList<Role> delRole = new LinkedList<>();

            for (String var : roles.split(" ")) {
                try {
                    addRole.add(blindServ.getRoleById(var));
                } catch (Exception e) {
                    System.out.println("Role doesnt exist anymore");
                }
            }


            

            try {
                delRole.add(blindServ.getRoleById("844136589163626526"));
                blindServ.modifyMemberRoles(blinded, addRole, delRole).complete();
            } catch (Exception e) {
                System.out.println("Role Blind doesnt exist anymore. This could be a serious issue.");
                blindServ.modifyMemberRoles(blinded, addRole, null).complete();
            }

            
            
            
            if(BlindCMD.blind.get(blinded)!=null){
                ScheduledExecutorService blind = BlindCMD.blind.get(blinded);
                BlindForceCMD.force.remove(BlindCMD.blindexe.get(blind));
                BlindCMD.blindexe.remove(blind);
                blind.shutdownNow();
            }
            BlindCMD.blind.remove(blinded);

            //remove from a group
            String id = event.getMember().getId();
            for (int ids : BlindGroupCMD.groups.keySet()) {
                ArrayList<String> var = BlindGroupCMD.groups.get(ids);
                if(var.contains(id)){
                    var.remove(id);
                    break;
                }
            }

        
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                
                stmt = c.prepareStatement("DELETE FROM ROLEREMOVAL WHERE USERID = ? AND GUILDID = ?;");
                stmt.setString(1, blinded.getId());
                stmt.setString(2, blindServ.getId());
                stmt.execute();
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                e.printStackTrace(); 
                return;
            }

            if(AdminMuteBlindCMD.userBlinded.contains(blinded)){
                MessageChannel log = event.getGuild().getTextChannelById(Data.modlog);
        
                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor("Unmute through Role removal.");
                
                eb.setColor(0);
                eb.setThumbnail(blinded.getUser().getAvatarUrl());

                eb.setDescription(":loud_sound: **Unblinded ** " + blinded.getAsMention() + "(" + blinded.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** Manually unblinded with Role Removal.");
                
                log.sendMessage(eb.build()).queue();
                AdminMuteBlindCMD.userBlinded.remove(blinded);
            }
            
            return;
        }
            
        

        if(event.getUser().getId().equals("177498563637542921")){
            Connection c = null;
            PreparedStatement stmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);
                
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
        
        MessageChannel log = event.getGuild().getTextChannelById(Data.modlog);
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Unmute through Role removal.");
        eb.setColor(0);
        Member warned = event.getMember();
        eb.setThumbnail(warned.getUser().getAvatarUrl());

        eb.setDescription(":loud_sound: **Unmuted ** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** Manually unmuted with Role Removal.");

        log.sendMessage(eb.build()).queue();


        Role muteR = event.getGuild().getRoleById(Data.stfuID);

        event.getGuild().removeRoleFromMember(warned, muteR).queue();

        Connection c = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
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

    


    

    
    //Private Message
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

    
    //Message Reaction
    @Override
    public void onGenericGuildMessageReaction(GenericGuildMessageReactionEvent event) {
        if (event.getUser().isBot())
            return;
        //This should be switched with a HashMap instead of a HashSet such that other servers could also at least technically use it.

        if(!event.getGuild().getId().equals(Data.ethid))
            return;

        

        if(Data.msgid.contains(event.getMessageId())){
            String emote = "";
            try{
                emote += (event.getReactionEmote().getEmote().isAnimated() ? "a" : "") +":"+ event.getReactionEmote().getAsReactionCode(); 
                //event.getReactionEmote().getEmote().isAnimated()
            } catch (Exception e) {
                emote += event.getReactionEmote().getName();
            }


            if(Data.emoteassign.containsKey(emote)){
                Role assign = event.getGuild().getRoleById(Data.emoteassign.get(emote));
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
    public void onButtonClick(ButtonClickEvent event) {
        if (Data.buttonid.contains(event.getMessageId()) || Data.msgid.contains(event.getMessageId())) {
            
            
            InteractionHook msgHook = null;
            boolean failed = false;
            try {
                msgHook = event.deferReply(true).complete();
            } catch (Exception e) {
                System.out.println("Why so slow :/");
                failed = true;
            }
            
            if(Data.emoteassign.containsKey(event.getComponentId())){
                
                Member mem = event.getMember();
                Guild guild = event.getGuild();
                Role chnge = guild.getRoleById(Data.emoteassign.get(event.getComponentId()));

                if(mem.getRoles().contains(chnge)){
                    String fail = "Removed the role ";
                    if(failed){
                        event.getUser().openPrivateChannel().complete().sendMessage(fail + chnge.getName()).queue();
                    } else {
                        msgHook.editOriginal(fail + chnge.getAsMention()).queue();
                    }
                    guild.removeRoleFromMember(mem, chnge).complete();
                } else {
                    String succes = "Added the role ";
                    if(failed){
                        event.getUser().openPrivateChannel().complete().sendMessage(succes + chnge.getName()).queue();
                    } else {
                        msgHook.editOriginal(succes + chnge.getAsMention()).queue();
                    }
                    guild.addRoleToMember(mem, chnge).complete();
                }
                return;
            }
            
        }
    }

    
    //Slash Commands
    @Override
    public void onSlashCommand (SlashCommandEvent event) {
        if(!slash.contains(event.getName()))
            return;
        InteractionHook msgHook = null;
        boolean failed = false;
        try {
            msgHook = event.deferReply(true).complete();
        } catch (Exception e) {
            System.out.println("Why so slow :/");
            failed = true;
        }
        if(event.getUser().isBot())
            return;

        String cmd = event.getName();
        
        if(cmd.equals("poll")){
            new PollCMD().slashCommand(event);
        } else if(cmd.equals("blind")){
            String unit = (event.getOption("unit")!=null) ? event.getOption("unit").getAsString() : null;
            boolean force = (event.getOption("force")!=null) ? event.getOption("force").getAsBoolean() : false;
            new BlindCMD().roleRemoval(event.getOption("time").getAsString(), event.getMember(), event.getGuild(), unit, force, event.getChannel());
        } else if(cmd.equals("role")){
            Role role = event.getOption("role").getAsRole();
            if(!Data.roles.contains(role.getId()) && !event.getMember().getId().equals(Data.myselfID)){
                String nope = "I can't give you that role.";
                if(failed){
                    event.getUser().openPrivateChannel().complete().sendMessage(nope).complete();
                } else {
                    msgHook.editOriginal(nope).queue();   
                }
                return;
            }
            List<Role> memRole = event.getMember().getRoles();
            if(memRole.contains(role)){
                event.getGuild().removeRoleFromMember(event.getMember(), role).complete();
                String remove = "I removed ";
                if(failed){
                    event.getUser().openPrivateChannel().complete().sendMessage(remove + role.getName() + " from you.").complete();
                } else {
                    msgHook.editOriginal(remove + role.getAsMention() + " from you.").queue();   
                }
            } else {
                event.getGuild().addRoleToMember(event.getMember(), role).complete();
                String added = "I gave you ";
                if(failed){
                    event.getUser().openPrivateChannel().complete().sendMessage(added + role.getName() + ".").complete();
                } else {
                    msgHook.editOriginal(added + role.getAsMention() + ".").queue();   
                }
            }
        }


            
    }


     
    //User Typing
    @Override
    public void onUserTyping(@Nonnull UserTypingEvent event) {
        if(event.getGuild() != null && event.getMember().getId().equals("848908721900093440") && event.getChannel().getId().equals("768600365602963496")){
            event.getGuild().getTextChannelById("789509420447039510").sendMessage("<@!223932775474921472> <:uhh:816589889898414100> <#768600365602963496>").queue();
        }
    }

    //Message Received
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
        } else if(user.getId().equals("778731540359675904") && Data.antibamboozle){
            String content = event.getMessage().getContentRaw();
            if (content.equals("Press the button to claim the points.")) {
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
