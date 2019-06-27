package interfaces.themesmanagement;

import interfaces.themestatistics.ThemesStatisticsDialog;
import interfaces.workshops.WorkshopsPanel;

import java.awt.BorderLayout;
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
import javax.swing.JLabel;
import javax.swing.JPanel;

import tools.components.Dialog;

import worldclasses.themes.Theme;
import worldclasses.themes.Tip;

public class ThemesManagementDialog extends Dialog {

    /* ATTRIBUTES ___________________________________________________________ */
    private ArrayList<Theme> themes;

    private JButton backButton;
    private JButton themesStatisticsButton;

    /* CONSTRUCTORS _________________________________________________________ */
    public ThemesManagementDialog() {
        
        this.themes = new ArrayList<>();

        this.initThemes();
        this.initComponents();
        this.initEvents();
    }

    /* METHODS ______________________________________________________________ */
    private void initComponents() {
        JPanel centerPanel;
        JPanel southPanel;

        JPanel labelsPanel;
        JPanel themesPanel;

        JPanel leftPanel;

        // Set up Dialog -------------------------------------------------------
        this.setLayout(new BorderLayout());
        this.setSize(500, 500);
        this.setMinimumSize(new Dimension(250, 250));
        this.setLocationRelativeTo(null);
        this.setTitle("Administrador de Temas");
        this.setResizable(true);

        // Set up Components ---------------------------------------------------
        this.backButton = new JButton("Volver");
        this.themesStatisticsButton = new JButton("Temas");

        centerPanel = new JPanel();
        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        labelsPanel = new JPanel(new GridLayout(1, 3));
        themesPanel = new JPanel();

        leftPanel = new JPanel(new BorderLayout());

        // ---------------------------------------------------------------------
        themesPanel.setLayout(new BoxLayout(themesPanel, BoxLayout.Y_AXIS));

        // ---------------------------------------------------------------------
        labelsPanel.add(new JLabel("Imagen", JLabel.CENTER));
        labelsPanel.add(new JLabel("Titulo", JLabel.CENTER));
        labelsPanel.add(new JLabel("Vistas", JLabel.CENTER));

        this.getThemes().forEach(i -> {
            themesPanel.add(new ThemeButton(i));
        });

        leftPanel.add(labelsPanel, BorderLayout.NORTH);
        leftPanel.add(themesPanel, BorderLayout.CENTER);

        southPanel.add(this.backButton);
        southPanel.add(this.themesStatisticsButton);

        this.add(leftPanel, BorderLayout.WEST);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    /* ______________________________________________________________________ */
    private void initEvents() {
        // Components Events ---------------------------------------------------
        this.backButton.addActionListener(ae -> {
            this.dispose();
        });

        this.themesStatisticsButton.addActionListener(ae -> {
            new ThemesStatisticsDialog().showDialog();
        });
    }

    /* ______________________________________________________________________ */
    private void initThemes() {
        String themesDirectoryPath = WorkshopsPanel.class.getResource("/tools").toString().substring(5);
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
                    this.getThemes().add(new Theme(
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
            String value = text.substring(start, end);

            start = text.indexOf('=', start) + 1;
            end = text.indexOf('\n', end + 1);
            String progress = text.substring(start, end);

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

    /* GETTERS ______________________________________________________________ */
    public ArrayList<Theme> getThemes() {
        return this.themes;
    }

    /* SETTERS ______________________________________________________________ */
    public void setThemes(ArrayList<Theme> themes) {
        this.themes = themes;
    }

    /* MAIN _________________________________________________________________ */
    public static void main(String[] args) {
        new ThemesManagementDialog().showTestDialog();
    }
}
