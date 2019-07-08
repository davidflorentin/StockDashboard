//C1872734

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application{
	
	private ObservableList<Stock> stockList = FXCollections.observableArrayList();
	private CategoryAxis xAxisVol = new CategoryAxis();
	private NumberAxis yAxisVol = new NumberAxis();
	private LineChart<String, Number> volumeChart = new LineChart<String,Number>(xAxisVol, yAxisVol);
	private CategoryAxis xAxisHL = new CategoryAxis();
	private NumberAxis yAxisHL = new NumberAxis();
	private LineChart<String, Number> highLowChart = new LineChart<String,Number>(xAxisHL, yAxisHL);
	private CategoryAxis xAxisOC = new CategoryAxis();
	private NumberAxis yAxisOC = new NumberAxis();
	private LineChart<String, Number> openCloseChart = new LineChart<String,Number>(xAxisOC, yAxisOC);
	private Boolean multiStockMode = false; //stores checkbox status
	private Boolean adjCloseSelected = false; //stores checkbox status
	private Stock currentStock; //stores latest stock so this can be entered into graph update functions when checkBox is clicked
	private final TableView<Stock> stockTable = new TableView<>();
	
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override public void start(Stage stage) {
		stage.setTitle("Stock Dashboard");
		
		//retrieve stock data from CSVs and create Stock objects
		addStocks();
		//initialise currentStock
		currentStock = stockList.get(0);
        //create table and link to attributes of Stock objects
		createTable();
 
        //create default graphs of first stock on list
        updateVolumeChart(stockList.get(0));
        updateOpenCloseChart(stockList.get(0));
        updateHighLowChart(stockList.get(0));
       
        
        //Setup stockTable listener for when stock rows are selected
        stockTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
        	if (newValue != null) {
        		currentStock = newValue;
        		System.out.println("newValue: " + newValue);
        		updateVolumeChart(currentStock);
        		updateOpenCloseChart(currentStock);
        		updateHighLowChart(currentStock);
        	}        	
        });
        
        
        //create button for report
        Button reportButton = new Button("Create Report");
        reportButton.setOnAction((ActionEvent event) -> {
        	createReport();
        });
        
        
        //Checkbox for adding multiple stocks to graph (https://docs.oracle.com/javafx/2/ui_controls/checkbox.htm)
        CheckBox multiStockModeSelector = new CheckBox("Check to add multiple stocks to charts");
        multiStockModeSelector.setIndeterminate(false);
        multiStockModeSelector.selectedProperty().addListener(new ChangeListener<Boolean>() {
        	public void changed(ObservableValue<? extends Boolean> ov,
        			Boolean old_val, Boolean new_val) {
        		multiStockMode = new_val;
        		System.out.println(multiStockMode);
        	}
        });
        
        //Checkbox for using adjusted close data in graph
        CheckBox adjCloseSelector = new CheckBox("Use Adjusted Close");
        adjCloseSelector.setIndeterminate(false);
        adjCloseSelector.selectedProperty().addListener(new ChangeListener<Boolean>() {
        	public void changed(ObservableValue<? extends Boolean> ov,
        			Boolean old_val, Boolean new_val) {
        		adjCloseSelected = new_val;
        		System.out.println("currentstock: " + currentStock);
        		updateOpenCloseChart(currentStock);
        		System.out.println("AdjClose");
        	}
        });
        
       
        //place nodes in GUI
        TabPane tabpane = new TabPane();
        Tab tab1 = new Tab();
        tab1.setText("Volume");
        tab1.setContent(volumeChart);
        Tab tab2 = new Tab();
        tab2.setText("Open/Close");
        tab2.setContent(openCloseChart);
        Tab tab3 = new Tab();
        tab3.setText("High/Low");
        tab3.setContent(highLowChart);
        tabpane.getTabs().addAll(tab1, tab2, tab3);
        
        HBox hbox = new HBox(5);
        hbox.setMargin(stockTable,  new Insets(5,5,5,5));
        hbox.getChildren().addAll(stockTable, tabpane);
                        
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMaxWidth(950);
        stage.setMinHeight(600);
        
        HBox buttonBox = new HBox(5);
        buttonBox.setMargin(reportButton, new Insets(5,5,5,5));
        buttonBox.setMargin(multiStockModeSelector, new Insets(9,9,5,5));
        buttonBox.setMargin(adjCloseSelector, new Insets(9,9,5,5));
        
        buttonBox.getChildren().addAll(reportButton, multiStockModeSelector, adjCloseSelector);
        
        root.setCenter(hbox);
        root.setTop(buttonBox);
        stage.show();
    }	
	
	
	public TableView createTable(){
		//Set up table of stocks
		
		stockTable.setItems(stockList);
		
	    stockTable.setPrefWidth(400);
	    stockTable.setEditable(true);

	    TableColumn SymbolCol = new TableColumn("Symbol");
	    SymbolCol.setEditable(true);
	    SymbolCol.setCellValueFactory(new PropertyValueFactory<Stock, String>("Symbol"));
	    SymbolCol.setPrefWidth(50);
	    
	    TableColumn NameCol = new TableColumn("Company Name");
	    NameCol.setEditable(true);
	    NameCol.setCellValueFactory(new PropertyValueFactory<Stock, String>("Name"));
	    NameCol.setPrefWidth(220);

	    TableColumn CloseCol = new TableColumn("Latest\nClose");
	    CloseCol.setEditable(true);
	    CloseCol.setCellValueFactory(new PropertyValueFactory<Stock, Double>("LastClose"));
	    CloseCol.setPrefWidth(60);
	    
	    TableColumn AdjCloseCol = new TableColumn("Latest\nAdj\nClose");
	    AdjCloseCol.setEditable(true);
	    AdjCloseCol.setCellValueFactory(new PropertyValueFactory<Stock, Double>("LastAdjClose"));
	    AdjCloseCol.setPrefWidth(60);
	    stockTable.getColumns().setAll(SymbolCol, NameCol, CloseCol, AdjCloseCol);
	    
	    return stockTable;
	}
	
	
	public LineChart updateVolumeChart(Stock newValue) {
		volumeChart.setTitle("Volume");
		XYChart.Series seriesVol = new XYChart.Series();
		//if multiStockMode checkbox is not selected then remove current data from graph. Otherwise data remains in graph.
		if (multiStockMode == false) {
			while (!volumeChart.getData().isEmpty()) {
				volumeChart.getData().remove(0);
			}
		}
		//Check data series is not already on graph. Return null if it is.
		for (XYChart.Series series : volumeChart.getData()) {
			if (series.getName() == newValue.getName()) {
				System.out.println(series.getName());
				return null;
			}		
		}
		
		seriesVol.setName(newValue.getName());
		//Loop through data and add dates/values to XYChart object
		for (int i = 0; i < newValue.getDates().size(); i++) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String strDateVol = dateFormat.format(newValue.getDates().get(i));
			seriesVol.getData().add(new XYChart.Data(strDateVol, newValue.getVolume().get(i)));
		}
	    
	    volumeChart.setCreateSymbols(false);// Disable data point markers
	    volumeChart.getData().add(seriesVol);
	    return volumeChart;
	}
	
	
	public LineChart updateOpenCloseChart(Stock newValue) {
		XYChart.Series seriesO = new XYChart.Series();
		XYChart.Series seriesC = new XYChart.Series();
		//if multiStockMode checkbox is not selected then remove current data from graph. Otherwise data remains in graph.
		if (multiStockMode == false) {	
			while (!openCloseChart.getData().isEmpty()) {
				openCloseChart.getData().remove(0);
			}
		}
		//Check data series is not already on graph. Return null if it is.
		for (XYChart.Series series : openCloseChart.getData()) {
			if ((series.getName()+"").equals(newValue.getName() + " open") ||
					(series.getName()+"").equals(newValue.getName() + " close") ||
					(series.getName()+"").equals(newValue.getName() + " adj close")) {
				return null;
			}		
		}
		seriesO.setName(newValue.getName() + " open");
		//Set names depending on whether 'adjusted close' is selected
		if (adjCloseSelected == true) {
			openCloseChart.setTitle("Daily Open/Adjusted Close");
			seriesC.setName(newValue.getName() + " adj close");
		}
		else {
			openCloseChart.setTitle("Daily Open/Close");
			seriesC.setName(newValue.getName() + " close");
		}
		//loop through dates and add date/values to XYChart object.
		for (int i = 0; i < newValue.getDates().size(); i++) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String strDateOC = dateFormat.format(newValue.getDates().get(i));
			seriesO.getData().add(new XYChart.Data(strDateOC, newValue.getOpen().get(i)));
			//Select adjClose or close depending on checkbox
			if (adjCloseSelected == true) {
				seriesC.getData().add(new XYChart.Data(strDateOC, newValue.getAdjClose().get(i)));
			}
			else {
				seriesC.getData().add(new XYChart.Data(strDateOC, newValue.getClose().get(i)));
			}	
		}
	    
		//call setAxis method to configure the yAxis with appropriate range for series
		yAxisOC = setAxis(newValue.getOpen(), newValue.getClose(), yAxisOC);
				
	    openCloseChart.setCreateSymbols(false); // Disable point markers
	    openCloseChart.getData().addAll(seriesO, seriesC);
	    return openCloseChart;
	}

	    
	public LineChart updateHighLowChart(Stock newValue) {
		highLowChart.setTitle("Daily high/low");
		XYChart.Series seriesH = new XYChart.Series();
		XYChart.Series seriesL = new XYChart.Series();
		//if multiStockMode checkbox is not selected then remove current data from graph. Otherwise data remains in graph.
		if (multiStockMode == false) {	
			while (!highLowChart.getData().isEmpty()) {
				highLowChart.getData().remove(0);
			}
		}
		//Check data series is not already on graph. Return null if it is.
		for (XYChart.Series series : highLowChart.getData()) {
			if ((series.getName()+"").equals(newValue.getName() + " high") ||
					(series.getName()+"").equals(newValue.getName() + " low")) {
				return null;
			}		
		}
		seriesH.setName(newValue.getName() + " high");
		seriesL.setName(newValue.getName() + " low");
		//loop through dates and add date/value to XYChart object
		for (int i = 0; i < newValue.getDates().size(); i++) {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String strDateHL = dateFormat.format(newValue.getDates().get(i));
				seriesH.getData().add(new XYChart.Data(strDateHL, newValue.getHigh().get(i)));
				seriesL.getData().add(new XYChart.Data(strDateHL, newValue.getLow().get(i)));
		}
		
		//call setAxis method to configure the yAxis with appropriate range for series
		yAxisHL = setAxis(newValue.getHigh(), newValue.getLow(), yAxisHL);
		
	    highLowChart.setCreateSymbols(false);  // Disable point markers
	    highLowChart.getData().addAll(seriesH, seriesL);
	   
	    return highLowChart;
	}



	public NumberAxis setAxis(ObservableList<Double> seriesA, ObservableList<Double> seriesB, NumberAxis yAxis) {
		//Sets Y axis with appropriate limits for the plotted data
		
		//Put all datapoints into one series, sort them and pick the lowest and highest values.
	    for (int i = 0; i < seriesB.size(); i++) {
	    	seriesA.add(seriesB.get(i));
	    }
	    Double lowestValue = seriesA.sorted().get(0);
	    Double highestValue = seriesA.sorted().get(seriesA.size()-1);
	    yAxis.setAutoRanging(false); //turn off NumberAxis autorange property
	    //if only one stock to be shown then set axis to cover the new data's range +/- 10%
	    if (multiStockMode == false) {
			yAxis.setLowerBound(lowestValue - ((highestValue - lowestValue)/100*10));
			yAxis.setUpperBound(highestValue + ((highestValue - lowestValue)/100*10));
	    }
	    //if multiple stock to be viewed together, compare new data range with current yAxis limits and resize if necessary
	    else if (multiStockMode == true){
	    	if (yAxis.getUpperBound() < highestValue) {
	    		yAxis.setUpperBound(highestValue);
	    	}
	    	if (yAxis.getLowerBound() > lowestValue) {
	    		yAxis.setLowerBound(lowestValue);
	    	}  
	    }
	    return yAxis;
	}
	
	
	public void addStocks() {	
	    try {
	    	//read in stock_list.csv and use name, symbol and csv for each stock to create a new Stock object
	    	String stockListLocation = (new File("").getAbsolutePath()) + "\\CMT205CWDATA\\stock_list.csv";
	        BufferedReader stockFile = new BufferedReader( new FileReader(stockListLocation) );
	        stockFile.readLine(); //reads first line
	        String newLine = null; //set to null so headers are skipped
	
	        while ((newLine = stockFile.readLine()) != null) {
	            String[] InArray = newLine.split(",");
	            String symbol = InArray[0];
	            String name = InArray[1];
	            String csv = InArray[2];
	            stockList.add(new Stock(symbol, name, csv));
	        }
	        stockFile.close();
	        System.out.println(stockList);
	    }
	    catch ( Exception e ) {
	        System.out.println( e );
	    }
	}
	
	
	public void createReport() {
		//creates text file report
		String newline = System.getProperty("line.separator");
		StringBuilder reportString = new StringBuilder();
		//For each Stock in stockList, call Stock2 method to obtain necessary details for report and append to string. 
		Integer n = 1;
		for (int i = 0; i < stockList.size(); i++) {
			
			String Number = "Number: " + n;
			String symbol = "Stock Symbol: " + stockList.get(i).getSymbol();
			String name = "Company Name: " + stockList.get(i).getName();

			Double high = Collections.max(stockList.get(i).getHigh());
			Integer indexHigh = stockList.get(i).getHigh().indexOf(high);
			Date highDate = stockList.get(i).getDates().get(indexHigh);
			String highest = "Highest: " + high + " on " + highDate;
			
			Double low = Collections.max(stockList.get(i).getLow());
			Integer indexLow = stockList.get(i).getLow().indexOf(low);
			Date lowDate = stockList.get(i).getDates().get(indexLow);
			String lowest = "Lowest: " + low + " on " + lowDate;
			
			Double total = 0.0;
			for (int j = 0; j < stockList.get(i).getClose().size(); j++) {
				total = total + stockList.get(i).getClose().get(j);
			}
			String average = "Average Close: " + total/stockList.get(i).getClose().size();
			
			String close = "Close: " + stockList.get(i).getClose().get(stockList.get(i).getClose().size()-1);
			
			StringBuilder reportEntry = new StringBuilder();
			reportEntry.append(Number).append(newline).append(symbol).append(newline).append(name)
				.append(newline).append(highest).append(newline).append(lowest).append(newline).append(average)
				.append(newline).append(close).append(newline).append(newline);
			
			reportString.append(reportEntry);
			
			n++;
		}
		
		System.out.println(reportString);
		
		try {
			//Write final reportString to textfile.
			PrintWriter writer = new PrintWriter("StockReport.txt", "UTF-8");
			writer.println(reportString);
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}
}
