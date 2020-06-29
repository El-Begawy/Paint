package eg.edu.alexu.csd.oop.Gui.FrontEnd;

import eg.edu.alexu.csd.oop.Commands.Command;
import eg.edu.alexu.csd.oop.Commands.changePropertiesCommand;
import eg.edu.alexu.csd.oop.Commands.deleteCommand;
import eg.edu.alexu.csd.oop.Gui.AlertBox.AlertBox;
import eg.edu.alexu.csd.oop.draw.BackEnd.PaintEngine;
import eg.edu.alexu.csd.oop.draw.BackEnd.ShapesHandler;
import eg.edu.alexu.csd.oop.draw.BackEnd.ShapesSimulator;
import eg.edu.alexu.csd.oop.draw.Shape;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static eg.edu.alexu.csd.oop.draw.BackEnd.PaintEngine.IDLE;

public class Controller {
    @FXML
    ImageView lineImage, rectangleImage, circleImage, ellipseImage, triangleImage, statusImage, roundRectangle;
    @FXML
    AnchorPane anchorPane;
    @FXML
    Canvas canvas;
    @FXML
    Label stroke, fill, statusLabel;
    @FXML
    ColorPicker strokeColor, fillColor;
    @FXML
    ChoiceBox<Double> strokeSizeChooser;
    private PaintEngine paintEngine;
    private int currentDrawing = IDLE;
    private ShapesHandler shapesHandler;
    private ArrayList<String> hoverArrayList;

