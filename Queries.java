// CSC 450 Project
// Group 12

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
import java.util.Date;

public class Queries extends Application {
	
	
  //initialize
	/////////
  ComboBox queryChoice = new ComboBox();
  
  //initial text
  Text result = new Text("Select query and run for output!");
  
  
  private Label label1 = new Label("Select Query:");
  private Button run = new Button("Run");
  private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd:hh:mm:ssa");
  
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
	  LocalDateTime now = LocalDateTime.now();
      String date_today = dtf.format(now);
	  label1.setFont(Font.font(null, FontWeight.BOLD, 20));
      
	  queryChoice.getItems().addAll("Show sales trends for various airline companies, over the past 3 years, by year, and flight class", 
    							  "Find the top airline company / companies by dollar-amount sold in the past year", 
    							  "Find the top airline company / companies by percentage of occupied aircraft seats in the past year",
    							  "Find the most frequent flyer(s) by number of miles travelled in the past year",
    							  "In what month(s) do Platinum customers fly most frequently (measured by number of flights they bought)?",
    							  "Find those customers who made most reservations they did not buy");
    
	  // make arrays that will be fetched
    
	  //qb
	  List<String> tickets_sold = new ArrayList<>();
	  List<String> seat_type = new ArrayList<>();
	  List<String> year = new ArrayList<>();
	  List<String> airline_name = new ArrayList<>();
	
	  //qc
	  List<String> airline = new ArrayList<>();
	  List<String> dollar_sold = new ArrayList<>();
	
	  //qd
	  List<String> airline_q3 = new ArrayList<>();
	  List<String> percent_filled = new ArrayList<>();
	
	  //qe
	  List<String> user_email = new ArrayList<>();
	  List<String> total_miles = new ArrayList<>();
	
	  //qf
	  List<String> month = new ArrayList<>();
	  List<String> month_count = new ArrayList<>();
	
