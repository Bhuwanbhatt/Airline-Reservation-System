// CSC 450 Project
// Group 12

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javax.swing.JFrame;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.*;
import java.util.*;
import javafx.collections.*; 
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ComboBox;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class Book_Buy_Flights extends Application {
	
	
  //initialize
	
  int id = 100 + new Random().nextInt(900);
  String r_id_num = Integer.toString(id);
  String r_id = "res" + r_id_num;  
  
  int sid = 100 + new Random().nextInt(900);
  String s_id_num = Integer.toString(sid);
  String sale_id = "sal" + r_id_num;  
  
  private TextField res_id2 = new TextField(r_id);
  private TextField bill_address = new TextField();
  private TextField user_email = new TextField();
  private TextField dep_airport = new TextField();
  private TextField dest_airport = new TextField();
  private TextField date_start = new TextField();
  private TextField date_end = new TextField();
  private TextField res_id = new TextField(r_id);
  private Button findFlights = new Button("Search for Flights");
  private Button getInfo = new Button("Get Flight Info");
  private Button bookPay = new Button("Book Flight");
  private Button pay = new Button("Complete Sale");
  ComboBox<String> flightOptions = new ComboBox<String>();
  ComboBox seatChoice = new ComboBox();
  ComboBox seatAmount = new ComboBox();
  String billAddress = "";
  Text t = new Text(" Search and select a flight # to get info! (Every field MUST be filled in)");
  int busPrice = 0;
  int econPrice = 0;
  int price = 0;
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd:hh:mm:ssa");

 
  
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
	  LocalDateTime now = LocalDateTime.now();
      String date_today = dtf.format(now);
      
    // Create UI
    GridPane gridPane = new GridPane();
    gridPane.setHgap(10);
    gridPane.setVgap(10);
    gridPane.add(new Label("Departure City:"), 0, 0 );
    gridPane.add(dep_airport, 1, 0);
    gridPane.add(new Label("Destination City:"), 0, 1);
    gridPane.add(dest_airport, 1, 1);
    gridPane.add(new Label("Enter start of desired date range ('yyyy/mm/dd:hh:mi:ssam'):"), 0, 2);
    gridPane.add(date_start, 1, 2);
    gridPane.add(new Label("Enter end of desired date range ('yyyy/mm/dd:hh:mi:ssam'):"), 0, 3);
    gridPane.add(date_end, 1, 3);
    gridPane.add(new Label("Enter user e-mail:"), 0, 4);
    gridPane.add(user_email, 1, 4);
    gridPane.add(new Label("Reservation ID (remember this if paying later):"), 0, 5);
    gridPane.add(res_id, 1, 5);
    res_id.setEditable(false);
    
    // add to comboboxes
    seatChoice.getItems().addAll("Business", "Economy");
    seatAmount.getItems().addAll(1,2,3,4,5,6,7,8,9,10, 11, 12, 13, 14, 15);
    
    
    // Set properties for UI (first set)
    gridPane.setAlignment(Pos.CENTER);
    dep_airport.setAlignment(Pos.BOTTOM_RIGHT); //string
    dest_airport.setAlignment(Pos.BOTTOM_RIGHT); //string
    date_start.setAlignment(Pos.BOTTOM_RIGHT); //string
    date_end.setAlignment(Pos.BOTTOM_RIGHT); //string
    
   // make arrays that will be fetched
    List<String> flightList = new ArrayList<>();
	List<String> econList = new ArrayList<>();
	List<String> busList = new ArrayList<>();
	List<String> timeList = new ArrayList<>();
	
	
	// finding the flights
    findFlights.setOnAction(e -> {
    try {
  	    Class.forName("oracle.jdbc.driver.OracleDriver"); 
        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@citdb.nku.edu:1521:csc450", "hookc1", "csc763"); 

        Statement stmt = conn.createStatement(); 
        
    try { 
    	ResultSet rset = stmt.executeQuery("select distinct flight.flight_id, econ_price, bus_price, departure_time "
        		+ "from flight, "
        		+ "reservation where (flight.flight_id = reservation.flight_id) AND  "
        		+ "'" + dep_airport.getText() + "'" + "IN (select city from flight, airport where (flight.airport_id_dest = airport_id)) AND"
        		+ "'" + dest_airport.getText() + "'" + "IN (select city from flight, airport where (flight.airport_id_origin = airport_id)) AND "
        		+ "flight.departure_time >= " + "to_date(" + "'" + date_start.getText() + "'" + ",'yyyy/mm/dd:hh:mi:ssam') AND "
        		+ "flight.departure_time <= " + "to_date(" + "'" + date_end.getText() + "'" + ",'yyyy/mm/dd:hh:mi:ssam')");
        
    	while (rset.next()) {

        	flightList.add(rset.getString("flight_id"));
        	econList.add(rset.getString("econ_price"));
        	busList.add(rset.getString("bus_price"));
        	timeList.add(rset.getString("departure_time"));
        } 
     
        stmt.close();
        conn.close();
        
        flightOptions.getItems().addAll(flightList);    
        
      SwingUtilities.invokeLater(new Runnable(){
    		 public void run(){
    			 JOptionPane.showMessageDialog(null, 
    					 "Check the drop down box for flights that meet your criteria!", 
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

    getInfo.setOnAction(e -> {
    for (int i = 0; i < flightList.size(); i++) {
    	if (flightList.get(i) == flightOptions.getValue()) {
    		t.setText(" Flight ID: " + flightList.get(i) + 
    				"\n Price of Economy: $" + econList.get(i) +
    				"\n Price of Business: $" + busList.get(i) +
    				"\n Date/Time of Departure: " + timeList.get(i));
    		busPrice = Integer.parseInt(busList.get(i));
        	econPrice = Integer.parseInt(econList.get(i));
    	}
    	
    }});
    
    bookPay.setOnAction(e -> {
    	if (seatChoice.getValue() == "Business") {
    		int seats = (Integer) seatAmount.getValue();
    		price = busPrice * seats;
    	}
    	else {
    		int seats = (Integer) seatAmount.getValue();
    		price = econPrice * seats;
    	}
    	
        try {
      	    Class.forName("oracle.jdbc.driver.OracleDriver"); 
            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@citdb.nku.edu:1521:csc450", "hookc1", "csc763"); 

            Statement stmt = conn.createStatement(); 
            
        try { 
        	
            stmt.executeUpdate("insert into RESERVATION values(" + "'" +
            				   r_id + "'" + "," +
            				   (Integer) seatAmount.getValue() + "," +
            				   "'" + (String) seatChoice.getValue() + "'" + "," + "'" +
            				   (String) flightOptions.getValue() + "'" + "," + "'" +
            				   user_email.getText() + "'" + "," + 
            				   "to_date(" + "'" + date_today + "'" + ",'yyyy/mm/dd:hh:mi:ssam')" + ")"
            					); 
          SwingUtilities.invokeLater(new Runnable(){
        		 public void run(){
        			 JOptionPane.showMessageDialog(null, 
        					 "Booked!", 
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
        					"Could not book. " + sqle, 
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
         }
    });
    
    pay.setOnAction(e -> {
        try {
      	    Class.forName("oracle.jdbc.driver.OracleDriver"); 
            Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@citdb.nku.edu:1521:csc450", "hookc1", "csc763"); 

            Statement stmt = conn.createStatement(); 
            
        try { 
        	
            stmt.executeUpdate("insert into SALE values(" + "'" +
            				   sale_id + "'" + "," + "'" +
            				   bill_address.getText() + "'" + "," +
            				   "'" + res_id2.getText() + "'" + ")"
            					); 
          SwingUtilities.invokeLater(new Runnable(){
        		 public void run(){
        			 JOptionPane.showMessageDialog(null, 
        					 "Sale completed!", 
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
        					"Could not book. " + sqle, 
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
         }
    });
    
    
    final HBox hbox = new HBox(findFlights, flightOptions, getInfo, t);
    gridPane.add(hbox, 1, 7);
    hbox.setAlignment(Pos.CENTER);
    
    final HBox hbox2 = new HBox(new Label("Select quantity of tickets: "), seatAmount, new Label(" Select seat type: "), seatChoice, bookPay);
    gridPane.add(hbox2, 1, 10);
    hbox2.setAlignment(Pos.CENTER);
    
    final HBox hbox3 = new HBox(new Label("Enter the Reservation ID (enter pre-existing ID if paying for a previous booking: "), res_id2, new Label(" Enter the billing address: "), bill_address, pay);
    gridPane.add(hbox3, 1, 12);
    hbox3.setAlignment(Pos.CENTER);
    
    
    //create an options menu
    MenuBar menu = new MenuBar();
    Menu menuOpt = new Menu("Options");
    menu.getMenus().addAll(menuOpt);
    VBox vBox = new VBox(10);
    MenuItem menuItemExit = new MenuItem("Exit");
    menuOpt.getItems().addAll(menuItemExit);
    menuItemExit.setOnAction(e -> System.exit(0));
    
    // Process events
    // Create a scene and place it in the stage
    vBox.getChildren().addAll(menu, gridPane, hbox, hbox2, hbox3);
    Scene scene = new Scene(vBox, 1200, 450);
    primaryStage.setTitle("Book a Flight"); // Set title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
  }
    
  public static void main(String[] args) {
	  
    launch(args);
  }
}