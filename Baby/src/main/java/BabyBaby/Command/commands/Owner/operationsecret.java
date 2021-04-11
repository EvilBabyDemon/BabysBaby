package BabyBaby.Command.commands.Owner;

import java.util.List;
import java.util.Random;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class operationsecret implements OwnerCMD {

    @Override
    public String getName() {
        return "operationsecret";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();

        Long x = Long.parseLong(cmds.get(0));
        Long y = Long.parseLong(cmds.get(1));
        List<Member> everyone = ctx.getGuild().getMembers();

        // setup code
        char[] c = new char[] {'|', 'I', 'l', '‖'};
        Random rand = new Random();

        for (Member var : everyone) {
            if(x <= Long.parseLong(var.getId()) && Long.parseLong(var.getId()) < y){
                try{
                    int len = rand.nextInt(20) + 10;
                    String name = "";
                    while(len-->0) name += c[rand.nextInt(c.length)];
                    var.modifyNickname(name).queue();
                } catch(Exception e){
                    ctx.getChannel().sendMessage(var.getUser().getAsTag()).queue();
                    continue;
                }
            }
        }
        
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<bottom ID> <top ID>", "April Fools Joke");
    }
    
}
