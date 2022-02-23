package BabyBaby.Listeners;


import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.audit.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.SelectMenuInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;

import org.jetbrains.annotations.NotNull;

import BabyBaby.CmdHandler;
import BabyBaby.Command.ISlashCMD;
import BabyBaby.Command.commands.Admin.*;
import BabyBaby.Command.commands.Bot.*;
//import BabyBaby.Command.commands.Bot.drawwithFerris;
import BabyBaby.Command.commands.Public.*;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;


public class BabyListener extends ListenerAdapter {


    private final CmdHandler manager;
    //private SelfUser botUser;
    private User owner;
    public final JDA bot;
    public static HashMap<String, String> prefix = new HashMap<>();
    private final String ownerID = "223932775474921472";
    //private static boolean typing = true;

    public BabyListener(JDA bot) throws IOException {
        this.bot = bot;
        manager = new CmdHandler(bot);
        owner = bot.getUserById(ownerID);
    }



    // Role Removal
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if(!event.getGuild().getId().equals(Data.ETH_ID))
            return;

        
        List<Role> removed = event.getRoles();
        if(!removed.contains(event.getGuild().getRoleById(Data.stfuID))){
            if(!removed.contains(event.getGuild().getRoleById(Data.BLIND_ID))){
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

            for (String roleID : roles.split(" ")) {
                try {
                    addRole.add(blindServ.getRoleById(roleID));
                } catch (Exception e) {
                    System.out.println("Role doesnt exist anymore");
                }
            }


            

            try {
                delRole.add(blindServ.getRoleById(Data.BLIND_ID));
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
                
                log.sendMessageEmbeds(eb.build()).queue();
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
        
        
        MessageChannel log = event.getGuild().getTextChannelById(Data.modlog);
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("Unmute through Role removal.");
        eb.setColor(0);
        Member warned = event.getMember();
        eb.setThumbnail(warned.getUser().getAvatarUrl());

        eb.setDescription(":loud_sound: **Unmuted ** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** Manually unmuted with Role Removal.");

        log.sendMessageEmbeds(eb.build()).queue();


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

    }
    
    //ButtonEvent
    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
            
        InteractionHook msgHook = null;
        boolean failed = false;
        try {
            msgHook = event.deferReply(true).complete();
        } catch (Exception e) {
            System.out.println("Why so slow :/");
            failed = true;
        }
        
        //tracking usage
        Data.slashAndButton++;
        Data.users.add(event.getUser().getId());

        if(Data.emoteassign.containsKey(event.getComponentId())){
            Role role = event.getGuild().getRoleById(Data.emoteassign.get(event.getComponentId()));
            Helper.roleGiving(event.getMember(), event.getGuild(), failed, role, msgHook);
            
            Data.cmdUses.putIfAbsent(role.getName(), 0);
            Data.cmdUses.computeIfPresent(role.getName(), (name, x) -> ++x);
        }
    }

    
    //SelectionMenu
    @Override
    public void onSelectMenuInteraction(SelectMenuInteractionEvent event) {
        InteractionHook msgHook = null;
        boolean failed = false;
        try {
            //event.editComponents(ActionRow.of(event.getSelectionMenu()), ActionRow.of(event.getSelectionMenu())).queue();
            
            msgHook = event.deferReply(true).complete();
        } catch (Exception e) {
            System.out.println("Why so slow :/");
            failed = true;
        }
        if(event.getUser().isBot())
            return;
        
        if(event.getSelectMenu().getId().equals("menu:class")){
            if(!failed){
                msgHook.editOriginal("You have selected " + event.getSelectedOptions().size()).queue();
            }
        }

    }
    
    
    //Slash Commands
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        InteractionHook hook = null;
        boolean failed = false;
        try {
            hook = event.deferReply(true).complete();
        } catch (Exception e) {
            System.out.println("Why so slow :/");
            failed = true;
        }
        if(event.getUser().isBot())
            return;
        
        //check if blinded and then just ignore cmd
        if(event.getGuild().getId().equals(Data.ETH_ID) && event.getMember().getRoles().contains(event.getGuild().getRoleById(Data.BLIND_ID))){
            String cheater = "Unblind yourself and don't try to cheat!";
            if(failed){
                event.getUser().openPrivateChannel().complete().sendMessage(cheater).complete();
            } else {
                hook.editOriginal(cheater).queue();   
            }
            return;
        }

        //tracking usage
        Data.slashAndButton++;
        Data.users.add(event.getUser().getId());

        String cmd = event.getName();
        ISlashCMD cmdClass = null;

        for (ISlashCMD cmdSlash : Data.slashcmds) {
            if(cmd.equals(cmdSlash.getName())) {
                cmdClass = cmdSlash;
                break;
            }
        }
        if(cmdClass == null) {
            Helper.unhook("Uhhh what? Please send a screenshot of this to my owner.", failed, hook, event.getUser());
            return;
        }

        cmdClass.handle(event, hook, failed);
        Data.cmdUses.putIfAbsent(cmdClass.getName(), 0);
        Data.cmdUses.computeIfPresent(cmdClass.getName(), (name, x) -> ++x);
    }   

     
    //User Typing
    @Override
    public void onUserTyping(UserTypingEvent event) {
        if(event.getGuild() != null && event.getMember().getId().equals("848908721900093440") && event.getChannel().getId().equals("768600365602963496")){
            event.getGuild().getTextChannelById("789509420447039510").sendMessage("<@!223932775474921472> <:uhh:816589889898414100> <#768600365602963496>").queue();
        }
    }

    

    //Message Received
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        
        if (!event.isFromGuild()){
            //main prefix is + in DM
            String prefixstr = "+";
            String raw = event.getMessage().getContentRaw();

            // starts with prefix -> send to command handler
            if (raw.startsWith(prefixstr)) {
                
                String[] split = event.getMessage().getContentRaw()
                    .replaceFirst("(?i)" + Pattern.quote(prefixstr), "")
                    .split("\\s+");
            
                List<String> args = Arrays.asList(split).subList(1, split.length);
                
                String cmdName = split[0].toLowerCase();
                
                UnBlindCMD cmd1 = new UnBlindCMD();
                TillBlindCMD cmd2 = new TillBlindCMD();

                if(cmdName.equals(cmd1.getName())){
                    cmd1.privhandle(event.getAuthor(), args);
                } else if(cmdName.equals(cmd2.getName())){
                    cmd2.privhandle(event.getAuthor(), args, event.getJDA());
                }

            }
            //if private message don't go into guild code
            return;
        }
        
        //Delete every msg in #newcomers
        if(event.getChannel().getId().equals(Data.ETH_NEWCOMERS_CH_ID) && !event.getAuthor().isBot()){
            event.getMessage().delete().queue(Void -> Data.mydel++, Throwable -> Data.otherdel++);
            return;
        }
        

        User user = event.getAuthor();
        /*
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
        } else */


        if(user.getId().equals("778731540359675904") && Data.antibamboozle){
            String content = event.getMessage().getContentRaw();
            if (content.equals("Press the button to claim the points.")) {
                button.tap(event);
            }
        }
        /*else if(user.getId().equals("590453186922545152") || user.getId().equals("223932775474921472")){
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
