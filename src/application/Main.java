package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
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
        outputArea.setPrefSize(450, 350);
        outputArea.setEditable(false);

        FlowPane root = new FlowPane();
        root.setPadding(new Insets(20));
        root.getChildren().add(outputArea);

        Scene scene = new Scene(root, 500, 400);

        primaryStage.setTitle("Lotto Generator");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Button to start thread

        Button generateLotto = new Button("Generate Lotto");
        generateLotto.setOnAction(actionEvent -> {
            // Create generator task
            LottoGenerator lottoTask = new LottoGenerator(results, outputArea);
            Thread lottoThread = new Thread(lottoTask, "Lotto-Thread");
            lottoThread.setDaemon(true);
            lottoThread.start();

            // Checker thread waits for generator to finish
            Thread lottoThread2 = new Thread(() -> {
                try {
                    lottoThread.join(); // Wait for generator to complete
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // Now check results
                LottoChecker lottoTask2 = new LottoChecker(results, outputArea);
                lottoTask2.run(); // Directly run since we're already in a thread
            }, "LottoChecker-Thread");

            lottoThread2.setDaemon(true);
            lottoThread2.start();
        });


        
        root.getChildren().add(generateLotto);

    }

    public static void main(String[] args) {
        launch(args);
    }
}
