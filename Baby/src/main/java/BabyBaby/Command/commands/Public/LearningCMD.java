package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.data.data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class LearningCMD implements PublicCMD {

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
        return "learning";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel();
        int countUsers = 0;
        String userNames = "";
        
        Guild called = ctx.getGuild();

        Connection c = null;
        Statement stmt = null;
         

        ResultSet rs;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(data.db);
            c.setAutoCommit(true);
            
            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT * FROM USERS WHERE GUILDUSER =" + called.getId() + ";");
            while ( rs.next() ) {
                String mutedUser = rs.getString("ID");
                countUsers++;
                userNames += called.getMemberById(mutedUser).getAsMention() + "(" + Math.round(((Long.parseLong(rs.getString("MUTETIME"))-System.currentTimeMillis())/60000.0)) + "m), ";
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
            eb.setTitle("People who are learning or rather should be!", null);
            eb.setColor(1);
            // eb.setDescription("Nothing to see here.");
            eb.addField("" + countUsers, userNames, false);
            eb.addField("people who should be studying right now",  shouldbeLearning, false);
            // eb.addBlankField(false);
            eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());

        channel.sendMessage(eb.build()).queue();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        // TODO Auto-generated method stub
        return null;
    }
    
}
