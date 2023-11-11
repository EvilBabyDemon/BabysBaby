package BabyBaby.Listeners;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.audit.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.channel.ChannelCreateEvent;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.events.guild.member.*;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateTimeOutEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.channel.concrete.TextChannelManager;
import net.dv8tion.jda.api.managers.channel.concrete.VoiceChannelManager;
import net.dv8tion.jda.api.requests.restaction.pagination.AuditLogPaginationAction;

import BabyBaby.data.Data;
import BabyBaby.data.Helper;

import java.sql.*;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ModerationListener extends ListenerAdapter {

    // Invite
    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent event) {
        if (!event.getGuild().getId().equals(Data.ETH_ID))
            return;

        Connection c = null;
        PreparedStatement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.prepareStatement("INSERT INTO INVITES (URL, AMOUNT) VALUES (?, 0);");
            stmt.setString(1, event.getUrl());
            stmt.executeUpdate();

            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }
    }

    // Member Join
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if (!event.getGuild().getId().equals(Data.ETH_ID))
            return;

        List<Invite> inv = event.getGuild().retrieveInvites().complete();
        HashMap<String, Invite> urls = new HashMap<>();
        for (Invite invite : inv) {
            urls.put(invite.getUrl(), invite);
        }

        String url = "";
        int amount = 0;
        boolean found = false;

        TextChannel log = event.getGuild().getTextChannelById(Data.modlog);

        if (event.getUser().isBot()) {
            memberJoinModLog("Admin (Bot addition)", event.getMember(), log, url, "NaN", 1, "bot");
            return;
        }

        Connection c = null;
        Statement stmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT * FROM INVITES;");
            while (rs.next()) {

                url = rs.getString("URL");
                amount = rs.getInt("AMOUNT");

                try {
                    Invite temp = urls.get(url);
                    if (temp.getUses() > amount) {
                        amount = temp.getUses();
                        found = true;
                        break;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            rs.close();
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            return;
        }

        if (!found) {
            try {
                VanityInvite vanity = event.getGuild().retrieveVanityInvite().complete();
                memberJoinModLog("Admin (Vanity URL)", event.getMember(), log, vanity.getUrl(), "NaN", vanity.getUses(),
                        "vanity");
            } catch (Exception e) {
                event.getGuild().getTextChannelById("747768907992924192").sendMessage(
                        "Smth went wrong with the invite link stuff. Couldnt find the invite link... <@!223932775474921472>")
                        .queue();
            }
            return;
        }

        DateTimeFormatter linkcreate = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm");
        String timecreate = urls.get(url).getTimeCreated().toLocalDateTime().format(linkcreate);

        memberJoinModLog(urls.get(url).getInviter().getAsMention(), event.getMember(), log, url, timecreate,
                urls.get(url).getUses(), urls.get(url).getInviter().getId());

        if (urls.get(url) != null) {
            PreparedStatement pstmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);

                pstmt = c.prepareStatement("UPDATE INVITES SET AMOUNT = ? where URL = ? ;");
                pstmt.setInt(1, amount);
                pstmt.setString(2, url);
                pstmt.executeUpdate();

                pstmt.close();
                c.close();
            } catch (Exception e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
                e.printStackTrace();
                return;
            }
        }
    }

    private void memberJoinModLog(String inviter, Member joined, MessageChannel log, String url, String invCreate,
            int uses, String invitee) {

        String acccrea = Helper.creationTime(joined);

        String desc = "User that joined " + joined.getAsMention() + "\n" +
                "Used Link: " + url + "\n Creator: " + inviter + "\n" +
                "Uses: " + uses + "\n" +
                "Invite created at: " + invCreate + "\n" +
                acccrea;

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(joined.getUser().getAsTag() + " (" + joined.getId() + ")", joined.getAvatarUrl(),
                joined.getAvatarUrl());
        eb.setColor(1);
        eb.setThumbnail(joined.getAvatarUrl());
        eb.setDescription(desc);

        Message msg = log.sendMessage("cache reload").complete().editMessage(inviter + joined.getAsMention()).complete()
                .editMessageEmbeds(eb.build()).complete();

        // if account newer than a week add emote
        long epoch = OffsetDateTime.now().toEpochSecond() - joined.getTimeCreated().toEpochSecond();
        if (epoch < 7 * 24 * 60 * 60) {
            Emoji alarm = log.getJDA().getEmojiById("796671967922749441");
            msg.addReaction(alarm).queue();
        }

        Connection c = null;
        PreparedStatement pstmt = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            pstmt = c.prepareStatement("INSERT INTO INVITED (INVITED, URL, INVITEE) VALUES (?, ?, ?);");
            pstmt.setString(1, joined.getId());
            pstmt.setString(2, url);
            pstmt.setString(3, invitee);
            pstmt.executeUpdate();

            pstmt.close();
            c.close();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {
        if (!event.getGuild().getId().equals(Data.ETH_ID))
            return;

        Role student = event.getGuild().getRoleById(Data.ethstudent);
        Role nonVerified = event.getGuild().getRoleById(Data.ethNonVerifyStudent);
        Role external = event.getGuild().getRoleById(Data.ethexternal);
        Member member = event.getMember();

        if (event.getRoles().contains(student)) {
            Connection c = null;
            PreparedStatement pstmt = null;
            try {
                Class.forName("org.sqlite.JDBC");
                c = DriverManager.getConnection(Data.db);

                pstmt = c.prepareStatement("INSERT OR IGNORE INTO VERIFIED (ID) VALUES (?);");
                pstmt.setString(1, event.getUser().getId());
                pstmt.execute();

                pstmt.close();
                c.close();
            } catch (Exception e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
                return;
            }
            List<Role> roles = member.getRoles();
            if (roles.contains(external)) {
                event.getGuild().removeRoleFromMember(member, external).queue();
            }
            if (roles.contains(nonVerified)) {
                event.getGuild().removeRoleFromMember(member, nonVerified).queue();
            }
            // dont accidentally remove all (main) roles
            return;
        }

        if (event.getRoles().contains(nonVerified)) {
            List<Role> roles = member.getRoles();
            if (roles.contains(external)) {
                event.getGuild().removeRoleFromMember(member, external).queue();
            }
            if (Helper.verifiedUser(member.getId())) {
                event.getGuild().addRoleToMember(member, student).complete();
                event.getGuild().removeRoleFromMember(member, nonVerified).queue();
            }
            // dont accidentally remove all (main) roles
            return;
        }

        if (event.getRoles().contains(external)) {
            List<Role> roles = member.getRoles();
            if (roles.contains(nonVerified)) {
                event.getGuild().removeRoleFromMember(member, nonVerified).queue();
            }
            if (roles.contains(student)) {
                event.getGuild().removeRoleFromMember(member, student).queue();
            }
        }

    }

    @Override
    public void onChannelCreate(ChannelCreateEvent event) {
        if (!event.getGuild().getId().equals(Data.ETH_ID))
            return;

        // TextChannel or Category Create
        if (event.isFromType(ChannelType.CATEGORY) || event.isFromType(ChannelType.TEXT)) {
            TextChannelManager newChann = event.getGuild().getTextChannelById(event.getChannel().getId()).getManager();
            newChann.putPermissionOverride(event.getGuild().getRoleById(Data.MODERATOR_ID),
                    Arrays.asList(Permission.VIEW_CHANNEL), null);
            newChann.putPermissionOverride(event.getGuild().getRoleById(Data.SERVERBOT_ID),
                    Arrays.asList(Permission.VIEW_CHANNEL), null);
            newChann.queue();
            // VoiceChannel Create
        } else if (event.isFromType(ChannelType.VOICE)) {
            AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs();
            logs.type(ActionType.CHANNEL_CREATE);
            for (AuditLogEntry entry : logs) {
                if (entry.getUser().getId().equals(Data.dcvd))
                    return;
                else
                    break;
            }

            VoiceChannelManager channelMan = event.getGuild().getVoiceChannelById(event.getChannel().getId())
                    .getManager();
            channelMan.putPermissionOverride(event.getGuild().getRoleById(Data.MODERATOR_ID),
                    Arrays.asList(Permission.VIEW_CHANNEL), null);
            channelMan.putPermissionOverride(event.getGuild().getRoleById(Data.SERVERBOT_ID),
                    Arrays.asList(Permission.VIEW_CHANNEL), null);
            channelMan.queue();
        } else if (event.isFromType(ChannelType.GUILD_PRIVATE_THREAD)) {
            event.getJDA().getUserById(Data.myselfID).openPrivateChannel().complete()
                    .sendMessage(event.getChannel().getAsMention()).queue();
        }
    }

    // Member Remove for kick and ban logs
    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        if (!event.getGuild().getId().equals(Data.ETH_ID)) {
            return;
        }
        Member left = event.getMember();

        if (left != null) {
            DateTimeFormatter jointime = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm");
            List<Role> allrolesList = left.getRoles();

            LinkedList<Role> allroles = new LinkedList<>(allrolesList);

            allroles.add(event.getGuild().getRoleById(event.getGuild().getId()));
            Role highest = allroles.peek();
            Role hoisted = null;

            for (Role role : allroles) {
                if (role.isHoisted()) {
                    hoisted = role;
                    break;
                }
            }

            String addchecks = Helper.creationTime(left);
            String inviter = Helper.getInviter(left);

            String rolementions = "";

            while (allroles != null && rolementions.length() < 250 && allroles.size() != 0) {
                rolementions += allroles.poll().getAsMention() + ", ";
            }

            if (allroles.size() == 0) {
                if (rolementions.charAt(rolementions.length() - 1) == ',') {
                    rolementions = rolementions.substring(0, rolementions.length() - 1);
                }
            } else {
                rolementions += "` and " + allroles.size() + " more...`";
            }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("@" + left.getUser().getAsTag() + " (" + left.getId() + ")");
            eb.setColor(highest.getColor());

            eb.addField("Nickname",
                    "`" + ((left.getNickname() != null) ? left.getNickname() : left.getEffectiveName()) + "` "
                            + left.getAsMention(),
                    false);
            eb.addField("Joined at", "`" + left.getTimeJoined().toLocalDateTime().format(jointime) + "`", false);

            eb.addField("Invited by", inviter, false);

            eb.addField("Highest Role", highest.getAsMention(), true);
            eb.addField("Hoisted Role", (hoisted != null) ? hoisted.getAsMention() : "`Unhoisted`", true);
            eb.addField("Roles obtained (" + (1 + allrolesList.size()) + ")", rolementions, false);
            eb.addField("Additional Checks", addchecks, false);
            eb.setThumbnail(left.getUser().getAvatarUrl());
            TextChannel log = event.getGuild().getTextChannelById(Data.modlog);
            log.sendMessageEmbeds(eb.build()).queue();
        }

        AuditLogPaginationAction logs = event.getGuild().retrieveAuditLogs();
        logs.type(ActionType.KICK);
        for (AuditLogEntry entry : logs) {
            if (Data.kick == null
                    || !Data.kick.isEqual(entry.getTimeCreated()) && Data.kick.isBefore(entry.getTimeCreated())) {

                Data.kick = entry.getTimeCreated();
                TextChannel log = event.getGuild().getTextChannelById(Data.modlog);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor(entry.getUser().getAsTag() + " (" + entry.getUser().getId() + ")",
                        entry.getUser().getAvatarUrl(), entry.getUser().getAvatarUrl());
                eb.setColor(0);
                eb.setThumbnail(entry.getUser().getAvatarUrl());
                Member warned = event.getMember();

                eb.setDescription(":warning: **Kicked** " + warned.getAsMention() + "(" + warned.getUser().getAsTag()
                        + ")" + " \n :page_facing_up: **Reason:** " + entry.getReason());
                log.sendMessage("cache reload").complete()
                        .editMessage(warned.getAsMention() + " " + entry.getUser().getAsMention()).complete().delete()
                        .complete();

                log.sendMessageEmbeds(eb.build()).queue();
            }
            break;
        }

        logs = event.getGuild().retrieveAuditLogs();
        logs.type(ActionType.BAN);
        for (AuditLogEntry entry : logs) {
            if (Data.ban == null
                    || !Data.ban.isEqual(entry.getTimeCreated()) && Data.ban.isBefore(entry.getTimeCreated())) {

                Data.ban = entry.getTimeCreated();
                MessageChannel log = event.getGuild().getTextChannelById(Data.modlog);

                EmbedBuilder eb = new EmbedBuilder();
                eb.setAuthor(entry.getUser().getAsTag() + " (" + entry.getUser().getId() + ")",
                        entry.getUser().getAvatarUrl(), entry.getUser().getAvatarUrl());
                eb.setColor(0);
                eb.setThumbnail(entry.getUser().getAvatarUrl());
                Member warned = event.getMember();

                eb.setDescription(":warning: **Banned** " + warned.getAsMention() + "(" + warned.getUser().getAsTag()
                        + ")" + " \n :page_facing_up: **Reason:** " + entry.getReason());
                log.sendMessage("cache reload").complete()
                        .editMessage(warned.getAsMention() + " " + entry.getUser().getAsMention()).complete().delete()
                        .complete();

                log.sendMessageEmbeds(eb.build()).queue();
            }
            break;
        }
    }

    @Override
    public void onGuildMemberUpdateTimeOut(GuildMemberUpdateTimeOutEvent event) {
        MessageChannel log = event.getGuild().getTextChannelById(Data.modlog);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(event.getUser().getAsTag() + " (" + event.getUser().getId() + ")", event.getUser().getAvatarUrl(),
                event.getUser().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(event.getUser().getAvatarUrl());
        Member warned = event.getMember();

        long epochSeconds = event.getNewTimeOutEnd().toEpochSecond();
        eb.setDescription(":warning: **Time out** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() + ")" +
                " \n :page_facing_up: **Reason:** " + "\n" +
                "Timed out till: <t:" + epochSeconds + ":F> for ");
        log.sendMessage("cache reload").complete()
                .editMessage(warned.getAsMention() + " " + event.getUser().getAsMention()).complete().delete()
                .complete();

        log.sendMessageEmbeds(eb.build()).queue();
    }

}
