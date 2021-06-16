package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class GetUnmutePerson implements Runnable {
    public User reminder;
    public Guild guild;

    public GetUnmutePerson(User user, Guild tempG) {
        reminder = user;
        guild = tempG;
    }

    public void run() {	

        Role muteR = guild.getRoleById(Data.stfuID);

        guild.removeRoleFromMember(guild.getMember(reminder), muteR).queue();

        Connection c = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.prepareStatement("DELETE FROM ADMINMUTE WHERE USERID = ? AND GUILDID = ?;");
            stmt.setString(1, reminder.getId());
            stmt.setString(2, guild.getId());
            stmt.execute();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            e.printStackTrace(); 
            return;
        }
        
        MessageChannel log = guild.getTextChannelById(Data.modlog);

        User bot = guild.getMemberById("781949572103536650").getUser();

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(bot.getAsTag() + " (" + bot.getId() + ")", bot.getAvatarUrl(), reminder.getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(reminder.getAvatarUrl());

        eb.setDescription(":loud_sound: **Unmuted ** " + reminder.getAsMention() + "(" + reminder.getAsTag() +")"+ " \n :page_facing_up: **Reason:** Mute Duration Expired");

        log.sendMessage(eb.build()).queue();

        if(MutePersonCMD.userMuted.get(guild.getMember(reminder))==null){
            MutePersonCMD.userMuted.remove(guild.getMember(reminder));
        } else {
            MutePersonCMD.userMuted.remove(guild.getMember(reminder));
        }


        
        
    }
}