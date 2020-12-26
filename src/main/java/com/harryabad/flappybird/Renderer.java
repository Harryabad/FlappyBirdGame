package com.harryabad.flappybird;

import javax.swing.*;
import java.awt.*;

public class Renderer extends JPanel {

    /*
    The Renderer class is used to help with double buffering
    Instead of drawing objects one by one, you draw on an image then
    tell the renderer to draw the entire image.
     */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // calls code from parent of class before calling below

        FlappyBird.flappyBird.repaint(g);

    }
}
