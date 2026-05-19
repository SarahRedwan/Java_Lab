package chatapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ChatClient extends Application {

    TextArea area = new TextArea();
    TextField field = new TextField();
    Button send = new Button("Send");

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
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // GUI setup
        area.setEditable(false);

        HBox bottom = new HBox(10, field, send);
        VBox root = new VBox(10, area, bottom);

        Scene scene = new Scene(root, 400, 400);
        stage.setScene(scene);
        stage.setTitle("Chat Client");
        stage.show();

        // Thread for receiving messages
        new Thread(() -> {
            String msg;

            try {
                while ((msg = in.readLine()) != null) {

                    String finalMsg = msg;

                    Platform.runLater(() ->
                            area.appendText(finalMsg + "\n"));
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        }).start();

        // Send button action
        send.setOnAction(e -> sendMessage());

        // Enter key support
        field.setOnAction(e -> sendMessage());
    }

    private void sendMessage() {
        String msg = field.getText();

        if (!msg.isEmpty()) {
            out.println(clientName + ": " + msg);
            field.clear();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
