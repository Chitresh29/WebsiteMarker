package view;

import model.IllegalOperationException;
import model.Model;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Nathan on 22/11/2017.
 */
public class UI implements Observer {

    Model model;
    Controller controller;
    JFileChooser chooser = new JFileChooser();
    JTabbedPane tabbedPane = new JTabbedPane();

    //Swing related
    public static final int WIDTH = 700;
    public static final int HEIGHT = 525;
    JFrame frame = new JFrame();

    //Panes & frames
    OptionsPane optionsPane;
    ResultsPane resultsPane;
    NeedsAttentionPane unmarkablePane;
    CSVJframe csvJframe;

    public UI(Model model) {
        this.model = model;
        model.addObserver(this);
        controller = new Controller(model);

        initializeFrame();
        initializeMenu();

        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        optionsPane = new OptionsPane(model, controller, frame);
        resultsPane = new ResultsPane(model, frame, controller);
        unmarkablePane = new NeedsAttentionPane(model);

        tabbedPane.addTab("Options", optionsPane);
        tabbedPane.addTab("Results", resultsPane);
        tabbedPane.add("Requires Attention", unmarkablePane);
        frame.add(tabbedPane);
    }

    private void initializeFrame() {
        frame = new JFrame("INFO101: Website Marker");
        frame.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        ImageIcon img = new ImageIcon("src/assets/tick.png");
        frame.setIconImage(img.getImage());

        //Position in middle
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - WIDTH / 2, dim.height / 2 - HEIGHT / 2);

        frame.pack();
        frame.setVisible(true);
    }

    private void initializeMenu() {
        JMenuBar menuBar;
        JMenu menu1;
        JMenu menu2;
        JMenu menu3;
        JMenuItem menuItem;
        menuBar = new JMenuBar();

        menu1 = new JMenu("File");
        menu2 = new JMenu("Help");
        menu3 = new JMenu("Generate");
        menuBar.add(menu1);
        menuBar.add(menu3);
        menuBar.add(menu2);

        frame.setJMenuBar(menuBar);

        menuItem = new JMenuItem("Open");
        menuItem.addActionListener(e -> {
            model.closeFiles();
            chooser.showOpenDialog(frame);
            try {
                controller.loadFolders(chooser.getSelectedFiles());
            } catch (IllegalOperationException exception) {
                displayError(this.frame, exception);
            }
        });
        menu1.add(menuItem);

        menuItem = new JMenuItem("Load Config");
        menuItem.addActionListener(e -> {
            JFileChooser configChooser = new JFileChooser();
            configChooser.setMultiSelectionEnabled(false);
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Config File", "ini");
            configChooser.setFileFilter(filter);
            configChooser.showOpenDialog(frame);
            try {
                controller.loadConfig(configChooser.getSelectedFile());
            } catch (IllegalOperationException exception) {
                displayError(this.frame, exception);
            }
        });
        menu1.add(menuItem);

        menuItem = new JMenuItem("Close");
        menuItem.addActionListener(e -> {
            controller.closeFiles();
        });
        menu1.add(menuItem);

        menuItem = new JMenuItem("About");
        menuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame,
                    "Build 1.1\nAuthor: Nathan Devery",
                    "About",
                    JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon("src/assets/victoriaLogo.png"));
        });
        menu2.add(menuItem);

        menuItem = new JMenuItem("Support");
        menuItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame,
                    "Email:\ninsertEmail@gmail.com",
                    "Support",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        menu2.add(menuItem);

        menuItem = new JMenuItem("CSV");
        menuItem.addActionListener(e -> {
            try {
                this.csvJframe = new CSVJframe(model, controller);
            } catch (IllegalOperationException exception) {
                displayError(frame, exception);
            }
        });
        menu3.add(menuItem);

        menuItem = new JMenuItem("Config File");
        menuItem.addActionListener(e -> {
            //TODO: implement
        });
        menu3.add(menuItem);
    }

    public static void displayError(JFrame frame, Exception e) {
        JOptionPane.showMessageDialog(frame, e.getMessage(), "Operation Error", JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void update(Observable o, Object arg) {
        resultsPane.redraw();
        unmarkablePane.redraw();
        if (csvJframe != null) csvJframe.redraw();
    }
}
