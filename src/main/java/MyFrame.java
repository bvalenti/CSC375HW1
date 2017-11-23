import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.Exchanger;

//public class MyFrame extends JPanel {
public class MyFrame extends javax.swing.JFrame {

    volatile int activeThreadCount = 16;
    GridBagConstraints gbc;
    int numThreads = 16;
    RoomSolver solvers[] = new RoomSolver[numThreads];
    RoomPrinter painters[] = new RoomPrinter[numThreads];
    int solns = 30;
    int crossOver = (int) (solns*0.6);
    int swaps = 256;
    double mutation = 0.0005;
    Object lock1 = new Object(), lock2 = new Object();
    Exchanger<ArrayList<Solution>> exchanger = new Exchanger<>();
    Room room = new Room(16, 16);


    public MyFrame() {
        room.initRoom();
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {
    }

    private void addComponents(Container pane) {
        pane.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        int count = 0;
        for (int j = 0; j < numThreads/8; j++) {
            for (int i = 0; i < 8; i++) {
                gbc.fill = GridBagConstraints.BOTH;
                gbc.gridx = i; gbc.gridy = j;
                gbc.weightx = 1.0;
                gbc.weighty = 0.5;
                gbc.insets = new Insets(0,0,0,0);
                if (j == 0 && i == 0) {
                    gbc.insets = new Insets(30,30,0,0);
                } else if (j == 0) {
                    gbc.insets = new Insets(30,0,0,0);
                } else if (i == 0) {
                    gbc.insets = new Insets(0,30,0,0);
                }

                painters[count] = new RoomPrinter();
                painters[count].setRoom(room);
                painters[count].printerRepaint();
                solvers[count] = new RoomSolver(room, painters[count], swaps, solns, exchanger, crossOver, mutation, lock1, lock2, this);
                pane.add(painters[count], gbc);
                System.out.println(count);
                count++;
                if (count == numThreads) break;
            }
        }
    }


    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MyFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MyFrame frame = new MyFrame();
                frame.setSize(2000,1000);
                frame.addComponents(frame.getContentPane());
//                frame.pack();
                frame.setVisible(true);
                frame.startThreads();
            }
        });
    }

    public void startThreads() {
        for (int i = 0; i < numThreads; i++) {
            new Thread(solvers[i]).start();
        }
    }
}
