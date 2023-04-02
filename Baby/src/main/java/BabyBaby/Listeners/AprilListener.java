package BabyBaby.Listeners;

import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import BabyBaby.Command.commands.Admin.AprilFools;
import BabyBaby.data.Data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.RoleManager;
import net.dv8tion.jda.api.requests.restaction.order.RoleOrderAction;

public class AprilListener extends ListenerAdapter {
    
    //only add and not remove as every add should trigger also a remove
    @Override
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) { 
        if(!event.getGuild().getId().equals(Data.ETH_ID)) {
            return;
        }
        if(AprilFools.turnOff) {
            return;
        }

        Guild guild = event.getGuild();

        List<Role> roles = guild.getRoles();
        LinkedList<Role> order = new LinkedList<>(roles);

        RoleOrderAction roa = guild.modifyRolePositions();

        String[] ids = {"1089996311522201715", "1089996425091371128" , "1089996512701984789", "1089996620625612921", "1089996706009063424", "1089996740654006412", "1089996797780447282"};
        LinkedList<Role> cr = new LinkedList<>();
        
        for (String id : ids) {
            Role role = guild.getRoleById(id);
            cr.add(role);
            order.remove(role);
        }

        Role baby = guild.getRoleById("840612800656572446");
        Role admin = guild.getRoleById("747753814723002500");
        Role mod = guild.getRoleById("815932497920917514");
        order.remove(baby);
        order.remove(admin);
        order.remove(mod);

        Comparator<Role> compRole = new Comparator<>() {
            @Override
            public int compare(Role o1, Role o2) {
                return guild.getMembersWithRoles(o1).size() + AprilFools.weights.getOrDefault(o1.getId(), 0) - guild.getMembersWithRoles(o2).size() + AprilFools.weights.getOrDefault(o2.getId(), 0);
            }
        };
        cr.sort(compRole);

        for(int i = cr.size()-1; i>=0; i--) {
            order.push(cr.get(i));
        }
        order.push(mod);
        order.push(admin);
        order.push(baby);

        if(roles.equals(order)) {
            //Order didnt change
            return;
        }
        System.out.println("Resort roles!");
        
        Comparator<Role> compRole2 = new Comparator<>() {
            @Override
            public int compare(Role o2, Role o1) {
                if(AprilFools.weights.getOrDefault(o1.getId(), 0) != 0) {
                    System.out.println(o1.getId() + " with weight: " + AprilFools.weights.getOrDefault(o1.getId(), 0));
                }
                if(AprilFools.weights.getOrDefault(o2.getId(), 0) != 0) {
                    System.out.println(o2.getId() + " with weight: " + AprilFools.weights.getOrDefault(o2.getId(), 0));
                }

                return guild.getMembersWithRoles(o1).size() + AprilFools.weights.getOrDefault(o1.getId(), 0) - guild.getMembersWithRoles(o2).size() + AprilFools.weights.getOrDefault(o2.getId(), 0);
            }
        };
        cr.sort(compRole2);

        Role tmp = null;
        for (Role move : cr) {
            roa.selectPosition(move);
            if(tmp == null) {
                roa.moveBelow(guild.getRoleById("815932497920917514"));
            } else {
                roa.moveBelow(tmp);
            }
        }

        roa.complete();
        

        for (Role ic : cr) {
            if (ic.getIcon() == null){
                continue;
            }
            if (cr.get(cr.size()-1).equals(ic)) {
                continue;
            }
            RoleManager rm = cr.get(cr.size()-1).getManager();
            Icon crown = null;
            try {
                crown = Icon.from(ic.getIcon().getIcon().download().join());
            } catch (IOException e) {
                guild.getMemberById(Data.myselfID).getUser().openPrivateChannel().complete().sendMessage("Icon download didnt work AAAAAAHHHH").queue();
                continue;
            }
            String reset = null;
            ic.getManager().setIcon(reset).complete();
            rm.setIcon(crown).complete();
            break;
        }
        System.out.println("Done with sorting.");

    }   



}
