package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

public class GetReminder implements Runnable {
    public User reminder;
    public Guild guild;
    public String text;
    public String channelID;
    public String pk;

    public GetReminder(User user, Guild tempG, String texts, String channel, String pkey) {
        reminder = user;
        guild = tempG;
        text = texts;
        pk = pkey;
        channelID = channel;

    }

    public void run() {	
        Connection c = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.prepareStatement("DELETE FROM REMINDERS WHERE PK = ?;");
            stmt.setString(1, pk);
            stmt.execute();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }
        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Reminder!");
        eb.setColor(0);
        eb.setDescription(text);

        guild.getTextChannelById(channelID).sendMessage(reminder.getAsMention()).setEmbeds(eb.build()).queue();
    }
}