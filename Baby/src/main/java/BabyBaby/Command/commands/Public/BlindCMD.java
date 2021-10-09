package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import javax.xml.namespace.QName;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.GetRolesBack;
import BabyBaby.data.Helper;
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

        boolean semester = false;
        int add = 0;
        try {
            semester = Boolean.parseBoolean(cmds.get(0));
            add = 1;
        } catch (Exception e) {
        }

        String unit = null;
        String amount = cmds.get(0+add);        
        if(cmds.size()>1+add){
            unit = cmds.get(1+add);
        } else {
            String[] retrieveStr = Helper.splitUnitAndTime(cmds.get(0+add));
            unit = retrieveStr[0];
            amount = retrieveStr[1];
        }
        

        roleRemoval(amount, ctx.getMember(), ctx.getGuild(), unit, false, ctx.getChannel(), semester);
        ctx.getMessage().delete().queueAfter(30, TimeUnit.SECONDS);
    }





    public void roleRemoval (String number, Member mem, Guild guild, String unit, boolean force, MessageChannel channel, Boolean semester){
        
        LinkedList<GuildChannel> gchan = new LinkedList<>();
        Role everyone = guild.getRoleById(guild.getId());
        
        for (GuildChannel guildChannel : guild.getChannels()) {
            if(semester && guildChannel.getParent() != null){
                String catName = guildChannel.getParent().getName().toLowerCase();
                if(catName.contains("gess")){
                    continue;
                }
            }
            if(!everyone.hasAccess(guildChannel)){
                gchan.add(guildChannel);
            }
        }

        Member silenced = mem;
        List<Role> begone = silenced.getRoles();
        LinkedList<Role> permrole = new LinkedList<>();

        

        double time;
        User blindUser = mem.getUser();
        ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);
        
        

        try{
            if(number.length() > 18 || Double.parseDouble(number) > Integer.MAX_VALUE){
                time=Integer.MAX_VALUE;
            } else {
                time = Double.parseDouble(number);
            }
        } catch (NumberFormatException e){
            channel.sendMessage("You probably forgot the space between the time and unit, if not use numbers pls!").complete().delete().queueAfter(30, TimeUnit.SECONDS);
            return;
        }

        if(time <= 0){
            channel.sendMessage("Use positive numbers thx!").complete().delete().queueAfter(30, TimeUnit.SECONDS);
            return;
        }			

        
        Object[] retrieverObj = Helper.getUnits(unit, time);
        String strUnit = ""+retrieverObj[0];
        long rounder = (long) retrieverObj[1];

        if(rounder <= 29){
            channel.sendMessage("Use values of at least 30 seconds please!").queue();
            return;
        } else if(force && rounder > 43200){
            channel.sendMessage("Your value has to be below 12 hours / 720 minutes (default unit) / 43200 seconds.").queue();
            return;
        }

        
        long timesql = (System.currentTimeMillis() + rounder*1000);


        Role highestbot = guild.getSelfMember().getRoles().get(0);

        //check if there is a role that is higher than a bot but also can see a channel
        for (Role role : begone) {
            String roleName = role.getName().toLowerCase();
            if(roleName.contains(". semester") || roleName.contains("all semesters")){
                continue;
            }
            for (GuildChannel guildChannel : gchan) {
                if(role.hasAccess(guildChannel)){
                    if(role.getPosition()>=highestbot.getPosition()){
                        channel.sendMessage("Sry you have a higher Role than this bot with viewing permissions. Can't take your roles away").complete().delete().queueAfter(30, TimeUnit.SECONDS);
                        return;
                    }
                    permrole.add(role);
                    break;
                }
            }
        }

        //Check if already in a group
        String id = mem.getId();
        for (int ids : BlindGroupCMD.groups.keySet()) {
            ArrayList<String> classList = BlindGroupCMD.groups.get(ids);
            if(classList.contains(id)){
                channel.sendMessage("You are still in a group. Pls leave that one first.").complete().delete().queueAfter(30, TimeUnit.SECONDS);
                return;
            }
        }


        


        Connection c = null;
        PreparedStatement stmt = null;
        String role = "";

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            for (Role roleToStr : permrole) {
                role += roleToStr.getId() + " ";
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

        String msg = " got blinded for ~" + time + " " + strUnit + ".";
        if(force){
            BlindForceCMD.force.add(scheduledclass);
            msg +=  " **Wait out the timer!!!** And hopefully you are productive!";
            Data.forcestats.add(mem.getId());
        } else {
            msg += " Either wait out the timer or write me (<@781949572103536650>) in Private chat \"+" + new UnBlindCMD().getName() + "\"";
        }
        channel.sendMessage(mem.getAsMention() + msg).complete().delete().queueAfter(30, TimeUnit.SECONDS);

        
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
