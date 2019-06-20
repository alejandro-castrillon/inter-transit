package interfaces.themestatistics;

import interfaces.workshops.WorkshopsFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import utilities.Dialog;
import worldclasses.themes.Theme;
import worldclasses.themes.Tip;

public class ThemesStatisticsDialog extends Dialog {

    /* ATRRIBUTES ___________________________________________________________ */
    private final ArrayList<Theme> themes;

    private JButton backButton;

    /* CONSRUCTORS __________________________________________________________ */
    public ThemesStatisticsDialog() {
        super(new JFrame(), true);
        this.themes = new ArrayList<>();

        this.initThemes();
        this.initComponents();
        this.initEvents();
    }

    /* METHODS ______________________________________________________________ */
    private void initComponents() {
        ArrayList<Integer> views;
        ArrayList<String> names;
        ArrayList<Color> colors;

        JPanel labelsPanel;
        JPanel themesPanel;

        Pie viewsPie;
        PieInformation viewsInfo;
        JPanel viewsPanel;

        JPanel centerPanel;
        JPanel westPanel;
        JPanel southPanel;

        // Set up Dialog -------------------------------------------------------
        this.setLayout(new BorderLayout());
        this.setSize(700, 500);
        this.setLocationRelativeTo(null);
        this.setMinimumSize(new Dimension(300, 200));
        this.setTitle("Inter Transit");

        // Set up Components ---------------------------------------------------
        this.backButton = new JButton("Volver");

        views = new ArrayList<>();
        names = new ArrayList<>();
        colors = new ArrayList<>();

        labelsPanel = new JPanel(new GridLayout(1, 3));
        themesPanel = new JPanel();

        viewsPie = new Pie();
        viewsInfo = new PieInformation(names, colors);
        viewsPanel = new JPanel(new BorderLayout());

        centerPanel = new JPanel(new GridLayout());
        westPanel = new JPanel(new BorderLayout());
        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // ---------------------------------------------------------------------
        themesPanel.setLayout(new BoxLayout(themesPanel, BoxLayout.Y_AXIS));

        for (int i = 0; i < themes.size(); i++) {
            Theme theme = themes.get(i);
            Integer view = theme.getViews();
            Color color = viewsPie.addValue(view);
            views.add(view);
            viewsInfo.addSomething(theme.getTitle(), color);
        }

        // ---------------------------------------------------------------------
        labelsPanel.add(new JLabel("Imagen", JLabel.CENTER));
        labelsPanel.add(new JLabel("Titulo", JLabel.CENTER));
        labelsPanel.add(new JLabel("Vistas", JLabel.CENTER));

        this.themes.forEach(i -> {
            themesPanel.add(new ThemeButton(i));
        });

        westPanel.add(labelsPanel, BorderLayout.NORTH);
        westPanel.add(themesPanel, BorderLayout.CENTER);

        viewsPanel.add(new JLabel("Vistas", JLabel.CENTER), BorderLayout.NORTH);
        viewsPanel.add(viewsPie, BorderLayout.CENTER);
        viewsPanel.add(viewsInfo, BorderLayout.EAST);

        centerPanel.add(viewsPanel);
        southPanel.add(this.backButton);

        this.add(westPanel, BorderLayout.WEST);
        this.add(centerPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    /* ______________________________________________________________________ */
    private void initEvents() {
        // Components Events ---------------------------------------------------
        this.backButton.addActionListener(ae -> {
            this.dispose();
        });
    }

    /* ______________________________________________________________________ */
    private void initThemes() {
        String themesDirectoryPath = WorkshopsFrame.class.getResource("/utilities").toString().substring(5);
        File themesDirectory;

        Object[] description = null;
        ArrayList<Tip> tips;
        String fileName;

        themesDirectoryPath = themesDirectoryPath.substring(0, themesDirectoryPath.indexOf("build")) + "src/files";
        themesDirectory = new File(themesDirectoryPath);

        if (themesDirectory.exists()) {
            for (File themeDirectory : themesDirectory.listFiles()) {

                tips = new ArrayList<>();
                for (File themeFile : themeDirectory.listFiles()) {

                    fileName = themeFile.getName();
                    if (fileName.contains("descripcion")) {

                        description = this.getDescription(
                                themesDirectoryPath + "/"
                                + themeDirectory.getName() + "/descripcion.txt");
                    } else {
                        tips.add(new Tip(
                                fileName.substring(0, fileName.indexOf(".txt")),
                                this.getFileText(themeFile)
                        ));
                    }
                }
                if (description != null) {
                    this.themes.add(new Theme(
                            null,
                            themeDirectory.getName(),
                            description[0] + "",
                            tips,
                            (int) description[1],
                            (double) description[2],
                            (int) description[3]
                    ));
                }
            }
        }
        for (Theme theme : this.themes) {
            System.out.println(theme);
        }
    }

    /* ______________________________________________________________________ */
    private Object[] getDescription(String themeDirectoryPath) {
        File descriptionFile = new File(themeDirectoryPath);

        if (descriptionFile.exists()) {
            String text = this.getFileText(descriptionFile);

            int start = text.indexOf('=') + 1;
            int end = text.indexOf('\n');
            String description = text.substring(start, end);

            start = text.indexOf('=', start) + 1;
            end = text.indexOf('\n', end + 1);
            String progress = text.substring(start, end);

            start = text.indexOf('=', start) + 1;
            end = text.indexOf('\n', end + 1);
            String value = text.substring(start, end);

            start = text.indexOf('=', start) + 1;
            end = text.indexOf('\n', end + 1);
            String views = text.substring(start, end);

            return new Object[]{
                description,
                Integer.parseInt(progress),
                Double.parseDouble(value),
                Integer.parseInt(views)
            };
        } else {
            System.out.println("description file do not exists");
        }
        return null;
    }

    /* ______________________________________________________________________ */
    private String getFileText(File file) {
        String text = "";
        String line;
        BufferedReader bufferedReader;

        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            line = bufferedReader.readLine();

            while (line != null) {
                text += line;
                line = bufferedReader.readLine();
                if (line != null) {
                    text += '\n';
                }
            }
            bufferedReader.close();

        } catch (IOException e) {
        }
        return text;
    }

    /* MAIN _________________________________________________________________ */
    public static void main(String[] args) {
        new ThemesStatisticsDialog().showDialog();
    }
}