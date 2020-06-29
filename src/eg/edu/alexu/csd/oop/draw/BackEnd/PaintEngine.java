package eg.edu.alexu.csd.oop.draw.BackEnd;

import eg.edu.alexu.csd.oop.Commands.Command;
import eg.edu.alexu.csd.oop.draw.JSONParser.JSONHandler;
import eg.edu.alexu.csd.oop.draw.Shape;
import eg.edu.alexu.csd.oop.draw.Shapes.Rectangle;
import eg.edu.alexu.csd.oop.draw.Shapes.*;
import eg.edu.alexu.csd.oop.firebase.FireBaseRoom;
import javafx.scene.canvas.Canvas;
import org.jfree.fx.FXGraphics2D;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class PaintEngine implements DrawingEngine {
    public static final Color boundingRectangleStroke = new Color(29, 93, 189, 255);
    public static final int IDLE = -1;
    public static final int DRAWING_LINE = 0;
    public static final int DRAWING_RECTANGLE = 1;
    public static final int DRAWING_CIRCLE = 2;
    public static final int DRAWING_ELLIPSE = 3;
    public static final int DRAWING_TRIANGLE = 4;
    public static final int DRAWING_ROUND_RECTANGLE = 5;
    public static List<Class<? extends eg.edu.alexu.csd.oop.draw.Shape>> supportedShapes = new LinkedList<>();
    public static final double EPS = 1;
    public double strokeSize = 0.5;
    public int shapeID = -1;
    public javafx.scene.paint.Color fillColor;
    public javafx.scene.paint.Color strokeColor;
    private FireBaseRoom fireBaseRoom;

    private FXGraphics2D fxGraphics2D = new FXGraphics2D((new Canvas()).getGraphicsContext2D());
    private ArrayList<eg.edu.alexu.csd.oop.draw.Shape> Shapes = new ArrayList<>();
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private Map<String, Boolean> existingShapes;
    public PaintEngine(Canvas canvas) {
        fxGraphics2D = new FXGraphics2D(canvas.getGraphicsContext2D());
        clearCanvas();
        supportedShapes.add(Line.class);
        supportedShapes.add(Rectangle.class);
        supportedShapes.add(Circle.class);
        supportedShapes.add(Ellipse.class);
        supportedShapes.add(Triangle.class);
        undoStack = new Stack<>();
        redoStack = new Stack<>();
        fillColor = new javafx.scene.paint.Color(0, 0, 0, 0);
        strokeColor = javafx.scene.paint.Color.BLACK;
        existingShapes = new HashMap<>();
        fireBaseRoom = new FireBaseRoom("", this);
    }

    public static String getShapeUniqueID(eg.edu.alexu.csd.oop.draw.Shape shape) {
        Field field = null;
        try {
            field = shape.getClass().getDeclaredField("uniqueID");
            field.setAccessible(true);
            return field.get(shape).toString();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void setFireBaseRoom(String UID) {
        clearCanvas();
        undoStack.clear();
        redoStack.clear();
        Shapes.clear();
        existingShapes.clear();
        fireBaseRoom.setRoomUID(UID);
    }

    public void unSetFireBaseRoom() {
        fireBaseRoom.leaveRoom();
        clearCanvas();
        undoStack.clear();
        redoStack.clear();
        Shapes.clear();
        existingShapes.clear();
    }

    public FireBaseRoom getFireBaseRoom() {
        return fireBaseRoom;
    }
    public Graphics getGraphics() {
        return fxGraphics2D;
    }

    public void addCommandToStack(Command command) {
        if (fireBaseRoom.isActive())
            return;
        redoStack.clear();
        undoStack.push(command);
    }

    private void clearCanvas() {
        java.awt.Rectangle rectangle = new java.awt.Rectangle(0, 0, 910, 544);
        fxGraphics2D.setColor(Color.white);
        fxGraphics2D.fill(rectangle);
        fxGraphics2D.setColor(Color.black);
    }

    static private eg.edu.alexu.csd.oop.draw.Shape makeShape(eg.edu.alexu.csd.oop.draw.Shape shape, int baseX, int baseY, Map<String, Double> propertiesMap, Color fillColor, Color color,
                                                             double strokeSize) {
        shape.setPosition(new Point(baseX, baseY));
        shape.setProperties(propertiesMap);
        Map<String, Double> currentMap = shape.getProperties();
        shape.setFillColor(fillColor);
        shape.setColor(color);
        try {
            shape.getClass().getMethod("setStrokeSize", new Class[]{double.class}).invoke(shape, (float) strokeSize);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return shape;
    }

    static public eg.edu.alexu.csd.oop.draw.Shape makeShape(int currentShape, int[] pointsX, int[] pointsY, Color fillColor, Color color, double strokeSize) throws IllegalAccessException, InstantiationException {
        ArrayList<Class<? extends eg.edu.alexu.csd.oop.draw.Shape>> currentClasses = new ArrayList<>(supportedShapes);
        Class<? extends eg.edu.alexu.csd.oop.draw.Shape> currentClass = currentClasses.get(currentShape);
        switch (currentShape) {
            case DRAWING_LINE:
                return makeShape(currentClass.newInstance(), pointsX[0], pointsY[0],
                        getPropertiesMap(new String[]{"XPosition", "YPosition"}, new Double[]{(double) pointsX[1], (double) pointsY[1]}),
                        fillColor, color, strokeSize);
            case DRAWING_RECTANGLE:
                return makeShape(currentClass.newInstance(), pointsX[0], pointsY[0],
                        getPropertiesMap(new String[]{"Width", "Height"}, new Double[]{(double) pointsX[1] - pointsX[0], (double) pointsY[1] - pointsY[0]}),
                        fillColor, color, strokeSize);
            case DRAWING_CIRCLE:
                return makeShape(currentClass.newInstance(), pointsX[0], pointsY[0],
                        getPropertiesMap(new String[]{"Radius"}, new Double[]{Math.sqrt(Math.pow(pointsX[0] - pointsX[1], 2) + Math.pow(pointsY[0] - pointsY[1], 2))}),
                        fillColor, color, strokeSize);
            case DRAWING_ELLIPSE:
                return makeShape(currentClass.newInstance(), pointsX[0], pointsY[0],
                        getPropertiesMap(new String[]{"Major", "Minor"},
                                new Double[]{(double) Math.abs(pointsX[1] - pointsX[0]), (double) Math.abs(pointsY[1] - pointsY[0])}),
                        fillColor, color, strokeSize);
            case DRAWING_TRIANGLE:
                return makeShape(currentClass.newInstance(), pointsX[0], pointsY[0],
                        getPropertiesMap(new String[]{"P2X", "P2Y", "P3X", "P3Y"},
                                new Double[]{(double) pointsX[1], (double) pointsY[1], (double) pointsX[2], (double) pointsY[2]}),
                        fillColor, color, strokeSize);
            case DRAWING_ROUND_RECTANGLE:
                return makeShape(currentClass.newInstance(), pointsX[0], pointsY[0],
                        getPropertiesMap(new String[]{"Width", "Length", "ArcWidth", "ArcLength"}, new Double[]{(double) pointsX[1] - pointsX[0], (double) pointsY[1] - pointsY[0], 25.0, 25.0}),
                        fillColor, color, strokeSize);
        }

        return null;
    }

    public void refresh(Graphics canvas) {
        clearCanvas();
        for (eg.edu.alexu.csd.oop.draw.Shape shape : Shapes)
            shape.draw(canvas);
    }

    public void addShape(eg.edu.alexu.csd.oop.draw.Shape shape) {
        String ShapeID = getShapeUniqueID(shape);
        if (existingShapes.containsKey(ShapeID))
            return;
        existingShapes.put(ShapeID, true);
        shape.draw(fxGraphics2D);
        Shapes.add(shape);
    }

    public void addShape(eg.edu.alexu.csd.oop.draw.Shape shape, boolean addToDB) {
        String ShapeID = getShapeUniqueID(shape);
        if (existingShapes.containsKey(ShapeID))
            return;
        if (addToDB && fireBaseRoom.isActive())
            fireBaseRoom.addShapeToDB(shape);
        existingShapes.put(ShapeID, true);
        shape.draw(fxGraphics2D);
        Shapes.add(shape);
    }

    public void removeShape(eg.edu.alexu.csd.oop.draw.Shape shape) {
        String ShapeID = getShapeUniqueID(shape);
        if (!existingShapes.containsKey(ShapeID))
            return;
        for (int i = 0; i < Shapes.size(); i++) {
            try {
                if ((Boolean) shape.getClass().getMethod("equalShape", new Class[]{eg.edu.alexu.csd.oop.draw.Shape.class}).invoke(shape, Shapes.get(i))) {
                    Shapes.remove(i);
                    existingShapes.remove(ShapeID);
                    if (fireBaseRoom.isActive())
                        fireBaseRoom.removeShapeFromDB(shape);

                    return;
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeShape(eg.edu.alexu.csd.oop.draw.Shape shape, boolean addToDB) {
        String ShapeID = getShapeUniqueID(shape);
        if (!existingShapes.containsKey(ShapeID))
            return;
        for (int i = 0; i < Shapes.size(); i++) {
            try {
                if ((Boolean) shape.getClass().getMethod("equalShape", new Class[]{eg.edu.alexu.csd.oop.draw.Shape.class}).invoke(shape, Shapes.get(i))) {
                    Shapes.remove(i);
                    if (addToDB && fireBaseRoom.isActive())
                        fireBaseRoom.removeShapeFromDB(shape);
                    existingShapes.remove(ShapeID);
                    return;
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateShape(eg.edu.alexu.csd.oop.draw.Shape oldShape, eg.edu.alexu.csd.oop.draw.Shape newShape) {
        for (int i = 0; i < Shapes.size(); i++) {
            try {
                if ((Boolean) oldShape.getClass().getMethod("equalShape", new Class[]{eg.edu.alexu.csd.oop.draw.Shape.class}).invoke(oldShape, Shapes.get(i))) {
                    if (fireBaseRoom.isActive())
                        fireBaseRoom.removeShapeFromDB(Shapes.get(i));
                    Shapes.remove(i);
                    Shapes.add(i, newShape);
                    if (fireBaseRoom.isActive())
                        fireBaseRoom.addShapeToDB(Shapes.get(i));
                    return;
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    static private Map<String, Double> getPropertiesMap(String[] args, Double[] vals) {
        Map<String, Double> propertiesMap = new HashMap<>();
        for (int i = 0; i < args.length; i++)
            propertiesMap.put(args[i], vals[i]);
        return propertiesMap;
    }

    public void undo() {
        if (undoStack.empty())
            return;
        shapeID = -1;
        Command command = undoStack.peek();
        undoStack.pop();
        redoStack.push(command);
        command.undoCommand();
    }

    public void redo() {
        if (redoStack.empty())
            return;
        shapeID = -1;
        Command command = redoStack.peek();
        redoStack.pop();
        undoStack.push(command);
        command.redoCommand();
    }

    public eg.edu.alexu.csd.oop.draw.Shape[] getShapes() {
        return Shapes.toArray(new eg.edu.alexu.csd.oop.draw.Shape[Shapes.size()]);
    }

    private void saveXML(String xml) {
        Document document;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.newDocument();
            Element rootEle = document.createElement("shapes");
            for (eg.edu.alexu.csd.oop.draw.Shape shape : Shapes) {
                Element element = document.createElement(shape.getClass().getSimpleName());
                Element position = document.createElement("Position");
                position.setAttribute("X", String.valueOf(shape.getPosition().getX()));
                position.setAttribute("Y", String.valueOf(shape.getPosition().getY()));
                element.appendChild(position);
                Element color = document.createElement("Color");
                color.setAttribute("red", String.valueOf(shape.getColor().getRed()));
                color.setAttribute("green", String.valueOf(shape.getColor().getGreen()));
                color.setAttribute("blue", String.valueOf(shape.getColor().getBlue()));
                color.setAttribute("alpha", String.valueOf(shape.getColor().getAlpha()));
                element.appendChild(color);
                if (shape.getFillColor() != null) {
                    Element fillColor = document.createElement("FillColor");
                    fillColor.setAttribute("red", String.valueOf(shape.getFillColor().getRed()));
                    fillColor.setAttribute("green", String.valueOf(shape.getFillColor().getGreen()));
                    fillColor.setAttribute("blue", String.valueOf(shape.getFillColor().getBlue()));
                    fillColor.setAttribute("alpha", String.valueOf(shape.getFillColor().getAlpha()));
                    element.appendChild(fillColor);
                }
                Element properties = document.createElement("Properties");
                Map<String, Double> propertiesMap = shape.getProperties();
                for (Map.Entry<String, Double> entry : propertiesMap.entrySet())
                    properties.setAttribute(entry.getKey(), String.valueOf(entry.getValue()));
                element.appendChild(properties);
                rootEle.appendChild(element);
            }
            document.appendChild(rootEle);
            try {
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                tr.transform(new DOMSource(document),
                        new StreamResult(new FileOutputStream(xml)));

            } catch (TransformerException | IOException te) {
                System.out.println(te.getMessage());
            }
        } catch (ParserConfigurationException pce) {
            System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
        }
    }

    public void save(String path) {
        if (path.substring(path.length() - 4).equals(".xml"))
            saveXML(path);
        else
            JSONHandler.save(Shapes, path);
    }

    public void load(String path) {
        if (path.substring(path.length() - 4).equals(".xml"))
            loadXML(path);
        else
            Shapes = JSONHandler.load(path,supportedShapes);
        refresh(fxGraphics2D);
    }

    public List<Class<? extends eg.edu.alexu.csd.oop.draw.Shape>> getSupportedShapes() {
        return supportedShapes;
    }

    @Override
    public void installPluginShape(String jarPath) {

        URLClassLoader child = null;
        try {
            child = new URLClassLoader(
                    new URL[]{Paths.get(jarPath).toUri().toURL()},
                    this.getClass().getClassLoader()
            );
            Class<? extends eg.edu.alexu.csd.oop.draw.Shape> classToLoad = (Class<? extends Shape>) Class.forName("eg.edu.alexu.csd.oop.draw.RoundRectangle", true, child);
            supportedShapes.add(classToLoad);
        } catch (MalformedURLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    static java.awt.Color toAwtColor(javafx.scene.paint.Color color) {
        if (color == null)
            return null;
        return new java.awt.Color((float) color.getRed(),
                (float) color.getGreen(),
                (float) color.getBlue(),
                (float) color.getOpacity());
    }

    private void loadXML(String xml) {
        Document document;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            document = db.parse(xml);
            document.getDocumentElement().normalize();
            Element element = document.getDocumentElement();
            NodeList nodeList = element.getChildNodes();
            Shapes.clear();
            refresh(fxGraphics2D);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node nNode = nodeList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    eg.edu.alexu.csd.oop.draw.Shape shape = null;
                    String className = nNode.getNodeName();
                    ArrayList<Class<? extends eg.edu.alexu.csd.oop.draw.Shape>> currentClasses = new ArrayList<>(supportedShapes);
                    for (Class<? extends Shape> currentClass : currentClasses) {
                        if (currentClass.getSimpleName().equals(className)) {
                            shape = currentClass.newInstance();
                            break;
                        }
                    }
                    for (int j = 0; j < nNode.getChildNodes().getLength(); j++) {
                        Node attributeNode = nNode.getChildNodes().item(j);
                        if (attributeNode.getNodeType() == Node.ELEMENT_NODE) {
                            switch (attributeNode.getNodeName()) {
                                case "Position":
                                    String P1 = attributeNode.getAttributes().getNamedItem("X").getNodeValue();
                                    String P2 = attributeNode.getAttributes().getNamedItem("Y").getNodeValue();
                                    shape.setPosition(new Point((int) Double.parseDouble(P1), (int) Double.parseDouble(P2)));
                                    break;
                                case "Color":
                                    String Red = attributeNode.getAttributes().getNamedItem("red").getNodeValue();
                                    String Green = attributeNode.getAttributes().getNamedItem("green").getNodeValue();
                                    String Blue = attributeNode.getAttributes().getNamedItem("blue").getNodeValue();
                                    String Alpha = attributeNode.getAttributes().getNamedItem("alpha").getNodeValue();
                                    shape.setColor(new Color((int) Double.parseDouble(Red), (int) Double.parseDouble(Green),
                                            (int) Double.parseDouble(Blue), (int) Double.parseDouble(Alpha)));
                                    break;
                                case "FillColor":
                                    Red = attributeNode.getAttributes().getNamedItem("red").getNodeValue();
                                    Green = attributeNode.getAttributes().getNamedItem("green").getNodeValue();
                                    Blue = attributeNode.getAttributes().getNamedItem("blue").getNodeValue();
                                    Alpha = attributeNode.getAttributes().getNamedItem("alpha").getNodeValue();
                                    shape.setFillColor(new Color((int) Double.parseDouble(Red), (int) Double.parseDouble(Green),
                                            (int) Double.parseDouble(Blue), (int) Double.parseDouble(Alpha)));
                                    break;
                                case "Properties":
                                    Map<String, Double> propertiesMap = new HashMap<>();
                                    NamedNodeMap namedNodeMap = attributeNode.getAttributes();
                                    for (int k = 0; k < namedNodeMap.getLength(); k++)
                                        propertiesMap.put(namedNodeMap.item(k).getNodeName(), Double.valueOf(namedNodeMap.item(k).getNodeValue()));
                                    shape.setProperties(propertiesMap);
                                    break;
                            }
                        }
                    }
                    addShape(shape);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
    }
}
