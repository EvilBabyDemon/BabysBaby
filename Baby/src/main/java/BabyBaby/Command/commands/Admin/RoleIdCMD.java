package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import BabyBaby.Command.IAdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

public class RoleIdCMD implements IAdminCMD {

    @Override
    public String getName() {
        return "roleid";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        Connection c = null;
        Statement stmt = null;
        MessageChannel channel = ctx.getChannel();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT ID FROM ASSIGNROLES;");
            String result = "";
            while (rs.next()) {
                String id = rs.getString("id");

                String rolename = "deleted-role";
                try {
                    rolename = ctx.getGuild().getRoleById(id).getName();
                } catch (Exception e) {
                }

                result += id + " " + rolename + "\n";
            }
            rs.close();
            stmt.close();
            c.close();
            channel.sendMessage(result).queue();
        } catch (Exception e) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }

        channel.deleteMessageById(ctx.getMessage().getId()).queue();

    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get all SelfAssignable role IDs");
    }

}
