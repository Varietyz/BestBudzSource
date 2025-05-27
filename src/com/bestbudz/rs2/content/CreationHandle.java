package com.bestbudz.rs2.content;

import com.bestbudz.rs2.entity.stoner.Stoner;

/**
 * Item handle
 * 
 * @author Jaybane
 *
 */
public interface CreationHandle {

	/**
	 * Handle
	 * 
	 * @param stoner
	 */
	public void handle(Stoner stoner, ItemCreation data);

}
