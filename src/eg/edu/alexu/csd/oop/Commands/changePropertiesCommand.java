package eg.edu.alexu.csd.oop.Commands;

import eg.edu.alexu.csd.oop.draw.BackEnd.PaintEngine;
import eg.edu.alexu.csd.oop.draw.Shape;

public class changePropertiesCommand implements Command {
    private Shape oldShape, newShape;
    private PaintEngine paintEngine;

    public changePropertiesCommand(PaintEngine _paintEngine, Shape oldShapeClone, Shape newShapeClone) {
        paintEngine = _paintEngine;
        oldShape = oldShapeClone;
        newShape = newShapeClone;
    }

    @Override
    public void undoCommand() {
        paintEngine.removeShape(newShape);
        try {
            paintEngine.addShape((Shape) oldShape.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        paintEngine.refresh(paintEngine.getGraphics());
    }

    @Override
    public void redoCommand() {
        paintEngine.removeShape(oldShape);
        try {
            paintEngine.addShape((Shape) newShape.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        paintEngine.refresh(paintEngine.getGraphics());
    }
}
