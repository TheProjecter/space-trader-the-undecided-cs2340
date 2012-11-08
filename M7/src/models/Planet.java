package models;

import java.util.*;
import java.util.Map.Entry;

/**
 * 
 * @author Samarth
 * 
 * This class represents a planet
 *
 */
public class Planet { //somehow extends SolarSystem
	private String resources;
	private String name;
	private Hashtable<String, ArrayList<TradeGood>> goods;
	private Hashtable<String, Integer> deflatedPrices;
	private SolarSystem galaxy;

	/**
	 * Constructor for the planet class
	 * @param name
	 */
	public Planet(String name) {
		this.name = name;
		
		goods = new Hashtable<String, ArrayList<TradeGood>>();
	}
//	
//	public Planet (String name, Hashtable<String, ArrayList<TradeGood>>) {
//		
//	}
	
	public Hashtable<String, Integer> getDeflatedPrices() {
		deflatedPrices = new Hashtable<String, Integer>();
		String[] resources = goods.keySet().toArray(new String[goods.size()]);
		for(int i = 0; i < goods.size(); i++) {
			if(!goods.get(resources[i]).isEmpty()) {
				deflatedPrices.put(resources[i], goods.get(resources[i]).get(0).getDeflatedPrice());
			}
			else {
				deflatedPrices.put(resources[i], 0);
			}
		}
		
		return deflatedPrices;
	}
	
	/**
	 * Gets the name of the planet
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	public void setResources(String resources) {
		this.resources = resources;
	}

	/**
	 * Sets the resources for the planet
	 * @param num
	 */
	public void setResources(int num) {
		switch (num) {
		case 0:
			resources = "No Special Resources";
			break;
		case 1:
			resources = "Mineral Rich";
			break;
		case 2:
			resources = "Mineral Poor";
			break;
		case 3:
			resources = "Desert";
			break;
		case 4:
			resources = "Lots of Water";
			break;
		case 5:
			resources = "Rich Soil";
			break;
		case 6:
			resources = "Rich Soil";
			break;
		case 7:
			resources = "Poor Soil";
			break;
		case 8:
			resources = "Lifeless";
			break;
		case 9:
			resources = "Weird Mushrooms";
			break;
		case 10:
			resources = "Lots of Herbs";
			break;
		case 11:
			resources = "Artistic";
			break;
		case 12:
			resources = "Warlike";
			break;
		}
	}
	
	/**
	 * Sets the trade goods of the planet
	 * @param goods
	 */
	public void setTradeGoods(Hashtable<String, ArrayList<TradeGood>> goods) {
		this.goods = goods;
	}
	
	/**
	 * Gets the trade goods of the planets
	 * @return
	 */
	public Hashtable<String, ArrayList<TradeGood>> getTradeGoods() {
		return goods;
	}
	
	/**
	 * Gets the resources of the planet
	 * @return
	 */
	public String getResources() {
		return resources;
	}
	
	/**
	 * Gets the galaxy that the planet belongs to
	 * @param galaxy
	 */
	public void setGalaxy(SolarSystem galaxy) {
		this.galaxy = galaxy;
	}
	
	public String write() {
		String out = "";
		out += "PlanetName " + name + "\n";
		out += "Resources " + resources + "\n\n";
		out += "TradeGoods " + "\n";
		for (Entry<String, ArrayList<TradeGood>> entry : goods.entrySet()) {
			//for (TradeGood good : entry.getValue()) {
			out += entry.getKey() + " " + entry.getValue().size() +" ";
			for (TradeGood good : entry.getValue()) {
				out += good.write();
				break;
			}
			out += "\n";
			//}
		}
		return out;
	}
}
