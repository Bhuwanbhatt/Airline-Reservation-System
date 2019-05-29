// CSC 450 Project
// Group 12

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javax.swing.JFrame;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.*;
import java.util.*;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
//combo box

public class Flight_Recorder extends Application {
	
  //create a random flight ID	
  int id = 100 + new Random().nextInt(900);
  String f_id_num = Integer.toString(id);
  String f_id = "fli" + f_id_num;
  
  //create the textfields
  private TextField flight_id = new TextField(f_id);
  private TextField depTime = new TextField();
  private TextField duration = new TextField();
  private TextField aircraft_type = new TextField();
  private TextField econ_price = new TextField();
  private TextField bus_price = new TextField();
  private TextField seats_econ = new TextField();
  private TextField seats_bus = new TextField();
  private TextField distance = new TextField();
  private TextField airline_id = new TextField();
  private TextField schedule_id = new TextField();
  private TextField airport_id_dest = new TextField();
  private TextField airport_id_origin = new TextField();
  private Button insert = new Button("Insert into Database");
  
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    // Create UI
    GridPane gridPane = new GridPane();
    gridPane.setHgap(5);
    gridPane.setVgap(5);
    gridPane.add(new Label("Flight ID (fli###):"), 0, 0 );
    gridPane.add(flight_id, 1, 0);
    gridPane.add(new Label("Enter the departure time ('yyyy/mm/dd:hh:mi:ssam):"), 0, 1);
    gridPane.add(depTime, 1, 1);
    gridPane.add(new Label("Enter the duration (ex: five and a half hours is 5.5, four hours is 4.0):"), 0, 2);
    gridPane.add(duration, 1, 2);
    gridPane.add(new Label("Enter the aircraft type (ex: Boeing, Cessna):"), 0, 3);
    gridPane.add(aircraft_type, 1, 3);
    gridPane.add(new Label("Enter the price of an economy seat (do not include '$' or decimals):"), 0, 4);
    gridPane.add(econ_price, 1, 4);
    gridPane.add(new Label("Enter the price of business seat (do not include '$' or decimals):"), 0, 5);
    gridPane.add(bus_price, 1, 5);
    gridPane.add(new Label("Enter the amount of economy seats:"), 0, 6);
    gridPane.add(seats_econ, 1, 6);
    gridPane.add(new Label("Enter the amount of business seats:"), 0, 7);
    gridPane.add(seats_bus, 1, 7);
    gridPane.add(new Label("Enter the distance traveled (miles):"), 0, 8);
    gridPane.add(distance, 1, 8);
    gridPane.add(new Label("Enter the airline ID:"), 0, 9);
    gridPane.add(airline_id, 1, 9);
    gridPane.add(new Label("Enter the schedule ID:"), 0, 10);
    gridPane.add(schedule_id, 1, 10);
    gridPane.add(new Label("Enter the airport ID of the destination airport:"), 0, 11);
    gridPane.add(airport_id_dest, 1, 11);
    gridPane.add(new Label("Enter the airport ID of the origin airport:"), 0, 12);
    gridPane.add(airport_id_origin, 1, 12);
    gridPane.add(insert, 1, 13);

    // Set properties for UI
    gridPane.setAlignment(Pos.CENTER);
    flight_id.setAlignment(Pos.BOTTOM_RIGHT); //string
    depTime.setAlignment(Pos.BOTTOM_RIGHT); //string
    duration.setAlignment(Pos.BOTTOM_RIGHT); //double
    aircraft_type.setAlignment(Pos.BOTTOM_RIGHT); //string
    econ_price.setAlignment(Pos.BOTTOM_RIGHT); //int
    bus_price.setAlignment(Pos.BOTTOM_RIGHT); //int
    seats_econ.setAlignment(Pos.BOTTOM_RIGHT); //int
    seats_bus.setAlignment(Pos.BOTTOM_RIGHT); //int
    distance.setAlignment(Pos.BOTTOM_RIGHT); //int
    airline_id.setAlignment(Pos.BOTTOM_RIGHT); //string
    schedule_id.setAlignment(Pos.BOTTOM_RIGHT); //string
    airport_id_dest.setAlignment(Pos.BOTTOM_RIGHT); //string
    airport_id_origin.setAlignment(Pos.BOTTOM_RIGHT); //string
    GridPane.setHalignment(insert, HPos.RIGHT);
    
