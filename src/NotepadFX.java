import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotepadFX extends Application {
    private TabPane tabs = new TabPane();
    private Stage stage;
    private double zoom = 1;
    private Label status = new Label("Ln 1, Col 1 | 100%");

    @Override
    public void start(Stage s) {
        stage = s;

        Tab add = new Tab("+");
        add.setClosable(false);
        tabs.getTabs().add(add);
        newTab();

        add.setOnSelectionChanged(e -> { if (add.isSelected()) newTab(); });

        MenuBar bar = new MenuBar(
                new Menu("File",
                        null,
                        item("New Tab", e -> newTab()),
                        item("Open", e -> open()),
                        item("Save", e -> save(false)),
                        item("Save As", e -> save(true)),
                        new SeparatorMenuItem(),
                        item("Close Tab", e -> closeTab()),
                        item("Close Window", e -> stage.close()),
                        new SeparatorMenuItem(),
                        item("Exit", e -> System.exit(0))
                ),
                new Menu("Edit",
                        null,
                        item("Undo", e -> area().undo()),
                        item("Copy", e -> area().copy()),
                        item("Paste", e -> area().paste()),
                        item("Delete", e -> area().replaceSelection("")),
                        item("Select All", e -> area().selectAll()),
                        new SeparatorMenuItem(),
                        item("Time/Date", e -> insertTime())
                ),
                new Menu("View",
                        null,
                        item("Zoom In", e -> zoom(0.1)),
                        item("Zoom Out", e -> zoom(-0.1))
                )
        );

        BorderPane root = new BorderPane(tabs);
        root.setTop(bar);
        root.setBottom(new HBox(status));

        Scene scene = new Scene(root, 900, 600);
        stage.setScene(scene);
        stage.setTitle("Modern Notepad FX");
        stage.show();
    }

    private MenuItem item(String name, javafx.event.EventHandler e) {
        MenuItem m = new MenuItem(name);
        m.setOnAction(e);
        return m;
    }

    private void newTab() {
        TextArea a = new TextArea();
        a.setStyle("-fx-font-size:" + (12 * zoom));
        a.caretPositionProperty().addListener((o, x, y) -> update(a));

        Tab t = new Tab("Untitled", a);
        tabs.getTabs().add(tabs.getTabs().size() - 1, t);
        tabs.getSelectionModel().select(t);
    }

    private TextArea area() { return (TextArea) tabs.getSelectionModel().getSelectedItem().getContent(); }

    private void open() {
        File f = new FileChooser().showOpenDialog(stage);
        if (f != null) try {
            newTab();
            area().setText(Files.readString(f.toPath()));
        } catch (Exception ignored) {}
    }

    private void save(boolean as) {
        File f = as ? new FileChooser().showSaveDialog(stage) : new File("untitled.txt");
        if (f != null) try (FileWriter w = new FileWriter(f)) {
            w.write(area().getText());
        } catch (Exception ignored) {}
    }

    private void closeTab() {
        Tab t = tabs.getSelectionModel().getSelectedItem();
        if (!t.getText().equals("+")) tabs.getTabs().remove(t);
    }

    private void insertTime() {
        TextArea a = area();
        a.insertText(a.getCaretPosition(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    private void zoom(double z) {
        zoom += z;
        area().setStyle("-fx-font-size:" + (12 * zoom));
    }

    private void update(TextArea a) {
        int pos = a.getCaretPosition();
        String txt = a.getText();
        int line = txt.substring(0, Math.min(pos, txt.length())).split("\n", -1).length;
        int col = pos - txt.lastIndexOf("\n", Math.max(0, pos - 1));
        status.setText("Ln " + line + ", Col " + col + " | " + (int)(zoom * 100) + "%");
    }

    public static void main(String[] args) { launch(); }
}
