package eg.edu.alexu.csd.oop.draw.BackEnd;

import com.google.firebase.database.DataSnapshot;
import eg.edu.alexu.csd.oop.draw.Shape;
import eg.edu.alexu.csd.oop.draw.Shapes.Rectangle;
import eg.edu.alexu.csd.oop.draw.Shapes.*;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShapesHandler {
    private PaintEngine paintEngine;
    private int clickedX;
    private int clickedY;

    public ShapesHandler(PaintEngine _paintEngine) {
        this.paintEngine = _paintEngine;
        clickedX = -1;
        clickedY = -1;
    }

    public static eg.edu.alexu.csd.oop.draw.Shape makeShapeFromDB(final DataSnapshot dataSnapshot) {
        eg.edu.alexu.csd.oop.draw.Shape currentShape = null;
        String className = dataSnapshot.child("Type").getValue().toString();
        ArrayList<Class<? extends eg.edu.alexu.csd.oop.draw.Shape>> currentClasses = new ArrayList<>(PaintEngine.supportedShapes);
        for (Class<? extends Shape> currentClass : currentClasses) {
            if (currentClass.getSimpleName().equals(className)) {
                try {
                    currentShape = currentClass.newInstance();
                    break;
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return currentShape;
    }

    public int intersectedShapeWithPoint(Point point) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        eg.edu.alexu.csd.oop.draw.Shape[] Shapes = paintEngine.getShapes();
        for (int i = Shapes.length - 1; i >= 0; i--) {
            Boolean isIntersect = (Boolean) Shapes[i].getClass().getMethod("containsPoint", new Class[]{Point.class}).invoke(Shapes[i], point);
            if (isIntersect) {
                clickedX = clickedY = -1;
                return i;
            }
        }
        return -1;
    }

    public boolean isIntersecting(eg.edu.alexu.csd.oop.draw.Shape shape, Point point) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return (Boolean) shape.getClass().getMethod("containsPoint", new Class[]{Point.class}).invoke(shape, point);
    }

    public void drawBoundingBox(eg.edu.alexu.csd.oop.draw.Shape shape) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        paintEngine.refresh(paintEngine.getGraphics());
        eg.edu.alexu.csd.oop.draw.Shapes.Rectangle boundingRectangle = ((Rectangle) shape.getClass().getMethod("getBoundingBox").invoke(shape));
        boundingRectangle.draw(paintEngine.getGraphics());
    }

    public void resizeShape(eg.edu.alexu.csd.oop.draw.Shape shape, Point destination) {
        Map<String, Double> prop = new HashMap<>();

        if (shape instanceof Circle) {
            prop.put("Radius", Math.sqrt(Math.pow(shape.getPosition().x - destination.x, 2) +
                    Math.pow(shape.getPosition().y - destination.y, 2)));
        } else if (shape instanceof Rectangle) {
            prop.put("Height", destination.getY() - shape.getPosition().y);
            prop.put("Width", destination.getX() - shape.getPosition().x);
        } else if (shape instanceof Ellipse) {
            prop.put("Minor", Math.abs(destination.getY() - shape.getPosition().getY()));
            prop.put("Major", Math.abs(destination.getX() - shape.getPosition().getX()));
        } else if (shape instanceof Line) {
            prop.put("XPosition", destination.getX());
            prop.put("YPosition", destination.getY());
        } else if (shape instanceof Triangle) {
            prop.put("P2X", destination.getX());
            prop.put("P2Y", destination.getY());
        } else {
            prop.put("Height", destination.getY() - shape.getPosition().y);
            prop.put("Width", destination.getX() - shape.getPosition().x);
        }
        shape.setProperties(prop);

    }

    private Point relativeDestination(Point destination) {
        return new Point((int) (destination.getX() - clickedX), (int) (destination.getY() - clickedY));
    }

    public void moveShape(eg.edu.alexu.csd.oop.draw.Shape shape, Point destination) {
        Point oldDestination = destination;
        if (shape instanceof Triangle || shape instanceof Line) {
            if (clickedX == -1) {
                clickedX = (int) oldDestination.getX();
                clickedY = (int) oldDestination.getY();
                return;
            }
            destination = relativeDestination(destination);
        }

        shape.setPosition(destination);
        clickedX = (int) oldDestination.getX();
        clickedY = (int) oldDestination.getY();

    }

    public boolean validShapeId(int shapeID) {
        eg.edu.alexu.csd.oop.draw.Shape[] shapes = paintEngine.getShapes();
        return shapeID >= 0 && shapeID < shapes.length;
    }

    public eg.edu.alexu.csd.oop.draw.Shape getShapeById(int shapeID) throws RuntimeException {
        eg.edu.alexu.csd.oop.draw.Shape[] shapes = paintEngine.getShapes();
        if (shapeID < 0 || shapeID >= shapes.length)
            throw new NullPointerException("Shape not found");
        return shapes[shapeID];
    }

}
