package eg.edu.alexu.csd.oop.draw.JSONParser;


import eg.edu.alexu.csd.oop.draw.Shape;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONHandler {
    public static void save(ArrayList<Shape> shapes, String p) {
        BufferedWriter output = null;
        try {
            File file = new File(p);
            output = new BufferedWriter(new FileWriter(file));
            output.write("{\n");
            for (int i = 0; i < shapes.size(); i++) {
                output.write("    \"");
                output.write(shapes.get(i).getClass().getSimpleName());
                output.write("\": {\n");
                output.write("        \"Position\": {\n");
                output.write("            \"X\": ");
                output.write(String.valueOf(shapes.get(i).getPosition().x));
                output.write(",\n");
                output.write("            \"Y\": ");
                output.write(String.valueOf(shapes.get(i).getPosition().y));
                output.write("\n");
                output.write("        },\n");
                output.write("        \"Color\": {\n");
                output.write("            \"red\": ");
                output.write(String.valueOf(shapes.get(i).getColor().getRed()));
                output.write(",\n");
                output.write("            \"green\": ");
                output.write(String.valueOf(shapes.get(i).getColor().getGreen()));
                output.write(",\n");
                output.write("            \"blue\": ");
                output.write(String.valueOf(shapes.get(i).getColor().getBlue()));
                output.write(",\n");
                output.write("            \"alpha\": ");
                output.write(String.valueOf(shapes.get(i).getColor().getAlpha()));
                output.write("\n");
                output.write("        },\n");
                output.write("        \"FillColor\": {\n");
                output.write("            \"red\": ");
                output.write(String.valueOf(shapes.get(i).getFillColor().getRed()));
                output.write(",\n");
                output.write("            \"green\": ");
                output.write(String.valueOf(shapes.get(i).getFillColor().getGreen()));
                output.write(",\n");
                output.write("            \"blue\": ");
                output.write(String.valueOf(shapes.get(i).getFillColor().getBlue()));
                output.write(",\n");
                output.write("            \"alpha\": ");
                output.write(String.valueOf(shapes.get(i).getFillColor().getAlpha()));
                output.write("\n");
                output.write("        },\n");
                output.write("        \"Properties\": {\n");
                Object[] x = shapes.get(i).getProperties().keySet().toArray();
                Object[] values = shapes.get(i).getProperties().values().toArray();
                for (int j = 0; j < shapes.get(i).getProperties().size(); j++) {
                    output.write("            \"");
                    output.write((String) x[j]);
                    output.write("\": ");
                    output.write(values[j].toString());
                    if (j == shapes.get(i).getProperties().size() - 1)
                        output.write("\n");
                    else
                        output.write(",\n");
                }
                output.write("        }\n");
                output.write("    }\n");
            }
            output.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Shape> load(String p, List<Class<? extends Shape>> currentClasses) {
        BufferedReader input = null;
        ArrayList<Shape> answer = new ArrayList<>();

        try {
            File file = new File(p);
            input = new BufferedReader(new FileReader(file));
            String line;
            Stack<String> s = new Stack<>();

            line = input.readLine();
            while ((line = input.readLine()) != null) {
                Shape current = null;
                for (Class<? extends Shape> currentClass : currentClasses) {
                    String comparator = "    \"" + currentClass.getSimpleName() + "\": {";
                    if (comparator.equals(line)) {
                        current = currentClass.newInstance();
                        break;
                    }
                }
                if(current == null)
                    continue;
                for (int i = 0; i < 4; i++) {
                    s.push(input.readLine());
                    ArrayList<String> attributes = new ArrayList<>();
                    ArrayList<Integer> values = new ArrayList<>();
                    String reg = "(?:\\s*)(?:\")([A-Z0-9a-z]*)(?:\": )(-?\\d*)(\\.?0?)(,?)";
                    while (!(line = input.readLine()).equals("        },") && !line.equals("        }")) {
                        Pattern pattern = Pattern.compile(reg);
                        Matcher matcher = pattern.matcher(line);
                        if(!matcher.matches()){
                            continue;
                        }
                        attributes.add(matcher.group(1));
                        values.add(Integer.parseInt(matcher.group(2)));
                    }
                    if (i == 0) {
                        Point x = new Point(values.get(0), values.get(1));
                        current.setPosition(x);
                    } else if (i == 1) {
                        Color x = new Color(values.get(0), values.get(1), values.get(2), values.get(3));
                        current.setColor(x);
                    } else if (i == 2) {
                        Color x = new Color(values.get(0), values.get(1), values.get(2), values.get(3));
                        current.setFillColor(x);
                    } else if (i == 3) {
                        HashMap<String, Double> x = new HashMap<>();
                        for (int j = 0; j < attributes.size(); j++) {
                            x.put(attributes.get(j), Double.valueOf(values.get(j)));
                        }
                        current.setProperties(x);
                    }
                }
                answer.add(current);
            }
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return answer;
    }

}