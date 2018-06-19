package proj.sample;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;
import javafx.util.Pair;
import proj.Utils.Utils;
import proj.vitesse.vehicule.DetecteurDeVehicule;
import proj.vitesse.vehicule.Video;



public class Controller implements Initializable {


    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    private Video video;
    private Point[] polygonePoints = new Point[4];;
    private int pointNumero = 0;
    private boolean chargee=false;
    public static double fps = 25;

    public static double vitesseMax;
    public static String pathForResult = System.getProperty("user.home") + "/Desktop/Vehicules/";
    @FXML
    public Label compteurVoitureDepass;
    @FXML
    public TextField vitesseMaxField;
    @FXML
    public Label filePath;
    @FXML
    public Button startButton,chargeeButton;
    @FXML
    private ImageView currentFrame;
    @FXML
    private void destinationClicked(ActionEvent e)
    {
        callMePlease(e);
    }

    @FXML
    private void aboutClicked(ActionEvent e) {
        Alert alert = new Alert(AlertType.INFORMATION); // infos
        alert.setTitle("About");
        alert.getDialogPane().setPrefSize(600,670);
        alert.setHeaderText("Realisé par:");
        alert.setContentText(" Meskali Reda \n Lagrini Youness" +
                "\n\n\nMIT License\n" +
                "\n" +
                "Copyright (c) 2018 Reda Meskali\n" +
                "\n" +
                "Permission is hereby granted, free of charge, to any person obtaining a copy\n" +
                "of this software and associated documentation files (the \"Software\"), to deal\n" +
                "in the Software without restriction, including without limitation the rights\n" +
                "to use, copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
                "copies of the Software, and to permit persons to whom the Software is\n" +
                "furnished to do so, subject to the following conditions:\n" +
                "\n" +
                "The above copyright notice and this permission notice shall be included in all\n" +
                "copies or substantial portions of the Software.\n" +
                "\n" +
                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR\n" +
                "IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,\n" +
                "FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE\n" +
                "AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER\n" +
                "LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,\n" +
                "OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE\n" +
                "SOFTWARE.\n\n\n");
        alert.showAndWait();
    }

    @FXML
    private void startClicked(ActionEvent e)
    {
        vitesseMax = Double.parseDouble(vitesseMaxField.getText());
        vitesseMaxField.setDisable(true);
        if(video.getFrame().getConvertisseursCoordonnees().size()>=1 ) {
        	startButton.setDisable(true);
        	double minSurface = metreToPixel(3);
        	double minWidth = metreToPixel(1);
        	double minHeight = metreToPixel(1);
        	double minDiagonale = metreToPixel(1.5);
        	DetecteurDeVehicule detecteurDeVehicule = new DetecteurDeVehicule(Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE,
        			minSurface, minWidth, minHeight, 0.2, 1.25, minDiagonale);
        	video.setDetecteurDeVehicule(detecteurDeVehicule);
        	System.out.println(video.getDetecteurDeVehicule());
        	chargeeButton.setDisable(true);
            fullProcess();
        } else {
        	Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Manque de référence !!");
            alert.setContentText("Ajouter au moins un rectangle de repérage et respectez l'ordre afin d'obtenir des résultats corrects !");
            Image image = new Image("proj/Images/reference.png");
            ImageView imageView = new ImageView(image);
            alert.setGraphic(imageView);
            alert.showAndWait();
        }
    }

    @FXML
    private void chargerClicked(ActionEvent e) {
        pointNumero = 0;
        System.out.println("Charger Clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Ouvrir flux video");
        Node node = (Node) e.getSource();
        FileChooser.ExtensionFilter videoFilter
                = new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.avi","*.mov","*.h64","*.h264");
        fileChooser.getExtensionFilters().add(videoFilter);
        File file = fileChooser.showOpenDialog(node.getScene().getWindow());
        System.out.println(file.getAbsolutePath());
        filePath.setText(file.getName());
        chargee=true;
        vitesseMaxField.textProperty().addListener((arg0, oldValue, newValue) -> {
            startButton.setDisable(!allValid());
        });
        setVideo(new Video(org.opencv.video.Video.createBackgroundSubtractorMOG2(),0.01, 200, 255, Imgproc.THRESH_BINARY, 5,
                Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE,
                200, 30, 30, 0.2, 4.0, 60, file.getAbsolutePath()));
        fps = getVideo().getFps();
        unFrame();
        new File(pathForResult).mkdir();
    }


    public static void callMePlease(ActionEvent e)
    {
        System.out.println("Destination clicked");
        Node node = (Node) e.getSource();

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(node.getScene().getWindow());
        directoryChooser.setTitle("Destination des resultats");

        if(selectedDirectory == null){
            new File(System.getProperty("user.home") + "/Desktop/Vehicules/").mkdir();
        }else{
            pathForResult = selectedDirectory.getAbsolutePath();
        }
        System.out.println(pathForResult);
    }