    //create an options menu
    MenuBar menu = new MenuBar();
    Menu menuOpt = new Menu("Options");
    menu.getMenus().addAll(menuOpt);
    VBox vBox = new VBox(10);
    
    MenuItem menuItemClear = new MenuItem("Clear");
    SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
    MenuItem menuItemExit = new MenuItem("Exit");
    menuOpt.getItems().addAll(menuItemClear, separatorMenuItem, menuItemExit);
    
    menuItemClear.setOnAction(e -> {
    	flight_id.clear();
    	depTime.clear();
    	duration.clear();
    	aircraft_type.clear();
    	econ_price.clear();
    	bus_price.clear();
    	seats_econ.clear();
    	seats_bus.clear();
    	distance.clear();
    	airline_id.clear();
      schedule_id.clear();
    	airport_id_dest.clear();
    	airport_id_origin.clear();
    	
    });
    menuItemExit.setOnAction(e -> System.exit(0));
    
    // Process events
    // Create a scene and place it in the stage
    vBox.getChildren().addAll(menu, gridPane);
    Scene scene = new Scene(vBox, 600, 500);
    primaryStage.setTitle("Insert new Flight"); // Set title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
   
    insert.setOnAction(e -> {
    try {
  	    Class.forName("oracle.jdbc.driver.OracleDriver"); 
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@citdb.nku.edu:1521:csc450", "hookc1", "csc763"); 

        Statement stmt = conn.createStatement(); 
        
    try { 
    	
        stmt.executeUpdate("insert into FLIGHT values (" + "'" +
        				   f_id + "'" + "," +
        				   "to_date(" + "'" + depTime.getText() + "'" + ",'yyyy/mm/dd:hh:mi:ssam')," +
        				   Double.parseDouble(duration.getText()) + "," + "'" +
        				   aircraft_type.getText() + "'" + "," + 
        				   Integer.parseInt(econ_price.getText()) + "," +
        				   Integer.parseInt(bus_price.getText()) + "," + 
        				   Integer.parseInt(seats_econ.getText()) + "," + 
        				   Integer.parseInt(seats_bus.getText()) + "," + 
        				   Integer.parseInt(distance.getText()) + "," + 
        				   "'" + airline_id.getText() + "'" + "," + 
        				   "'" + schedule_id.getText() + "'" + "," + 
        				   "'" + airport_id_dest.getText() + "'" + "," + 
        				   "'" + airport_id_origin.getText() + "'" + ")"
        					); 
      SwingUtilities.invokeLater(new Runnable(){
    		 public void run(){
    			 JOptionPane.showMessageDialog(null, 
    					 "Inserted successfully!", 
                        "NOTIFICATION",
                       JOptionPane.PLAIN_MESSAGE
                        );
    		 }
    	 });
        					
    } 
    catch (SQLException sqle) { 
    	SwingUtilities.invokeLater(new Runnable(){
    		public void run(){
    			JOptionPane.showMessageDialog(null, 
    					"Could not insert tuple. " + sqle, 
    					"ERROR", 
    					JOptionPane.ERROR_MESSAGE);
    		}
        });
    }
     
        stmt.close();
        conn.close();
    }
     catch (SQLException sqle) {
    	 SwingUtilities.invokeLater(new Runnable(){
    		 public void run(){
    			 JOptionPane.showMessageDialog(null, 
    					 "SQLException : " + sqle, 
                         "ERROR", 
                         JOptionPane.ERROR_MESSAGE);
    		 }
    	 });
     }
     catch (ClassNotFoundException s) {
    	 SwingUtilities.invokeLater(new Runnable(){
    		 public void run(){
    			 JOptionPane.showMessageDialog(null, 
    					 "ClassNotFoundException : " + s, 
                         "ERROR", 
                         JOptionPane.ERROR_MESSAGE);
    		 }
    	 });
  
     }});
  }

  public static void main(String[] args) {
    launch(args);
  }
}