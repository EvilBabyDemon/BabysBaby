package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class FlashedCMD implements PublicCMD {

    @Override
    public String getName() {
        return "flashed";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel();
        int countUsers = 0;
        String userNames = "";
        String cache = "";
        
        Guild called = ctx.getGuild();

        Connection c = null;
        Statement stmt = null;
         

        ResultSet rs;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT * FROM ROLEREMOVAL WHERE GUILDID =" + called.getId() + ";");
            while ( rs.next() ) {
                String mutedUser = rs.getString("USERID");
                countUsers++;
                userNames += called.getMemberById(mutedUser).getAsMention() + "(" + Math.round(((Long.parseLong(rs.getString("MUTETIME"))-System.currentTimeMillis())/60000.0)) + "m), ";
                cache += called.getMemberById(mutedUser).getAsMention();
            }
            rs.close();
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage( e.getClass().getName() + ": " + e.getMessage()).queue();
            e.printStackTrace(); 
            return;
        }          
        
        

        String shouldbeLearning = "<@!223932775474921472>";

        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname() : ctx.getMember().getEffectiveName();

        EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("People who are learning or rather should be! (Blinded)", null);
            eb.setColor(1);
            eb.addField("" + countUsers, userNames, false);
            eb.addField("people who should be studying right now",  shouldbeLearning, false);
            // eb.addBlankField(false);
            eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());
        channel.sendMessage("cache refresh").complete().editMessage("a " + cache).complete().delete().queue();
        channel.sendMessageEmbeds(eb.build()).queue();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get all people that blinded themself to concentrate better.");
    }
    
}