    public void fullProcess()  {
        new Thread(new Worker()).start();
        System.out.println("Done " + Controller.fps);
    }
    public void unFrame() {
        if(video.process()) {
            showImage(video.getFrame().getFrameCourant());
        }
    }

    public double metreToPixel(double input) {
        double pixel = video.getFrame().getConvertisseursCoordonnees().get(0).getPolygoneImage().getSommet(1).x
                - video.getFrame().getConvertisseursCoordonnees().get(0).getPolygoneImage().getSommet(0).x;
        double metre = video.getFrame().getConvertisseursCoordonnees().get(0).getPolygoneMondeReel().getSommet(1).y
                - video.getFrame().getConvertisseursCoordonnees().get(0).getPolygoneMondeReel().getSommet(0).y;
        return (input*pixel)/metre;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        startButton.setDisable(true);
        currentFrame.setImage(new Image("proj/Images/1071x597.png"));
        currentFrame.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                if (video != null) {
                    Mat image = video.getFrame().getFrameCourant();
                    double x = event.getSceneX();
                    double y = event.getSceneY();
                    polygonePoints[pointNumero % 4] = new Point(x, y);
                    System.out.println(x + "," + y + " " + image.size() + " " + pointNumero);
                    Imgproc.circle(image, new Point(x, y), 2, new Scalar(0, 0, 255), -1);
                    video.getFrame().setFrameCourant(image);
                    showImage(video.getFrame().getFrameCourant());
                    pointNumero++;
                    if (pointNumero % 4 == 0) {
                        System.out.println(polygonePoints[0] + " " + polygonePoints[1] + " " + polygonePoints[2] + " " + polygonePoints[3]);

                        Dialog<Pair<String, String>> dialog = new Dialog<>();
                        dialog.setTitle("Real parameters");
                        dialog.initStyle(StageStyle.UNDECORATED);
                        // Set the button types.
                        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
                        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType);

                        GridPane gridPane = new GridPane();
                        gridPane.setHgap(10);
                        gridPane.setVgap(10);
                        gridPane.setPadding(new Insets(20, 150, 10, 10));

                        TextField realWidth = new TextField();
                        realWidth.setPromptText("Real Width (METERS)");
                        TextField realHeight = new TextField();
                        realHeight.setPromptText("Real Height (METERS)");

                        gridPane.add(new Label("Real Width:"), 0, 0);
                        gridPane.add(realWidth, 1, 0);
                        gridPane.add(new Label("Real Height:"), 0, 1);
                        gridPane.add(realHeight, 1, 1);

                        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
                        loginButton.setDisable(true);

                        Platform.setImplicitExit(false);


                        realWidth.textProperty().addListener((observable, oldValue, newValue) -> {
                            if (Utils.isNumeric(realHeight.getText())) {
                                loginButton.setDisable((newValue.trim().isEmpty() || !Utils.isNumeric(newValue.trim())));
                            }
                        });

                        realHeight.textProperty().addListener((observable, oldValue, newValue) -> {
                            if (Utils.isNumeric(realWidth.getText())) {
                                loginButton.setDisable((newValue.trim().isEmpty() || !Utils.isNumeric(newValue.trim())));
                            }
                        });

                        dialog.getDialogPane().setContent(gridPane);

                        Platform.runLater(() -> realWidth.requestFocus());

                        dialog.setResultConverter(dialogButton -> {
                            if (dialogButton == loginButtonType) {
                                return new Pair<>(realWidth.getText(), realHeight.getText());
                            }
                            return null;
                        });

                        Optional<Pair<String, String>> result = dialog.showAndWait();
                        result.ifPresent(pair -> {
                            video.ajouterReference(polygonePoints[0], polygonePoints[1], polygonePoints[2], polygonePoints[3], Double.parseDouble(pair.getKey()), Double.parseDouble(pair.getValue()));
                            System.out.println("realWidth=" + pair.getKey() + ", realHeight=" + pair.getValue());
                        });


                        video.getFrame().dessinerReferences();
                        showImage(video.getFrame().getFrameCourant());
                    }
                    event.consume();
                }
            }
        });
    }

    public boolean allValid()
    {
        return (Utils.isNumeric(vitesseMaxField.getText()) &&
                chargee);
    }
    public void showImage(Mat img) {
        //BufferedImage bufImage = null;
        try {
            Image imageToShow = Utils.mat2Image(img);
            updateImageView(currentFrame, imageToShow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void updateImageView(ImageView view, Image image)
    {
        Utils.onFXThread(view.imageProperty(), image);
    }

    class Worker implements Runnable {
        public void run() {
            while(true) {
                if(video.process()) {
                    showImage(video.getFrame().getFrameCourant());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateCompteur();
                        }
                    });
                }
                else break;
            }
            chargeeButton.setDisable(false);
        }
    }

    private void updateCompteur() {
        compteurVoitureDepass.setText(Integer.toString(video.getCompteur()));
    }
}
