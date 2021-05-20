package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.GetRolesBack;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.*;

public class RemoveRolesForce implements PublicCMD{
    public static HashSet<GetRolesBack> force = new HashSet<>();


    @Override
    public void handleOwner(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public String getName() {
        return "forceblind";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        if(!ctx.getGuild().getId().equals(data.ethid))
            return;

        ctx.getMessage().addReaction(data.check).queue();


        LinkedList<GuildChannel> gchan = new LinkedList<>();

        for (GuildChannel var : ctx.getGuild().getChannels()) {
            if(!var.getId().equals("769261792491995176") && !var.getId().equals("815881148307210260") && var.getParent() != null){
                gchan.add(var);
            }
        }



        Member silenced = ctx.getMember();
        List<Role> begone = silenced.getRoles();
        LinkedList<Role> permrole = new LinkedList<>();
        MessageChannel channel = ctx.getChannel();


        if(!ctx.getGuild().getId().equals(data.ethid))
        return;

        Guild called = ctx.getGuild();

        List<String> cmds = ctx.getArgs();

        if(cmds.size() == 0){
            channel.sendMessage("The command is +" + getName() +" <time> [unit] (default unit is minutes)").queue();
            return;
        }

        String number = cmds.get(0);
        
        double time;
        String sunit;
        User muteUser = ctx.getAuthor(); 
        ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);
        
        

        try{
        if(number.length() > 18 || Double.parseDouble(number) > Integer.MAX_VALUE){
            time=Integer.MAX_VALUE;
        } else {
                time = Double.parseDouble(number);
        }
        } catch (NumberFormatException e){
            channel.sendMessage("You probably forgot the space between the time and unit, if not use numbers pls!").queue();
            return;
        }

        if(time <= 0){
            channel.sendMessage("Use positive numbers thx!").queue();
            return;
        }			

        
        long rounder = 0;
        
        if(cmds.size() < 2) {
            sunit = "minutes";
            rounder = (long) (time*60);
        } else {
            String timeunit = cmds.get(1);
            timeunit = timeunit.toLowerCase();
            if (timeunit.startsWith("h")){
                sunit = "hours";
                rounder = (long) (time*3600);
            } else if(timeunit.startsWith("m")){
                sunit = "minutes";
                rounder = (long) (time*60);
            } else if(timeunit.startsWith("d")){
                sunit = "days";
                rounder = (long) (time*24*3600);
            } else {
                sunit = "seconds";
                rounder = (long) (time);
            }
        }

        if(rounder <= 29){
            channel.sendMessage("Use numbers above 29 seconds pls! (As it takes a while to remove and add roles.)").queue();
            return;
        } else if(rounder > 43200){
            ctx.getChannel().sendMessage("Your value has to be below 12 hours (43000 seconds).").queue();
            return;
        }


        
        long timesql = (System.currentTimeMillis() + rounder*1000);


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


        Connection c = null;
        PreparedStatement stmt = null;
        String role = "";

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            for (Role var : permrole) {
                role += var.getId() + " ";
            }

            stmt = c.prepareStatement("INSERT INTO ROLEREMOVAL (USERID, GUILDID, MUTETIME, ROLES, ADMINMUTE) VALUES (?, ?, ?, ?, ?);");
            stmt.setString(1, ctx.getAuthor().getId());
            stmt.setString(2, ctx.getGuild().getId());
            stmt.setString(3, timesql + "");
            stmt.setString(4, role);
            stmt.setString(5, "false");
            
            stmt.executeUpdate();

            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }

        
        
        GetRolesBack scheduledclass = new GetRolesBack(muteUser, called, role);
        mute.schedule(scheduledclass, rounder , TimeUnit.SECONDS);

        
        
        
        RemoveRoles.blind.put(ctx.getMember(), mute);
        RemoveRoles.blindexe.put(mute, scheduledclass);
        force.add(scheduledclass);
        channel.sendMessage("You got blinded for ~" + time + " " + sunit + ". **Wait out the timer!!!** And hopefully you are productive!").queue();

        try {
            ctx.getAuthor().openPrivateChannel().complete().sendMessage("You got blinded for ~" + time + " " + sunit + ". **Wait out the timer!!!** And hopefully you are productive!").queue();
        } catch (Exception e) {
            System.out.println("Author didn't allow private message.");
        }

        LinkedList<Role> tmp = new LinkedList<>();
        try {
            tmp.add(ctx.getGuild().getRoleById("844136589163626526"));
        } catch (Exception e) {
            System.out.println("Role Blind doesnt exist anymore. This could be a serious issue.");
        }
        
        called.modifyMemberRoles(ctx.getMember(), tmp, permrole).complete();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<time> [unit] (Default is minutes)", "This removes all your roles and you won't see the server for that time and **there is no way to manually unblind yourself earlier**.");
    }
    
}
