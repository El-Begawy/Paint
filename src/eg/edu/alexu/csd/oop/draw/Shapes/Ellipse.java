package eg.edu.alexu.csd.oop.draw.Shapes;

import eg.edu.alexu.csd.oop.draw.BackEnd.PaintEngine;
import eg.edu.alexu.csd.oop.draw.Shape;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Ellipse implements Shape {
    private Point center;
    private Color color, fillColor;
    private int r1, r2;
    private double strokeSize = 0.5;
    public String uniqueID = UUID.randomUUID().toString();
    public Point getPosition() {
        return center;
    }

    public void setPosition(Point position) {
        center = position;
    }

    public Map<String, Double> getProperties() {
        HashMap<String, Double> properties = new HashMap<String, Double>();
        properties.put("Major", (double) r1);
        properties.put("Minor", (double) r2);
        return properties;
    }

    public void setProperties(Map<String, Double> properties) {
        r1 = properties.get("Major").intValue();
        r2 = properties.get("Minor").intValue();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color color) {
        fillColor = color;
    }

    public double getStrokeSize() {
        return strokeSize;
    }

    public void setStrokeSize(double size) {
        strokeSize = size;
    }

    public void draw(Graphics canvas) {
        int tmpR1 = r1 * 2;
        int tmpR2 = r2 * 2;
        int x = center.x - (tmpR1 / 2);
        int y = center.y - (tmpR2 / 2);
        ((FXGraphics2D) canvas).setStroke(new BasicStroke((float) getStrokeSize()));
        if (fillColor != null) {
            canvas.setColor(fillColor);
            canvas.fillOval(x, y, tmpR1, tmpR2);
        }
        canvas.setColor(color);
        canvas.drawOval(x, y, tmpR1, tmpR2);
    }

    public Object clone() throws CloneNotSupportedException {
        Ellipse ellipse = new Ellipse();
        ellipse.setPosition(new Point((int) getPosition().getX(), (int) getPosition().getY()));
        ellipse.setColor(new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), getColor().getAlpha()));
        ellipse.setFillColor(new Color(getFillColor().getRed(), getFillColor().getGreen(), getFillColor().getBlue(), getFillColor().getAlpha()));
        ellipse.r1 = this.r1;
        ellipse.r2 = this.r2;
        ellipse.setPosition(this.getPosition());
        ellipse.setStrokeSize(this.getStrokeSize());
        ellipse.uniqueID = this.uniqueID;
        return ellipse;
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
    public boolean containsPoint(Point point) {
        double F1 = Math.pow(point.getX() - center.getX(), 2) / (r1 * r1);
        double F2 = Math.pow(point.getY() - center.getY(), 2) / (r2 * r2);
        return F1 + F2 <= 1;
    }

    public Rectangle getBoundingBox() throws InstantiationException, IllegalAccessException {
        return (Rectangle) PaintEngine.makeShape(PaintEngine.DRAWING_RECTANGLE,
                new int[]{(int) getPosition().getX() - r1, (int) getPosition().getX() + r1},
                new int[]{(int) getPosition().getY() - r2, (int) getPosition().getY() + r2},
                null, PaintEngine.boundingRectangleStroke, 10);
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
