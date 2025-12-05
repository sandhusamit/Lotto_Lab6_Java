package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.Hashtable;
import java.util.List;

public class Main extends Application {

    private TextArea outputArea;
    private Hashtable<Integer, List<Integer>> results = new Hashtable<>();

    @Override
    public void start(Stage primaryStage) {

        // ===================== UI SETUP =====================
        outputArea = new TextArea();
        outputArea.setPrefSize(450, 300);
        outputArea.setEditable(false);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.TOP_CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        // Labels and text fields
        Label minNumLbl = new Label("Minimum Number:");
        TextField minNumTextField = new TextField("0");

        Label maxNumLbl = new Label("Maximum Number:");
        TextField maxNumTextField = new TextField("100");

        Label ballsQtyLbl = new Label("Balls Quantity:");
        TextField ballsQtyTextField = new TextField("6");

        grid.add(minNumLbl, 0, 0);
        grid.add(minNumTextField, 1, 0);
        grid.add(maxNumLbl, 0, 1);
        grid.add(maxNumTextField, 1, 1);
        grid.add(ballsQtyLbl, 0, 2);
        grid.add(ballsQtyTextField, 1, 2);

        // Output area spans both columns
        grid.add(outputArea, 0, 3, 2, 1);

        // Generate button
        Button generateLotto = new Button("Generate Lotto");
        grid.add(generateLotto, 0, 4, 2, 1);
        GridPane.setMargin(generateLotto, new Insets(10, 0, 0, 0));
        generateLotto.setMaxWidth(Double.MAX_VALUE);

        // ===================== BUTTON ACTION =====================
        generateLotto.setOnAction(actionEvent -> {
            int min, max, ballsQty;

            // Parse user inputs safely
            try {
                min = Integer.parseInt(minNumTextField.getText());
            } catch (NumberFormatException e) {
                min = 0;
            }

            try {
                max = Integer.parseInt(maxNumTextField.getText());
            } catch (NumberFormatException e) {
                max = 100;
            }

            try {
                ballsQty = Integer.parseInt(ballsQtyTextField.getText());
            } catch (NumberFormatException e) {
                ballsQty = 6;
            }

            // Clear previous results
            outputArea.clear();

            // Start generator thread
            LottoGenerator lottoTask = new LottoGenerator(results, outputArea, min, max, ballsQty);
            Thread lottoThread = new Thread(lottoTask, "Lotto-Thread");
            lottoThread.setDaemon(true);
            lottoThread.start();

            // Start checker thread after generator finishes
            Thread lottoThread2 = new Thread(() -> {
                try {
                    lottoThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                LottoChecker lottoTask2 = new LottoChecker(results, outputArea);
                lottoTask2.run();
            }, "LottoChecker-Thread");

            lottoThread2.setDaemon(true);
            lottoThread2.start();
        });

        // ===================== SCENE SETUP =====================
        Scene scene = new Scene(grid, 500, 500);
        primaryStage.setTitle("Lotto Generator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
