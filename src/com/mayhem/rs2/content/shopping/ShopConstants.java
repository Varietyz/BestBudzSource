package com.mayhem.rs2.content.shopping;

/**
 * Holds the Shop Constants
 * @author Daniel
 *
 */
public class ShopConstants {
	
	/**
	 * Color of send messages
	 */
	public static final String COLOUR = "<col=7A007A>";
	
	/**
	 * Shops that are available to Iron players
	 */
	public static int[] IRON_SHOPS = { 3, 27, 5, 6, 7, 30, 100, 101, 102, 103, 120, 121, 106, 349, 353, 351, 20, 26, 29, 32, 33, 37, 38, 39, 41, 87, 90, 89, 91, 92, 93, 94, 95, 362, 0, 15, 16, 17, 18, 20, 25, 28, 30, 31, 32, 33, 34, 35, 37, 40};
	
	/**
	 * Shops that players may view/sell but not purchase
	 */
	/** Added 0 to no buy shops to test with ironman **/
	public static int[] IRON_NO_BUY_SHOPS = {29,0};
	
	public static int[] IRON_BUY_ITEMS = {10010,11260,954,1735,590,1523,1755,946,952,12938};
	
	
	 // Items that can't be sold to shop  If you want to add items you need to just put the ID
	
	public static int[] NO_SELL = {};
	
}
