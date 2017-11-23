import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public final class RoomPrinter extends JPanel {

    private Room current;
    private JFrame frame;
    int count;

    RoomPrinter() {
//        current = tmp;
//        frame = inFrame;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Font font = new Font("SansSerif", Font.BOLD, 20);
        Color c;
        String toDraw = "";
        for (int i = 0; i < current.width; i++) {
            for (int j = 0; j < current.length; j++) {
                if (current.seats.get(i).get(j).Affinity == 1) {
                    c = new Color(255,255,255);
                    g.setColor(c);
                    toDraw = "1";
                } else if (current.seats.get(i).get(j).Affinity == 2) {
                    c = new Color(255,230,230);
                    g.setColor(c);
                    toDraw = "2";
                } else if (current.seats.get(i).get(j).Affinity == 3) {
                    c = new Color(255,200,200);
                    g.setColor(c);
                    toDraw = "3";
                } else if (current.seats.get(i).get(j).Affinity == 4) {
                    c = new Color(255,170,170);
                    g.setColor(c);
                    toDraw = "4";
                } else if (current.seats.get(i).get(j).Affinity == 5) {
                    c = new Color(255,140,140);
                    g.setColor(c);
                    toDraw = "5";
                } else if (current.seats.get(i).get(j).Affinity == 6) {
                    c = new Color(255,110,110);
                    g.setColor(c);
                    toDraw = "6";
                } else if (current.seats.get(i).get(j).Affinity == 7) {
                    c = new Color(255,80,80);
                    g.setColor(c);
                    toDraw = "7";
                } else if (current.seats.get(i).get(j).Affinity == 8) {
                    c = new Color(255,50,50);
                    g.setColor(c);
                    toDraw = "8";
                } else if (current.seats.get(i).get(j).Affinity == 9) {
                    c = new Color(255,20,20);
                    g.setColor(c);
                    toDraw = "9";
                } else if (current.seats.get(i).get(j).Affinity == 10) {
                    c = new Color(255,0,0);
                    g.setColor(c);
                    toDraw = "10";
                }
                g.fillRect(10*i, 10*j, 10, 10);
                g.setColor(Color.BLACK);
//                g.setFont(font);
//                g.drawString(toDraw,30*i, 30*j);
            }
        }
    }


    public void paintRoom(Room toPaint) {
        current = toPaint;
        printerRepaint();
//        frame.setSize(current.width*300,current.length*300);
//        frame.getContentPane().add(new RoomPrinter());
//        frame.setLocationRelativeTo(null);
//        frame.setBackground(Color.LIGHT_GRAY);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setVisible(true);
    }

    public void printerRepaint() {
        validate();
        repaint();
    }

//    public void printerRepaint() {
//        frame.getContentPane().validate();
//        frame.getContentPane().repaint();
//    }

    public synchronized void setRoom(Room rm) {current = rm;}
}
