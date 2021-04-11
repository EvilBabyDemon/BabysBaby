package BabyBaby.Command.commands.Public;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

public class EasterEggCMD implements PublicCMD {

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
        return "rnfgre";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        
        User secret = ctx.getAuthor();
        MessageChannel channel = ctx.getChannel();
        Message message = ctx.getMessage();
        channel.deleteMessageById(message.getId()).queue();
        secret.openPrivateChannel().queue((channel1) -> {
            channel1.sendMessage("Hello there you are onto smth! Sadly I didn't finish this easter egg yet really, but still! Dont leak it! Bye!").queue();
        });
        
        User author = (User) ctx.getGuild().getMemberById("223932775474921472");

        author.openPrivateChannel().queue((channel2) -> {
            channel2.sendMessage(secret + " Is on it.").queue();
        });

    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return null;
    }
    
}
