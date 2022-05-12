package BabyBaby.Command.commands.Slash;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.ISlashCMD;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

public class WhoisSlashCMD implements ISlashCMD {

	@Override
	public String getName() {
		return "whois";
	}

	@Override
	public void handle(SlashCommandInteractionEvent event, InteractionHook hook, boolean failed) {
		boolean pleb = !event.getMember().hasPermission(Permission.MODERATE_MEMBERS);

		MessageChannel channel = event.getChannel();

		Member stalking = null;
		if (event.getOption("user") == null && event.getOption("userid") == null) {
			stalking = event.getMember();
		} else if (event.getOption("user") != null) {
			stalking = event.getOption("user").getAsMember();
		} else {
			stalking = event.getGuild().getMemberById(event.getOption("userid").getAsString());
		}
		boolean ephemeral = event.getOption("ephemeral", false, OptionMapping::getAsBoolean);

		String nickname = (event.getMember().getNickname() != null) ? event.getMember().getNickname()
				: event.getMember().getEffectiveName();
		List<Role> allrolesList = stalking.getRoles();

		LinkedList<Role> allroles = new LinkedList<>();

		for (Role role : allrolesList) {
			allroles.add(role);
		}
		allroles.add(event.getGuild().getRoleById(event.getGuild().getId()));
		Role highest = allroles.peek();
		Role hoisted = null;

		for (Role role : allroles) {
			if (role.isHoisted()) {
				hoisted = role;
				break;
			}
		}

		DateTimeFormatter jointime = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm");
		DateTimeFormatter createtime = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");

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
		OffsetDateTime created = stalking.getUser().getTimeCreated();
		OffsetDateTime now = OffsetDateTime.now();
		int day = now.getDayOfYear() - created.getDayOfYear();
		day = (day < 0) ? 365 + day : day;
		int year = now.getYear() - created.getYear() + ((now.getDayOfYear() < created.getDayOfYear()) ? -1 : 0);

		String multyear = ((year + Math.round(day / 365.0)) == 1) ? " year ago" : " years ago";
		String multday = (day == 1) ? " day ago" : " days ago";
		String actualtime = (year > 0) ? (year + Math.round(day / 365.0)) + multyear : day + multday;

		String addchecks = "Created as: **a " + ((stalking.getUser().isBot()) ? "bot" : "user")
				+ " account** \n Created at: **" + stalking.getUser().getTimeCreated().format(createtime) + "** `("
				+ actualtime + ")`";

		LinkedList<String> invID = new LinkedList<>();
		String invitee = "";

		Connection c = null;
		PreparedStatement pstmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection(Data.db);

			pstmt = c.prepareStatement("SELECT INVITEE FROM INVITED WHERE INVITED = ?;");
			pstmt.setString(1, stalking.getId());
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				invID.add(rs.getString("INVITEE"));
			}

			pstmt.close();
			c.close();
		} catch (Exception e) {
			System.out.println(e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
		}

		if (invID.size() == 0) {
			invitee = "No one found.";
		}
		for (String userID : invID) {
			try {
				invitee += event.getGuild().getMemberById(userID).getAsMention();
			} catch (Exception e) {
			}
		}

		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("@" + stalking.getUser().getAsTag() + " (" + stalking.getId() + ")");
		eb.setColor(highest.getColor());

		eb.addField("Nickname",
				"`" + ((stalking.getNickname() != null) ? stalking.getNickname() : stalking.getEffectiveName()) + "` "
						+ stalking.getAsMention(),
				false);
		eb.addField("Joined at", "`" + stalking.getTimeJoined().toLocalDateTime().format(jointime) + "`", false);
		if (!pleb) {
			eb.addField("Invited by", invitee, false);
		}
		eb.addField("Highest Role", highest.getAsMention(), true);
		eb.addField("Hoisted Role", (hoisted != null) ? hoisted.getAsMention() : "`Unhoisted`", true);
		eb.addField("Roles obtained (" + (1 + allrolesList.size()) + ")", rolementions, false);
		eb.addField("Additional Checks", addchecks, false);
		eb.setFooter("Summoned by: " + nickname, event.getUser().getAvatarUrl());
		eb.setThumbnail(stalking.getUser().getAvatarUrl());

		boolean spamPrev = event.getGuild().getId().equals(Data.ETH_ID)
				&& !event.getChannel().getId().equals(Data.SPAM_ID);

		if (ephemeral || spamPrev && pleb) {
			Helper.unhook(eb.build(), failed, hook, event.getUser());
		} else {
			channel.sendMessage("Cache reload").complete().editMessage(invitee).complete().delete().complete();
			channel.sendMessageEmbeds(eb.build()).queue();
			Helper.unhook("Done", failed, hook, event.getUser());
		}
	}

	@Override
	public CommandDataImpl initialise(Guild eth) {
		// whois
		CommandDataImpl whois = new CommandDataImpl(getName(), "Cmd to see stuff about a user.");
		whois.addOption(OptionType.USER, "user", "User to stalk.", false);
		whois.addOption(OptionType.STRING, "userid", "Id of user for the case you can't type their username.", false);
		whois.addOption(OptionType.BOOLEAN, "ephemeral", "True if message should be ephemeral. Default is false",
				false);
		return whois;
	}

}
