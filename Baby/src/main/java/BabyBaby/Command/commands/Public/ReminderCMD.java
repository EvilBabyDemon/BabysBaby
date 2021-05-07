package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ReminderCMD implements PublicCMD {


    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

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
        return "remind";
    }

    @Override
    public void handlePublic(CommandContext ctx) {

        /*
        CREATE TABLE "REMINDERS" (
        "USERID"	TEXT,
        "TEXTS"	TEXT,
        "GUILDID"	TEXT,
        "CHANNELID"	TEXT,
        "TIME"	TEXT
        );

        */

        MessageChannel channel = ctx.getChannel();

        List<String> cmds = ctx.getArgs();
        String number = cmds.get(0);
		
        double time;
        String sunit;
        
        if(cmds.size() == 0){
            channel.sendMessage("Something went wrong please get my help page with (prefix)help " + getName()).queue();
            return;
        }

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
            rounder = (long) (time*60);
        } else {
            sunit = "seconds";
            rounder = (long) (time);
        }
        

        if(rounder <= 0){
            channel.sendMessage("Use numbers above 1 second pls!").queue();
            return;
        }

        String message = ctx.getMessage().getContentRaw();
        try{
        message = message.substring(1 + getName().length() + 1 + cmds.get(0).length() + 1 + cmds.get(1).length() + 1);
        } catch (Exception e){
            message = "Just a Reminder. Didn't specify about what.";
        }
        String pk = "";
        long timesql = (System.currentTimeMillis() + rounder*1000);

        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            
            stmt = c.prepareStatement("INSERT INTO REMINDERS (USERID, TEXTS, GUILDID, CHANNELID, TIME) VALUES (?, ?, ?, ?, ?);");
            //SELECT last_insert_rowid() FROM REMINDERS;
            //SELECT MAX(_ROWID_) FROM <table> LIMIT 1;

            stmt.setString(1, ctx.getAuthor().getId());
            stmt.setString(2, message);
            stmt.setString(3, ctx.getGuild().getId());
            stmt.setString(4, ctx.getChannel().getId());
            stmt.setString(5, "" + timesql);

            stmt.execute();

            stmt = c.prepareStatement("SELECT MAX(_ROWID_) FROM REMINDERS LIMIT 1;");
            
            ResultSet rs = stmt.executeQuery();

            pk = rs.getString("MAX(_ROWID_)");
            
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }
        if(pk.equals("")){
            ctx.getChannel().sendMessage("Well I fucked up.... Couldnt get the pk.").queue();
            return;
        }
        ScheduledExecutorService remind = Executors.newScheduledThreadPool(1);
        remind.schedule(new GetReminder(ctx.getAuthor(), ctx.getGuild(), message, ctx.getChannel().getId(), "pkey"), rounder , TimeUnit.SECONDS);

        channel.sendMessage(ctx.getAuthor().getAsMention() + " You will get Reminded in ~" + time + " " + sunit + ".").complete().delete().queueAfter(10, TimeUnit.SECONDS);


    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<time> <Unit (s,m,h or d)> [Your Text could be written here]", "Cmd to Renind me.");
    }
    
}
