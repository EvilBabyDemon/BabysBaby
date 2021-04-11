package BabyBaby.Command.commands.Public;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class DuckyCMD implements PublicCMD {

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
        return "ducky";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        MessageChannel channel = ctx.getChannel();
        String content = "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fupload.wikimedia.org%2Fwikipedia%2Fcommons%2Fd%2Fd2%2FRubber_Duck_Front_View_in_Fine_Day_20140107.jpg&f=1&nofb=1";
        channel.deleteMessageById(ctx.getMessage().getId()).queue();
        channel.sendMessage(content).queue();
        
    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "Get a debugging ducky pic, which you can talk to about any coding problems. ^-^ \n For even more ducky contact <@!817846061347242026>");
    }
    
}
