package BabyBaby.Command.commands.Public;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import BabyBaby.ColouredStrings.ColouredStringAsciiDoc;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;

public class GetRoleCMD implements IPublicCMD {
    boolean flipflop = false;

    @Override
    public boolean getWhiteListBool() {
        return true;
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        handlePublic(ctx);

    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        handlePublic(ctx);
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getPublicHelp(prefix);
    }

    @Override
    public String getName() {
        return "role";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        if (!ctx.getGuild().getId().equals(Data.ETH_ID))
            return;

        MessageChannel channel = ctx.getChannel();
        ctx.getMessage().delete().queueAfter(15, TimeUnit.SECONDS);
        List<String> cmds = ctx.getArgs();

        Connection c = null;
        Statement stmt = null;
        HashSet<String> cats = new HashSet<String>();

        ResultSet rs;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.createStatement();

            rs = stmt.executeQuery("SELECT categories FROM ASSIGNROLES;");
            while (rs.next()) {
                String cat = rs.getString("categories");
                cats.add(cat);
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (cmds.size() != 0) {

            HashMap<String, String> emoterolelist = Data.emoteassign;
            HashMap<String, Object> namerole = new HashMap<>();
            for (String roleID : emoterolelist.keySet()) {
                Role tmp = ctx.getGuild().getRoleById(emoterolelist.get(roleID));
                namerole.put(tmp.getName().toLowerCase(), tmp);
            }
            for (String strCats : cats) {
                namerole.put(strCats.toLowerCase(), strCats);
            }

            String msg = ctx.getMessage().getContentRaw();
            msg = msg.substring(1 + getName().length() + 1);
            msg.toLowerCase();
            if (msg.length() > 100) {
                channel.sendMessage("I think you are using this command wrong...").complete().delete().queueAfter(10,
                        TimeUnit.SECONDS);
                return;
            }

            if (namerole.containsKey(msg)) {

                if (namerole.get(msg) instanceof Role)
                    gibRole(ctx, (Role) namerole.get(msg));
                else
                    sendEmbed(ctx, (String) namerole.get(msg));
                return;
            }

            String finalorso = msg;

            List<Object[]> minedit = namerole.keySet().parallelStream()
                    .map(role -> new Object[] { Helper.minDistance(finalorso, role), namerole.get(role) })
                    .collect(Collectors.toList());

            LinkedList<Object[]> smallest = new LinkedList<Object[]>();

            smallest.add(minedit.remove(0));
            int smallestint = (int) smallest.get(0)[0];
            for (Object[] minEditObj : minedit) {
                int x = (int) minEditObj[0];
                if (smallestint == x) {
                    smallest.add(minEditObj);
                } else if (smallestint > x) {
                    smallest = new LinkedList<>();
                    smallest.add(minEditObj);
                    smallestint = x;
                }
            }

            if (smallestint == 100) {
                channel.sendMessage("I don't think this role exists.").complete().delete().queueAfter(10,
                        TimeUnit.SECONDS);
                ;
                return;
            }

            if (smallest.size() != 1) {
                channel.sendMessage(
                        "Sorry you gotta write more precise as there is more than one Role you could have meant.")
                        .complete().delete().queueAfter(10, TimeUnit.SECONDS);
                return;
            }

            if (smallest.size() == 1) {
                if (smallest.get(0)[1] instanceof Role)
                    gibRole(ctx, (Role) smallest.get(0)[1]);
                else
                    sendEmbed(ctx, (String) smallest.get(0)[1]);
                return;
            }
        }

        String msg = "";

        LinkedList<LinkedList<String>> emotes = new LinkedList<>();
        LinkedList<String> categ = new LinkedList<>();
        LinkedList<String> roles = new LinkedList<>();

        for (String strCats : cats) {
            HashMap<Role, Object[]> sorting = new HashMap<>();
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);

                stmt = c.createStatement();

                Guild called = ctx.getGuild();
                rs = stmt.executeQuery("SELECT * FROM ASSIGNROLES WHERE categories='" + strCats + "';");
                while (rs.next()) {
                    String rcat = rs.getString("ID");
                    String emoteStr = rs.getString("EMOTE");
                    String orig = emoteStr;

                    try {
                        Long.parseLong(emoteStr);
                        try {
                            emoteStr = ctx.getJDA().getEmojiById(emoteStr).getAsMention();
                        } catch (Exception e) {
                            emoteStr = "ERROR";
                        }
                    } catch (Exception e) {
                    }

                    msg = emoteStr + " : " + called.getRoleById(rcat).getAsMention() + "\n";
                    sorting.put(called.getRoleById(rcat), new Object[] { orig, msg });
                }
                rs.close();
                stmt.close();
                c.close();
            } catch (Exception e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
                return;
            }

            LinkedList<Object[]> sorted = rolesorter(sorting);
            LinkedList<String> tempo = new LinkedList<>();
            msg = "";
            for (Object[] obj : sorted) {
                tempo.add((String) obj[0]);
                msg += (String) obj[1];
            }

            emotes.add(tempo);
            categ.add(strCats);
            roles.add(msg);
            msg = "";
        }

