package chatapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class ChatClient extends Application {

    VBox messages = new VBox(10);
    ScrollPane scroll = new ScrollPane();
    TextField field = new TextField();
    Button send = new Button("Send");
    Button sendImage = new Button("📷");

    PrintWriter out;
    BufferedReader in;

    String clientName;

    @Override
    public void start(Stage stage) throws Exception {

        TextInputDialog dialog = new TextInputDialog("Client1");
        dialog.setHeaderText("Enter your name:");
        clientName = dialog.showAndWait().orElse("Client");

        Socket socket = new Socket("localhost", 1234);

        out = new PrintWriter(socket.getOutputStream(), true);
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        messages.setStyle("-fx-padding: 10;");
        scroll.setContent(messages);
        scroll.setFitToWidth(true);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        field.setPrefHeight(35);
        send.setPrefHeight(35);
        sendImage.setPrefHeight(35);

        HBox bottom = new HBox(10, field, send, sendImage);
        bottom.setStyle("-fx-padding: 10;");

        VBox root = new VBox(scroll, bottom);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Scene scene = new Scene(root, 500, 600);
        stage.setScene(scene);
        stage.setTitle("Chat Client");
        stage.show();

        new Thread(() -> {
            String msg;
            try {
                while ((msg = in.readLine()) != null) {
                    handleIncoming(msg);
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        }).start();

        send.setOnAction(e -> sendMessage());
        field.setOnAction(e -> sendMessage());
        sendImage.setOnAction(e -> sendImage());
    }

    private void handleIncoming(String msg) {

        Platform.runLater(() -> {

            if (msg.startsWith("IMAGE:")) {

                String base64 = msg.substring(6);
                byte[] bytes = Base64.getDecoder().decode(base64);

                Image img = new Image(new ByteArrayInputStream(bytes));
                ImageView view = new ImageView(img);

                view.setFitWidth(200);
                view.setPreserveRatio(true);

                messages.getChildren().add(view);

            } else {
                Label label = new Label(msg);
                label.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5;");
                messages.getChildren().add(label);
            }

            scroll.setVvalue(1.0);
        });
    }

    private void sendMessage() {
        String msg = field.getText();
        if (!msg.isEmpty()) {
            out.println(clientName + ": " + msg);
            field.clear();
        }
    }

    private void sendImage() {

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(null);

        if (file != null) {
            try {
                byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
                String base64 = Base64.getEncoder().encodeToString(bytes);

                // FIX: Do NOT display locally — server will send it back once
                out.println("IMAGE:" + base64);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
