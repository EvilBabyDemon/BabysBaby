package BabyBaby.Command.commands.Admin;

import BabyBaby.Command.IAdminCMD;
import BabyBaby.Command.CommandContext;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;




public class GetWarningsFromUserCMD implements IAdminCMD{

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
        try{
            Member warned = ctx.getGuild().getMemberById(person);
            eb.setAuthor("Warnings from " + warned.getEffectiveName() + " (" + warned.getUser().getAsTag() + ")", warned.getUser().getAvatarUrl(), warned.getUser().getAvatarUrl());
        } catch (Exception e){
            eb.setAuthor("Warnings from " + person);
            return;
        }
        
        eb.setColor(1);


        Connection c = null;
        PreparedStatement stmt = null;

        try { 	
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            
            String sql = "SELECT * FROM WARNINGS WHERE USER = ?;";
            stmt = c.prepareStatement(sql);
            stmt.setLong(1, Long.parseLong(person));
            ResultSet rs = stmt.executeQuery();
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

        channel.sendMessageEmbeds(eb.build()).queue();

        ctx.getMessage().addReaction(Data.check).queue();
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get all the warnings from a specific User.");
    }
    
}
