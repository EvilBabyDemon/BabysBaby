package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.LinkedList;

import BabyBaby.Command.IAdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class AddRoleCMD implements IAdminCMD {

    @Override
    public String getName() {
        return "addrole";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        LinkedList<String> cmds = new LinkedList<>(ctx.getArgs());

        String id = cmds.remove(0);
        String emote = cmds.remove(0);

        if (emote.contains("<")) {
            emote = emote.split(":")[2];
            emote.replace(">", "");
        }
        MessageChannel channel = ctx.getChannel();
        String categ = "";
        if (cmds.size() == 0) {
            categ = "Other";
        } else {
            for (String strCateg : cmds) {
                categ += strCateg + " ";
            }
            categ = categ.substring(0, categ.length() - 1);
        }

        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            String sql = "INSERT INTO ASSIGNROLES (ID,CATEGORIES,EMOTE) VALUES (?, ?, ?);";
            pstmt = c.prepareStatement(sql);
            pstmt.setLong(1, Long.parseLong(id));
            pstmt.setString(2, categ);
            pstmt.setString(3, emote);
            pstmt.executeUpdate();

            pstmt.close();
            c.close();
        } catch (Exception e) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            return;
        }

        Data.emoteassign.put(emote, id);
        Data.roles.add(id);
        ctx.getMessage().addReaction(Data.check).queue();

    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<RoleID> <emote> [category]",
                "Command to add a selfassignable role.");
    }

}
