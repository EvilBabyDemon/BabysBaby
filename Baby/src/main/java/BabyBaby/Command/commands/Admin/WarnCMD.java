package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.LinkedList;

import BabyBaby.Command.IAdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class WarnCMD implements IAdminCMD {

    @Override
    public String getName() {
        return "warn";
    }

    @Override
    public void handleAdmin(CommandContext ctx) {
        LinkedList<String> cmds = new LinkedList<>();
        MessageChannel channel = ctx.getChannel();

        for (String arg : ctx.getArgs()) {
            cmds.add(arg);
        }

        String person = cmds.remove(0);
        if (cmds.size() == 0) {
            channel.sendMessage("Give a reason please!").queue();
            return;
        }
        String reason = ctx.getMessage().getContentRaw().substring(getName().length() + person.length() + 3);

        person = person.replace("<", "");
        person = person.replace(">", "");
        person = person.replace("!", "");
        person = person.replace("@", "");

        try {
            if (!ctx.getGuild().getMemberById(person).getId().equals(person)) {
                channel.sendMessage("This is not a user. Use help warn to find out how to use this cmd.").queue();
                return;
            }
        } catch (Exception e) {
            channel.sendMessage("This is not a user. Use help warn to find out how to use this cmd.").queue();
            return;
        }

        LocalDate time = LocalDate.now();

        String date = time.getDayOfMonth() + "." + time.getMonthValue() + "." + time.getYear();

        Connection c = null;
        PreparedStatement stmt = null;

        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection(Data.db);

            String sql = "INSERT INTO WARNINGS (USER,REASON,DATE) " +
                    "VALUES (?,?,?);";
            stmt = c.prepareStatement(sql);

            stmt.setLong(1, Long.parseLong(person));
            stmt.setString(2, reason);
            stmt.setString(3, date);
            stmt.executeUpdate();

            stmt.close();
            c.close();
        } catch (Exception e) {
            channel.sendMessage(e.getClass().getName() + ": " + e.getMessage()).queue();
            e.printStackTrace();
            return;
        }

        MessageChannel log = ctx.getGuild().getTextChannelById(Data.modlog);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor(ctx.getAuthor().getAsTag() + " (" + ctx.getAuthor().getId() + ")", ctx.getAuthor().getAvatarUrl(),
                ctx.getAuthor().getAvatarUrl());
        eb.setColor(0);
        eb.setThumbnail(ctx.getGuild().getMemberById(person).getUser().getAvatarUrl());
        Member warned = ctx.getGuild().getMemberById(person);

        eb.setDescription(":warning: **Warned** " + warned.getAsMention() + "(" + warned.getUser().getAsTag() + ")"
                + " \n :page_facing_up: **Reason:** " + reason);

        log.sendMessageEmbeds(eb.build()).queue();

        ctx.getChannel().sendMessageEmbeds(eb.build()).queue();

        warned.getUser().openPrivateChannel().complete().sendMessageEmbeds(eb.build()).queue();

        ctx.getMessage().addReaction(Data.check).queue();

    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<person as Ping> <reason>",
                "Command to warn people if they do stupid stuff.");
    }

}