package BabyBaby.Command.commands.Slash;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import BabyBaby.Command.commands.Public.GetReminder;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import BabyBaby.Command.ISlashCMD;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class RemindSlashCMD implements ISlashCMD {

    @Override
    public String getName() {
        return "remind";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {

        /*
         * CREATE TABLE "REMINDERS" (
         * "USERID" TEXT,
         * "TEXTS" TEXT,
         * "GUILDID" TEXT,
         * "CHANNELID" TEXT,
         * "TIME" TEXT
         * );
         * 
         */

        String unit = event.getOption("unit") != null ? event.getOption("unit").getAsString() : null;

        double time = event.getOption("time").getAsDouble();

        if (time <= 0) {
            Helper.unhook("Use positive numbers thx!", failed, hook, event.getUser());
            return;
        }

        Object[] retrieverObj = Helper.getUnits(unit, time);
        String strUnit = "" + retrieverObj[0];
        long rounder = (long) retrieverObj[1];

        if (rounder <= 0) {
            Helper.unhook("Use numbers above 0 second pls!", failed, hook, event.getUser());
            return;
        }

        String message = event.getOption("text", "Just a Reminder. Didn't specify about what.",
                OptionMapping::getAsString);
        String pk = "";
        long timesql = System.currentTimeMillis() + rounder * 1000;

        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.prepareStatement(
                    "INSERT INTO REMINDERS (USERID, TEXTS, GUILDID, CHANNELID, TIME) VALUES (?, ?, ?, ?, ?);");
            // SELECT last_insert_rowid() FROM REMINDERS;
            // SELECT MAX(_ROWID_) FROM <table> LIMIT 1;

            stmt.setString(1, event.getUser().getId());
            stmt.setString(2, message);
            stmt.setString(3, event.getGuild().getId());
            stmt.setString(4, event.getChannel().getId());
            stmt.setString(5, "" + timesql);

            stmt.execute();

            stmt = c.prepareStatement("SELECT MAX(_ROWID_) FROM REMINDERS LIMIT 1;");

            ResultSet rs = stmt.executeQuery();

            pk = rs.getString("MAX(_ROWID_)");

            stmt.close();
            c.close();
        } catch (Exception e) {
            Helper.unhook(e.getClass().getName() + ": " + e.getMessage(), failed, hook, event.getUser());
            return;
        }
        if (pk.equals("")) {
            Helper.unhook("Well I fucked up.... Couldnt get the pk.", failed, hook, event.getUser());
            return;
        }
        ScheduledExecutorService remind = Executors.newScheduledThreadPool(1);
        remind.schedule(new GetReminder(event.getUser(), event.getGuild(), message, event.getChannel().getId(), pk),
                rounder, TimeUnit.SECONDS);
        Helper.unhook(event.getUser().getAsMention() + " You will get Reminded in ~" + time + " " + strUnit + ".",
                failed, hook, event.getUser());

    }

    @Override
    public CommandDataImpl initialise(Guild eth) {
        CommandDataImpl remind = new CommandDataImpl(getName(), "A command to remind yourself.");

        remind.addOption(OptionType.NUMBER, "time", "In how many Time units do you want to get reminded?", true);
        remind.addOption(OptionType.STRING, "unit", "Default is minutes. Others are seconds, minutes, hours, days.");
        remind.addOption(OptionType.STRING, "text", "The thing you want to get reminded about");

        return remind;
    }

}
