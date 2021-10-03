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
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ReminderCMD implements PublicCMD {

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

        if(cmds.size()==0){
            ctx.getChannel().sendMessage("Arguments missing").queue();
            return;
        }

        String unit = null;
        String number = cmds.get(0);        
        String[] retrieveStr = Helper.splitUnitAndTime(cmds.get(0));
        unit = retrieveStr[0];
        number = retrieveStr[1];

        boolean extraUnit = false;
        if(unit == null){
            unit = cmds.get(1);
            extraUnit = true;
        }
		
        double time;
        


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

        Object[] retrieverObj = Helper.getUnits(unit, time);
        String strUnit = ""+retrieverObj[0];
        long rounder = (long) retrieverObj[1];
        

        if(rounder <= 0){
            channel.sendMessage("Use numbers above 0 second pls!").queue();
            return;
        }

        String message = ctx.getMessage().getContentRaw();
        try{
        message = message.substring(1 + getName().length() + 1 + cmds.get(0).length() + 1 + (extraUnit ? cmds.get(1).length() + 1 : 0));
        } catch (Exception e){
            message = "Just a Reminder. Didn't specify about what.";
        }
        String pk = "";
        long timesql = System.currentTimeMillis() + rounder*1000;

        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
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
        remind.schedule(new GetReminder(ctx.getAuthor(), ctx.getGuild(), message, ctx.getChannel().getId(), pk), rounder , TimeUnit.SECONDS);

        channel.sendMessage(ctx.getAuthor().getAsMention() + " You will get Reminded in ~" + time + " " + strUnit + ".").complete().delete().queueAfter(10, TimeUnit.SECONDS);


    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<time> <Unit> [Your Text could be written here]", "Command to remind you.");
    }
    
}
