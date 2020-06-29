package eg.edu.alexu.csd.oop.draw.Shapes;


import eg.edu.alexu.csd.oop.draw.BackEnd.PaintEngine;
import eg.edu.alexu.csd.oop.draw.Shape;
import org.jfree.fx.FXGraphics2D;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Line implements Shape {
    private Point firstPoint = new Point(0, 0), secondPoint = new Point(0, 0);
    private Color currentColor = Color.black;
    private Color fillColor;
    private double strokeSize = 0.5;
    public String uniqueID = UUID.randomUUID().toString();
    private Line(int x1, int y1, int x2, int y2) {
        firstPoint.setLocation(x1, y1);
        secondPoint.setLocation(x2, y2);
    }

    public Line() {
    }

    public Point getPosition() {
        return firstPoint;
    }

    public void setPosition(Point position) {
        firstPoint.x += (int) position.getX();
        firstPoint.y += (int) position.getY();
        secondPoint.x += (int) position.getX();
        secondPoint.y += (int) position.getY();
    }

    public double getStrokeSize() {
        return strokeSize;
    }

    public void setStrokeSize(double size) {
        strokeSize = size;
    }
    public Map<String, Double> getProperties() {
        HashMap answer = new HashMap();
        answer.put("XPosition", secondPoint.getX());
        answer.put("YPosition", secondPoint.getY());
        return answer;
    }

    public void setProperties(Map<String, Double> properties) {
        for (Map.Entry<String, Double> entry : properties.entrySet()) {
            if (entry.getKey().equals("XPosition")) {
                secondPoint.x = entry.getValue().intValue();
            } else if (entry.getKey().equals("YPosition")) {
                secondPoint.y = entry.getValue().intValue();
            }
        }
    }
    public void setPosition(Point p1, Point p2) {
        firstPoint = p1;
        secondPoint = p2;
    }

    public Color getColor() {
        return currentColor;
    }

    public void setColor(Color color) {
        currentColor = color;
    }

    public Color getFillColor() {
        return fillColor;
    }

    public void setFillColor(Color color) {
        fillColor = color;
    }

    public void draw(Graphics canvas) {
        ((FXGraphics2D) canvas).setStroke(new BasicStroke((float) getStrokeSize()));
        canvas.setColor(currentColor);
        canvas.drawLine(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);

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
        Line copyLine = new Line(firstPoint.x, firstPoint.y, secondPoint.x, secondPoint.y);
        copyLine.setColor(new Color(getColor().getRed(), getColor().getGreen(), getColor().getBlue(), getColor().getAlpha()));
        copyLine.setFillColor(new Color(getFillColor().getRed(), getFillColor().getGreen(), getFillColor().getBlue(), getFillColor().getAlpha()));
        copyLine.setStrokeSize(strokeSize);
        copyLine.uniqueID = this.uniqueID;
        return copyLine;
    }

    public double distance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    public boolean containsPoint(Point point) {
        return Math.abs(distance(firstPoint, point) + distance(point, secondPoint) - distance(firstPoint, secondPoint)) <= PaintEngine.EPS;
    }

    public Rectangle getBoundingBox() throws InstantiationException, IllegalAccessException {
        return (Rectangle) PaintEngine.makeShape(PaintEngine.DRAWING_RECTANGLE,
                new int[]{(int) firstPoint.getX(), (int) secondPoint.getX()},
                new int[]{(int) firstPoint.getY(), (int) secondPoint.getY()},
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
