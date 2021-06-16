package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.GetRolesBack;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.*;

public class BlindCMD implements PublicCMD{
    
    public static HashMap<Member, ScheduledExecutorService> blind = new HashMap<>();
    public static HashMap<ScheduledExecutorService, GetRolesBack> blindexe = new HashMap<>();


    
    @Override
    public String getName() {
        return "blind";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        if(!ctx.getGuild().getId().equals(Data.ethid))
            return;

        ctx.getMessage().addReaction(Data.check).queue();

        List<String> cmds = ctx.getArgs();

        if(cmds.size() == 0){
            ctx.getChannel().sendMessage("The command is +" + getName() +" <time> [unit] (default unit is minutes)").queue();
            return;
        }

        String unit = null;
        String amount;
        
        
        amount = cmds.get(0);        
        if(cmds.size()>1){
            unit = cmds.get(1);
        } else {
            String s = cmds.get(0);
            for(int i = 0; i < s.length(); i++) {
                if (Character.isLetter(s.charAt(i))) {
                amount = s.substring(0, i);
                unit = s.substring(i, i+1);
                break;
                }
            }
        }
        

        roleRemoval(amount, ctx.getMember(), ctx.getGuild(), unit, false, ctx.getChannel());

    }





    public void roleRemoval (String number, Member mem, Guild guild, String unit, boolean force, MessageChannel channel){
        
        LinkedList<GuildChannel> gchan = new LinkedList<>();
        Role everyone = guild.getRoleById(guild.getId());
        
        for (GuildChannel var : guild.getChannels()) {
            if(!everyone.hasAccess(var)){
                gchan.add(var);
            }
        }

        Member silenced = mem;
        List<Role> begone = silenced.getRoles();
        LinkedList<Role> permrole = new LinkedList<>();

        

        double time;
        String sunit;
        User blindUser = mem.getUser();
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
        
        if(unit == null) {
            sunit = "minutes";
            rounder = (long) (time*60);
        } else {
            unit = unit.toLowerCase();
            if (unit.startsWith("h")){
                sunit = "hours";
                rounder = (long) (time*3600);
            } else if(unit.startsWith("m")){
                sunit = "minutes";
                rounder = (long) (time*60);
            } else if(unit.startsWith("d")){
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
        } else if(force && rounder > 43200){
            channel.sendMessage("Your value has to be below 12 hours (43200 seconds).").queue();
            return;
        }

        
        long timesql = (System.currentTimeMillis() + rounder*1000);


        Role highestbot = guild.getSelfMember().getRoles().get(0);

        //check if there is a role that is higher than a bot but also can see a channel
        for (Role var : begone) {
            for (GuildChannel var2 : gchan) {
                if(var.hasAccess(var2)){
                    if(var.getPosition()>=highestbot.getPosition()){
                        channel.sendMessage("Sry you have a higher Role than this bot with viewing permissions. Can't take your roles away").queue();
                        return;
                    }
                    permrole.add(var);
                    break;
                }
            }
        }

        //Check if already in a group
        String id = mem.getId();
        for (int ids : BlindGroupCMD.groups.keySet()) {
            ArrayList<String> var = BlindGroupCMD.groups.get(ids);
            if(var.contains(id)){
                channel.sendMessage("You are still in a group. Pls leave that one first.").queue();
                return;
            }
        }


        


        Connection c = null;
        PreparedStatement stmt = null;
        String role = "";

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            for (Role var : permrole) {
                role += var.getId() + " ";
            }

            stmt = c.prepareStatement("INSERT INTO ROLEREMOVAL (USERID, GUILDID, MUTETIME, ROLES, ADMINMUTE) VALUES (?, ?, ?, ?, ?);");
            stmt.setString(1, mem.getId());
            stmt.setString(2, guild.getId());
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

        
        
        GetRolesBack scheduledclass = new GetRolesBack(blindUser, guild, role);
        mute.schedule(scheduledclass, rounder , TimeUnit.SECONDS);

        
        
        
        blind.put(mem, mute);
        blindexe.put(mute, scheduledclass);

        String msg = " got blinded for ~" + time + " " + sunit + ".";
        if(force){
            BlindForceCMD.force.add(scheduledclass);
            msg +=  " **Wait out the timer!!!** And hopefully you are productive!";
        } else {
            msg += " Either wait out the timer or write me (<@781949572103536650>) in Private chat \"+" + new UnBlindCMD().getName() + "\"";
        }
        channel.sendMessage(mem.getAsMention() + msg).queue();
        
        LinkedList<Role> addrole = new LinkedList<>();
        try {
            addrole.add(guild.getRoleById("844136589163626526"));
        } catch (Exception e) {
            System.out.println("Role Blind doesnt exist anymore. This could be a serious issue.");
        }
        
        guild.modifyMemberRoles(mem, addrole, permrole).complete();
        try {
            blindUser.openPrivateChannel().complete().sendMessage("You" + msg).queue();
        } catch (Exception e) {
            System.out.println("Author didn't allow private message.");
        }
    } 



    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<time> [unit] (Default is minutes)", "This removes all your roles and you won't see the server for that time but its still work in progress.");
    }
    
}
