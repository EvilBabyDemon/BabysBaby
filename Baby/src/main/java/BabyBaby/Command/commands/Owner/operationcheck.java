package BabyBaby.Command.commands.Owner;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class operationcheck implements OwnerCMD{

    @Override
    public String getName() {
        return "operationcheck";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        List<Member> everyone = ctx.getGuild().getMembers();
        LinkedList<String> oldchars = new LinkedList<>();
        // setup code
        char[] c = new char[] {'|', 'I', 'l', '‖'};
        Random rand = new Random();
        for(int i = 97; i < 123; i++){
            if(i == 108) continue;
            oldchars.add("" + ((char) i));
        }	
        for (Member var : everyone) {
            boolean notchanged = false;
            for (String stringvar : oldchars) {
                if(var.getNickname() == null || var.getNickname().contains(stringvar)){
                    notchanged = true;
                    break;
                }
            }
            if(notchanged){
                try{
                    int len = rand.nextInt(30) + 2;
                    String name = "";
                    while(len-->0) name += c[rand.nextInt(c.length)];
                    var.modifyNickname(name).queue();
                    ctx.getChannel().sendMessage(var.getAsMention()).queue();
                } catch(Exception e){
                    ctx.getChannel().sendMessage(var.getUser().getAsTag()).queue();
                    continue;
                }
            }
        }
        ctx.getChannel().sendMessage("Done").queue();
        
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "", "April Fools Joke check over everyone again.");
    }
    
}
