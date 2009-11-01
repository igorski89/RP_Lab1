import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: igorevsukov
 * Date: Oct 25, 2009
 * Time: 10:45:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainClass {
    public static void main(String[] args) {
    	MainFrame mainWindow = new MainFrame();
    	mainWindow.setLocation(100, 100);
    	mainWindow.setSize(1000, 600);
    	mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	mainWindow.setVisible(true);

//        try {
//            System.setProperty("apple.laf.useScreenMenuBar", "true");
//            System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Random Process Lab 1");
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        }
//        catch (ClassNotFoundException e) {
//            System.out.println("ClassNotFoundException: " + e.getMessage());
//        }
//        catch (InstantiationException e) {
//            System.out.println("InstantiationException: " + e.getMessage());
//        }
//        catch (IllegalAccessException e) {
//            System.out.println("IllegalAccessException: " + e.getMessage());
//        }
//        catch (UnsupportedLookAndFeelException e) {
//            System.out.println("UnsupportedLookAndFeelException: " + e.getMessage());
//        }
//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                MainFrame mainWindow = new MainFrame();
//                mainWindow.setLocation(100, 100);
//                mainWindow.setSize(1000, 600);
//                mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//                mainWindow.setVisible(true);
//            }
//        });
    }
}
