package view;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

public interface GameWorld {
	
	public static final String RESUME = "Resume";
	public static final String PLAY = "Play";
	public static final String PAUSE = "Pause";
	public static final String RESET = "Reset";
	public static final String STEP = "Step";
	
	public static double SCENE_WIDTH = 700;
	public static double SCENE_HEIGHT = 500;
	
	public static double GRID_WIDTH = 400;
	public static double GRID_HEIGHT = 400;
	
	public static final double GRID_PADDING = SCENE_WIDTH / 20;

	
	/*
	 * Add simulations to this combobox when implemented
	 */
	ObservableList<String> options = FXCollections.observableArrayList(
			"Game of Life",
			"Wa-Tor World",
			"Predator Prey",
			"Segregation Simulator");
	public static final ComboBox<String> SIMULATIONS = new ComboBox<>(options);
	
	// TODO: Jordan padding should really go into MainView, since it'll have to change.
	public static final int PADDING = 10;

}
