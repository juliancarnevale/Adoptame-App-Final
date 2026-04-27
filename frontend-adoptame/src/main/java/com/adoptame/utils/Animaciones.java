package com.adoptame.utils;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animaciones {
    public static void animarVista(Node nodo) {
        FadeTransition ft = new FadeTransition(Duration.millis(500), nodo);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        
        TranslateTransition tt = new TranslateTransition(Duration.millis(500), nodo);
        tt.setFromY(20);
        tt.setToY(0);
        
        ft.play();
        tt.play();
    }
}