package BabyBaby.Command.commands.Admin;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;




public class GetWarningsFromUser implements AdminCMD{

    @Override
    public void handleOwner(CommandContext ctx) {
        handleAdmin(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getAdminHelp(prefix);
    }

    @Override
    public String getName() {
        return "warned";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel();
    
        String person = ctx.getArgs().get(0);
        person = person.replace("<", "");
        person = person.replace(">", "");
        person = person.replace("!", "");
        person = person.replace("@", "");

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Warnings from " + ctx.getGuild().getMemberById(person).getAsMention(), null);
        eb.setColor(1);


        Connection c = null;
        PreparedStatement stmt = null;

        try { 	
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");

            
            String sql = "SELECT * FROM WARNINGS WHERE USER = ?;";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, person);
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                String time = rs.getString("DATE");
                String reason = rs.getString("REASON");
                eb.addField(time, reason, true);
            }
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }
        
        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();
        eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());

        channel.sendMessage(eb.build()).queue();

        ctx.getMessage().addReaction(data.check).queue();
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get all the users that got a Warning.");
    }
    
}
