package BabyBaby.Command.commands.Public;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.Command.StandardHelp;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class sieb implements PublicCMD {

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
        return "sieb";
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();
        
        String[] cmd = new String [cmds.size()];

        int dooku = 0;
        for (String var : cmds) {
            cmd[dooku] = var;
            dooku++;
        }

        HashSet<Member> counter = new HashSet<>();

        List<Member> tmp = ctx.getGuild().getMembers();
        Role role1 = ctx.getGuild().getRoleById(cmd[0]);
        for (Member var : tmp) {
            if(var.getRoles().contains(role1))
                counter.add(var);
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

        String mention = "";
        for (Member var : counter) {
            mention += var.getAsMention() + "\n";
        }
        String cmdrole = "";
        for(int i = 1; i < cmd.length; i ++){
            if(i%2==1)
                cmdrole += "<@&" + cmd[i] + "> ";
            else
                cmdrole += cmd[i] + " ";
        }


        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();

        LinkedList <String> cacherefresh = new LinkedList<>();

        
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("People with ");
        eb.addField("" + counter.size(), (cmdrole.length()>1024) ? cmdrole.substring(0, 1024) : cmdrole, false);
        eb.setColor(1);
        if(mention.length() <= 6000 && mention.length() > 5){
            dooku = 0;
            while(mention.length() > 1024){
                String submention = mention.substring(0, 1024);
                String[] part = submention.split("\n");
                submention = mention.substring(0, 1024 - part[part.length-1].length());
                eb.addField(""+ dooku, submention, true);
                mention = mention.substring(1024-part[part.length-1].length());
                dooku++;
                cacherefresh.add(submention);
            }
            cacherefresh.add(mention);
            eb.addField(""+dooku, mention, true);
        } else {
            ctx.getChannel().sendMessage("This is over 6000 chars (or empty), can't send this big messages. Sry!").queue();
            return;
        }
        eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());
        

        String lastone = cacherefresh.removeLast();

        for (String var : cacherefresh) {
            ctx.getChannel().sendMessage("wait a sec").queue(response -> {
                response.editMessage(var).queue(response2 -> {
                    response2.delete().queue();
                });
            });		
        }

        ctx.getChannel().sendMessage("wait a sec").queue(response -> {
            response.editMessage(lastone).queue(response2 -> {
                response2.delete().queue(response3 -> {
                    ctx.getChannel().sendMessage(eb.build()).queue();
                });
            });
        });	


        ctx.getMessage().addReaction(":checkmark:769279808244809798").queue();

    }   

    @Override
    public MessageEmbed getPublicHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "<roleID> {<!/&/|> <roleID>}", "Command to find out who has the Role AmongUs but also the Anime role for example.");
    }
    
}
