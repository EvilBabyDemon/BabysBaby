package BabyBaby.Command.commands.Public;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.Command.commands.Admin.AdminMuteBlindCMD;
import BabyBaby.Command.commands.Slash.BlindSlashCMD;
import BabyBaby.data.GetRolesBack;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class UnBlindCMD implements IPublicCMD {

    @Override
    public String getName() {
        return "unblind";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        /*
         * if(!MuteCMD.userMuted.containsKey(ctx.getAuthor())){
         * ctx.getAuthor().openPrivateChannel().queue((channel) -> {
         * channel.
         * sendMessage("You were never blinded. Or at least not by this bot. If you were pls report this problem to Lukas"
         * ).queue();
         * });
         * return;
         * }
         */
        actualcmd(ctx.getAuthor(), ctx.getArgs());
    }

    public void privhandle(User author, List<String> args) {
        actualcmd(author, args);
    }

    private void actualcmd(User author, List<String> cmds) {
        /*
         * if(author.getId().equals("177498563637542921")){
         * author.openPrivateChannel().queue(privchannel -> {
         * privchannel.
         * sendMessage("Nope, not getting unmuted till the timer runs out. You did this to yourself."
         * ).queue();
         * });
         * return;
         * }
         */

        String authorID = author.getId();

        LinkedList<GetRolesBack> classList = new LinkedList<>();

        for (Member member : BlindSlashCMD.blind.keySet()) {
            if (member.getId().equals(authorID)) {
                classList.add(BlindSlashCMD.blindexe.get(BlindSlashCMD.blind.get(member)));
            }
        }
        GetRolesBack blindclass = null;
        if (cmds.size() == 0) {
            switch (classList.size()) {
                case 0:
                    author.openPrivateChannel().queue(privchannel -> {
                        privchannel.sendMessage(
                                "You were never blinded. Or at least not by this bot. If you were pls report this problem to Lukas.")
                                .queue();
                    });
                    return;
                case 1:
                    blindclass = classList.get(0);
                    break;
                default:
                    author.openPrivateChannel().queue(privchannel -> {
                        privchannel.sendMessage("Pls use +" + getName()
                                + " <key> as there are multiple servers you are blinded on. These are the keys:")
                                .queue();
                        for (GetRolesBack classRoles : classList) {
                            privchannel
                                    .sendMessage("Key: " + classRoles.guild.getId() + " " + classRoles.guild.getName())
                                    .queue();
                        }
                        // TO DO FIX FOR multiple server
                    });
                    return;
            }
        } else {
            for (GetRolesBack classRole : classList) {
                if (classRole.guild.getId().equals(cmds.get(0))) {
                    blindclass = classRole;
                    break;
                }
            }
        }

        Member mem = blindclass.guild.getMember(blindclass.blind);
        if (AdminMuteBlindCMD.userBlinded.contains(mem)) {
            author.openPrivateChannel().complete().sendMessage("You got blinded by admins. You can't unblind yourself.")
                    .complete();
            return;
        }

        if (BlindSlashCMD.forceSet.contains(blindclass)) {
            author.openPrivateChannel().queue(privchannel -> {
                privchannel.sendMessage(
                        "You did a Force Blind. That are the consequences to your actions. Do not contact the admins! If it is an emergency contact Lukas, same if it is pobably a Bug.")
                        .queue();
            });
            return;
        }

        Guild blindServ = blindclass.guild;
        Member blinded = blindServ.getMember(blindclass.blind);

        ScheduledExecutorService blind = BlindSlashCMD.blind.get(blinded);
        BlindSlashCMD.blindexe.remove(blind);
        blind.shutdownNow();
        BlindSlashCMD.blind.remove(blinded);

        String roles = "";

        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.prepareStatement("SELECT ROLES FROM ROLEREMOVAL WHERE USERID = ? AND GUILDID = ?;");
            stmt.setString(1, authorID);
            stmt.setString(2, Data.ETH_ID);
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
            delRole.add(blindServ.getRoleById("844136589163626526"));
        } catch (Exception e) {
            System.out.println("Role Blind doesnt exist anymore. This could be a serious issue.");
        }

        blindServ.modifyMemberRoles(blinded, addRole, delRole).complete();

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

        try {
            author.openPrivateChannel().complete().sendMessage("You shall see light again! \n" +
                    "If you were only shortly blinded: **I advise to press CTRL + R to reload Discord as you may not see some messages else!**\n"
                    +
                    "If you were blinded for a long time: **Right click on the server and click \"Mark read All\" or Shift + ESC**")
                    .queue();
        } catch (Exception e) {
            System.out.println("Author didn't allow private message.");
        }
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "",
                "Command to unblind you early after using " + new BlindSlashCMD().getName() + ".");
    }

}