	  //qg
	  List<String> user_email_qG = new ArrayList<>();
	  List<String> numResNoSale = new ArrayList<>();
	  
	  
	  run.setOnAction(e -> {
		  try {
		  	    Class.forName("oracle.jdbc.driver.OracleDriver"); 
		        Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@citdb.nku.edu:1521:csc450", "hookc1", "csc763"); 

		        Statement stmt = conn.createStatement(); 
		        
		        if (queryChoice.getValue() == "Find those customers who made most reservations they did not buy") {
		        	ResultSet rset = stmt.executeQuery("select distinct(user_email), sum(distinct(reservation.quant_tickets)) as num_reservations_no_sale "
			        		+ "from reservation, sale "
			        		+ "where reservation.res_id NOT IN (SELECT res_id from sale) "
			        		+ "group by (user_email)");
		        	
		        	ResultSetMetaData md = rset.getMetaData();
		            int colCount = md.getColumnCount();
		            String output = "";
		            
		            List<String> outputs = new ArrayList<>();
			        while (rset.next()) {
			        	for (int i = 1; i <= colCount; i++) {
			                if (i > 1) {
			                	output = output + "  ";
			                }
			                String columnValue = rset.getString(i);
			                output = output + md.getColumnName(i) + ": " + columnValue;
			            }
			        	outputs.add("\n" + output );

			        }
				        String temp = outputs.toString();
				        temp = temp.replace("[", "");
				        temp = temp.replace("]", "");
				        result.setText(temp.toString());
				stmt.close();
		        conn.close();
		     }
		        
		        else if (queryChoice.getValue() == "Show sales trends for various airline companies, over the past 3 years, by year, and flight class") {
		        	ResultSet rset = stmt.executeQuery("select sum(quant_tickets) as tickets_sold, reservation.seat_type, extract(year from (to_date(flight.departure_time))) as year, airline.name "
		        			+ "from reservation, flight, airline "
		        			+ "where (reservation.flight_id = flight.flight_id) AND (flight.airline_id = airline.airline_id) AND (extract(year from (to_date(flight.departure_time))) >= 2016) "
		        			+ "group by (reservation.seat_type, extract(year from (to_date(flight.departure_time))), airline.name)");
		        	
		        	ResultSetMetaData md = rset.getMetaData();
		            int colCount = md.getColumnCount();
		          
		            List<String> outputs = new ArrayList<>();
			        while (rset.next()) {
			        	String output = "";
			        	for (int i = 1; i <= colCount; i++) {
			                if (i > 1) {
			                	output = output + "  ";
			                }
			                String columnValue = rset.getString(i);
			                output = output + md.getColumnName(i) + ": " + columnValue;
			            }
			        	
			        	outputs.add("\n" + output );

		        }
			        String temp = outputs.toString();
			        temp = temp.replace("[", "");
			        temp = temp.replace("]", "");
			        result.setText(temp.toString());
		     
		        stmt.close();
		        conn.close();
		     }
		        
		        else if (queryChoice.getValue() == "Find the top airline company / companies by dollar-amount sold in the past year") {
		        	ResultSet rset = stmt.executeQuery("select airline_id, dollar_sold from (select airline_id, sum(total_value) "
		        			+ "as dollar_sold from (select airline_id, econ_price * quant_tickets as total_value from flight "
		        			+ "join reservation on reservation.flight_id = flight.flight_id where (to_date(departure_time) >= to_date('2018/04/08:10:00:00','YYYY/MM/DD:hh:mi:ssam')) "
		        			+ "union select airline_id, bus_price * quant_tickets as total_value from flight  "
		        			+ "join reservation on reservation.flight_id = flight.flight_id where (to_date(departure_time) >= to_date('2018/04/08:10:00:00','YYYY/MM/DD:hh:mi:ssam'))) "
		        			+ "group by airline_id) where dollar_sold = (select max(dollar_sold) as dollar_sold from (select airline_id, sum(total_value) as dollar_sold "
		        			+ "from ( select airline_id, econ_price * quant_tickets as total_value  from flight join reservation on reservation.flight_id = flight.flight_id union "
		        			+ "select airline_id, bus_price * quant_tickets as total_value from flight  join reservation on reservation.flight_id = flight.flight_id) group by airline_id))");
		        	
		        	ResultSetMetaData md = rset.getMetaData();
		            int colCount = md.getColumnCount();
		          
		            List<String> outputs = new ArrayList<>();
			        while (rset.next()) {
			        	String output = "";
			        	for (int i = 1; i <= colCount; i++) {
			                if (i > 1) {
			                	output = output + "  ";
			                }
			                String columnValue = rset.getString(i);
			                output = output + md.getColumnName(i) + ": " + columnValue;
			            }
			        	
			        	outputs.add("\n" + output );

		        }
			        String temp = outputs.toString();
			        temp = temp.replace("[", "");
			        temp = temp.replace("]", "");
			        result.setText(temp.toString());
		     
		        stmt.close();
		        conn.close();
		     }
		        
		        else if (queryChoice.getValue() == "Find the top airline company / companies by percentage of occupied aircraft seats in the past year") {
		        	ResultSet rset = stmt.executeQuery("select airline_id, percent_filled from (select airline_id, sum(numTickets)/sum(seats)*100 as percent_filled "
		        			+ "from (select airline.airline_id as airline_id, flight.flight_id, flight.seats_econ + seats_bus as seats, reservation.quant_tickets as numTickets "
		        			+ "from flight join reservation on flight.flight_id = reservation.flight_id join airline on flight.airline_id = airline.airline_id) group by airline_id) "
		        			+ "where percent_filled = (select max(percent_filled)  from (select (total_tickets/total_seats)*100 as percent_filled "
		        			+ "from (select airline_id, sum(seats) as total_seats, sum(numTickets) as total_tickets "
		        			+ "from (select airline.airline_id as airline_id, flight.flight_id, flight.seats_econ + seats_bus as seats, reservation.quant_tickets as numTickets "
		        			+ "from flight join reservation on flight.flight_id = reservation.flight_id join airline on flight.airline_id = airline.airline_id) group by airline_id)))");
		        	
		        	ResultSetMetaData md = rset.getMetaData();
		            int colCount = md.getColumnCount();
		          
		            List<String> outputs = new ArrayList<>();
			        while (rset.next()) {
			        	String output = "";
			        	for (int i = 1; i <= colCount; i++) {
			                if (i > 1) {
			                	output = output + "  ";
			                }
			                String columnValue = rset.getString(i);
			                output = output + md.getColumnName(i) + ": " + columnValue;
			            }
			        	
			        	outputs.add("\n" + output );

		        }
			        String temp = outputs.toString();
			        temp = temp.replace("[", "");
			        temp = temp.replace("]", "");
			        result.setText(temp.toString());
		     
		        stmt.close();
		        conn.close();
		     }
		        
		        else if (queryChoice.getValue() == "Find the most frequent flyer(s) by number of miles travelled in the past year") {
		        	ResultSet rset = stmt.executeQuery("select user_email, total_miles from (select user_info.user_email, sum(flight.distance) as total_miles "
		        			+ "from reservation join user_info on reservation.user_email = user_info.user_email join flight on flight.flight_id = reservation.flight_id "
		        			+ "where (to_date(flight.departure_time) >= to_date('2018/04/08:10:00:00','YYYY/MM/DD:hh:mi:ssam')) group by user_info.user_email) "
		        			+ "where total_miles = (select max(total_miles) as total_miles from (select sum(flight.distance) as total_miles "
		        			+ "from reservation join user_info on reservation.user_email = user_info.user_email join flight on flight.flight_id = reservation.flight_id where (to_date(flight.departure_time) >= to_date('2018/04/08:10:00:00','YYYY/MM/DD:hh:mi:ssam')) "
		        			+ "group by user_info.user_email))");
		        	
		        	ResultSetMetaData md = rset.getMetaData();
		            int colCount = md.getColumnCount();
		          
		            List<String> outputs = new ArrayList<>();
			        while (rset.next()) {
			        	String output = "";
			        	for (int i = 1; i <= colCount; i++) {
			                if (i > 1) {
			                	output = output + "  ";
			                }
			                String columnValue = rset.getString(i);
			                output = output + md.getColumnName(i) + ": " + columnValue;
			            }
			        	
			        	outputs.add("\n" + output );

		        }
			        String temp = outputs.toString();
			        temp = temp.replace("[", "");
			        temp = temp.replace("]", "");
			        result.setText(temp.toString());
		     
		        stmt.close();
		        conn.close();
		     }
		        
		        else if (queryChoice.getValue() == "In what month(s) do Platinum customers fly most frequently (measured by number of flights they bought)?") {
		        	ResultSet rset = stmt.executeQuery("select month, monthCount from (select month, count(month) as monthCount from (select extract(month from res_date) as month from reservation join user_info on reservation.user_email = user_info.user_email where club_status = 'Platinum') group by month) where monthCount = (select max(monthCount) as targetMonth from (select count(month) as monthCount from (select extract(month from res_date) as month from reservation join user_info on reservation.user_email = user_info.user_email where club_status = 'Platinum') group by month))");
		        	
		        	ResultSetMetaData md = rset.getMetaData();
		            int colCount = md.getColumnCount();
		          
		            List<String> outputs = new ArrayList<>();
			        while (rset.next()) {
			        	String output = "";
			        	for (int i = 1; i <= colCount; i++) {
			                if (i > 1) {
			                	output = output + "  ";
			                }
			                String columnValue = rset.getString(i);
			                output = output + md.getColumnName(i) + ": " + columnValue;
			            }
			        	
			        	outputs.add("\n" + output );

		        }
			        String temp = outputs.toString();
			        temp = temp.replace("[", "");
			        temp = temp.replace("]", "");
			        result.setText(temp.toString());
		     
		        stmt.close();
		        conn.close();
		     }
		  }
		     catch (SQLException sqle) {
		        System.out.println("SQLException : " + sqle);
		     }
		     catch (ClassNotFoundException s) {
		        System.out.println("ClassNotFoundException : " + s);
		     }
	  });
	  
	  ////////// UI
	  /////////////
	  final HBox label = new HBox(label1);
	  label.setAlignment(Pos.TOP_CENTER);
    
	  final HBox hbox = new HBox(queryChoice, run);
	  hbox.setAlignment(Pos.TOP_CENTER);
	  
	  final HBox output = new HBox(result);
	  output.setAlignment(Pos.TOP_CENTER);

	  //create an options menu
	  MenuBar menu = new MenuBar();
	  Menu menuOpt = new Menu("Options");
	  menu.getMenus().addAll(menuOpt);
	  VBox vBox = new VBox(10);
	  MenuItem menuItemExit = new MenuItem("Exit");
	  menuOpt.getItems().addAll(menuItemExit);
	  menuItemExit.setOnAction(e -> System.exit(0));
    
	  // Create a scene and place it in the stage
	  vBox.getChildren().add(menu);
	  vBox.getChildren().addAll(label, hbox, output);
	  Scene scene = new Scene(vBox, 800, 450);
	  primaryStage.setTitle("Run Queries"); // Set title
	  primaryStage.setScene(scene); // Place the scene in the stage
	  primaryStage.show(); // Display the stage
    
  }
    
  public static void main(String[] args) {
	  
    launch(args);
  }
}