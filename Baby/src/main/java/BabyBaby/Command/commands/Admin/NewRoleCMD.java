package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.components.Button;

public class NewRoleCMD implements AdminCMD {

    @Override
    public String getName() {
        return "newrole";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        String id = ctx.getArgs().get(0);
        Role newRole = ctx.getGuild().getRoleById(id);

        Connection c = null;
        PreparedStatement stmt = null;
        String emoteStr = "";
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.prepareStatement("SELECT * FROM ASSIGNROLES WHERE ID=?;");
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();

            emoteStr = rs.getString("EMOTE");

        } catch (Exception e) {
            return;
        }
        boolean gemo = false;
        try {
            Long.parseLong(emoteStr);
            gemo = true;
        } catch (Exception e) {
        }
        String msgID = ctx.getChannel().sendMessage("Get " + newRole.getName() + " with this button:").setActionRow(Button.primary(emoteStr, gemo ? Emoji.fromEmote(ctx.getJDA().getEmoteById(emoteStr)): Emoji.fromUnicode(emoteStr))).complete().getId();
        Data.buttonid.add(msgID);
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<role id>", "Sends a button with which you can get the role specified. For announcements and stuff like that");
    }
    
}
