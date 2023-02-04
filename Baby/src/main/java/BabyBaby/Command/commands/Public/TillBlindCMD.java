package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class TillBlindCMD implements IPublicCMD {

    @Override
    public String getName() {
        return "till";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        actualcmd(ctx.getAuthor(), ctx.getArgs(), ctx.getJDA());
    }

    public void privhandle(User author, List<String> args, JDA jda) {
        actualcmd(author, args, jda);
    }

    private void actualcmd(User author, List<String> cmds, JDA jda) {

        Connection c = null;
        PreparedStatement stmt = null;
        String user = "";

        try {

            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.prepareStatement("SELECT * FROM ROLEREMOVAL WHERE USERID = ?;");
            stmt.setString(1, author.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String guildname = jda.getGuildById(rs.getString("GUILDID")).getName();

                long ms = Long.parseLong(rs.getString("MUTETIME")) - System.currentTimeMillis();

                user += guildname + " | <t:" + ms + ":R> left (<t:" + ms + ":F>)) \n ";
            }

            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("How long you are still blinded:", null);
        eb.setColor(1);
        eb.setDescription(user);
        eb.setFooter("Summoned by: " + author.getAsTag(), author.getAvatarUrl());

        try {
            author.openPrivateChannel().complete().sendMessageEmbeds(eb.build()).queue();
        } catch (Exception e) {
            System.out.println("Author didn't allow private message.");
        }
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Command to find out how long you are blind.");
    }

}
