package BabyBaby.Command.commands.Owner;

import java.security.acl.Owner;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class ChangeLogCMD implements OwnerCMD {

    @Override
    public String getName() {
        return "anim";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        String result = "<:plusplus:816779826202411038> ";

        for (String var : ctx.getArgs()) {
            result += ctx.getGuild().getEmoteById(var).getAsMention() + " ";
        }
        Message tmp = ctx.getChannel().sendMessage(result).complete();

        for (String var : ctx.getArgs()) {
            tmp.addReaction(ctx.getGuild().getEmoteById(var)).complete();
        }
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<emote id> {emote id}", "To send animated emotes for changelog");
    }
    
}
