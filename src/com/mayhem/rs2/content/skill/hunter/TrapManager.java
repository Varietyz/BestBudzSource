package com.mayhem.rs2.content.skill.hunter;

//import com.google.common.collect.Range;
import com.mayhem.rs2.content.skill.Skills;
import com.mayhem.rs2.entity.Location;
import com.mayhem.rs2.entity.World;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @author Jack
 */
public class TrapManager {
    
    private static final Set<HunterTrap> trapList = Collections.synchronizedSet(new HashSet<HunterTrap>());
    
//    private static final ImmutableMap<Integer, Range > MAXIMUM_TRAPS = ImmutableMap
//            .of(1, Range.openClosed (1, 40),
//                2, Range.openClosed(40, 80),
//                3, Range.closed(80, Integer.MAX_VALUE));
            
    public static final int TRAP_FALL_TIME = (int) Math.floor((150 * 1000) * 0.8);
    
    public static void process() {
        trapList.stream()
                .forEach(trap -> {
                    if(Arrays.asList(World.getPlayers()).contains(trap.getOwner())) {
                        if(System.currentTimeMillis() > trap.getSetupTime() + TRAP_FALL_TIME) {
                            trap.fallDown();
                            deregisterTrap(trap);
                        } else {
                            trap.process();
                        }
                    } else {
                        trap.destruct();
                        deregisterTrap(trap);
                    }
                });
    }
    
    public static boolean canLayTrap(Player player, HunterTrap trap) {
        if(!trapExists(trap.getLocation())) {
            if(player.getLevels()[Skills.HUNTER] >= trap.getData().getLevel()) {
                //if(getPlayerTrapCount(player) < getMaximumTraps(player.getLevels()[Skills.HUNTER])) {
                    return true;
                } else {
                    player.send(new SendMessage("You have the maximum number of traps for your current level."));
                    return false;
                }
            }else {
                player.send(new SendMessage("You need a hunter level of " + trap.getData().getLevel()
                    + " to place this trap."));
                return false;
            }
       // } else {
         //   return false;
        }
   // }
    
    public static void registerTrap(HunterTrap trap) {
        trapList.add(trap);
        trap.create();
    }
    
    public static void deregisterTrap(HunterTrap trap) {
        trapList.remove(trap);
    }
    
    public static int getPlayerTrapCount(Player player) {
        return (int) trapList.stream().filter(trap -> trap.getOwner().equals(player)).count();
    }
    
   // public static int getMaximumTraps(int level) {
       // return MAXIMUM_TRAPS.entrySet().stream()
             //   .filter(entry-> entry.getValue().contains(level))
              //  .map(entry -> entry.getKey())
               // .findAny().orElse(1);
    //}
    
    public static Optional<HunterTrap> getTrapByLocation(Location loc) {
        return trapList.stream()
                .filter(trap -> trap.getLocation().equals(loc))
                .findAny();
    }
    
    public static boolean trapExists(Location loc) {
        return getTrapByLocation(loc).isPresent();
    }
}
