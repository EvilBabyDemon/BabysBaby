package BabyBaby.Command.commands.Slash;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.ISlashCMD;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class RolesleftSlashCMD implements ISlashCMD {
    Comparator<Role> compRole = new Comparator<>() {
        @Override
        public int compare(Role o1, Role o2) {
            return o2.getPosition() - o1.getPosition();
        }
    };

    @Override
    public String getName() {
        return "rolesleft";
    }

    @Override
    public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
        Guild guild = event.getGuild();

        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs;
        HashMap<String, LinkedList<Role>> roles = new HashMap<>();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);
            stmt = c.prepareStatement("SELECT * FROM ASSIGNROLES;");
            rs = stmt.executeQuery();
            while (rs.next()) {
                String roleID = rs.getString("ID");
                String categ = rs.getString("categories");
                LinkedList<Role> ids = roles.getOrDefault(categ, new LinkedList<>());
                ids.add(guild.getRoleById(roleID));
                ids.sort(compRole);
                roles.put(categ, ids);
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }

        String message = "";
        List<Role> user = event.getMember().getRoles();
        for (String categ : roles.keySet()) {
            LinkedList<Role> roleList = roles.get(categ);
            String pings = "";
            for (Role role : roleList) {
                if (!user.contains(role)) {
                    pings += role.getAsMention() + "\n";
                }
            }
            if (pings.length() > 1) {
                message += "**" + categ + "**\n" + pings;
            }
        }
        if (message.length() == 0) {
            message = "You have all roles! Maybe tone it down a bit...";
        } else {
            message = "These are all the roles you can get:\n" + message;
        }

        Helper.unhook(message, failed, hook, event.getUser());
    }

    @Override
    public CommandDataImpl initialise(Guild eth) {
        CommandDataImpl rolesleft = new CommandDataImpl(getName(), "A command to see which roles you still could get.");
        return rolesleft;
    }

}
