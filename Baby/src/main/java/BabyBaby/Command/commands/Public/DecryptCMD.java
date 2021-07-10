package BabyBaby.Command.commands.Public;

import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import CryptPart.KeyDecrypt;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import BabyBaby.ColouredStrings.StandardHelpEmbed ;

public class DecryptCMD implements PublicCMD {

    @Override
    public String getName() {
        return "decrypt";
    }

    @Override
    public void handlePublic(CommandContext ctx) {



        MessageChannel channel = ctx.getChannel();

        /*  we need to load the list into a new Linked List
            because ctx.getArgs() returns a fixed size list
            which we can't remove args from */
        List<String> args = new LinkedList<>(ctx.getArgs());

        String key = args.remove(0);
        String content = String.join(" ", args);  // joins the list properly

        /*if (cryptdeleter)
            channel.deleteMessageById(message.getId()).queue();
        else
            message.addReaction(check).queue();
        */
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Decrypted text with key: " + key);
        eb.setDescription(KeyDecrypt.decrypter(content, key));

        channel.sendMessageEmbeds(eb.build()).queue();


    }

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        StandardHelpEmbed make = new StandardHelpEmbed();
        return make.StandardHelp(prefix, getName(), "Decrypt text with Viginere or Caesar.", "decrypt <text>");
    }
    
}
