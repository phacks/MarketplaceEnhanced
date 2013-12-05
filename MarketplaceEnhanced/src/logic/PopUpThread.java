package logic;

import javax.swing.JOptionPane;

class PopUpThread extends Thread {

    public String text;

    public PopUpThread(String text) {
        super();
        this.text = text;
    }

    public void run() {
        JOptionPane.showMessageDialog(null, text);
    }

}