        LinkedList<EmbedBuilder> emb = new LinkedList<>();
        int count = 0;
        HashMap<String, String> tmp = new HashMap<>();
        LinkedList<Integer> emotewhen = new LinkedList<>();

        for (int i = 0; i < categ.size(); i++) {
            int x = roles.get(i).split("\n").length;
            count += x;
            if (count > 20) {
                emotewhen.add(i);
                emb.add(embeds(tmp, ctx));
                count = x;
                tmp = new HashMap<>();
            }
            tmp.put(categ.get(i), roles.get(i));
        }

        emotewhen.add(categ.size());
        emb.add(embeds(tmp, ctx));
        int max = 0;
        for (EmbedBuilder eb : emb) {
            LinkedList<String> emoList = new LinkedList<>();
            max = emotewhen.remove(0) - max;
            for (int i = 0; i < max; i++) {
                try {
                    emoList.addAll(emotes.remove(0));
                } catch (Exception e) {
                    break;
                }

            }

            ArrayList<Button> butt = new ArrayList<>();
            for (String emoID : emoList) {
                boolean gemo = false;
                try {
                    Long.parseLong(emoID);
                    gemo = true;
                } catch (Exception e) {
                }

                try {
                    butt.add(Button.primary(emoID,
                            gemo ? ctx.getJDA().getEmojiById(emoID) : Emoji.fromUnicode(emoID)));
                } catch (Exception e) {
                    ctx.getChannel().sendMessage("Reaction with ID:" + emoID + " is not accessible.").complete()
                            .delete().queueAfter(10, TimeUnit.SECONDS);
                }
            }

            MessageCreateAction msgAct = channel.sendMessageEmbeds(eb.build());

            LinkedList<ActionRow> acR = new LinkedList<>();
            for (int i = 0; i < butt.size(); i += 5) {
                ArrayList<Button> row = new ArrayList<>();
                for (int j = 0; j < 5 && j + i < butt.size(); j++) {
                    row.add(butt.get(i + j));
                }
                acR.add(ActionRow.of(row));
            }
            msgAct.setComponents(acR);
            Message msgs = msgAct.complete();
            Data.msgid.add(msgs.getId());
            try {
                msgs.delete().queueAfter(90, TimeUnit.SECONDS);
            } catch (Exception e) {
            }
        }
        channel.deleteMessageById(ctx.getMessage().getId()).queue();
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("Help page of: `" + getName() + "`");
        embed.setDescription(
                "Command to see all roles with emotes such that you can assign them yourself. Or add the role name and directly get a Role added or removed.");

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use")
                .addNormal(prefix + getName() + " " + "[Role name | Category]")
                .build(), false);
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("Example for normal use:")
                .addNormal(prefix + getName() + " " + "")
                .addBlueAboveEq("Example for addding/removing role:")
                .addNormal(prefix + getName() + " " + "1. Semester")
                .addBlueAboveEq("Example for getting category embed:")
                .addNormal(prefix + getName() + " " + "Channel Roles")
                .build(), true);
        return embed.build();

    }

    public EmbedBuilder embeds(HashMap<String, String> fields, CommandContext ctx) {

        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Roles you can assign yourself", null);

        if (flipflop) {
            eb.setColor(Color.BLACK);
        } else {
            eb.setColor(Color.WHITE);
        }
        flipflop = !flipflop;

        for (String field : fields.keySet()) {
            eb.addField(field, fields.get(field), true);
        }

        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();
        eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());

        return eb;
    }

    public LinkedList<Object[]> rolesorter(HashMap<Role, Object[]> sorting) {
        LinkedList<Object[]> res = new LinkedList<>();
        while (sorting.size() != 0) {
            Role highest = null;
            for (Role role : sorting.keySet()) {
                if (highest == null || role.getPosition() > highest.getPosition()) {
                    highest = role;
                }
            }
            res.add(sorting.get(highest));
            sorting.remove(highest);
        }
        return res;
    }

    private void gibRole(CommandContext ctx, Role role) {
        // 767315361443741717 External data.ethexternal
        // 747786383317532823 Student data.ethstudent
        MessageChannel channel = ctx.getChannel();
        Member member = ctx.getMember();
        List<Role> autroles = member.getRoles();
        Message sent;
        // Removing Role
        if (autroles.contains(role)) {
            // External
            if (role.getId().equals(Data.ethexternal)) {
                Role student = ctx.getGuild().getRoleById(Data.ethstudent);
                if (autroles.contains(student)) {
                    ctx.getGuild().addRoleToMember(member, student).complete();
                    ctx.getGuild().removeRoleFromMember(member, role).complete();
                    sent = channel.sendMessage("Removed the Role " + role.getName() + ".").complete();
                } else {
                    sent = channel.sendMessage("You need at least either the Student or External Role").complete();
                }
                // Student
            } else if (role.getId().equals(Data.ethstudent)) {
                Role external = ctx.getGuild().getRoleById(Data.ethexternal);

                if (autroles.contains(external)) {
                    ctx.getGuild().addRoleToMember(member, external).complete();
                    ctx.getGuild().removeRoleFromMember(member, role).complete();
                    sent = channel.sendMessage("Removed the Role " + role.getName() + ".").complete();
                } else {
                    sent = channel.sendMessage("You need at least either the Student or External Role").complete();
                }
                // Smth else
            } else {
                ctx.getGuild().removeRoleFromMember(member, role).complete();
                sent = channel.sendMessage("Removed the Role " + role.getName() + ".").complete();
            }

            // Adding Role
        } else {
            // External
            if (role.getId().equals(Data.ethexternal)) {
                Role student = ctx.getGuild().getRoleById(Data.ethstudent);
                ctx.getGuild().addRoleToMember(member, role).complete();
                ctx.getGuild().removeRoleFromMember(member, student).complete();
                sent = channel.sendMessage("Gave you the Role " + role.getName() + " and removed " + student.getName())
                        .complete();
                // Student
            } else if (role.getId().equals(Data.ethstudent)) {

                if (!Helper.verifiedUser(member.getId())) {
                    String doverify = "You have to get verified to get the role ";
                    String suffix = ". You can do that here: https://dauth.spclr.ch/ and write the token to <@306523617188118528>";
                    member.getUser().openPrivateChannel().complete().sendMessage(doverify + role.getName() + suffix)
                            .complete();
                    return;
                }

                Role external = ctx.getGuild().getRoleById(Data.ethexternal);
                ctx.getGuild().addRoleToMember(member, role).complete();
                ctx.getGuild().removeRoleFromMember(member, external).complete();
                sent = channel.sendMessage("Gave you the Role " + role.getName() + " and removed " + external.getName())
                        .complete();
                // Smth else
            } else {
                ctx.getGuild().addRoleToMember(member, role).complete();
                sent = channel.sendMessage("Gave you the Role " + role.getName() + ".").complete();
            }
        }
        sent.delete().queueAfter(10, TimeUnit.SECONDS);
    }

    private void sendEmbed(CommandContext ctx, String cat) {

        Connection c = null;
        PreparedStatement stmt = null;
        ResultSet rs;
        MessageChannel channel = ctx.getChannel();

        String msg = "";
        LinkedList<String> roles = new LinkedList<>();

        HashMap<Role, Object[]> sorting = new HashMap<>();
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.prepareStatement("SELECT * FROM ASSIGNROLES WHERE categories=?;");
            stmt.setString(1, cat);
            Guild called = ctx.getGuild();
            rs = stmt.executeQuery();
            while (rs.next()) {
                String rcat = rs.getString("ID");
                String emote = rs.getString("EMOTE");
                String orig = emote;

                if (emote == null || emote.length() == 0) {
                    emote = "";
                } else {
                    emote = emote.contains(":") ? "<" + emote + ">" : emote;
                }
                msg = emote + " : " + called.getRoleById(rcat).getAsMention() + "\n";
                sorting.put(called.getRoleById(rcat), new Object[] { orig, msg });
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }

        LinkedList<Object[]> sorted = rolesorter(sorting);
        LinkedList<String> emotes = new LinkedList<>();
        msg = "";
        for (Object[] obj : sorted) {
            emotes.add((String) obj[0]);
            msg += (String) obj[1];
        }

        roles.add(msg);

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle(cat);
        eb.setColor(1);
        eb.setDescription(roles.get(0));

        eb.setFooter("Click on the Emotes to assign yourself Roles.");

        ArrayList<Button> butt = new ArrayList<>();
        for (String emoID : emotes) {
            if (emoID == null || emoID.length() == 0)
                continue;
            String emote = emoID;
            boolean gemo = false;
            if ((gemo = emote.contains(":"))) {
                emote = emote.split(":")[2];
            }

            try {
                butt.add(Button.primary(emoID,
                        gemo ? ctx.getGuild().getEmojiById(emote) : Emoji.fromUnicode(emote)));
            } catch (Exception e) {
                ctx.getChannel().sendMessage("Reaction with ID:" + emote + " is not accessible.").complete().delete()
                        .queueAfter(10, TimeUnit.SECONDS);
            }
        }

        MessageCreateAction msgAct = channel.sendMessageEmbeds(eb.build());

        LinkedList<ActionRow> acR = new LinkedList<>();
        for (int i = 0; i < butt.size(); i += 5) {
            ArrayList<Button> row = new ArrayList<>();
            for (int j = 0; j < 5 && j + i < butt.size(); j++) {
                row.add(butt.get(i + j));
            }
            acR.add(ActionRow.of(row));
        }

        msgAct.setComponents(acR);

        Data.msgid.add(msgAct.complete().getId());
    }
}
