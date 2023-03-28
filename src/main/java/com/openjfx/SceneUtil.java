package com.openjfx;

import javafx.scene.control.TextFormatter;

import java.util.function.UnaryOperator;

public final class SceneUtil {
    public static final UnaryOperator<TextFormatter.Change> numberFormatter = change -> {
        String potentialNewVal = change.getControlNewText();
        if (!potentialNewVal.matches("\\d{0,3}(,\\d{0,2})?")) {
            System.out.println("\"" + potentialNewVal + "\" hat das falsche Format.");
            return null;
        }
        if (potentialNewVal.startsWith(",")) {
            change.setText('0' + change.getText());
            // System.out.println("Caret-Position:" + change.getCaretPosition());
            // System.out.println("Anchor-Position:" + change.getAnchor() + "\n");
            change.setCaretPosition(change.getCaretPosition()+1);
            change.setAnchor(change.getAnchor()+1);
            // change.setRange(change.getRangeStart(), change.getRangeEnd()+1);
            return change;
        }

        // Entferne führende Nullen
        if (potentialNewVal.startsWith("0") && !potentialNewVal.startsWith("0,")) {
            int s = potentialNewVal.length();
            int i = 1;
            while(i < s && potentialNewVal.charAt(i) == '0')
                i++;
            if (i >= s)
                change.setText("0");
            else
                change.setText(potentialNewVal.substring(i)); // Erster Index ohne Null

            change.setRange(0, change.getControlText().length());
        }

        return change;
    };

    private SceneUtil() { }
}
