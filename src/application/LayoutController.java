package application;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import tesler.will.torrent.Lan;

public class LayoutController implements Initializable {
    @FXML
    private TextField tf_loadtorrent;
    @FXML
    private TextField tf_outdir;
    @FXML
    private GridPane grid;
    @FXML
    private ImageView iv_qr;
    @FXML
    private Label l_instructions;
    @FXML
    private Button bt_skip;
    @FXML
    private ImageView iv_exit;

    private boolean torrentChosen = false, outputChosen = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        RotateTransition rt = new RotateTransition(Duration.millis(10000), iv_qr);
        rt.setByAngle(360);
        rt.setCycleCount(RotateTransition.INDEFINITE);
        rt.play();

        final Delta dragDelta = new Delta();
        grid.setOnMousePressed(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            // record a delta distance for the drag and drop operation.
            Stage stage = (Stage) grid.getScene().getWindow();
            dragDelta.x = stage.getX() - mouseEvent.getScreenX();
            dragDelta.y = stage.getY() - mouseEvent.getScreenY();
          }
        });
        grid.setOnMouseDragged(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            Stage stage = (Stage) grid.getScene().getWindow();
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);
          }
        });

        tf_loadtorrent.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Torrent File");
                chooser.getExtensionFilters().add(new ExtensionFilter("TORRENT", "*.torrent"));
                Window window = ((Node) event.getTarget()).getScene().getWindow();
                File torrentFile = chooser.showOpenDialog(window);
                if (torrentFile != null) {
                    if (outputChosen) {
                        rt.stop();
                        revealQR();
                    }
                    tf_loadtorrent.setText(torrentFile.getName());
                    torrentChosen = true;
                }
            }
        });
        tf_outdir.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                DirectoryChooser chooser = new DirectoryChooser();
                chooser.setTitle("Output Folder");
                Window window = ((Node) event.getTarget()).getScene().getWindow();
                File selectedDirectory = chooser.showDialog(window);
                if (selectedDirectory != null) {
                    if (torrentChosen) {
                        rt.stop();
                        revealQR();
                    }
                    tf_outdir.setText(selectedDirectory.getName());
                    outputChosen = true;
                }
            }
        });

        iv_exit.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                iv_exit.setImage(new Image("file:res/btn_close_selected.png"));
            }
        });

        iv_exit.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                iv_exit.setImage(new Image("file:res/btn_close_normal.png"));
            }
        });

        iv_exit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                iv_exit.setImage(new Image("file:res/btn_close_pressed.png"));
                Stage stage = (Stage) iv_exit.getScene().getWindow();
                stage.close();
            }
        });
    }

    private void revealQR() {
        tf_outdir.setVisible(false);
        tf_loadtorrent.setVisible(false);
        Lan lan = new Lan();
        File file = new File("qr.png");
        lan.generateQR(file, 400);
        Image image = new Image("file:" + file.getAbsolutePath());
        file.delete();
        iv_qr.setOpacity(0);
        iv_qr.setImage(image);
        iv_qr.setRotate(0);
        FadeTransition ft = new FadeTransition(Duration.millis(3000), iv_qr);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();

        ft.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // TODO Auto-generated method stub
                l_instructions.setVisible(true);
                bt_skip.setVisible(true);
            }
        });
    }

    class Delta { double x, y; }
}
