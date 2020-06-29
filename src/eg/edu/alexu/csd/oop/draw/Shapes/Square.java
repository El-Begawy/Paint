package eg.edu.alexu.csd.oop.draw.Shapes;

import eg.edu.alexu.csd.oop.draw.BackEnd.PaintEngine;
import eg.edu.alexu.csd.oop.draw.Shape;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Square implements Shape {
    private java.awt.Rectangle R;
    private Color currentColor;
    private Color fillColor;
    private double strokeSize = 0.5;
    public String uniqueID = UUID.randomUUID().toString();
    public Square(int x1, int y1, int side) {
        R = new java.awt.Rectangle(x1, y1, side, side);
    }

    public Square() {
        R = new java.awt.Rectangle();
    }

    public java.awt.Rectangle getR() {
        return R;
    }

    public void setPosition(Point position) {
        R.setLocation(position);
    }

    public Point getPosition() {
        return R.getLocation();
    }

    public double getStrokeSize() {
        return strokeSize;
    }

    public void setStrokeSize(double size) {
        strokeSize = size;
    }
    public Map<String, Double> getProperties() {
        HashMap answer = new HashMap();
        answer.put("Side", Double.valueOf(R.getHeight()));
        return answer;
    }

    public void setProperties(Map<String, Double> properties) {
        for (Map.Entry<String, Double> entry : properties.entrySet()) {
            if (entry.getKey().equals("Side")) {
                R.setSize(entry.getValue().intValue(), entry.getValue().intValue());
            }
        }

    }

    public void setColor(Color color) {
        currentColor = color;
    }

    public Color getColor() {
        return currentColor;
    }

    public void setFillColor(Color color) {
        fillColor = color;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void draw(Graphics canvas) {
        Point pos = this.getPosition();
        int X1 = pos.x;
        int X2 = pos.x + (int) R.getWidth();
        int Y1 = pos.y;
        int Y2 = pos.y + (int) R.getHeight();
        int minX = Math.min(X1, X2);
        int minY = Math.min(Y1, Y2);
        int maxX = Math.max(X1, X2);
        ((FXGraphics2D) canvas).setStroke(new BasicStroke((float) getStrokeSize()));
        canvas.setColor(currentColor);
        canvas.drawRect(minX, minY, maxX - maxX, maxX - maxX);
        if(fillColor != null){
            canvas.setColor(fillColor);
            canvas.fillRect(minX, minY, maxX - maxX, maxX - maxX);
        }
    }

    private Map<String, Integer> getPositionMap() {
        HashMap<String, Integer> result = new HashMap<>();
        result.put("X", (int) getPosition().getX());
        result.put("Y", (int) getPosition().getY());
        return result;
    }

    private Map<String, Integer> getColorMap(Color currentColor) {
        HashMap<String, Integer> result = new HashMap<>();
        result.put("Red", currentColor.getRed());
        result.put("Blue", currentColor.getBlue());
        result.put("Green", currentColor.getGreen());
        result.put("Alpha", currentColor.getAlpha());
        return result;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("Type", getClass().getSimpleName());
        result.put("Position", getPositionMap());
        if (getFillColor() != null)
            result.put("FillColor", getColorMap(getFillColor()));
        result.put("Color", getColorMap(getColor()));
        result.put("Stroke", getStrokeSize());
        result.put("Properties", getProperties());
        return result;
    }
    public Object clone() throws CloneNotSupportedException {
        Square square = new Square();
        square.setColor(this.getColor());
        square.setFillColor(this.getFillColor());
        square.setPosition(this.getPosition());
        square.setProperties(this.getProperties());
        square.uniqueID = this.uniqueID;
        return square;
    }

    public void setUniqueID(String UID) {
        uniqueID = UID;
    }
    public boolean containsPoint(Point point) {
        Point pos = this.getPosition();
        int X1 = pos.x;
        int X2 = pos.x + (int) R.getWidth();
        int Y1 = pos.y;
        int Y2 = pos.y + (int) R.getHeight();
        int minX = Math.min(X1, X2);
        int minY = Math.min(Y1, Y2);
        int maxX = Math.max(X1, X2);
        int maxY = Math.max(Y1, Y2);
        return (point.getX() >= minX && point.getX() <= maxX && point.getY() >= minY && point.getY() <= maxY);
    }

    public Rectangle getBoundingBox() throws InstantiationException, IllegalAccessException {
        return (Rectangle) PaintEngine.makeShape(PaintEngine.DRAWING_RECTANGLE,
                new int[]{(int) getPosition().getX(), (int) R.getWidth()},
                new int[]{(int) getPosition().getY(), (int) R.getHeight()},
                null, PaintEngine.boundingRectangleStroke, 10);
    }

    public boolean equalShape(Shape shape) {
        Field field = null;
        try {
            field = shape.getClass().getDeclaredField("uniqueID");
            field.setAccessible(true);
            String value = field.get(shape).toString();
            return uniqueID.equals(value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
