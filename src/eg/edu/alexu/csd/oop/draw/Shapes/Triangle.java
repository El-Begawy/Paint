package eg.edu.alexu.csd.oop.draw.Shapes;

import eg.edu.alexu.csd.oop.draw.BackEnd.PaintEngine;
import eg.edu.alexu.csd.oop.draw.Shape;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Triangle implements Shape {
    public Point P1 = new Point(0, 0), P2 = new Point(0, 0), P3 = new Point(0, 0);
    private Color strokeColor;
    private Color fillColor;
    private double strokeSize = 0.5;
    public String uniqueID = UUID.randomUUID().toString();
    public Point getPosition() {
        return P1;
    }

    public void setPosition(Point position) {
        P1.x += (int) position.getX();
        P1.y += (int) position.getY();
        P2.x += (int) position.getX();
        P2.y += (int) position.getY();
        P3.x += (int) position.getX();
        P3.y += (int) position.getY();
    }

    public double getStrokeSize() {
        return strokeSize;
    }

    public void setStrokeSize(double size) {
        strokeSize = size;
    }

    public Map<String, Double> getProperties() {
        Map<String, Double> properitesMap = new HashMap<String, Double>();
        properitesMap.put("P2X", (double) P2.x);
        properitesMap.put("P2Y", (double) P2.y);
        properitesMap.put("P3X", (double) P3.x);
        properitesMap.put("P3Y", (double) P3.y);
        return properitesMap;
    }

    public void setProperties(Map<String, Double> properties) {
        for (Map.Entry<String, Double> entry : properties.entrySet()) {
            if (entry.getKey().equals("P2X")) {
                this.P2.x = entry.getValue().intValue();
            } else if (entry.getKey().equals("P2Y")) {
                this.P2.y = entry.getValue().intValue();
            } else if (entry.getKey().equals("P3X")) {
                this.P3.x = entry.getValue().intValue();
            } else if (entry.getKey().equals("P3Y")) {
                this.P3.y = entry.getValue().intValue();
            }
        }
    }

    public Color getColor() {
        return strokeColor;
    }

    public void setColor(Color color) {
        strokeColor = color;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color color) {
        fillColor = color;
    }

    public void draw(Graphics canvas) {
        ((FXGraphics2D) canvas).setStroke(new BasicStroke((float) getStrokeSize()));
        if (fillColor != null) {
            canvas.setColor(fillColor);
            canvas.fillPolygon(new int[]{P1.x, P2.x, P3.x}, new int[]{P1.y, P2.y, P3.y}, 3);
        }
        canvas.setColor(strokeColor);
        canvas.drawPolygon(new int[]{P1.x, P2.x, P3.x}, new int[]{P1.y, P2.y, P3.y}, 3);
    }

    public Object clone() throws CloneNotSupportedException {
        Triangle triangle = new Triangle();
        triangle.P1 = new Point((int) P1.getX(), (int) P1.getY());
        triangle.P2 = new Point((int) P2.getX(), (int) P2.getY());
        triangle.P3 = new Point((int) P3.getX(), (int) P3.getY());
        triangle.setColor(new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), getColor().getAlpha()));
        triangle.setFillColor(new Color(getFillColor().getRed(), getFillColor().getGreen(), getFillColor().getBlue(), getFillColor().getAlpha()));
        triangle.setStrokeSize(this.getStrokeSize());
        triangle.uniqueID = this.uniqueID;
        return triangle;
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

    public void setUniqueID(String UID) {
        uniqueID = UID;
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
    private double area(Point p1, Point p2, Point p3) {
        return Math.abs((p1.getX() * (p2.getY() - p3.getY()) + p2.getX() * (p3.getY() - p1.getY()) + p3.getX() * (p1.getY() - p2.getY())) / 2.0);
    }

    public boolean containsPoint(Point point) {
        double A = area(P1, P2, P3);
        double A1 = area(point, P2, P3);
        double A2 = area(P1, point, P3);
        double A3 = area(P1, P2, point);
        return Math.abs(A1 + A2 + A3 - A) <= PaintEngine.EPS;
    }

    public Rectangle getBoundingBox() throws InstantiationException, IllegalAccessException {
        int minX = Math.min((int) P1.getX(), Math.min((int) P2.getX(), (int) P3.getX()));
        int maxX = Math.max((int) P1.getX(), Math.max((int) P2.getX(), (int) P3.getX()));
        int minY = Math.min((int) P1.getY(), Math.min((int) P2.getY(), (int) P3.getY()));
        int maxY = Math.max((int) P1.getY(), Math.max((int) P2.getY(), (int) P3.getY()));
        return (Rectangle) PaintEngine.makeShape(PaintEngine.DRAWING_RECTANGLE,
                new int[]{minX, maxX},
                new int[]{minY, maxY},
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
