package BabyBaby.Command.commands.Slash;

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
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
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

		DateTimeFormatter jointime = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm");

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

		String addchecks = Helper.creationTime(stalking);

		String inviter = Helper.getInviter(stalking);

		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle("@" + stalking.getUser().getAsTag() + " (" + stalking.getId() + ")");
		eb.setColor(highest.getColor());

		eb.addField("Nickname",
				"`" + ((stalking.getNickname() != null) ? stalking.getNickname() : stalking.getEffectiveName()) + "` "
						+ stalking.getAsMention(),
				false);
		eb.addField("Joined at", "`" + stalking.getTimeJoined().toLocalDateTime().format(jointime) + "`", false);
		if (!pleb) {
			eb.addField("Invited by", inviter, false);
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
			channel.sendMessage("Cache reload").complete().editMessage(inviter + stalking.getAsMention()).complete().delete().complete();
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
