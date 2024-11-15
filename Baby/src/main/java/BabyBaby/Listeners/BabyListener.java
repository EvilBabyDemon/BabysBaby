package BabyBaby.Listeners;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.audit.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;

import org.jetbrains.annotations.NotNull;

import BabyBaby.CmdHandler;
import BabyBaby.Command.commands.Public.*;
import BabyBaby.Command.commands.Slash.BlindSlashCMD;
import BabyBaby.data.Data;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class BabyListener extends ListenerAdapter {

    private final CmdHandler manager;
    // private SelfUser botUser;
    private User owner;
    public final JDA bot;
    public static HashMap<String, String> prefix = new HashMap<>();
    private final String ownerID = "223932775474921472";
    // private static boolean typing = true;

    public BabyListener(JDA bot) throws IOException {
        this.bot = bot;
        manager = new CmdHandler(bot);
        owner = bot.getUserById(ownerID);
    }

    // Role Removal
    @Override
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {
        if (!event.getGuild().getId().equals(Data.ETH_ID))
            return;

        List<Role> removed = event.getRoles();
        if (!removed.contains(event.getGuild().getRoleById(Data.BLIND_ID))) {
            return;
        }

        if (!BlindSlashCMD.blind.containsKey(event.getMember())) {
            return;
        }

        AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs();
        for (AuditLogEntry entry : logs) {
            if (entry.getType().equals(ActionType.MEMBER_ROLE_UPDATE)) {
                if (entry.getUser().getId().equals(event.getJDA().getSelfUser().getId()))
                    return;
                else
                    break;
            }
        }

        Guild blindServ = event.getGuild();
        Member blinded = event.getMember();
        String roles = "";

        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.prepareStatement("SELECT ROLES FROM ROLEREMOVAL WHERE USERID = ? AND GUILDID = ?;");
            stmt.setString(1, blinded.getId());
            stmt.setString(2, blindServ.getId());
            ResultSet rs = stmt.executeQuery();

            roles = rs.getString("ROLES");

            stmt.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        LinkedList<Role> addRole = new LinkedList<>();
        LinkedList<Role> delRole = new LinkedList<>();

        for (String roleID : roles.split(" ")) {
            Role role = blindServ.getRoleById(roleID);
            if (role == null) {
                System.out.println(roleID + "Role doesnt exist anymore");
                continue;
            }
            addRole.add(role);
        }

        try {
            delRole.add(blindServ.getRoleById(Data.BLIND_ID));
            blindServ.modifyMemberRoles(blinded, addRole, delRole).complete();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Role Blind doesnt exist anymore. This could be a serious issue.");
            blindServ.modifyMemberRoles(blinded, addRole, null).complete();
        }

        if (BlindSlashCMD.blind.get(blinded) != null) {
            ScheduledExecutorService blind = BlindSlashCMD.blind.get(blinded);
            BlindSlashCMD.forceSet.remove(BlindSlashCMD.blindexe.get(blind));
            BlindSlashCMD.blindexe.remove(blind);
            blind.shutdownNow();
        }
        BlindSlashCMD.blind.remove(blinded);

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.prepareStatement("DELETE FROM ROLEREMOVAL WHERE USERID = ? AND GUILDID = ?;");
            stmt.setString(1, blinded.getId());
            stmt.setString(2, blindServ.getId());
            stmt.execute();
            stmt.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

    }

    // User Typing
    /*
    @Override
    public void onUserTyping(UserTypingEvent event) {
        if (event.getGuild() != null && event.getMember().getId().equals("848908721900093440")
                && event.getChannel().getId().equals("768600365602963496")) {
            event.getGuild().getTextChannelById("789509420447039510")
                    .sendMessage("<@!223932775474921472> <:uhh:816589889898414100> <#768600365602963496>").queue();
        }
    }*/

    // Message Received
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {

        if (!event.isFromGuild()) {
            // main prefix is + in DM
            String prefixstr = "+";
            String raw = event.getMessage().getContentRaw();

            // starts with prefix -> send to command handler
            if (raw.startsWith(prefixstr)) {

                String[] split = event.getMessage().getContentRaw()
                        .replaceFirst("(?i)" + Pattern.quote(prefixstr), "")
                        .split("\\s+");

                List<String> args = Arrays.asList(split).subList(1, split.length);

                String cmdName = split[0].toLowerCase();

                UnBlindCMD cmd1 = new UnBlindCMD();
                TillBlindCMD cmd2 = new TillBlindCMD();

                if (cmdName.equals(cmd1.getName())) {
                    cmd1.privhandle(event.getAuthor(), args);
                } else if (cmdName.equals(cmd2.getName())) {
                    cmd2.privhandle(event.getAuthor(), args, event.getJDA());
                }

            }
            // if private message don't go into guild code
            return;
        }

        // Delete every msg in #newcomers
        if (event.getChannel().getId().equals(Data.ETH_NEWCOMERS_CH_ID) && !event.getAuthor().isBot()) {
            event.getMessage().delete().queue(Void -> Data.mydel++, Throwable -> Data.otherdel++);
            return;
        }

        User user = event.getAuthor();
        if (user.isBot() || event.isWebhookMessage()) {
            return;
        }

        String prefixstr = prefix.get(event.getGuild().getId());
        if (prefixstr == null || prefixstr.length() == 0)
            prefixstr = "+";
        String raw = event.getMessage().getContentRaw();

        // starts with prefix -> send to command handler
        if (raw.startsWith(prefixstr)) {
            manager.handle(event, prefixstr);
        }
    }

    User getOwner() {
        if (owner == null) {
            return bot.getUserById(ownerID);
        }
        return owner;
    }

    void sendDM(User user, String message) {
        user.openPrivateChannel().complete().sendMessage(message).queue();
    }

}
