package utils;

import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * StyleUtils
 *
 * This class is utilites for styling
 *  - css update, convert color to string of rgb
 */

public class StyleUtils {

    //Index for setBG/setPencolor with commands
    private static List<Color> INDEXED_COLORS = List.of(
            Color.RED,
            Color.ORANGE,
            Color.YELLOW,
            Color.GREEN,
            Color.BLUE,
            Color.PURPLE,
            Color.FUCHSIA
    );

    public static Color colorByIndex(int idx) { return INDEXED_COLORS.get(idx); }

    public static void updateCssAttribute(Node node, String key, String value) {
        node.setStyle(
                node.getStyle().replaceAll(key+":.*;", "") + key + ": " + value + ";"
        );
        System.out.println(node.getStyle());
    }

    public static String colorToStyleString(Color c) {
        return "rgb(" + c.getRed()*255 + ", "  + c.getGreen()*255 + ", " + c.getBlue()*255 + ")";
    }
}
