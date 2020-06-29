package eg.edu.alexu.csd.oop.draw.BackEnd;

import eg.edu.alexu.csd.oop.draw.Shape;

import java.awt.*;

public interface DrawingEngine {

    /* redraw all shapes on the canvas */
    public void refresh(Graphics canvas);

    public void addShape(eg.edu.alexu.csd.oop.draw.Shape shape);

    public void removeShape(eg.edu.alexu.csd.oop.draw.Shape shape);

    public void updateShape(eg.edu.alexu.csd.oop.draw.Shape oldShape, eg.edu.alexu.csd.oop.draw.Shape newShape);

    /* return the created shapes objects */
    public eg.edu.alexu.csd.oop.draw.Shape[] getShapes();

    /* return the classes (types) of supported shapes already exist and the
     * ones that can be dynamically loaded at runtime (see Part 3) */
    public java.util.List<Class<? extends Shape>> getSupportedShapes();

    public void installPluginShape(String jarPath);

    /* limited to 20 steps. Only consider in undo & redo
     * these actions: addShape, removeShape, updateShape */
    public void undo();

    public void redo();

    /* use the file extension to determine the type,
     * or throw runtime exception when unexpected extension */
    public void save(String path);

    public void load(String path);
}