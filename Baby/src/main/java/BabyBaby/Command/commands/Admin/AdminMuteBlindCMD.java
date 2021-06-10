package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.Command.commands.Public.RemoveRoles;
import BabyBaby.data.GetRolesBack;
import BabyBaby.data.data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class AdminMuteBlindCMD implements AdminCMD{
    public static HashSet<Member> userBlinded = new HashSet<>();


    @Override
    public void handleOwner(CommandContext ctx) {
       handleAdmin(ctx);
        
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getAdminHelp(prefix);
    }

    @Override
    public String getName() {
        return "muteblind";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        if(!ctx.getGuild().getId().equals(data.ethid)){
            return;
        }


        
        LinkedList<String> cmds = new LinkedList<>();

        for (String var : ctx.getArgs()) {
            cmds.add(var);
        }
        

        String person = cmds.remove(0);
        int leng = person.length();
        
        person = person.replace("<", "");
        person = person.replace(">", "");
        person = person.replace("!", "");
        person = person.replace("@", "");

        Member blinded;
        try {
            blinded = ctx.getGuild().getMemberById(person);
        } catch (Exception e) {
            ctx.getChannel().sendMessage("This is not a snowflake ID or this user is not on this server.").queue();
            return;
        }



        int time = 0;
        String reason = "";
        if(cmds.size() > 0){
            try {
                time = Integer.parseInt(cmds.get(0)); 
                if(cmds.size()>1)
                    reason = ctx.getMessage().getContentRaw().substring(1 + getName().length() + 1 + cmds.get(0).length() + 1 + leng + 1);
            } catch (Exception e) {
                reason = ctx.getMessage().getContentRaw().substring(1 + getName().length() + 1 + leng + 1);
            }
        }


        LinkedList<GuildChannel> gchan = new LinkedList<>();

        for (GuildChannel var : ctx.getGuild().getChannels()) {
            if(!var.getId().equals("769261792491995176") && !var.getId().equals("815881148307210260") && var.getParent() != null){
                gchan.add(var);
            }
        }



        List<Role> begone = blinded.getRoles();
        LinkedList<Role> permrole = new LinkedList<>();
        MessageChannel channel = ctx.getChannel();

        Guild called = ctx.getGuild();

        Role highestbot = null;
        for (Role var : ctx.getSelfMember().getRoles()) {
            highestbot = var;
            break;
        }

        for (Role var : begone) {
            for (GuildChannel var2 : gchan) {
                if(var.hasAccess(var2)){
                    if(var.getPosition()>highestbot.getPosition()){
                        channel.sendMessage("Sry you have a higher Role than this bot with viewing permissions. Can't take your roles away").queue();
                        return;
                    }
                    permrole.add(var);
                    break;
                }
            }
        }




        MessageChannel log = ctx.getGuild().getTextChannelById(data.modlog);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(ctx.getAuthor().getAsTag() + " (" + ctx.getAuthor().getId() + ")", ctx.getAuthor().getAvatarUrl(), ctx.getAuthor().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(blinded.getUser().getAvatarUrl());
        

        eb.setDescription(":warning: **Muteblinded for** " + (time==0? "Infinite" : time) + " minutes " + blinded.getAsMention() + "(" + blinded.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + reason);

        log.sendMessage(eb.build()).queue();

        ctx.getChannel().sendMessage(eb.build()).queue();

        
        long timesql = 0;
        
        ctx.getMessage().addReaction(data.check).queue();


        
        String role = "";


        if(userBlinded.contains(blinded)){

            Connection c = null;
            PreparedStatement stmt = null;
            int timeold = 0;

            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(data.db);
    
                stmt = c.prepareStatement("SELECT * FROM ROLEREMOVAL WHERE USERID=? AND GUILDID=?;");
                stmt.setString(1, blinded.getId());
                stmt.setString(2, ctx.getGuild().getId());
                
                ResultSet rs = stmt.executeQuery();
                
                role = rs.getString("ROLES");
                timeold = rs.getInt("MUTETIME");

                stmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                return;
            }

            if(timeold!=0){
                ScheduledExecutorService tmp = RemoveRoles.blind.get(blinded);
                RemoveRoles.blindexe.remove(tmp);
                try {
                    tmp.shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                RemoveRoles.blind.remove(blinded);
            }


            if(time != 0){
                time = time*60;
                GetRolesBack scheduledclass = new GetRolesBack(blinded.getUser(), called, role);
                timesql = (System.currentTimeMillis() + time*1000);
                ScheduledExecutorService blind = Executors.newScheduledThreadPool(1);
                blind.schedule(scheduledclass, time , TimeUnit.SECONDS);
    
                blind.schedule(scheduledclass, time , TimeUnit.SECONDS);
                RemoveRoles.blind.put(blinded, blind);
                RemoveRoles.blindexe.put(blind, scheduledclass);
    
            } else {
                RemoveRoles.blind.put(blinded, null);
            }


            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(data.db);
    
                stmt = c.prepareStatement("REPLACE INTO ROLEREMOVAL (MUTETIME) VALUES (?) WHERE USERID=? AND GUILDID=?;");
                stmt.setString(2, blinded.getId());
                stmt.setString(3, ctx.getGuild().getId());
                stmt.setString(1, timesql + "");
                
                
                stmt.executeUpdate();
    
                stmt.close();
                c.close();
            } catch ( Exception e ) {
                channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
                return;
            }

            return;
        } 


        userBlinded.add(blinded);



        
        for (Role var : permrole) {
            role += var.getId() + " ";
        }

        
        

        
        if(time != 0){
            GetRolesBack scheduledclass = new GetRolesBack(blinded.getUser(), called, role);
            timesql = (System.currentTimeMillis() + time*60*1000);
            ScheduledExecutorService blind = Executors.newScheduledThreadPool(1);
            blind.schedule(scheduledclass, time*60 , TimeUnit.SECONDS);
            userBlinded.add(ctx.getGuild().getMemberById(person));

            blind.schedule(scheduledclass, time , TimeUnit.SECONDS);
            RemoveRoles.blind.put(blinded, blind);
            RemoveRoles.blindexe.put(blind, scheduledclass);

        } else {
            userBlinded.add(ctx.getGuild().getMemberById(person));
        }

        Connection c = null;
        PreparedStatement stmt = null;
        

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);

            stmt = c.prepareStatement("INSERT INTO ROLEREMOVAL (USERID, GUILDID, MUTETIME, ROLES, ADMINMUTE) VALUES (?, ?, ?, ?, ?);");
            stmt.setString(1, blinded.getId());
            stmt.setString(2, ctx.getGuild().getId());
            stmt.setString(3, timesql + "");
            stmt.setString(4, role);
            stmt.setString(5, "true");
            
            stmt.executeUpdate();

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }

        

        LinkedList<Role> tmp = new LinkedList<>();
        try {
            tmp.add(ctx.getGuild().getRoleById("844136589163626526"));
        } catch (Exception e) {
           ctx.getChannel().sendMessage("Role Blind doesnt exist anymore. This could be a serious issue.").queue();
        }
        
        called.modifyMemberRoles(ctx.getMember(), tmp, permrole).complete();

    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "args", "Command to mute blind a person that didn't behave by the rules.");
    }
    
}