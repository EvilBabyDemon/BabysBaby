package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.GetUnmute;
import BabyBaby.data.Helper;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class MuteCMD implements PublicCMD {
    public static HashMap<Member, ScheduledExecutorService> userMuted = new HashMap<>();
    public static HashMap<ScheduledExecutorService, GetUnmute> variables = new HashMap<>();

    @Override
    public void handleAdmin(CommandContext ctx) {
        ctx.getChannel().sendMessage("This feature is disabled for Admins because you cant use it anyway..... <:kekwait:786877342072832020>").queue();
    }

    @Override
    public String getName() {
        return "muteme";
    }

    @Override
    public void handlePublic(CommandContext ctx) {

        if(!ctx.getGuild().getId().equals(Data.ethid))
            return;

        MessageChannel channel = ctx.getChannel();
        Guild called = ctx.getGuild();

        List<String> cmds = ctx.getArgs();
		
        String unit = null;
        String number = cmds.get(0);        
        if(cmds.size()>1){
            unit = cmds.get(1);
        } else {
            String[] retrieveStr = Helper.splitUnitAndTime(cmds.get(0));
            unit = retrieveStr[0];
            number = retrieveStr[1];
        }

        double time;
        User muteUser = ctx.getAuthor(); 
        ScheduledExecutorService mute = Executors.newScheduledThreadPool(1);
        
        if(cmds.size() == 0){
            channel.sendMessage("The command is +" + getName() +" <time> [unit] (default unit is minutes)").queue();
            return;
        }

        try{
            if(number.length() > 18 || Double.parseDouble(number) > Integer.MAX_VALUE){
                time=Integer.MAX_VALUE;
            } else {
                    time = Double.parseDouble(number);
            }
        } catch (NumberFormatException e){
            channel.sendMessage("Those are not numbers.").queue();
            return;
        }

        if(time <= 0){
            channel.sendMessage("Use positive numbers thx!").queue();
            return;
        }			

        
        List<Role> stfuroles = called.getRolesByName("STFU", true); 
        Role muteR = stfuroles.get(0);


        Object[] retrieverObj = Helper.getUnits(unit, time);
        String strUnit = ""+retrieverObj[0];
        long rounder = (long) retrieverObj[1];

        if(rounder <= 0){
            channel.sendMessage("Use numbers above 0 seconds pls!").queue();
            return;
        }

        
        Connection c = null;
        PreparedStatement stmt = null;
        long timesql = (System.currentTimeMillis() + rounder*1000);
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.prepareStatement("INSERT INTO USERS (ID, GUILDID, MUTETIME) VALUES (?,?,?);");           

            stmt.setString(1, ctx.getAuthor().getId());
            stmt.setString(2, called.getId());
            stmt.setString(3, timesql+""); 
            stmt.executeUpdate();

            stmt.close();
            c.close();
            
        } catch ( Exception e ) {
            channel.sendMessage( e.getClass().getName() + ": " + e.getMessage()).queue();
            e.printStackTrace(); 
            return;
        }


        GetUnmute scheduledclass = new GetUnmute(muteUser, called, muteR);
        mute.schedule(scheduledclass, rounder , TimeUnit.SECONDS);

        userMuted.put(ctx.getMember(), mute);
        variables.put(mute, scheduledclass);

        called.addRoleToMember(ctx.getMember(), muteR).queue();
        channel.sendMessage("You got muted for ~" + time + " " + strUnit + ". Either wait out the timer or write me (<@781949572103536650>) in Private chat \"+" + new UnmuteMeCMD().getName() + "\"").queue();

    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<time> [unit]", "Mute yourself to learn better.");
    }
    
}
