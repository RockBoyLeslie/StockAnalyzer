package com.leslie.stock.analyzer.swing;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class MessageWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    public MessageWindow() {
        this.setSize(600, 300);
        this.setAlwaysOnTop(true);
        this.setFocusable(true);
        this.setFocusableWindowState(true);
    }

    public void popup(Object message) {
        JOptionPane.showMessageDialog(this, message);
    } 
}
