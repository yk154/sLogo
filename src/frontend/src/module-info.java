module frontend {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires java.xml;
    requires java.desktop;

//    exports view.Interpreter;
//    exports view.turtle;
//    exports Paper;

    requires backend;
    exports gui;
}