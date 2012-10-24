package systems;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.util.*;
import java.util.Map.Entry;

import models.*;
import views.*;

/**
 * Main Controller class for the game. Does all of the labor required.
 * @author Justin
 */
public class MainController extends JFrame {
	private JFrame frame;
	private Dimension dim;
	private JPanel cards;
	private int frameWidth, frameHeight;
	private final String INTRO = "Intro Screen";
	private final String START = "Start Screen";
	private final String UNIVERSE = "Universe Screen";
	private final String MARKET = "Market Screen";
	
	private Hashtable hash;
	private SolarSystem curGalaxy;
	private Planet curPlanet;
	private Universe universe;
	private Planet[] planetList;
	private SolarSystem[] galaxies;
	
	private StartView startView;
	private MarketView marketView;
	private Cargo cargo;
	private Spaceship playerShip;
	private Player player;
	
	/**
	 * Constructor for Main Controller
	 */
	public MainController() {
		dim = new Dimension(679, 473);
		frameWidth = (int)dim.getWidth();
		frameHeight = (int)dim.getHeight();
		frame = new JFrame("Space Trader");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setPreferredSize(dim);
		frame.setResizable(false);
		
		addViews(frame.getContentPane());
		
		
		
		frame.setVisible(true);
		frame.pack();
	}
	
	/**
	 * Sets up all of the views and adds panels as cards.
	 * 
	 * @return If cards were successfully added
	 */
	public void addViews(Container pane) {
		//Generate intro view
		IntroView introView = new IntroView();
		JPanel introCard = introView.getPanel();
		introView.getBtnNewGame().addActionListener(new NewGameListener());
		introView.getBtnLoadGame().addActionListener(new LoadGameListener());
		
		//Generate start view
		startView = new StartView();
		JPanel startCard = startView.getPanel();
		startView.getBtnDone().addActionListener(new PlayerDoneListener());
		
		//Generate galaxy map
		universe = new Universe();
		generateGalaxies();
		refreshTradeGoods();
		//FOR DEBUGGIN ONLY
		/*for(int i = 0; i < galaxies.length; i++) {
			System.out.println(galaxies[i].toString());
		}*/
		///////////////////
		UniverseView universeView = new UniverseView();
		hash = universeView.drawGalaxies(galaxies, new PlanetListener());
		JPanel universeCard = universeView.getPanel();
		
		//Generate market view
		cargo = new Cargo();
		playerShip = new Flea();
		refreshCargoGoods();
		marketView = new MarketView(new BuyListener(), new SellListener());
		JPanel marketCard = marketView.getPanel();
		
		//Add cards to card layout
		cards = new JPanel(new CardLayout());
		cards.add(introCard, INTRO);
		cards.add(startCard, START);
		cards.add(universeCard, UNIVERSE);
		cards.add(marketCard, MARKET);

        pane.add(cards, BorderLayout.CENTER);
	}

	public void nextState(String next) {
		CardLayout cl = (CardLayout)(cards.getLayout());
        cl.show(cards, next);
	}
	
