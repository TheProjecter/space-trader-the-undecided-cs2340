package systems;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

import models.*;
import views.*;

public class MainController extends JFrame {
	private JFrame frame;
	private Dimension dim;
	private JPanel cards;
	private final String INTRO = "Intro Screen";
	private final String START = "Start Screen";
	private final String UNIVERSE = "Universe Screen";
	
	private Universe universe;
	private SolarSystem[] galaxies;
	
	public MainController() {
		dim = new Dimension(639, 473);
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
		StartView startView = new StartView();
		JPanel startCard = startView.getPanel();
		startView.getBtnDone().addActionListener(new PlayerDoneListener(startView));
		
		//Generate galaxy map
		universe = new Universe();
		generateGalaxies();
		//FOR DEBUGGIN ONLY
		for(int i = 0; i < 10; i++) {
			System.out.println(galaxies[i].toString());
		}
		///////////////////
		UniverseView universeView = new UniverseView();
		universeView.drawGalaxies(galaxies);
		JPanel universeCard = universeView.getPanel();
		
		//Add cards to card layout
		cards = new JPanel(new CardLayout());
		cards.add(introCard, INTRO);
		cards.add(startCard, START);
		cards.add(universeCard, UNIVERSE);

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
		StartView startView;
		Player player;
		
		public PlayerDoneListener(StartView startView) {
			this.startView = startView;
		}
		
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
	
	public SolarSystem[] generateGalaxies() {
		String[] names = universe.getNames();
		galaxies = new SolarSystem[names.length];
		String name = "";
		int x;
		int y;
		int tech;
		Random rand = new Random();
		
		for(int i = 0; i < names.length; i++) {
			x = rand.nextInt(630) + 20;
			y = rand.nextInt(430) + 20;
			tech = rand.nextInt(8);
			name = names[i];
			SolarSystem galaxy = new SolarSystem(name, x, y);
			galaxy.setTech(tech);
			galaxies[i] = galaxy;
		}
		
		return galaxies;
	}
	
	
	public static void main(String[] args) {
		MainController main = new MainController();
	}
}


/**
 * TO DO:
 * SEND INFO TO PLAYER MODEL!
 */