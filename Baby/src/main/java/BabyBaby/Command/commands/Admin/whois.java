package BabyBaby.Command.commands.Admin;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.ColouredStrings.ColouredStringAsciiDoc;
import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import me.duncte123.botcommons.messaging.EmbedUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class whois implements AdminCMD{

    @Override
    public String getName() {
        return "whois";
    }

    @Override
    public void handleOwner(CommandContext ctx) {
        handleAdmin(ctx);
	}
        
    

    @Override
    public MessageEmbed getOwnerHelp(String prefix) {
        return getAdminHelp(prefix);
    }
    
    @Override
    public void handleAdmin(CommandContext ctx) {
        List<String> cmds = ctx.getArgs();
			Member stalking; 
			MessageChannel channel = ctx.getChannel();

			if(cmds.size() >= 1){
                String person = cmds.get(0);
				person = person.replaceAll("<", "");
				person = person.replaceAll(">", "");
				person = person.replaceAll("!", "");
				person = person.replaceAll("@", "");
				stalking = ctx.getGuild().getMemberById(person);
			} else{
				stalking = ctx.getMember();
			}

			

			String nickname = (ctx.getMember().getNickname() != null) ? ctx.getMember().getNickname()
						: ctx.getMember().getEffectiveName();
			List<Role> allrolesList = stalking.getRoles();
			
			LinkedList<Role> allroles = new LinkedList<>();
			
			for (Role var : allrolesList) {
				allroles.add(var);
			}
			allroles.add(ctx.getGuild().getRoleById(ctx.getGuild().getId()));
			Role highest = allroles.peek();
			Role hoisted = null;
			
			for (Role var : allroles) {
				if(var.isHoisted()){
					hoisted = var;
					break;
				}
			}
		
			DateTimeFormatter jointime = DateTimeFormatter.ofPattern("E, dd.MM.yyyy, HH:mm");
			DateTimeFormatter createtime = DateTimeFormatter.ofPattern("E, dd.MM.yyyy");
			
			String rolementions = "";
			
			while(allroles != null && rolementions.length() < 250 && allroles.size() != 0){
				rolementions += allroles.poll().getAsMention() + ", ";
			}
			
			if(allroles.size() == 0){
				if(rolementions.charAt(rolementions.length()-1) == ','){
					rolementions = rolementions.substring(0, rolementions.length()-1);
				} 
			} else {
				rolementions += "` and " + allroles.size() + " more...`";
			}
			OffsetDateTime created = stalking.getUser().getTimeCreated();
			OffsetDateTime now = OffsetDateTime.now();
			int day = now.getDayOfYear() - created.getDayOfYear();
			int year =  now.getYear() - created.getYear() + ((now.getDayOfYear()<created.getDayOfYear())?-1:0);
			String actualtime = (year >0) ?  (year + Math.round(day/365.0)) + " years ago": day + " days ago";
			
			String addchecks = "Created as: **a " + ((stalking.getUser().isBot()) ? "bot" : "user") + " account** \n Created at: **" + stalking.getUser().getTimeCreated().format(createtime) + "** `(" + actualtime + ")`"; 
			
			
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("@" + stalking.getUser().getAsTag() + " (" + stalking.getId() + ")");
			eb.setColor(highest.getColor());
			
			eb.addField("Nickname", "`" + ((stalking.getNickname() != null) ? stalking.getNickname() : stalking.getEffectiveName()) + "` " + stalking.getAsMention(), false);
			eb.addField("Joined at", "`" + stalking.getTimeJoined().toLocalDateTime().format(jointime) + "`", false);
			eb.addField("Highest Role", highest.getAsMention(), true);
			eb.addField("Hoisted Role",(hoisted != null) ? hoisted.getAsMention(): "`Unhoisted`", true);
			eb.addField("Roles obtained (" + (1+allrolesList.size()) + ")" , rolementions, false);
			eb.addField("Additional Checks", addchecks, false);
			// eb.addBlankField(false);
			eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());
			eb.setThumbnail(stalking.getUser().getAvatarUrl());

			channel.sendMessage(eb.build()).queue();
        
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        EmbedBuilder embed = EmbedUtils.getDefaultEmbed();

        embed.setTitle("Help page of: `" + getName()+"`");
        embed.setDescription("Return Info about yourself or others.");

        // general use
        embed.addField("", new ColouredStringAsciiDoc()
                .addBlueAboveEq("general use")
                .addOrange(prefix + getName() + "[id/mention]")
                .build(), false);

        return embed.build();
    }


    
}
