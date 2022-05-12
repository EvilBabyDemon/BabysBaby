package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.List;

import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
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
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        List<Emote> emoList = guild.getJDA().getEmotesByName("dinkdonk", true);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setThumbnail(emoList.get(0).getImageUrl());
        eb.setTitle("Reminder!");
        eb.setColor(0);
        eb.setDescription(text);
        String message = "New Reminder from " + guild.getTextChannelById(channelID) != null
                ? guild.getTextChannelById(channelID).getAsMention()
                : guild.getName() + " " + channelID + "!";
        reminder.openPrivateChannel().complete().sendMessage(message).setEmbeds(eb.build()).queue();
        // guild.getTextChannelById(channelID).sendMessage(reminder.getAsMention()).setEmbeds(eb.build()).queue();
    }
}