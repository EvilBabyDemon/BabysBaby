package BabyBaby.Command.commands.Owner;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.OwnerCMD;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

public class BigSiebCMD implements OwnerCMD{

    @Override
    public String getName() {
        return "bigsieb";    
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();
        
        String[] cmd = new String [cmds.size()];

        int dooku = 0;
        for (String var : cmds) {
            cmd[dooku] = var;
            dooku++;
        }

        HashSet<Member> counter = new HashSet<>();

        List<Member> tmp = ctx.getGuild().getMembers();
        
        if(cmd[0].equals(data.ethid)){
            for (Member var : tmp) {
                counter.add(var);
            }
        } else {
            Role role1 = ctx.getGuild().getRoleById(cmd[0]);
            for (Member var : tmp) {
                if(var.getRoles().contains(role1))
                    counter.add(var);
            }
        }   
        for(int i = 1; i < cmd.length-1; i +=2){
            Role role = ctx.getGuild().getRoleById(cmd[i+1]);
            switch (cmd[i]){
                case "!":
                    List<Member> removerMembers = ctx.getGuild().getMembersWithRoles(role);
                    for (Member var : removerMembers) {
                        if(counter.contains(var)){
                            counter.remove(var);
                        }
                    }
                    break;
                case "&":
                    LinkedList<Member> save = new LinkedList<>();
                    for (Member var : counter) {
                        if(!var.getRoles().contains(role))
                            save.add(var);
                    }
                    counter.removeAll(save);
                    break;
                case "|":
                    List<Member> adderMem = ctx.getGuild().getMembersWithRoles(role);
                    for (Member var : adderMem) 
                        counter.add(var);
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
        for (Member var : sorted) {
            mention += var.getAsMention() + "\n";
        }
        String cmdrole = "";
        for(int i = 0; i < cmd.length; i ++){
            if(i%2==0)
                cmdrole += ctx.getGuild().getRoleById(cmd[i]).getAsMention() + " ";
            else
                cmdrole += cmd[i] + " ";
        }


        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();

        LinkedList <String> cacherefresh = new LinkedList<>();

        LinkedList<EmbedBuilder> alleb = new LinkedList<>();

        if(mention.length() <= 5980 - ((cmdrole.length()>1024) ? 1024 : cmdrole.length())){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("People with ");
            eb.addField("" + counter.size(), (cmdrole.length()>1024) ? cmdrole.substring(0, 1024) : cmdrole, false);
            eb.setColor(1);
            dooku = 0;
            while(mention.length() > 1024){
                String submention = mention.substring(0, 1024);
                String[] part = submention.split("\n");
                submention = mention.substring(0, 1024 - part[part.length-1].length() -1 );
                eb.addField(""+ dooku, submention, true);
                mention = mention.substring(submention.length());
                dooku++;
                cacherefresh.add(submention);
            }
            cacherefresh.add(mention);
            eb.addField(""+dooku, mention, true);
            
            eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());
            alleb.add(eb);
        } else {

            int embsize = 5800 - ((cmdrole.length()>1024) ? 1024 : cmdrole.length());
            String firstmention = mention.substring(0, embsize);
            String[] firstpart = firstmention.split("\n");
            firstmention = firstmention.substring(0, embsize - firstpart[firstpart.length-1].length());
            mention = mention.substring(firstmention.length());

            EmbedBuilder first = new EmbedBuilder();
            first.setTitle("People with ");
            first.addField("" + counter.size(), (cmdrole.length()>1024) ? cmdrole.substring(0, 1024) : cmdrole, false);
            first.setColor(1);
            dooku = 0;
            while(firstmention.length() > 1024){
                String submention = firstmention.substring(0, 1024);
                String[] part = submention.split("\n");
                submention = firstmention.substring(0, 1024 - part[part.length-1].length() - 1);
                first.addField(""+ dooku, submention, true);
                firstmention = firstmention.substring(submention.length());
                dooku++;
                cacherefresh.add(submention);
            }
            cacherefresh.add(firstmention);
            first.addField(""+dooku, firstmention, true);
            
            first.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());

            alleb.add(first);


            int dookueb = 2;
            while(mention.length()>5990){
                String embmention = mention.substring(0, 5990);
                String[] embpart = embmention.split("\n");
                embmention = embmention.substring(0, 5990 - embpart[embpart.length-1].length() - 1);
                mention = mention.substring(embmention.length());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("Page " + dookueb++);
                eb.setColor(1);
                dooku = 0;
                while(embmention.length() > 1024){
                    String submention = embmention.substring(0, 1024);
                    String[] part = submention.split("\n");
                    submention = embmention.substring(0, 1024 - part[part.length-1].length());
                    embmention = embmention.substring(submention.length());
                    eb.addField(""+ dooku, submention, true);
                    dooku++;
                    cacherefresh.add(submention);
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
                String submention = mention.substring(0, 1024);
                String[] part = submention.split("\n");
                submention = mention.substring(0, 1024 - part[part.length-1].length() - 1);
                eb.addField(""+ dooku, submention, true);
                mention = mention.substring(submention.length());
                dooku++;
                cacherefresh.add(submention);
            }
            cacherefresh.add(mention);
            eb.addField(""+dooku, mention, true);
            alleb.add(eb);
        }
        
        Message editor = ctx.getChannel().sendMessage("wait a sec").complete();
        for (String var : cacherefresh) {
            if(var == null || var.length()==0)
                continue;
            editor.editMessage(var + " ").complete();
        }
        editor.delete().queue();

        ctx.getMessage().addReaction(data.check).queue();
        
        while(alleb.size()>10){
            MessageAction embeds = ctx.getChannel().sendMessage(alleb.remove().build());
            for (int i = 0; i < 10; i++) {
                embeds.embed(alleb.remove().build());
            }
            embeds.queue();
        }



        
        for (EmbedBuilder var : alleb) {
            ctx.getChannel().sendMessage(var.build()).queue();
        }
        

       
    }

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<roleID> {<!/&/|> <roleID>}", "Command to find out who has the Role AmongUs but also the Anime role for example.");
    }
    
}
