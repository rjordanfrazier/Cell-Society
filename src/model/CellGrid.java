package model;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import config.Cells;
import config.Configuration;
//import config.ConfigurationLoader;
import config.XMLParser;
import exceptions.MalformedXMLSourceException;
import exceptions.QueryExpressionException;
import exceptions.UnrecognizedQueryMethodException;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import utils.Utils;

/**
 * author: Austin Gartside, Jordan Frazier and Charles Xu
 */
public abstract class CellGrid extends GridPane {

	protected ResourceBundle myResources;
	public static final String RESRC_PATH = "resources/SimulationResources";	
	
	private static final int[] HEX_ROW_DELTAS = {-1, -1, 0, 1, 1, 0};
	private static final int[] HEX_COL_DELTAS = {0, -1, -1, 0, 1, 1};
	
	private static final int[] TRI_ROW_DELTAS = {-1, -1, 0, 1,  1, 1, 0, -1};
	private static final int[] TRI_COL_DELTAS = {0, -1, -1, -1, 0, 1, 1, 1};
	
	private static final int[] RECT_ROW_DELTAS = {0, 1, 0, -1};
	private static final int[] RECT_COL_DELTAS = {1, 0, -1, 0};
	
	private Cell[][] grid;	
	private String simulationName;
	private Configuration myConfig;
	
	private int[] rowDeltas;
	private int[] colDeltas;
	private String myShape;
	private boolean isToroidal;
	
	public CellGrid(Configuration config) {
		myResources = ResourceBundle.getBundle(RESRC_PATH);
		myConfig = config;
		if (config.getNumRows() <= 0 || config.getNumCols() <= 0) {
			throw new IllegalArgumentException("Cannot have 0 or less rows/cols");
		}
		//gonna have to change this
		myShape = "rectangle";
		isToroidal = true;
		chooseRowDeltas();
		grid = new Cell[config.getNumRows()][config.getNumCols()];
	}

	//we have to change render so that it does not use a render method within the cell class or uses if tree
	public void renderGrid(GridPane cellPane) {
		for(int i = 0; i < getNumRows(); i++) {
			for (int j = 0; j < getNumCols(); j++) {
//				ColumnConstraints colC = new ColumnConstraints();
//				colC.setPercentWidth(100);
//				cellPane.getColumnConstraints().add(colC);
//				RowConstraints rowC = new RowConstraints();
//				rowC.setPercentHeight(100);
//				cellPane.getRowConstraints().add(rowC);
//				
				Cell currentCell = grid[i][j];
				Node updatedCell = currentCell.render();
				cellPane.add(updatedCell, j, i);
			}
		}	
		
		
	}
	
	/**
	 * Returns the neighbors of a shape. May need to change
	 * row/column deltas based on definition of 'neighbor'
	 * (diagonals or not)
	 * Returns the neighbors of a shape. May need to change
	 * row/column deltas based on definition of 'neighbor'
	 * (diagonals or not)
	 * 
	 * @param cell - the shape
	 * @return - ArrayList<Cell> of cell's neighbors
	 */
	
	public ArrayList<Cell> getNeighbors(Cell cell, int vision) {
		// could change implementation based on definition of 'neighbor'
		ArrayList<Cell> neighbors = new ArrayList<>();
		int rowPos = cell.getRowPos();
		int colPos = cell.getColPos();
		for (int i = 0; i < cell.getRowDeltas().length; i++) {
			if(vision>1){
				for(int j = 1; j<=vision; j++){
					int newRowPos = rowPos + getRowDeltas()[i]*j;
					int newColPos = colPos + getColDeltas()[i]*j;
					getValidNeighbor(neighbors, newRowPos, newColPos);
				}
			}
			else{
				int newRowPos = rowPos + cell.getRowDeltas()[i];
				int newColPos = colPos + cell.getColDeltas()[i];
				getValidNeighbor(neighbors, newRowPos, newColPos);
			}
		}
		return neighbors;
	}

