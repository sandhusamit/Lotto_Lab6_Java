package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class SQLManager {

    private static final String URL  = "jdbc:mysql://localhost:3306/lotto_db";
    private static final String USER = "root";
    private static final String PASS = "Sandhu@1";

    public SQLManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL Driver Loaded.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    // Run to insert the set of numbers thats been generated for user lotto. 

    public void insertRun(int runNumber, String numbers) {
        String query = "INSERT INTO lotto_results (run_number, numbers) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, runNumber);
            stmt.setString(2, numbers);

            stmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Pulls runs from database and returns in hashtable
    public Hashtable<Integer, List<Integer>> viewRuns() {
        Hashtable<Integer, List<Integer>> results = new Hashtable<>();
//Query for mySQL
        String query = "SELECT run_number, numbers FROM lotto_results";

        //Cpnnection and statement prepare 
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

        	//execute statement with return 
            var rs = stmt.executeQuery();

            //loop through return and convert to integer to add into hashtable
            while (rs.next()) {

                int runNumber = rs.getInt("run_number");
                String numbersString = rs.getString("numbers"); // ex: "[12, 44, 5, 99, 7, 13]"

                // Convert string -> List<Integer>
                List<Integer> numbersList = parseNumbers(numbersString);

                // Insert into Hashtable
                results.put(runNumber, numbersList);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }
    //Helper Method - Parser to help handle string conversion 
    private List<Integer> parseNumbers(String nums) {
        List<Integer> list = new ArrayList<>();

        // Remove brackets: "[12, 44, 5]" → "12, 44, 5"
        nums = nums.replace("[", "").replace("]", "");

        // Split by comma
        String[] parts = nums.split(",");

        for (String p : parts) {
            try {
                list.add(Integer.parseInt(p.trim()));
            } catch (Exception ignored) {}
        }

        return list;
    }


}
