import config.Configuration;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Cell;
import model.CellGrid;
import model.ConfigurationLoader;
import model.GameOfLifeSimulation;

public class MainView extends Application implements ButtonHandler {

	private Stage stage;
	private Scene scene;
	private BorderPane root;
	private FlowPane cellPane;
	Configuration config;

	private final String RESET = "Reset";
	private final String PLAY = "Play";
	private final String PAUSE = "Pause";
	private final String RESUME = "Resume";
	
	private int buttonCounter;
	private Timeline gameloop;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		// Do configuration loader to get the information for scene / etc.
//		config = ConfigurationLoader.loader().setSource("testxml.xml").load().getConfig();
		root = new BorderPane();
		scene = new Scene(root, 800, 500);
		// scene = new Scene(root, config.getSceneWidth(),
		// config.getSceneHeight());
		stage = primaryStage;
		cellPane = new FlowPane();
		cellPane.setPrefWidth(300);
		cellPane.setPrefHeight(300);
		cellPane.setPrefWrapLength(5);
		// cellPane.setPrefWidth(config.getGridWidth());
		// cellPane.setPrefHeight(config.getGridHeight());

		// Configuration.getConfig();

		
		buttonCounter = 0;
		
		CellGrid a = new GameOfLifeSimulation(5, 5);

		gameloop = new Timeline();
		gameloop.setCycleCount(Timeline.INDEFINITE);
		gameloop.getKeyFrames().add(new KeyFrame(Duration.seconds(2), e -> {
			System.out.println(a.getGrid()[0][0].getCurrentstate());
			cellPane = new FlowPane();
			a.updateGrid();
			a.renderGrid(cellPane);
			root.setCenter(cellPane);
		}));

		// add the buttons
		createAllButtons();//

		stage.setScene(scene);
		stage.show();

	}

	public void createAllButtons() throws Exception {
		VBox buttonContainer = new VBox(PADDING);

		SimulationButton playBtn = new SimulationButton(PLAY);
		setStartEventHandler(playBtn);

		SimulationButton resumeBtn = new SimulationButton(RESUME);
		setStartEventHandler(resumeBtn);

		SimulationButton pauseBtn = new SimulationButton(PAUSE);
		setStopEventHandler(pauseBtn);

		SimulationButton resetBtn = new SimulationButton(RESET);
		setStopEventHandler(resetBtn);

		VBox basicBtnBox = new VBox(PADDING);
		basicBtnBox.getChildren().addAll(playBtn, pauseBtn, resumeBtn, resetBtn);
		VBox additionalSliders = new VBox(PADDING);

		// loop through the rest of the things needed from config.getShit,
		// create necessary sliders
//		for (String str : config.getAllCustomParamNames()) {
//			SimulationSlider slider = new SimulationSlider(str);
//			additionalSliders.getChildren().add(slider);
//		}

		buttonContainer.getChildren().addAll(basicBtnBox, additionalSliders);
		root.setRight(buttonContainer);

	}

	public void setStartEventHandler(SimulationButton btn) {
		btn.setOnAction(e -> {
			gameloop.playFromStart();
//			config.setRunning(true);
		});
	}

	public void setStopEventHandler(SimulationButton btn) {
		btn.setOnAction(e -> {
			// If button is reset, then reset parameters back to what's on XML
			// file
			if (btn.getDisplayName().equals(RESET)) {
				try {
					config = ConfigurationLoader.loader().setSource("testxml.xml").load().getConfig();
				} catch (Exception e1) {
					e1.printStackTrace();
					throw new IllegalArgumentException("Failed loading XML file");
				}
			}
			gameloop.pause();
//			config.setRunning(false);
		});
	}

}
