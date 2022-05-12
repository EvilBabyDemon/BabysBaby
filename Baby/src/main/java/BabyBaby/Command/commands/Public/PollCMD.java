package BabyBaby.Command.commands.Public;

import java.util.LinkedList;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IPublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PollCMD implements IPublicCMD {

	@Override
	public boolean getWhiteListBool() {
		return true;
	}

	@Override
	public String getName() {
		return "poll";
	}

	@Override
	public void handlePublic(CommandContext ctx) {

		LinkedList<String> cmds = new LinkedList<>();
		String temp = "";
		for (String arg : ctx.getArgs()) {
			if (arg.endsWith("\"")) {
				cmds.add(temp + arg);
				temp = "";
			} else {
				temp += arg + " ";
			}
		}

		if (cmds.size() > 12) {
			ctx.getChannel().sendMessage("You can only have at most 11 Options.").queue();
			return;
		}

		if (cmds.size() < 3) {
			ctx.getChannel().sendMessage("You need at least 2 Options.").queue();
			return;
		}

		String[] emot = { "0️⃣", "1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣", ":keycap_ten:" };

		String topic = cmds.remove();
		topic = topic.replaceAll("\"", "");
		if (topic.length() > 256) {
			ctx.getChannel().sendMessage("Your Title can't be longer than 256 chars.").queue();
			return;
		}
		String options = "";
		int amount = 0;
		while (cmds.size() != 0) {
			options += emot[amount] + " : " + cmds.remove().replaceAll("\"", "") + "\n";
			amount++;
		}

		if (options.length() > 2000) {
			ctx.getChannel().sendMessage("All your options together can't be more than 2000 chars, so keep it simpler!")
					.queue();
			return;
		}

		EmbedBuilder eb = new EmbedBuilder();
		eb.setTitle(topic);
		eb.setColor(0);
		eb.setDescription(options);
		String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
				: ctx.getMember().getEffectiveName();
		eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());

		Message built = ctx.getChannel().sendMessageEmbeds(eb.build()).complete();
		for (int i = 0; i < amount; i++) {
			built.addReaction(emot[i]).queue();
		}

	}

	@Override
	public MessageEmbed getPublicHelp(String prefix) {
		return StandardHelp.Help(prefix, getName(),
				"<\"Topic\"> <\"1. option\"> <\"2. option\"> {\"even more options\"}", "Poll Command to make a Poll.");
	}

}