	private void getValidNeighbor(List<Cell> neighbors, int newRowPos, int newColPos) {
		if (!rowOutOfBounds(newRowPos) && !colOutOfBounds(newColPos)) {
			neighbors.add(grid[newRowPos][newColPos]);
		}
		else if(isToroidal){
			//System.out.println("out of bounds row pos is: " + newRowPos);
			//System.out.println("out of bounds col pos is: " + newColPos);
			if(rowOutOfBounds(newRowPos)){
				newRowPos = gridRowWrap(newRowPos);
			}
			if(colOutOfBounds(newColPos)){
				newColPos = gridColWrap(newColPos);
			}
			//System.out.println("Row Pos is: " + newRowPos);
			//System.out.println("Col Pos is: " + newColPos);
			neighbors.add(grid[newRowPos][newColPos]);
		}
	}
	
	public int gridRowWrap(int newRowPos){
		int wrapRowPos;
		if(newRowPos<0){
			wrapRowPos = getNumRows()+newRowPos;
		}
		else{
			wrapRowPos = newRowPos-getNumRows();
		}
		return wrapRowPos;
	}
	public int gridColWrap(int newColPos){
		int wrapColPos;
		if(newColPos<0){
			wrapColPos = getNumCols()+newColPos;
		}
		else{
			wrapColPos = newColPos-getNumCols();
		}
		return wrapColPos;
	}
	
	public boolean rowOutOfBounds(int row){
		return row<0 || row>=getNumRows();
	}
	
	public boolean colOutOfBounds(int col){
		return col<0 || col>=getNumCols();
	}
	
	private boolean isToroidal(){
		return isToroidal;
	}

	/* backend does this too
	private void updateCurrentState(Cell cell) {
		cell.setCurrentstate(cell.getFuturestate());
	}
	/* backend does this too
	private void updateCurrentState(Cell cell) {
		cell.setCurrentstate(cell.getFuturestate());
	}

	private void setFutureState(Cell cell, String futurestate) {
		cell.setFuturestate(futurestate);
	}
	*/

//	private void setFutureState(Cell cell, String futurestate) {
//		cell.setFuturestate(futurestate);
//	}
	

	/**
	 * Save each cell to the configuration which then could be serialized 
	 * @return
	 */
	public Configuration save() {
		myConfig.getInitialCells().clear();
		for(int i = 0; i < getNumRows(); i++) {
			for (int j = 0; j < getNumCols(); j++) {
				myConfig.getInitialCells().add(grid[i][j].serialize());
			}
		}
		return myConfig;
	}
	
	private void chooseRowDeltas(){
		if(myShape.equals("hexagon")){
			rowDeltas = HEX_ROW_DELTAS;
			colDeltas = HEX_COL_DELTAS;
		}
		else if(myShape.equals("triangle")){
			rowDeltas = TRI_ROW_DELTAS;
			colDeltas = TRI_COL_DELTAS;
		}
		else{
			rowDeltas = RECT_ROW_DELTAS;
			colDeltas = RECT_COL_DELTAS;
		}
	}
	
	public void setDeltas(int[] newRowDeltas, int[] newColDeltas){
		rowDeltas = newRowDeltas;
		colDeltas = newColDeltas;
	}
	
	private int[] getRowDeltas(){
		return rowDeltas;
	}
	
	private int[] getColDeltas(){
		return colDeltas;
	}
	/**
	 * Load cellgrid from config
	 */
	public void load() {}
	
	// TODO (cx15) deserialize grid. each cell does not need a deserialize

	public int getNumRows() {
		return grid.length;
	}

	public int getNumCols() {
		return grid[0].length;
	}
	
	public void setGridCell(int row, int col, Cell myCell){
		grid[row][col] = myCell;
	}
	
	public Cell getGridCell(int row, int col){
		return grid[row][col];
	}
	
	public Configuration getConfig() {
		return myConfig;
	}

	public abstract void updateGrid();

	public abstract void updateCell(Cell myCell);
	
	public abstract String getSimulationName();

	public abstract void initSimulation();

}
