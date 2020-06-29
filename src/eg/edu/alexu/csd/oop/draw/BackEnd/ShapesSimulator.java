package eg.edu.alexu.csd.oop.draw.BackEnd;

import eg.edu.alexu.csd.oop.Commands.Command;
import eg.edu.alexu.csd.oop.Commands.makeShapeCommand;
import eg.edu.alexu.csd.oop.draw.Shape;

import java.awt.*;
import java.util.ArrayList;

public class ShapesSimulator {
    private PaintEngine paintEngine;
    private ArrayList<Point> Points;
    private int pointsAdded;
    private int drawingShape;

    public ShapesSimulator(PaintEngine _paintEngine, int _drawingShape) {
        this.paintEngine = _paintEngine;
        this.Points = new ArrayList<>();
        this.drawingShape = _drawingShape;
        this.pointsAdded = 0;
    }

    public void addPoint(int clickedX, int clickedY) {
        Points.add(new Point(clickedX, clickedY));
        pointsAdded++;
    }

    public boolean shapeDone() {
        if (drawingShape == PaintEngine.DRAWING_TRIANGLE)
            return (pointsAdded == 3);
        return (pointsAdded == 2);
    }

    public void simulate(int lastX, int lastY) throws InstantiationException, IllegalAccessException {
        if (pointsAdded <= 0)
            return;
        int[] xCoordinates = new int[pointsAdded + 1];
        int[] yCoordinates = new int[pointsAdded + 1];
        for (int i = 0; i < pointsAdded; i++) {
            xCoordinates[i] = (int) Points.get(i).getX();
            yCoordinates[i] = (int) Points.get(i).getY();
        }
        xCoordinates[pointsAdded] = lastX;
        yCoordinates[pointsAdded] = lastY;
        paintEngine.refresh(paintEngine.getGraphics());
        if (drawingShape != PaintEngine.DRAWING_TRIANGLE) {

            eg.edu.alexu.csd.oop.draw.Shape shapeSimulated = PaintEngine.makeShape(drawingShape, xCoordinates, yCoordinates,
                    PaintEngine.toAwtColor(paintEngine.fillColor), PaintEngine.toAwtColor(paintEngine.strokeColor), paintEngine.strokeSize);
            shapeSimulated.draw(paintEngine.getGraphics());
        } else {
            for (int i = 0; i + 1 <= pointsAdded; i++) {
                eg.edu.alexu.csd.oop.draw.Shape shapeSimulated = PaintEngine.makeShape(PaintEngine.DRAWING_LINE, new int[]{xCoordinates[i], xCoordinates[i + 1]},
                        new int[]{yCoordinates[i], yCoordinates[i + 1]}, PaintEngine.toAwtColor(paintEngine.fillColor),
                        PaintEngine.toAwtColor(paintEngine.strokeColor), paintEngine.strokeSize);
                shapeSimulated.draw(paintEngine.getGraphics());
            }
        }
    }

    public void drawFinalShape() throws InstantiationException, IllegalAccessException {
        int[] xCoordinates = new int[pointsAdded];
        int[] yCoordinates = new int[pointsAdded];
        for (int i = 0; i < pointsAdded; i++) {
            xCoordinates[i] = (int) Points.get(i).getX();
            yCoordinates[i] = (int) Points.get(i).getY();
        }
        paintEngine.refresh(paintEngine.getGraphics());
        eg.edu.alexu.csd.oop.draw.Shape shapeSimulated = PaintEngine.makeShape(drawingShape, xCoordinates, yCoordinates,
                PaintEngine.toAwtColor(paintEngine.fillColor), PaintEngine.toAwtColor(paintEngine.strokeColor), paintEngine.strokeSize);
        paintEngine.addShape(shapeSimulated, true);
        try {
            Command command = new makeShapeCommand(paintEngine, (Shape) shapeSimulated.clone());
            paintEngine.addCommandToStack(command);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }
}
