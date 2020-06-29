package eg.edu.alexu.csd.oop.Commands;

import eg.edu.alexu.csd.oop.draw.BackEnd.PaintEngine;
import eg.edu.alexu.csd.oop.draw.Shape;

public class makeShapeCommand implements Command {
    private Shape shape;
    private PaintEngine paintEngine;

    public makeShapeCommand(PaintEngine _paintEngine, Shape shapeClone) {
        paintEngine = _paintEngine;
        shape = shapeClone;
    }

    @Override
    public void undoCommand() {
        paintEngine.removeShape(shape);
        paintEngine.refresh(paintEngine.getGraphics());
    }

    @Override
    public void redoCommand() {
        try {
            paintEngine.addShape((Shape) shape.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        paintEngine.refresh(paintEngine.getGraphics());
    }
}
