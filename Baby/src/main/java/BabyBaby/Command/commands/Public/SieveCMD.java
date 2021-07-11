package BabyBaby.Command.commands.Public;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.ColouredStrings.ColouredStringAsciiDoc;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.PublicCMD;
import BabyBaby.data.Data;
import BabyBaby.data.Helper;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class SieveCMD implements PublicCMD {
    boolean pleb = true;
    final String NOT_ID = "You used an ID which was neither from a Role or Channel.";

    @Override
    public String getName() {
        return "sieve";
    }
    @Override
    public List<String> getAliases() {
        return Arrays.asList("bigsieve");
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        if(ctx.getMessage().getContentRaw().contains("bigsieve")){
            pleb = false;
        }
        handlePublic(ctx);
    }

    @Override
    public void handlePublic(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();
        
        String[] cmd = new String [cmds.size()];

        int dooku = 0;
        for (String arg : cmds) {
            cmd[dooku] = arg;
            dooku++;
        }

        HashSet<Member> counter = new HashSet<>();

        List<Member> allMem = ctx.getGuild().getMembers();
        if(cmd[0].equals(Data.ethid)){
            counter = new HashSet<>(allMem);
        } else {
            Role role = null;
            GuildChannel channel = null;
            boolean roleID = false;
            try {
                Long.parseLong(cmd[0]);
            } catch (Exception e) {
                ctx.getChannel().sendMessage(NOT_ID).queue();
                return;
            }
            
            role = ctx.getGuild().getRoleById(cmd[0]);
            channel = ctx.getGuild().getGuildChannelById(cmd[0]);
            
            if(role == null && channel == null){
                ctx.getChannel().sendMessage(NOT_ID).queue();
                return;
            } else if(role != null){
                roleID = true;
            }

            if(roleID) {
                counter = new HashSet<>(ctx.getGuild().getMembersWithRoles(role));
            }else {
                for (Member member : allMem) {
                    if(member.hasAccess(channel))
                        counter.add(member);
                }
            }
        }   

        for(int i = 1; i < cmd.length-1; i +=2){
            Role role = null;
            GuildChannel channel = null;
            boolean roleID = false;
            try {
                Long.parseLong(cmd[0]);
            } catch (Exception e) {
                ctx.getChannel().sendMessage(NOT_ID).queue();
                return;
            }
            role = ctx.getGuild().getRoleById(cmd[i+1]);
            channel = ctx.getGuild().getGuildChannelById(cmd[i+1]);

            if(role == null && channel == null){
                ctx.getChannel().sendMessage(NOT_ID).queue();
                return;
            } else if(role != null){
                roleID = true;
            }

            switch (cmd[i]){
                case "!":
                    if(roleID){
                        List<Member> removerMembers = ctx.getGuild().getMembersWithRoles(role);
                        counter.removeAll(removerMembers);
                    } else {
                        LinkedList<Member> save = new LinkedList<>();
                        for (Member member : counter) {
                            if(member.hasAccess(channel))
                                save.add(member);
                        }
                        counter.removeAll(save);
                    }
                    break;
                case "&":
                    if(roleID){
                        LinkedList<Member> save = new LinkedList<>();
                        for (Member member : counter) {
                            if(!member.getRoles().contains(role))
                                save.add(member);
                        }
                        counter.removeAll(save);
                    } else {
                        LinkedList<Member> save = new LinkedList<>();
                        for (Member member : counter) {
                            if(!member.hasAccess(channel))
                                save.add(member);
                        }
                        counter.removeAll(save);
                    }
                    
                    break;
                case "|":
                    if(roleID){
                        List<Member> adderMem = ctx.getGuild().getMembersWithRoles(role);
                        for (Member member : adderMem) 
                            counter.add(member);
                    } else {
                        for (Member member : allMem) {
                            if(member.hasAccess(channel))
                                counter.add(member);
                        }
                    }
                    
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
                cmdrole += (ctx.getGuild().getRoleById(cmd[i])!= null) ? ctx.getGuild().getRoleById(cmd[i]).getAsMention() : ctx.getGuild().getGuildChannelById(cmd[i]).getAsMention() + " ";
            else
                cmdrole += cmd[i] + " ";
        }


        String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
                : ctx.getMember().getEffectiveName();

        LinkedList <String> cacherefresh = new LinkedList<>();
        LinkedList<EmbedBuilder> alleb = new LinkedList<>();

        if(mention.length() <= 5980 - ((cmdrole.length()>1024) ? 1024 : cmdrole.length())){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("People with or have access to");
            eb.addField("" + counter.size(), (cmdrole.length()>1024) ? cmdrole.substring(0, 1024) : cmdrole, false);
            eb.setColor(1);
            dooku = 0;
            while(mention.length() > 1024){
                mention = Helper.addFieldSieve(eb, cacherefresh, dooku++, mention);
            }
            cacherefresh.add(mention);
            eb.addField(""+dooku, mention, true);
            eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());
            alleb.add(eb);

        } else {

            int embsize = 5800 - ((cmdrole.length()>1024) ? 1024 : cmdrole.length());
            String firstmention = mention.substring(0, embsize);
            String[] firstpart = firstmention.split("\n");
            firstmention = firstmention.substring(0, embsize - firstpart[firstpart.length-1].length()-1);
            mention = mention.substring(firstmention.length());

            EmbedBuilder first = new EmbedBuilder();
            first.setTitle("People with or have access to");
            first.addField("" + counter.size(), (cmdrole.length()>1024) ? cmdrole.substring(0, 1024) : cmdrole, false);
            first.setColor(1);
            dooku = 0;
            while(firstmention.length() > 1024){
                firstmention = Helper.addFieldSieve(first, cacherefresh, dooku++, firstmention);
            }
            cacherefresh.add(firstmention);
            first.addField(""+dooku, firstmention, true);
            
            alleb.add(first);

            if(pleb){
                first.setFooter("Summoned by: " + nickname + ". Not shown members: " + mention.split("\n").length, ctx.getAuthor().getAvatarUrl());
            } else{ 
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
    public MessageEmbed getPublicHelp(String prefix) {
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();

        embed.setTitle("Help page of: `" + getName() +"`");
        embed.setDescription("Command to find out who has the Role AmongUs but also the Anime role for example. Get all role id's with `" + prefix + "allroles`. You can also input channelID's");

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use")
                .addNormal(prefix + getName() + " " + "<roleID> {<!/&/|> <roleID>}")
                .addBlueAboveEq("Example Italian ! Swiss-German & German")
                .addNormal(prefix + getName() + " 747792281855197285 ! 747791537089544283 & 747791435570610256")
                .build(), false);

        return embed.build();
    }
    
}
