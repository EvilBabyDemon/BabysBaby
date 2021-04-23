package BabyBaby.Command.commands.Public;

import java.util.LinkedList;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class PollCMD implements PublicCMD{

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
		return "poll";
	}

	@Override
	public void handlePublic(CommandContext ctx) {
		
		LinkedList<String> cmds = new LinkedList<>();
		String temp = "";
		for (String var : ctx.getArgs()) {
			if(var.endsWith("\"")){
				cmds.add(temp + var);
				temp = "";
			}	else{
				temp += var + " ";
			}
		}

		if(cmds.size()>10){
			ctx.getChannel().sendMessage("You can only have at most 9 Options.").queue();
			return;
		}

		if(cmds.size()<3){
			ctx.getChannel().sendMessage("You need at least 2 Options.").queue();
			return;
		}

		String[] emot = {"0️⃣","1️⃣","2️⃣","3️⃣","4️⃣","5️⃣","6️⃣","7️⃣","8️⃣","9️⃣"};
 

		String topic = cmds.remove();
		topic = topic.replaceAll("\"", "");
		String options = "";
		int amount = 0;
		while(cmds.size()!=0){
			options += emot[amount] + " : " + cmds.remove().replaceAll("\"", "") + "\n";
			amount++;
		}

		EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(topic);
		eb.setColor(0);
        eb.setDescription(options);
		String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();
		eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());

		Message built = ctx.getChannel().sendMessage(eb.build()).complete();
		for (int i = 0; i < amount; i++) {
			built.addReaction(emot[i]).queue();
		}
		
	}

	@Override
	public MessageEmbed getPublicHelp(String prefix) {
		return StandardHelp.Help(prefix, getName(), "<\"Topic\"> <\"1. option\"> <\"2. option\"> {\"even more options\"}", "Poll Command to make a Poll.");
	}
    
}
