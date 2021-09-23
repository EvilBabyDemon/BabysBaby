package BabyBaby.Command.commands.Admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import BabyBaby.Command.AdminCMD;
import BabyBaby.Command.CommandContext;
import BabyBaby.Command.StandardHelp;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;

public class WhoisCMD implements AdminCMD{

    @Override
    public String getName() {
        return "whois";
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
			
			for (Role role : allrolesList) {
				allroles.add(role);
			}
			allroles.add(ctx.getGuild().getRoleById(ctx.getGuild().getId()));
			Role highest = allroles.peek();
			Role hoisted = null;
			
			for (Role role : allroles) {
				if(role.isHoisted()){
					hoisted = role;
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
			day = (day<0)? 365+day:day;
			int year =  now.getYear() - created.getYear() + ((now.getDayOfYear()<created.getDayOfYear())?-1:0);

			String multyear = ((year + Math.round(day/365.0)) == 1) ? " year ago" : " years ago";
			String multday = (day== 1) ? " day ago" : " days ago";
			String actualtime = (year >0) ?  (year + Math.round(day/365.0)) + multyear : day + multday;
			
			String addchecks = "Created as: **a " + ((stalking.getUser().isBot()) ? "bot" : "user") + " account** \n Created at: **" + stalking.getUser().getTimeCreated().format(createtime) + "** `(" + actualtime + ")`"; 
			
			String invitee = "NaN";

			Connection c = null;
			PreparedStatement pstmt = null;
			try {
				Class.forName("org.sqlite.JDBC");
				c = DriverManager.getConnection(Data.db);
				
				pstmt = c.prepareStatement("SELECT INVITEE FROM INVITED WHERE INVITED = ?;");
				pstmt.setString(1, stalking.getId());	
				ResultSet rs = pstmt.executeQuery();
				
				if(rs.next()){
					invitee = rs.getString("INVITEE");
				}
				
				pstmt.close();
				c.close();
			} catch ( Exception e ) {	
				System.out.println(e.getClass().getName() + ": " + e.getMessage());
				e.printStackTrace();
			}
			//Not sure atm if this throws an error when empty


			try {
				invitee = ctx.getGuild().getMemberById(invitee).getAsMention(); 
			} catch (Exception e) {
			}
			
			EmbedBuilder eb = new EmbedBuilder();
			eb.setTitle("@" + stalking.getUser().getAsTag() + " (" + stalking.getId() + ")");
			eb.setColor(highest.getColor());
			
			eb.addField("Nickname", "`" + ((stalking.getNickname() != null) ? stalking.getNickname() : stalking.getEffectiveName()) + "` " + stalking.getAsMention(), false);
			eb.addField("Joined at", "`" + stalking.getTimeJoined().toLocalDateTime().format(jointime) + "`", false);
			eb.addField("Invited by", invitee, false);
			eb.addField("Highest Role", highest.getAsMention(), true);
			eb.addField("Hoisted Role",(hoisted != null) ? hoisted.getAsMention(): "`Unhoisted`", true);
			eb.addField("Roles obtained (" + (1+allrolesList.size()) + ")" , rolementions, false);
			eb.addField("Additional Checks", addchecks, false);
			// eb.addBlankField(false);
			eb.setFooter("Summoned by: " + nickname, ctx.getAuthor().getAvatarUrl());
			eb.setThumbnail(stalking.getUser().getAvatarUrl());

			channel.sendMessageEmbeds(eb.build()).queue();
        
    }

    @Override
    public MessageEmbed getAdminHelp(String prefix) {
        return StandardHelp.Help(prefix, getName(), "[id/mention]", "Return Info about yourself or others.");
    }


    
}
