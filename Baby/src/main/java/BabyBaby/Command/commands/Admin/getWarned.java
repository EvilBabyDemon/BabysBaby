package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class getWarned implements AdminCMD {

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
        return "getwarned";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel();
        HashSet<String> UserIds = new HashSet<>();
        
        Connection c = null;
        Statement stmt = null;

        try { 	
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:testone.db");

            stmt = c.createStatement();
            String sql = "SELECT USER FROM WARNINGS;";
            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next() ) 
                UserIds.add(rs.getString("USER"));
            
            stmt.close();
            c.close();
        } catch ( Exception e ) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("All Users with Warnings.", null);
        eb.setColor(1);

        String all = "";
        if(UserIds.size() != 0){
            for (String var : UserIds){
                all += ctx.getGuild().getMemberById(var).getAsMention() + "\n";
            }
        }

        eb.setDescription(all);
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
