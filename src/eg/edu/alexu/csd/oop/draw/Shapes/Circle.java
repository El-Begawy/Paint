package eg.edu.alexu.csd.oop.draw.Shapes;

import eg.edu.alexu.csd.oop.draw.BackEnd.PaintEngine;
import eg.edu.alexu.csd.oop.draw.Shape;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Circle implements Shape {
    private Point center;
    private Color bcolor, colorfill;
    private int radius;
    private double strokeSize = 0.5;
    public String uniqueID = UUID.randomUUID().toString();
    public Point getPosition() {
        return center;
    }

    public void setPosition(Point position) {
        center = position;
    }

    public double getStrokeSize() {
        return strokeSize;
    }

    public void setStrokeSize(double size) {
        strokeSize = size;
    }

    public Map<String, Double> getProperties() {
        HashMap<String, Double> properties = new HashMap<String, Double>();
        properties.put("Radius", (double) radius);
        return properties;
    }

    public void setProperties(Map<String, Double> properties) {
        radius = properties.get("Radius").intValue();

    }

    public Color getColor() {
        return bcolor;
    }

    public void setColor(Color color) {
        bcolor = color;
    }

    public Color getFillColor() {
        return colorfill;
    }

    public void draw(Graphics canvas) {
        ((FXGraphics2D) canvas).setStroke(new BasicStroke((float) getStrokeSize()));
        int Diameter = radius * 2;
        int x = center.x - (Diameter / 2);
        int y = center.y - (Diameter / 2);
        if (colorfill != null) {
            canvas.setColor(colorfill);
            canvas.fillOval(x, y, Diameter, Diameter);
        }
        canvas.setColor(bcolor);
        canvas.drawOval(x, y, Diameter, Diameter);
    }

    public void setFillColor(Color color) {
        colorfill = color;
    }

    public Object clone() throws CloneNotSupportedException {
        Circle circle = new Circle();
        circle.setPosition(new Point((int) getPosition().getX(), (int) getPosition().getY()));
        circle.setColor(new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), getColor().getAlpha()));
        circle.setFillColor(new Color(getFillColor().getRed(), getFillColor().getGreen(), getFillColor().getBlue(), getFillColor().getAlpha()));
        circle.radius = this.radius;
        circle.setStrokeSize(this.getStrokeSize());
        circle.uniqueID = this.uniqueID;
        return circle;
    }

    public boolean containsPoint(Point point) {
        double dis = Math.sqrt(Math.pow(point.getX() - center.getX(), 2) + Math.pow(point.getY() - center.getY(), 2));
        return dis <= radius;
    }

    public Rectangle getBoundingBox() throws InstantiationException, IllegalAccessException {
        return (Rectangle) PaintEngine.makeShape(PaintEngine.DRAWING_RECTANGLE,
                new int[]{(int) getPosition().getX() - radius, (int) getPosition().getX() + radius},
                new int[]{(int) getPosition().getY() - radius, (int) getPosition().getY() + radius},
                null, PaintEngine.boundingRectangleStroke, 10);
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

    public void setUniqueID(String UID) {
        uniqueID = UID;
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
