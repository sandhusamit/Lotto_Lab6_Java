package application;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class LottoChecker implements Runnable {

    // DB manager
    SQLManager sqlManager = new SQLManager();

    private Hashtable<Integer, List<Integer>> lottoResults;
    private TextArea outputArea;

    // Constructor
    public LottoChecker(Hashtable<Integer, List<Integer>> lottoResults, TextArea outputArea) {
        this.lottoResults = lottoResults;
        this.outputArea = outputArea;
    }

    // Hardcoded winning numbers
    private static final List<Integer> WINNER = Arrays.asList(5, 12, 23, 34, 45, 56);

    // Compare database results
    private void compareRunsDB() {
        // If you want to use the passed lottoResults instead of querying DB, you can skip this line
        Hashtable<Integer, List<Integer>> lottoResults_DB = lottoResults != null ? lottoResults : sqlManager.viewRuns();

        for (Integer runId : lottoResults_DB.keySet()) {
            List<Integer> lottoNumbers = lottoResults_DB.get(runId);

            boolean isWinner = compareResults(lottoNumbers, WINNER);
            updateUI(runId, lottoNumbers, isWinner);
        }
    }
    
    private void showWinner()
    {
    	Platform.runLater(() ->
    	{
    		outputArea.appendText("Winner: " + WINNER +"\n");
    	});
    }
    

    // Compare two lists ignoring order
    private boolean compareResults(List<Integer> nums1, List<Integer> nums2) {
        return nums1.containsAll(nums2) && nums2.containsAll(nums1);
    }

    // Update JavaFX TextArea safely
    private void updateUI(int runNumber, List<Integer> nums, boolean isWinner) {
        Platform.runLater(() -> {
            if (isWinner) {
                outputArea.appendText("Run " + runNumber + ": " + nums + " - WINNER!\n");
            } else {
                outputArea.appendText("Run " + runNumber + ": " + nums + " - L\n");
            }
        });
    }

    @Override
    public void run() {
        compareRunsDB();
        showWinner();
    }
}
