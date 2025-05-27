package com.mayhem.rs2.content.combat.impl;

import com.mayhem.rs2.content.combat.Hit;
import com.mayhem.rs2.entity.Entity;
import com.mayhem.rs2.entity.item.Item;
import com.mayhem.rs2.entity.player.Player;
import com.mayhem.rs2.entity.player.net.out.impl.SendMessage;

/**
 * @author Andy || ReverendDread Apr 16, 2017
 */
public class RingOfSuffering implements CombatEffect {
	
	/* Constants */
	public static final String SUFF_DAMAGE_KEY = "suffdamage";
	
	/**
	 * Checks if player has requirements, then processes.
	 * @param player {@link Player} the Player.
	 * @param e {@link Entity} the Enemy.
	 * @param damage the Damage.
	 */
	public static void recoil(Player player, Entity e, int damage) {
			
		if (damage <= 0 || player == null || e == null)
			return;
		
		Item ring = player.getEquipment().getItems()[12];
		
		if (ring == null)
			return;
		
		if ((ring.getId() == 19550) && (player.getSufferingCharges() > damage)) {
			int dmg = (int) Math.ceil(damage * 0.1D);
			if (dmg > 0) {
				
				player.setSufferingCharges(player.getSufferingCharges() - dmg); //Deincrements ring of suffering a charge.
				player.getAttributes().set(SUFF_DAMAGE_KEY, dmg); //Sets damage amount to recoil.
				new RingOfSuffering().execute(player, e); //Execute the effect.
				
				//Checks for ring charges, and degrades.
				if (player.getSufferingCharges() <= 0) {
					player.setSufferingCharges(0);
					player.getEquipment().getItems()[12].setId(19550);
					player.getEquipment().update();
					player.send(new SendMessage("Your ring of suffering runs out of recoil charges."));
				}
				
			}
		}	
	}
	
	/**
	 * Handles charging the ring of suffering.
	 * @param player {@link Player} the Player.
	 * @param item {@link Item} secondary item.
	 */
	public static void charge(Player player, Item ring, Item item) {
		int chargeAmount = item.getAmount() * 40;
		if (chargeAmount + player.getSufferingCharges() > 100_000) { //Check for overcharging the ring.
			player.send(new SendMessage("You can't charge your ring with this many charges. Current charges: " 
					+ player.getSufferingCharges() + "."));
			return;
		}
		player.setSufferingCharges(player.getSufferingCharges() + chargeAmount); //Add the charges.
		player.getInventory().remove(item);
		if (ring.getId() == 19950) { //turn normal ring into (r), otherwise dont.
			ring.setId(20665);
		}
		player.getInventory().update();
		player.send(new SendMessage("You charge your ring of suffering with " + chargeAmount + " recoil charges."));	
		player.send(new SendMessage("You now have " + player.getSufferingCharges() + " charges."));
	}
	
	@Override
	public void execute(Player p, Entity e) {
		if (e.isDead())
			return;
		e.hit(new Hit(p.getAttributes().getInt(SUFF_DAMAGE_KEY))); //Apply hit to entity.
		e.getAttributes().remove(SUFF_DAMAGE_KEY); //Remove the key from map, so its not used.
	}

}
