package application;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class LottoGenerator implements Runnable {

    private SecureRandom random = new SecureRandom();
    // ===================== DATABASE MANAGER =====================
    SQLManager sqlManager = new SQLManager();
    private Hashtable<Integer, List<Integer>> lottoResults;

    private TextArea outputArea;   // UI TextArea reference

    private int max;   // 0–100 inclusive
    private int ballsQty;
    
    private int min;
    // Constructor
    public LottoGenerator(Hashtable<Integer, List<Integer>> lottoResults, TextArea outputArea, int min, int max, int ballsQty) {
        this.lottoResults = lottoResults;
        this.outputArea = outputArea;
        this.min = min;
        this.max = max;
        this.ballsQty = ballsQty;
    }

    // Generate ONE set of 6 UNIQUE lotto numbers
    public List<Integer> generateLottoNumbers() {
        List<Integer> lottoNumbers = new ArrayList<>();

        while (lottoNumbers.size() < ballsQty) {
            int num = random.nextInt(max);
            if (!lottoNumbers.contains(num)) {
                lottoNumbers.add(num);
            }
        }

        return lottoNumbers;
    }

    // Insert a list into the MySQL table
    private void insertIntoDatabase(int runNumber, List<Integer> nums) {
        String numbersAsString = nums.toString(); // e.g. "[12, 55, 7, 99, 31, 44]"
        sqlManager.insertRun(runNumber, numbersAsString);
    }

    // Append result to JavaFX TextArea
    private void updateUI(int runNumber, List<Integer> nums) {
        Platform.runLater(() -> {
            outputArea.appendText("Run " + runNumber + ": " + nums + "\n");
        });
    }
    

    @Override
    public void run() {

        for (int run = 1; run <= 5; run++) {

            // 1. Generate a unique set
            List<Integer> numbers = generateLottoNumbers();

            // 2. Store in the Hashtable
            lottoResults.put(run, numbers);

            // 3. Insert into the DB
            insertIntoDatabase(run, numbers);

            // 4. Update UI safely
            updateUI(run, numbers);

            try {
                Thread.sleep(500); // just so the output is readable
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