    void setStage(Stage stage) {
        canvas.setFocusTraversable(true);
        stage.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                currentDrawing = -1;
                canvas.setOnMouseClicked(null);
                canvas.setOnMouseMoved(null);
                selectShape();
                paintEngine.refresh(paintEngine.getGraphics());
            } else if (keyEvent.getCode() == KeyCode.Z) {
                paintEngine.undo();
            } else if (keyEvent.getCode() == KeyCode.X) {
                paintEngine.redo();
            }
        });
    }

    @FXML
    public void initialize() {
        hoverArrayList = new ArrayList<>();
        setBlackBackground();
        paintEngine = new PaintEngine(canvas);
        shapesHandler = new ShapesHandler(paintEngine);
        InitImages(new String[]{"lineH.png", "rectangleH.png", "circleH.png", "ellipseH.png", "TriangleH.png", "roundedrectangleH.png"},
                lineImage, rectangleImage, circleImage, ellipseImage, triangleImage, roundRectangle);
        strokeColor.setOnAction(actionEvent -> {
            paintEngine.strokeColor = strokeColor.getValue();
            canvas.requestFocus();
        });
        fillColor.setOnAction(actionEvent -> {
            paintEngine.fillColor = fillColor.getValue();
            canvas.requestFocus();
        });
        strokeColor.setValue(Color.BLACK);
        fillColor.setValue(new Color(0, 0, 0, 0));
        stroke.setTextFill(Color.web("#E6E6E6"));
        fill.setTextFill(Color.web("#E6E6E6"));
        statusLabel.setTextFill(Color.web("#E6E6E6"));
        selectShape();
        strokeSizeChooser.setValue(0.5);
        strokeSizeChooser.setOnAction(actionEvent -> paintEngine.strokeSize = strokeSizeChooser.getValue());
    }

    private void InitImages(String[] paths, ImageView... imageViews) {
        for (int i = 0; i < imageViews.length; i++) {
            int finalI = i;
            String str2 = paths[i];
            hoverArrayList.add(str2);
            imageViews[i].setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    imageViews[finalI].setImage(new Image(getClass().getResourceAsStream("images/" + hoverArrayList.get(finalI))));
                }
            });
            imageViews[i].setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    String curStr = new String(hoverArrayList.get(finalI)).replace("H", "");
                    imageViews[finalI].setImage(new Image(getClass().getResourceAsStream("images/" + curStr)));
                }
            });
            imageViews[i].addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    currentDrawing = finalI;
                    paintEngine.refresh(paintEngine.getGraphics());
                    canvas.setOnMouseDragged(null);
                    addShape();
                    statusImage.setImage(new Image(getClass().getResourceAsStream("images/pencil.png")));
                }
            });
        }
    }

    private void setBlackBackground() {
        BackgroundFill myBF = new BackgroundFill(javafx.scene.paint.Color.BLACK, new CornerRadii(1),
                new Insets(0.0, 0.0, 0.0, 0.0));// or null for the padding
        anchorPane.setBackground(new Background(myBF));
    }


    private void addShape() {
        if (paintEngine.getSupportedShapes().size() <= currentDrawing)
            return;
        ShapesSimulator shapesSimulator = new ShapesSimulator(paintEngine, currentDrawing);
        canvas.setOnMouseClicked(mouseEvent -> {
            shapesSimulator.addPoint((int) mouseEvent.getX(), (int) mouseEvent.getY());
            if (shapesSimulator.shapeDone()) {
                try {
                    shapesSimulator.drawFinalShape();
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if (!shapesSimulator.shapeDone()) {
                canvas.setOnMouseMoved(mouseEvent1 -> {
                    try {
                        shapesSimulator.simulate((int) mouseEvent1.getX(), (int) mouseEvent1.getY());
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
            } else {
                canvas.setOnMouseClicked(null);
                canvas.setOnMouseMoved(null);
                selectShape();
            }
        });
    }

    private void selectShape() {

        statusImage.setImage(new Image(getClass().getResourceAsStream("images/openhand.png")));
        canvas.setOnMouseClicked(new EventHandler<MouseEvent>() {
            Shape oldShape = null;
            boolean isDrag = false;

            @Override
            public void handle(MouseEvent mouseEvent) {
                paintEngine.shapeID = -1;
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    isDrag = false;
                    try {
                        paintEngine.shapeID = shapesHandler.intersectedShapeWithPoint(new Point((int) mouseEvent.getX(), (int) mouseEvent.getY()));
                        if (shapesHandler.validShapeId(paintEngine.shapeID)) {
                            shapesHandler.drawBoundingBox(shapesHandler.getShapeById(paintEngine.shapeID));
                        } else
                            paintEngine.refresh(paintEngine.getGraphics());
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                if (shapesHandler.validShapeId(paintEngine.shapeID)) {
                    try {
                        oldShape = (Shape) shapesHandler.getShapeById(paintEngine.shapeID).clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                canvas.setOnKeyPressed(KeyEvent -> {
                    if (KeyEvent.getCode() == KeyCode.DELETE && shapesHandler.validShapeId(paintEngine.shapeID)) {
                        Command command = null;
                        try {
                            command = new deleteCommand(paintEngine, (Shape) shapesHandler.getShapeById(paintEngine.shapeID).clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                        paintEngine.addCommandToStack(command);
                        paintEngine.removeShape(shapesHandler.getShapeById(paintEngine.shapeID));
                        paintEngine.refresh(paintEngine.getGraphics());
                        paintEngine.shapeID = -1;
                    }
                });
                canvas.setOnMouseDragged(mouseEvent1 -> {
                    if (mouseEvent1.getButton().equals(MouseButton.PRIMARY)) {
                        try {
                            if (!shapesHandler.validShapeId(paintEngine.shapeID))
                                return;
                            boolean draggedShape = shapesHandler.isIntersecting(shapesHandler.getShapeById(paintEngine.shapeID), new Point((int) mouseEvent1.getX(), (int) mouseEvent1.getY()));
                            if (draggedShape) {
                                isDrag = true;
                                shapesHandler.moveShape(shapesHandler.getShapeById(paintEngine.shapeID), new Point((int) mouseEvent1.getX(), (int) mouseEvent1.getY()));
                                shapesHandler.drawBoundingBox(shapesHandler.getShapeById(paintEngine.shapeID));
                                canvas.setOnMouseReleased(mouseDragEvent -> {
                                    if (!shapesHandler.validShapeId(paintEngine.shapeID) || !isDrag)
                                        return;
                                    try {
                                        Command command = new changePropertiesCommand(paintEngine, oldShape,
                                                (Shape) shapesHandler.getShapeById(paintEngine.shapeID).clone());
                                        paintEngine.getFireBaseRoom().addShapeToDB(shapesHandler.getShapeById(paintEngine.shapeID));
                                        paintEngine.refresh(paintEngine.getGraphics());
                                        paintEngine.addCommandToStack(command);
                                        isDrag = false;
                                    } catch (CloneNotSupportedException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (mouseEvent1.getButton().equals(MouseButton.SECONDARY)) {
                        try {
                            if (!shapesHandler.validShapeId(paintEngine.shapeID))
                                return;
                            isDrag = true;
                            shapesHandler.resizeShape(shapesHandler.getShapeById(paintEngine.shapeID), new Point((int) mouseEvent1.getX(), (int) mouseEvent1.getY()));
                            shapesHandler.drawBoundingBox(shapesHandler.getShapeById(paintEngine.shapeID));
                            canvas.setOnMouseReleased(mouseDragEvent -> {
                                if (!shapesHandler.validShapeId(paintEngine.shapeID) || !isDrag)
                                    return;
                                try {
                                    Command command = new changePropertiesCommand(paintEngine, oldShape,
                                            (Shape) shapesHandler.getShapeById(paintEngine.shapeID).clone());
                                    paintEngine.getFireBaseRoom().addShapeToDB(shapesHandler.getShapeById(paintEngine.shapeID));
                                    paintEngine.refresh(paintEngine.getGraphics());
                                    paintEngine.addCommandToStack(command);
                                    isDrag = false;
                                } catch (CloneNotSupportedException e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    @FXML
    private void save() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML (*.xml)", "*.xml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));
        File file = fileChooser.showSaveDialog(null);
        if (file == null)
            return;
        paintEngine.save(file.getPath());
        AlertBox.infoBox("Save Successful!", "Save");
    }

    @FXML
    private void load() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML (*.xml)", "*.xml"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON (*.json)", "*.json"));
        File file = fileChooser.showOpenDialog(null);
        if (file == null)
            return;
        paintEngine.load(file.getPath());
        AlertBox.infoBox("Load Successful!", "Load");
    }

    @FXML
    private void loadJar() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Jar (*.jar)", "*.jar"));
        File file = fileChooser.showOpenDialog(null);
        if (file == null)
            return;
        paintEngine.installPluginShape(file.getPath());
        AlertBox.infoBox("Plugin Loaded", "Plugin Manager");
    }

    @FXML
    private void closeApp() {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void makeFireBaseRoom() {
        String UID = UUID.randomUUID().toString();
        paintEngine.setFireBaseRoom(UID);
        AlertBox.infoBox("Your Room ID has been copied to clipboard\nID: " + UID, "Made Room Successfully!");
        Toolkit.getDefaultToolkit()
                .getSystemClipboard()
                .setContents(new StringSelection(UID), null);
    }

    @FXML
    private void leaveFireBaseRoom() {
        paintEngine.unSetFireBaseRoom();
        AlertBox.infoBox("Left Room Successfully!", "Room Left");
    }

    @FXML
    private void connectToFireBaseRoom() {
        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("Join Room");
        dialog.setHeaderText("Join Drawing Room");
        dialog.setContentText("Please enter room ID:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(s ->
        {
            if (isUUID(result.get())) {
                paintEngine.setFireBaseRoom(result.get());
                AlertBox.infoBox("Room Joined!", "Online Painting");
            } else
                AlertBox.infoBox("Invalid Room ID", "Error!");
        });
    }

    private boolean isUUID(String s) {
        return s.matches("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
    }
}