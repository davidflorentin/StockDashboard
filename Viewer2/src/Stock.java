//C1872734

import java.util.*;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


//Stock2 class based on Week6 lab example of TableView
public class Stock {

    private StringProperty symbol;
    private StringProperty name;
    private String csv;
    private ObservableList<Date> dates = FXCollections.observableArrayList();
    private ObservableList<Double> open = FXCollections.observableArrayList();
    private ObservableList<Double> close = FXCollections.observableArrayList();
    private ObservableList<Double> high = FXCollections.observableArrayList();
    private ObservableList<Double> low = FXCollections.observableArrayList();
    private ObservableList<Double> volume = FXCollections.observableArrayList();
    private ObservableList<Double> adjClose = FXCollections.observableArrayList();
    private DoubleProperty lastClose;
    private DoubleProperty lastAdjClose;
    
    
    //setters and getters for attributes
    public final void setSymbol(String value) {
        symbolProperty().set(value);
    }

    public final String getSymbol() {
        return symbolProperty().get();
    }

    //property field for TableView to populate from
    public StringProperty symbolProperty() {
        if (symbol == null) {
            symbol = new SimpleStringProperty();
        }
        return symbol;
    }
    
    public final void setName(String value) {
        nameProperty().set(value);
    }

    public final String getName() {
        return nameProperty().get();
    }

    public StringProperty nameProperty() {
        if (name == null) {
            name = new SimpleStringProperty();
        }
        return name;
    }
    
    public final void setLastClose(Double value) {
        lastCloseProperty().set(value);
    }
    public final Double getLastClose() {
        return lastCloseProperty().get();
    }

    public DoubleProperty lastCloseProperty() {
        if (lastClose == null) {
            lastClose = new SimpleDoubleProperty();
        }
        return lastClose;
    }
    
    public final void setLastAdjClose(Double value) {
        lastAdjCloseProperty().set(value);
    }
    public final Double getLastAdjClose() {
        return lastAdjCloseProperty().get();
    }

    public DoubleProperty lastAdjCloseProperty() {
        if (lastAdjClose == null) {
            lastAdjClose = new SimpleDoubleProperty();
        }
        return lastAdjClose;
    }
    
    
   	public ObservableList<Date> getDates() {
   		return dates;
   	}
 
   	public ObservableList<Double> getOpen() {
   		return open;
   	}
   	
   	public ObservableList<Double> getClose() {
   		return close;
   	}
   	
   	public ObservableList<Double> getHigh() {
   		return high;
   	}
   	
   	public ObservableList<Double> getLow() {
   		return low;
   	}
   	
   	public ObservableList<Double> getVolume() {
   		return volume;
   	}
   	
   	public ObservableList<Double> getAdjClose() {
   		return adjClose;
   	}
   	
   	//private final SimpleStringProperty symbolTable;
    
   	
   	public Stock( String inSymbol, String inName, String inCsv ) {
   		setSymbol(inSymbol);
   		setName(inName);
   		csv = inCsv;
   		
        try {
        	//locate and read CSV for stock and loop through rows, storing each value in appropriate list
        	String filepath = new File("").getAbsolutePath();
            BufferedReader stockFile = new BufferedReader( new FileReader(filepath + "\\CMT205CWDATA\\" + csv) );
            stockFile.readLine(); //reads first line
            String newLine = null; //set to null so headers are skipped

            while ((newLine = stockFile.readLine()) != null) {
                //System.out.println(newLine);
                String[] InArray = newLine.split(",");
                Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(InArray[0]);
                dates.add(date1);
                double open1 = Double.parseDouble(InArray[1]);
                open.add(open1);
                double high1 = Double.parseDouble(InArray[2]);
                high.add(high1);
                double low1 = Double.parseDouble(InArray[3]);
                low.add(low1);
                double close1 = Double.parseDouble(InArray[4]);
                close.add(close1);
                double volume1 = Double.parseDouble(InArray[5]);
                volume.add(volume1);
                double adjClose1 = Double.parseDouble(InArray[6]);
                adjClose.add(adjClose1);
            }

            stockFile.close();
            
            //dates are in csv in reverse order, so reverse all ArrayLists
            Collections.reverse(dates);
            Collections.reverse(open);
            Collections.reverse(high);
            Collections.reverse(low);
            Collections.reverse(close);
            Collections.reverse(volume);
            Collections.reverse(adjClose);
            
            setLastClose(close.get(close.size()-1));
            setLastAdjClose(adjClose.get(adjClose.size()-1));
        }
        catch ( Exception e ) {
            System.out.println( e );
        }
    }
  	
   	public String toString() {
   		return symbol + "\t" + name + "\t" + close.get(close.size()-1);
   	}
}
