package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;



public class MutePersonCMD implements AdminCMD {
    public static HashMap<Member, ScheduledExecutorService> userMuted = new HashMap<>();


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
        return "mute";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        if(!ctx.getGuild().getId().equals(data.ethid))
            return;
        
        LinkedList<String> cmds = new LinkedList<>();

        for (String var : ctx.getArgs()) {
            cmds.add(var);
        }
        

        String person = cmds.remove(0);
        
        person = person.replace("<", "");
        person = person.replace(">", "");
        person = person.replace("!", "");
        person = person.replace("@", "");
        boolean inList = false;
        try {
            inList  = MutePersonCMD.userMuted.containsKey(ctx.getGuild().getMemberById(person));
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
                    reason = ctx.getMessage().getContentRaw().substring(1 + getName().length() + 1 + cmds.get(0).length() + 1);
            } catch (Exception e) {
                reason = ctx.getMessage().getContentRaw().substring(1 + getName().length() + 1);
            }
        }

        MessageChannel log = ctx.getGuild().getTextChannelById(data.modlog);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(ctx.getAuthor().getAsTag() + " (" + ctx.getAuthor().getId() + ")", ctx.getAuthor().getAvatarUrl(), ctx.getAuthor().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(ctx.getGuild().getMemberById(person).getUser().getAvatarUrl());
        Member warned = ctx.getGuild().getMemberById(person);

        eb.setDescription(":warning: **Muted for** " + (time==0? "Infinite" : time) + " " + warned.getAsMention() + "(" + warned.getUser().getAsTag() +")"+ " \n :page_facing_up: **Reason:** " + reason);

        log.sendMessage(eb.build()).queue();

        ctx.getChannel().sendMessage(eb.build()).queue();

        Role muteR = ctx.getGuild().getRoleById(data.stfuID);

        if(inList){
            ctx.getGuild().removeRoleFromMember(ctx.getMember(), muteR).queue();

            if(MutePersonCMD.userMuted.get(warned)==null){
                MutePersonCMD.userMuted.remove(warned);
            } else {
                MutePersonCMD.userMuted.remove(warned);
            }

        }

        ctx.getGuild().addRoleToMember(ctx.getMember(), muteR).queue();

        

        GetUnmutePerson scheduledclass = new GetUnmutePerson(warned.getUser(), ctx.getGuild(), "");
        long timesql = 0;
        if(time != 0){
            timesql = (System.currentTimeMillis() + time*60*1000);
            ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);
            mute.schedule(scheduledclass, time*60 , TimeUnit.SECONDS);
            userMuted.put(ctx.getMember(), mute);
        } else {
            userMuted.put(ctx.getMember(), null);
        }


        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
             
            stmt = c.prepareStatement("REPLACE INTO ADMINMUTE (GUILDID, USERID, TIME) VALUES (?, ?, ?);");           
            stmt.setString(1, warned.getUser().getId());
            stmt.setString(2, ctx.getGuild().getId());
            stmt.setLong(3, timesql);
            stmt.executeUpdate();

            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            ctx.getChannel().sendMessage( e.getClass().getName() + ": " + e.getMessage()).queue();
            e.printStackTrace(); 
            return;
        }

        ctx.getMessage().addReaction(data.check).queue();

    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<User Ping> [Time]", "Command to mute a person.");
    }
    
}
