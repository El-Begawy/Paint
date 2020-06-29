package eg.edu.alexu.csd.oop.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import eg.edu.alexu.csd.oop.draw.BackEnd.PaintEngine;
import eg.edu.alexu.csd.oop.draw.BackEnd.ShapesHandler;
import eg.edu.alexu.csd.oop.draw.Shape;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class FireBaseRoom {
    private String roomUID;
    private FirebaseDatabase firebaseDatabase;
    private ChildEventListener shapesListener;
    private PaintEngine paintEngine;

    public FireBaseRoom(String UID, PaintEngine _paintEngine) {
        roomUID = UID;
        paintEngine = _paintEngine;
        initFireBase();
    }

    private void initFireBase() {
        FirebaseOptions options = null;
        try {
            InputStream serviceAccount = getClass().getResourceAsStream("serviceAccount.json");
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://paint-1d918.firebaseio.com/")
                    .build();
            FirebaseApp.initializeApp(options);
            firebaseDatabase = FirebaseDatabase.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRoomUID() {
        return roomUID;
    }

    public void setRoomUID(String roomUID) {
        this.roomUID = roomUID;
        listenForChanges();
    }

    public boolean isActive() {
        return !(roomUID.equals(""));
    }

    public void leaveRoom() {
        if (!isActive())
            return;
        firebaseDatabase.getReference().child(roomUID).removeEventListener(shapesListener);
        roomUID = "";
    }

    public void addShapeToDB(Shape shape) {
        try {
            firebaseDatabase.getReference().child(roomUID).child(PaintEngine.getShapeUniqueID(shape))
                    .setValue(shape.getClass().getMethod("toMap", new Class[]{}).invoke(shape), (databaseError, databaseReference) -> {
                    });
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void removeShapeFromDB(Shape shape) {
        firebaseDatabase.getReference().child(roomUID).child(PaintEngine.getShapeUniqueID(shape)).removeValue((databaseError, databaseReference) -> {

        });
    }

    private void listenForChanges() {
        shapesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //System.out.println(dataSnapshot.toString());
                Shape shape = ShapesHandler.makeShapeFromDB(dataSnapshot);
                Point position = new Point(Integer.parseInt(dataSnapshot.child("Position").child("X").getValue().toString()),
                        Integer.parseInt(dataSnapshot.child("Position").child("Y").getValue().toString()));
                shape.setPosition(position);
                int Red = Integer.parseInt(dataSnapshot.child("Color").child("Red").getValue().toString());
                int Blue = Integer.parseInt(dataSnapshot.child("Color").child("Blue").getValue().toString());
                int Green = Integer.parseInt(dataSnapshot.child("Color").child("Green").getValue().toString());
                int Alpha = Integer.parseInt(dataSnapshot.child("Color").child("Alpha").getValue().toString());
                shape.setColor(new Color(Red, Green, Blue, Alpha));
                Red = Integer.parseInt(dataSnapshot.child("FillColor").child("Red").getValue().toString());
                Blue = Integer.parseInt(dataSnapshot.child("FillColor").child("Blue").getValue().toString());
                Green = Integer.parseInt(dataSnapshot.child("FillColor").child("Green").getValue().toString());
                Alpha = Integer.parseInt(dataSnapshot.child("FillColor").child("Alpha").getValue().toString());
                shape.setFillColor(new Color(Red, Green, Blue, Alpha));
                HashMap<String, Double> propertiesMap = new HashMap<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("Properties").getChildren())
                    propertiesMap.put(dataSnapshot1.getKey(), Double.parseDouble(dataSnapshot1.getValue().toString()));
                shape.setProperties(propertiesMap);
                try {
                    shape.getClass().getMethod("setUniqueID", new Class[]{String.class}).invoke(shape, dataSnapshot.getKey());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                paintEngine.addShape(shape, false);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Shape shape = ShapesHandler.makeShapeFromDB(dataSnapshot);
                Point position = new Point(Integer.parseInt(dataSnapshot.child("Position").child("X").getValue().toString()),
                        Integer.parseInt(dataSnapshot.child("Position").child("Y").getValue().toString()));
                shape.setPosition(position);
                int Red = Integer.parseInt(dataSnapshot.child("Color").child("Red").getValue().toString());
                int Blue = Integer.parseInt(dataSnapshot.child("Color").child("Blue").getValue().toString());
                int Green = Integer.parseInt(dataSnapshot.child("Color").child("Green").getValue().toString());
                int Alpha = Integer.parseInt(dataSnapshot.child("Color").child("Alpha").getValue().toString());
                shape.setColor(new Color(Red, Green, Blue, Alpha));
                Red = Integer.parseInt(dataSnapshot.child("FillColor").child("Red").getValue().toString());
                Blue = Integer.parseInt(dataSnapshot.child("FillColor").child("Blue").getValue().toString());
                Green = Integer.parseInt(dataSnapshot.child("FillColor").child("Green").getValue().toString());
                Alpha = Integer.parseInt(dataSnapshot.child("FillColor").child("Alpha").getValue().toString());
                shape.setFillColor(new Color(Red, Green, Blue, Alpha));
                HashMap<String, Double> propertiesMap = new HashMap<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("Properties").getChildren())
                    propertiesMap.put(dataSnapshot1.getKey(), Double.parseDouble(dataSnapshot1.getValue().toString()));
                shape.setProperties(propertiesMap);
                try {
                    shape.getClass().getMethod("setUniqueID", new Class[]{String.class}).invoke(shape, dataSnapshot.getKey());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                paintEngine.removeShape(shape, false);
                paintEngine.refresh(paintEngine.getGraphics());
                paintEngine.addShape(shape, false);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Shape shape = ShapesHandler.makeShapeFromDB(dataSnapshot);
                Point position = new Point(Integer.parseInt(dataSnapshot.child("Position").child("X").getValue().toString()),
                        Integer.parseInt(dataSnapshot.child("Position").child("Y").getValue().toString()));
                shape.setPosition(position);
                int Red = Integer.parseInt(dataSnapshot.child("Color").child("Red").getValue().toString());
                int Blue = Integer.parseInt(dataSnapshot.child("Color").child("Blue").getValue().toString());
                int Green = Integer.parseInt(dataSnapshot.child("Color").child("Green").getValue().toString());
                int Alpha = Integer.parseInt(dataSnapshot.child("Color").child("Alpha").getValue().toString());
                shape.setColor(new Color(Red, Green, Blue, Alpha));
                Red = Integer.parseInt(dataSnapshot.child("FillColor").child("Red").getValue().toString());
                Blue = Integer.parseInt(dataSnapshot.child("FillColor").child("Blue").getValue().toString());
                Green = Integer.parseInt(dataSnapshot.child("FillColor").child("Green").getValue().toString());
                Alpha = Integer.parseInt(dataSnapshot.child("FillColor").child("Alpha").getValue().toString());
                shape.setFillColor(new Color(Red, Green, Blue, Alpha));
                HashMap<String, Double> propertiesMap = new HashMap<>();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.child("Properties").getChildren())
                    propertiesMap.put(dataSnapshot1.getKey(), Double.parseDouble(dataSnapshot1.getValue().toString()));
                shape.setProperties(propertiesMap);
                try {
                    shape.getClass().getMethod("setUniqueID", new Class[]{String.class}).invoke(shape, dataSnapshot.getKey());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                paintEngine.removeShape(shape, false);
                paintEngine.refresh(paintEngine.getGraphics());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.toException().printStackTrace();
            }
        };
        firebaseDatabase.getReference().child(roomUID).addChildEventListener(shapesListener);
    }
}
