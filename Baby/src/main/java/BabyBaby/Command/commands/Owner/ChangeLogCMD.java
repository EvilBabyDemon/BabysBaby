package BabyBaby.Command.commands.Owner;

import java.util.LinkedList;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IOwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.Emoji;

public class ChangeLogCMD implements IOwnerCMD {

    @Override
    public String getName() {
        return "anim";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        String result = "<:plusplus:816779826202411038> ";

        LinkedList<Emoji> emo = new LinkedList<>();

        for (String arg : ctx.getArgs()) {
            emo.add(ctx.getGuild().getEmojisByName(arg, true).get(0));
        }

        for (Emoji emote : emo) {
            result += emote.getFormatted() + " ";
        }
        Message tmp = ctx.getChannel().sendMessage(result).complete();

        for (Emoji emote : emo) {
            tmp.addReaction(emote).complete();
        }
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<emote id> {emote id}", "To send animated emotes for changelog");
    }

}
