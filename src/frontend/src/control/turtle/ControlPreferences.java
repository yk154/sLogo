package control.turtle;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import parsing.BackgroundChangeListener;
import parsing.PenColorChangeListener;
import utils.StyleUtils;
import view.turtle.UIpaper;
import view.turtle.UIpreferences;

import javax.swing.text.Style;

import static utils.StyleUtils.colorToStyleString;
import static utils.StyleUtils.updateCssAttribute;

/**
 * ControlPreferences
 *
 * Control user preferable features
 *  - PaletteHandler, PenHandler, TurtleView icon Handler
 *
 * @author Amy Kim
 */

public class ControlPreferences implements BackgroundChangeListener, PenColorChangeListener {
    private UIpreferences ui;
    private UIpaper uiPaper;

    public ControlPreferences(UIpreferences ui, UIpaper uiPaper) {
        this.ui = ui;
        this.uiPaper = uiPaper;

        ui.getMyPalette().setOnAction(this::myPaletteHandler);
        ui.getMyPen().setOnAction(this::myPenHandler);
        ui.getTurtleChooser().getSelectionModel().selectedItemProperty().addListener((e, o, n) -> {
            this.uiPaper.getMyTurtleView(1).getIcon().setImage(UIpreferences.TURTLE_IMAGES.get(n));
        });
        ui.getPenSlider().setOnMouseMoved(this::myStrokeThicknessHandler);
    }

    private void myPaletteHandler(ActionEvent e) {
        var palette = ui.getMyPalette();
        var color = palette.getValue();
        var paper = uiPaper.getMyPaper();

        updateCssAttribute(palette, "-fx-background-color", colorToStyleString(color));
        updateCssAttribute(paper, "-fx-background-color", colorToStyleString(color));
    }

    private void myPenHandler(ActionEvent e) {
        var pen = ui.getMyPen();
        var color = pen.getValue();
        uiPaper.setMyColor(color);
        updateCssAttribute(pen, "-fx-background-color", colorToStyleString(color));
    }

    private void myStrokeThicknessHandler(MouseEvent e){
        var slider = ui.getPenSlider();
        uiPaper.setMyStrokeWidth(slider.getValue());
    }

    @Override
    public void notifyBackgroundChange(int colorIdx) {
        var palette = ui.getMyPalette();
        var paper = uiPaper.getMyPaper();
        updateCssAttribute(palette, "-fx-background-color", colorToStyleString(StyleUtils.colorByIndex(colorIdx)));
        updateCssAttribute(paper, "-fx-background-color", colorToStyleString(StyleUtils.colorByIndex(colorIdx)));
        palette.setValue(StyleUtils.colorByIndex(colorIdx));
    }

    @Override
    public void notifyPenColorChange(int idx) {
        var pen = ui.getMyPen();
        uiPaper.setMyColor(StyleUtils.colorByIndex(idx));
        pen.setValue(StyleUtils.colorByIndex(idx));
        updateCssAttribute(pen, "-fx-background-color", colorToStyleString(StyleUtils.colorByIndex(idx)));
    }
}
