package BabyBaby.Command.commands.Owner;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.IOwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class BigSieveCMD implements IOwnerCMD{

    @Override
    public String getName() {
        return "bigsieve";    
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();
        
        String[] cmd = new String [cmds.size()];

        int dooku = 0;
        for (String arg : cmds) {
            cmd[dooku] = arg;
            dooku++;
        }

        HashSet<Member> counter = new HashSet<>();

        List<Member> tmp = ctx.getGuild().getMembers();
        
        if(cmd[0].equals(Data.ETH_ID)){
            for (Member member : tmp) {
                counter.add(member);
            }
        } else {
            Role role1 = ctx.getGuild().getRoleById(cmd[0]);
            for (Member member : tmp) {
                if(member.getRoles().contains(role1))
                    counter.add(member);
            }
        }

        for(int i = 1; i < cmd.length-1; i +=2){
            Role role = ctx.getGuild().getRoleById(cmd[i+1]);
            switch (cmd[i]){
                case "!":
                    List<Member> removerMembers = ctx.getGuild().getMembersWithRoles(role);
                    for (Member member : removerMembers) {
                        if(counter.contains(member)){
                            counter.remove(member);
                        }
                    }
                    break;
                case "&":
                    LinkedList<Member> save = new LinkedList<>();
                    for (Member member : counter) {
                        if(!member.getRoles().contains(role))
                            save.add(member);
                    }
                    counter.removeAll(save);
                    break;
                case "|":
                    List<Member> adderMem = ctx.getGuild().getMembersWithRoles(role);
                    for (Member member : adderMem) 
                        counter.add(member);
                    break;
            }
        }
        LinkedList<Member> sorted = new LinkedList<>(counter);
        Comparator<Member> idcomp = new Comparator<>(){
            @Override
            public int compare(Member o1, Member o2) {
                return (o1.getIdLong() - o2.getIdLong()>0) ? 1 : -1;
            }
        };
        sorted.sort(idcomp);

        String mention = "";
        for (Member member : sorted) {
            mention += member.getAsMention() + "\n";
        }
        String cmdrole = "";
        for(int i = 0; i < cmd.length; i ++){
            if(i%2==0)
                cmdrole += ctx.getGuild().getRoleById(cmd[i]).getAsMention() + " ";
            else
                cmdrole += cmd[i] + " ";
        }

        LinkedList <String> cacherefresh = new LinkedList<>();

        LinkedList<EmbedBuilder> alleb = new LinkedList<>();

        if(mention.length() <= 5980 - ((cmdrole.length()>1024) ? 1024 : cmdrole.length())){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("People with ");
            eb.addField("" + counter.size(), (cmdrole.length()>1024) ? cmdrole.substring(0, 1024) : cmdrole, false);
            eb.setColor(1);
            dooku = 0;
            while(mention.length() > 1024){
                mention = Helper.addFieldSieve(eb, cacherefresh, dooku++, mention);
            }
            cacherefresh.add(mention);
            eb.addField(""+dooku, mention, true);
            
            alleb.add(eb);
        } else {

            int embsize = 5800 - ((cmdrole.length()>1024) ? 1024 : cmdrole.length());
            String firstmention = mention.substring(0, embsize);
            String[] firstpart = firstmention.split("\n");
            firstmention = firstmention.substring(0, embsize - firstpart[firstpart.length-1].length()-1);
            mention = mention.substring(firstmention.length());

            EmbedBuilder first = new EmbedBuilder();
            first.setTitle("People with ");
            first.addField("" + counter.size(), (cmdrole.length()>1024) ? cmdrole.substring(0, 1024) : cmdrole, false);
            first.setColor(1);
            dooku = 0;
            while(firstmention.length() > 1024){
                firstmention = Helper.addFieldSieve(first, cacherefresh, dooku++, firstmention);
            }
            cacherefresh.add(firstmention);
            first.addField(""+dooku, firstmention, true);
            
            alleb.add(first);

            int dookueb = 2;
            while(mention.length()>5990){
                String embmention = mention.substring(0, 5990);
                String[] embpart = embmention.split("\n");
                embmention = embmention.substring(0, 5990 - embpart[embpart.length-1].length()-1);
                mention = mention.substring(embmention.length());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Page " + dookueb++);
                eb.setColor(1);
                dooku = 0;
                while(embmention.length() > 1024){
                    embmention = Helper.addFieldSieve(eb, cacherefresh, dooku++, embmention);
                }
                cacherefresh.add(embmention);
                eb.addField(""+dooku, embmention, true);
                alleb.add(eb);
            }

            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("Page " + dookueb);
            eb.setColor(1);
            dooku = 0;
            while(mention.length() > 1024){
                mention = Helper.addFieldSieve(eb, cacherefresh, dooku++, mention);
            }
            cacherefresh.add(mention);
            eb.addField(""+dooku, mention, true);
            alleb.add(eb);
        }
        
        Message editor = ctx.getChannel().sendMessage("wait a sec").complete();
        
        for (String pings : cacherefresh) {
            if(pings == null || pings.length()==0)
                continue;
            editor.editMessage(pings + " ").complete();
        }
        editor.delete().queue();
        
        ctx.getMessage().addReaction(Data.check).queue();
        
        for (EmbedBuilder eb : alleb) {
            ctx.getChannel().sendMessageEmbeds(eb.build()).queue();
        }
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<roleID> {<!/&/|> <roleID>}", "Command to find out who has the Role AmongUs but also the Anime role for example.");
    }
}