	/**
	 * Listener class for new game selection.
	 * @author Justin
	 */
	public class NewGameListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			nextState(START);
		}
	}
	
	/**
	 * Listener class for load game selection.
	 * @author Justin
	 */
	public class LoadGameListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
		}
	}
	
	/**
	 * Listener class for finishing character creation.
	 * @author Justin
	 */
	public class PlayerDoneListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			if(startView.checkFields()) {
				int pilot = startView.getPPoints();
				int fighter = startView.getFPoints();
				int engineer = startView.getEPoints();
				int trader = startView.getTPoints();
				String name = startView.getTextField();
				int difficulty;
				if(startView.difficultyGroup().getSelection() == startView.easy()) {
					difficulty = 1;
				}
				else if(startView.difficultyGroup().getSelection() == startView.medium()) {
					difficulty = 2;
				}
				else {
					difficulty = 3;
				}
				
				player = new Player(pilot, fighter, trader, engineer, difficulty, name);
				nextState(UNIVERSE);
			}
		}
	}
	
	public class PlanetListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			curGalaxy = (SolarSystem)hash.get(e.getSource());
			curPlanet = curGalaxy.getPlanets()[0];
			universe.setCurGalaxy(curGalaxy);
			universe.setCurPlanet(curPlanet);
			System.out.println(curGalaxy.toString());
			
			marketView.setPlanetName(curPlanet.getName());
			setMarketValues();
			nextState(MARKET);
		}
	}
	
	public class BuyListener implements ActionListener {
		Hashtable<String, ArrayList<TradeGood>> iPlanet;
		Hashtable<String, ArrayList<TradeGood>> iPlayer;
		
		public void actionPerformed(ActionEvent e) {
			iPlanet = curPlanet.getTradeGoods();
			iPlayer = cargo.getTradeGoods();
			int amount = marketView.getAmount();
			int remaining = playerShip.getRemSpace();
			
			if(remaining - amount >= 0) {
				marketView.getSpaceWarning().setVisible(false);
				String resource = "";
				if(e.getSource() == marketView.getWaterBuy())
					resource = "Water";
				else if(e.getSource() == marketView.getFursBuy())
					resource = "Furs";
				else if(e.getSource() == marketView.getGamesBuy())
					resource = "Games";
				else if(e.getSource() == marketView.getFoodBuy())
					resource = "Food";
				else if(e.getSource() == marketView.getFirearmsBuy())
					resource = "Firearms";
				else if(e.getSource() == marketView.getMachinesBuy())
					resource = "Machines";
				else if(e.getSource() == marketView.getMedicineBuy())
					resource = "Medicine";
				else if(e.getSource() == marketView.getNarcoticsBuy())
					resource = "Narcotics";
				else if(e.getSource() == marketView.getOreBuy())
					resource = "Ore";
				else if(e.getSource() == marketView.getRobotsBuy())
					resource = "Robots";
				
				ArrayList<TradeGood> tPlayer = iPlayer.get(resource);
				ArrayList<TradeGood> tPlanet = iPlanet.get(resource);
				if(tPlanet.size() >= amount) {
					
					for(int i = 0; i < amount; i++) {
						TradeGood tempGood = tPlanet.remove(tPlanet.size()-1);
						tPlayer.add(tempGood);
					}
					
					playerShip.setRemSpace(remaining - amount);
					setMarketValues();
				}
				else {
					marketView.getSpaceWarning().setText("Not enough of that to buy!");
					marketView.getSpaceWarning().setVisible(true);
				}
			}
			else {
				marketView.getSpaceWarning().setText("No space remaining in cargo hold!");
				marketView.getSpaceWarning().setVisible(true);
			}
		}
		
	}
	
	public class SellListener implements ActionListener {
		Hashtable<String, ArrayList<TradeGood>> iPlanet;
		Hashtable<String, ArrayList<TradeGood>> iPlayer;
		
		public void actionPerformed(ActionEvent e) {
			iPlanet = curPlanet.getTradeGoods();
			iPlayer = cargo.getTradeGoods();
			int amount = marketView.getAmount();
			int remaining = playerShip.getRemSpace();
			String resource = "";
			
			if(e.getSource() == marketView.getWaterSell())
				resource = "Water";
			else if(e.getSource() == marketView.getFursSell())
				resource = "Furs";
			else if(e.getSource() == marketView.getGamesSell())
				resource = "Games";
			else if(e.getSource() == marketView.getFoodSell())
				resource = "Food";
			else if(e.getSource() == marketView.getFirearmsSell())
				resource = "Firearms";
			else if(e.getSource() == marketView.getMachinesSell())
				resource = "Machines";
			else if(e.getSource() == marketView.getMedicineSell())
				resource = "Medicine";
			else if(e.getSource() == marketView.getNarcoticsSell())
				resource = "Narcotics";
			else if(e.getSource() == marketView.getOreSell())
				resource = "Ore";
			else if(e.getSource() == marketView.getRobotsSell())
				resource = "Robots";
			
			ArrayList<TradeGood> tPlanet = iPlanet.get(resource);
			ArrayList<TradeGood> tPlayer = iPlayer.get(resource);
			if(tPlayer.size() >= amount) {
				marketView.getSpaceWarning().setVisible(false);
				for(int i = 0; i < amount; i++) {
					TradeGood tempGood = tPlayer.remove(tPlayer.size()-1);
					tPlanet.add(tempGood);
				}
				
				playerShip.setRemSpace(remaining + amount);
				setMarketValues();
			}
			else {
				marketView.getSpaceWarning().setText("Not enough of that to sell!");
				marketView.getSpaceWarning().setVisible(true);
			}
		}
		
	}
	
	/**
	 * Method to generate all of the galaxies.
	 * @return A list of galaxies in order to draw to map
	 */
	public SolarSystem[] generateGalaxies() {
		String[] names = universe.getNames();
		galaxies = new SolarSystem[names.length];
		String name = "";
		int x;
		int y;
		int tech;
		Random rand = new Random();
		
		for(int i = 0; i < names.length; i++) {
			x = rand.nextInt((frameWidth/names.length)-20) + (i*(frameWidth-20)/names.length);
			int scale = rand.nextInt(names.length-1);
			y = rand.nextInt((frameHeight/names.length)-20) + (scale*(frameHeight-20)/names.length);
			tech = rand.nextInt(8);
			name = names[i];
			SolarSystem galaxy = new SolarSystem(name, x, y);
			galaxy.setTech(tech);
			galaxies[i] = galaxy;
		}
		
		planetList = generatePlanets();
		
		return galaxies;
	}
	
	/**
	 * Method to generate planets called once all of the galaxies are created.
	 * @return An array of all the planets for record
	 */
	public Planet[] generatePlanets() {
		Random rand = new Random();
		String[] planetNames = universe.getPNames();
		Planet[] allPlanets = new Planet[planetNames.length];
		
		for(int i = 0; i < planetNames.length; i++) {
				int num = rand.nextInt(13);
				Planet[] planets = new Planet[1]; //change to more to add more planets
				planets[0] = new Planet(planetNames[i]); //loop through to create more planets
				planets[0].setResources(num);
				planets[0].setGalaxy(galaxies[i]);
				galaxies[i].setPlanets(planets);
				allPlanets[i] = planets[0];
		}
		refreshTradeGoods();
		return allPlanets;
	}
	
	public void refreshTradeGoods() {
		Random rand = new Random();
		TradeGood[] listGoods = new TradeGood[] {
				new Water(),
				new Furs(),
				new Games(),
				new Food(),
				new Firearms(),
				new Machines(),
				new Medicine(),
				new Narcotics(),
				new Ore(),
				new Robots()
		};
		
		for(int i = 0; i < galaxies.length; i++) {
			SolarSystem galaxy = galaxies[i];
			Planet[] planets = galaxy.getPlanets();
			
			for(int j = 0; j < planets.length; j++) {
				Planet planet = planets[j];
				Hashtable<String, ArrayList<TradeGood>> tempGoods = new Hashtable<String, ArrayList<TradeGood>>();
				
				for(TradeGood resource : listGoods) {
					int quantity;
					if(galaxies[i].getTechLevelNum() >= resource.getMTLP())
						quantity = rand.nextInt(10) + 3; //produces at least 3 of each possible resource
					else
						quantity = 0;
					
					//calculate price
					double fract = rand.nextInt(resource.getVar()+1);
					int offset = (int)fract*resource.getPrice();
					int flux = resource.getIPL()*(galaxies[i].getTechLevelNum()-resource.getMTLP());
					int price = resource.getPrice() + flux + offset;
					resource.setTotalPrice(price);
					
					ArrayList<TradeGood> list = new ArrayList<TradeGood>(quantity);
					for(int n = 0; n < quantity; n++) {
						list.add(resource);
					}
					tempGoods.put(resource.getName(), list);
				}
				
				planet.setTradeGoods(tempGoods);
			}
		}
		
	}
	
	public void refreshCargoGoods() {
		TradeGood[] listGoods = new TradeGood[] {
				new Water(),
				new Furs(),
				new Games(),
				new Food(),
				new Firearms(),
				new Machines(),
				new Medicine(),
				new Narcotics(),
				new Ore(),
				new Robots()
		};
		
		Hashtable<String, ArrayList<TradeGood>> tempGoods = new Hashtable<String, ArrayList<TradeGood>>();
				
		for(TradeGood resource : listGoods) {
			int quantity = 0;
			ArrayList<TradeGood> list = new ArrayList<TradeGood>(quantity);
			for(int n = 0; n < quantity; n++) {
				list.add(resource);
			}
			tempGoods.put(resource.getName(), list);
		}
				
		cargo.setTradeGoods(tempGoods);
	}

	public void setMarketValues() {
		Hashtable<String, ArrayList<TradeGood>> iPlanet = curPlanet.getTradeGoods();
		Hashtable<String, ArrayList<TradeGood>> iPlayer = cargo.getTradeGoods();
		
		marketView.setLblRemaining(""+playerShip.getRemSpace());
		for(Entry entry : iPlanet.entrySet()) {
			ArrayList<TradeGood> resource = (ArrayList<TradeGood>)entry.getValue();
			int quantity = resource.size();
			
			if((String)entry.getKey() == "Water") {
				marketView.getLblMwater().setText(""+quantity);
				if(quantity == 0)
					marketView.getWaterBuy().setEnabled(false);
				else {
					marketView.getWaterBuy().setEnabled(true);
					marketView.getWaterBuy().setText("Buy["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Furs") {
				marketView.getLblMfurs().setText(""+quantity);
				if(quantity == 0)
					marketView.getFursBuy().setEnabled(false);
				else {
					marketView.getFursBuy().setEnabled(true);
					marketView.getFursBuy().setText("Buy["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Games") {
				marketView.getLblMgames().setText(""+quantity);
				if(quantity == 0)
					marketView.getGamesBuy().setEnabled(false);
				else {
					marketView.getGamesBuy().setEnabled(true);
					marketView.getGamesBuy().setText("Buy["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Firearms") {
				marketView.getLblMfirearms().setText(""+quantity);
				if(quantity == 0)
					marketView.getFirearmsBuy().setEnabled(false);
				else {
					marketView.getFirearmsBuy().setEnabled(true);
					marketView.getFirearmsBuy().setText("Buy["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Food") {
				marketView.getLblMfood().setText(""+quantity);
				if(quantity == 0)
					marketView.getFoodBuy().setEnabled(false);
				else {
					marketView.getFoodBuy().setEnabled(true);
					marketView.getFoodBuy().setText("Buy["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Machines") {
				marketView.getLblMmachines().setText(""+quantity);
				if(quantity == 0)
					marketView.getMachinesBuy().setEnabled(false);
				else {
					marketView.getMachinesBuy().setEnabled(true);
					marketView.getMachinesBuy().setText("Buy["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Robots") {
				marketView.getLblMrobots().setText(""+quantity);
				if(quantity == 0)
					marketView.getRobotsBuy().setEnabled(false);
				else {
					marketView.getRobotsBuy().setEnabled(true);
					marketView.getRobotsBuy().setText("Buy["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Medicine") {
				marketView.getLblMmedicine().setText(""+quantity);
				if(quantity == 0)
					marketView.getMedicineBuy().setEnabled(false);
				else {
					marketView.getMedicineBuy().setEnabled(true);
					marketView.getMedicineBuy().setText("Buy["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Narcotics") {
				marketView.getLblMnarcotics().setText(""+quantity);
				if(quantity == 0)
					marketView.getNarcoticsBuy().setEnabled(false);
				else {
					marketView.getNarcoticsBuy().setEnabled(true);
					marketView.getNarcoticsBuy().setText("Buy["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Ore") {
				marketView.getLblMore().setText(""+quantity);
				if(quantity == 0)
					marketView.getOreBuy().setEnabled(false);
				else {
					marketView.getOreBuy().setEnabled(true);
					marketView.getOreBuy().setText("Buy["+resource.get(0).getTotalPrice()+"]");
				}
			}
		}
		
		for(Entry entry : iPlayer.entrySet()) {
			ArrayList<TradeGood> resource = (ArrayList<TradeGood>)entry.getValue();
			int quantity = resource.size();
			boolean disable = false;
			
			if(resource.size() > 0 && curGalaxy.getTechLevelNum() < resource.get(0).getMTLU())
				disable = true;
			
			if((String)entry.getKey() == "Water") {
				marketView.getLblCwater().setText(""+quantity);
				if(quantity == 0 || disable)
					marketView.getWaterSell().setEnabled(false);
				else {
					marketView.getWaterSell().setEnabled(true);
					marketView.getWaterSell().setText("Sell["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Furs") {
				marketView.getLblCfurs().setText(""+quantity);
				if(quantity == 0 || disable)
					marketView.getFursSell().setEnabled(false);
				else {
					marketView.getFursSell().setEnabled(true);
					marketView.getFursSell().setText("Sell["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Games") {
				marketView.getLblCgames().setText(""+quantity);
				if(quantity == 0 || disable)
					marketView.getGamesSell().setEnabled(false);
				else {
					marketView.getGamesSell().setEnabled(true);
					marketView.getGamesSell().setText("Sell["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Firearms") {
				marketView.getLblCfirearms().setText(""+quantity);
				if(quantity == 0 || disable)
					marketView.getFirearmsSell().setEnabled(false);
				else {
					marketView.getFirearmsSell().setEnabled(true);
					marketView.getFirearmsSell().setText("Sell["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Food") {
				marketView.getLblCfood().setText(""+quantity);
				if(quantity == 0 || disable)
					marketView.getFoodSell().setEnabled(false);
				else {
					marketView.getFoodSell().setEnabled(true);
					marketView.getFoodSell().setText("Sell["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Machines") {
				marketView.getLblCmachines().setText(""+quantity);
				if(quantity == 0 || disable)
					marketView.getMachinesSell().setEnabled(false);
				else {
					marketView.getMachinesSell().setEnabled(true);
					marketView.getMachinesSell().setText("Sell["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Robots") {
				marketView.getLblCrobots().setText(""+quantity);
				if(quantity == 0 || disable)
					marketView.getRobotsSell().setEnabled(false);
				else {
					marketView.getRobotsSell().setEnabled(true);
					marketView.getRobotsSell().setText("Sell["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Medicine") {
				marketView.getLblCmedicine().setText(""+quantity);
				if(quantity == 0 || disable)
					marketView.getMedicineSell().setEnabled(false);
				else {
					marketView.getMedicineSell().setEnabled(true);
					marketView.getMedicineSell().setText("Sell["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Narcotics") {
				marketView.getLblCnarcotics().setText(""+quantity);
				if(quantity == 0 || disable)
					marketView.getNarcoticsSell().setEnabled(false);
				else {
					marketView.getNarcoticsSell().setEnabled(true);
					marketView.getNarcoticsSell().setText("Sell["+resource.get(0).getTotalPrice()+"]");
				}
			}
			else if((String)entry.getKey() == "Ore") {
				marketView.getLblCore().setText(""+quantity);
				if(quantity == 0 || disable)
					marketView.getOreSell().setEnabled(false);
				else {
					marketView.getOreSell().setEnabled(true);
					marketView.getOreSell().setText("Sell["+resource.get(0).getTotalPrice()+"]");
				}
			}
			
			marketView.getLblRemcredits().setText(""+player.getCredits());
		}
		
	}
	
	
	/**
	 * Main method (should move to own driver class)
	 */
	public static void main(String[] args) {
		new MainController();
	}
}

/**
 * To Do
 * =====
 * +Add listeners for all planets (should make own listener class)
 * +Add marketplace interactions and general economy
 * +Fix location generation of planets to disallow overlapping
 */
