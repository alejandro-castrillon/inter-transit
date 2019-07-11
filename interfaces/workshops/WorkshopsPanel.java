package interfaces.workshops;

import interfaces.createtheme.CreateThemeDialog;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import tools.Pair;
import tools.Tools;
import tools.components.DialogPane;
import tools.components.Panel;
import tools.filemanager.BinaryFileManager;
import tools.filemanager.PlainFileManager;

import worldclasses.Settings;
import worldclasses.themes.Tip;
import worldclasses.accounts.Account;
import worldclasses.accounts.AdminAccount;
import worldclasses.accounts.UserAccount;
import worldclasses.themes.Theme;

public class WorkshopsPanel extends Panel {

    /* ATTRIBUTES ___________________________________________________________ */
    private static final long serialVersionUID = -2890938347215601719L;

    private Account account;
    private ArrayList<Theme> themes;

    private AccountButton accountButton;
    private JTree themesTree;
    private JPanel tipPanel;

    private JButton backButton;
    private JButton createButton;
    private JButton removeButton;

    /* CONSTRUCTORS _________________________________________________________ */
    public WorkshopsPanel(Account account) {
        this.account = account;
        this.themes = new ArrayList<>();

        this.initComponents();
        this.initEvents();
    }

    /* METHODS ______________________________________________________________ */
    private void initComponents() {
        JScrollPane treeScrollPane;

        JPanel westPanel;
        JPanel buttonsPanel;
        JPanel southPanel;

        boolean isAdmin;

        // Set up Panel --------------------------------------------------------
        this.setLayout(new BorderLayout());

        // Set up Components ---------------------------------------------------
        this.accountButton = new AccountButton(this.getAccount());
        this.themesTree = new JTree(this.initTree());

        westPanel = new JPanel(new BorderLayout());
        this.tipPanel = new JPanel();

        this.backButton = new JButton("Volver");
        this.createButton = new JButton("Nuevo");
        this.removeButton = new JButton("Eliminar");

        treeScrollPane = new JScrollPane(this.themesTree);

        buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // ---------------------------------------------------------------------
        this.themesTree.setCellRenderer(new DefaultTreeCellRenderer() {
            private static final long serialVersionUID = 5756325787925811601L;

            @Override
            public Component getTreeCellRendererComponent(JTree t, Object v, boolean s, boolean e, boolean l, int r, boolean h) {
                super.getTreeCellRendererComponent(t, v, s, e, l, r, h);

                Settings settings = Settings.getCurrentSettings();
                setFont(settings.getFont());

                String theme = settings.getTheme();
                if (theme.equals(Settings.LIGHT_THEME)) {
                    setForeground(Color.black);
                } else if (theme.equals(Settings.DARK_THEME)) {
                    setForeground(Color.white);
                }
                return this;
            }
        });

        isAdmin = this.getAccount() instanceof AdminAccount;
        this.createButton.setVisible(isAdmin);
        this.removeButton.setVisible(isAdmin);

        // ---------------------------------------------------------------------
        buttonsPanel.add(this.removeButton);
        buttonsPanel.add(this.createButton);

        westPanel.add(this.accountButton, BorderLayout.NORTH);
        westPanel.add(treeScrollPane, BorderLayout.CENTER);
        westPanel.add(buttonsPanel, BorderLayout.SOUTH);

        southPanel.add(this.backButton);

        this.add(westPanel, BorderLayout.WEST);
        this.add(this.tipPanel, BorderLayout.CENTER);
        this.add(southPanel, BorderLayout.SOUTH);
    }

    /* ______________________________________________________________________ */
    private void initEvents() {
        // Components Events ---------------------------------------------------
        this.accountButton.addActionListener(ae -> {
            this.setAccountAction();
        });

        this.themesTree.addTreeSelectionListener((TreeSelectionEvent tse) -> {
            this.showTip(tse);
        });

        this.createButton.addActionListener(ae -> {
            this.createTheme();
        });

        this.removeButton.addActionListener(ae -> {
            this.removeTheme();
        });
    }

    /* ______________________________________________________________________ */
    private DefaultTreeModel initTree() {
        DefaultMutableTreeNode root;
        DefaultTreeModel defaultTreeModel;

        String themesDirectoryPath;
        File themesDirectory;

        // ---------------------------------------------------------------------
//        themesDirectoryPath = Settings.getResource() + "/src/docs";
        themesDirectoryPath = Tools.getResource("/docs");
        System.out.println("ddd" + themesDirectoryPath);

        themesDirectory = new File(themesDirectoryPath);

        // ---------------------------------------------------------------------
        root = new DefaultMutableTreeNode("Documentos");
        defaultTreeModel = new DefaultTreeModel(root);

        // ---------------------------------------------------------------------
        if (themesDirectory.exists()) {
            for (File listFile : themesDirectory.listFiles()) {
                root.add(this.initFiles(listFile.getAbsolutePath()));
            }
        }

        this.setThemes(this.initTheme(themesDirectoryPath).getFiles());

        return defaultTreeModel;
    }

    /* ______________________________________________________________________ */
    private DefaultMutableTreeNode initFiles(String path) {
        File file = new File(path);
        DefaultMutableTreeNode defaultMutableTreeNode;
        defaultMutableTreeNode = new DefaultMutableTreeNode(
                file.getName().replace(".txt", "")
        );

        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                if (!listFile.getName().equals("descripcion.txt")) {
                    defaultMutableTreeNode.add(this.initFiles(
                            listFile.getAbsolutePath()
                    ));
                }
            }
        }
        return defaultMutableTreeNode;
    }

    /* ______________________________________________________________________ */
    private Theme initTheme(String path) {
        File file = new File(path);
        Object[] themeData;
        Theme theme;

        try {
            themeData = this.getThemeData(path + "/descripcion.txt");
            theme = new Theme(
                    null,
                    file.getName().replace(".txt", ""),
                    themeData[0] + "",
                    Double.parseDouble(themeData[1] + ""),
                    Integer.parseInt(themeData[2] + ""),
                    new ArrayList<>()
            );
        } catch (FileNotFoundException | NumberFormatException e) {
            if (file.isDirectory()) {
                theme = new Theme(file.getName(), "");
            } else {
                theme = new Tip(file.getName(), Tools.getFileText(file));
            }
        }

        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                if (!listFile.getName().equals("descripcion.txt")) {
                    theme.getFiles().add(this.initTheme(
                            listFile.getAbsolutePath()
                    ));
                }
            }
        }
        return theme;
    }

    /* ______________________________________________________________________ */
    private Object[] getThemeData(String themeDirectoryPath) throws FileNotFoundException {
        File descriptionFile = new File(themeDirectoryPath);

        if (descriptionFile.exists()) {
            String text = Tools.getFileText(descriptionFile);

            int start = text.indexOf('=') + 1;
            int end = text.indexOf('\n');
            String description = text.substring(start, end);

            start = text.indexOf('=', start) + 1;
            end = text.indexOf('\n', end + 1);
            String value = text.substring(start, end);

            start = text.indexOf('=', start) + 1;
            end = text.indexOf('\n', end + 1);
            String views = text.substring(start, end);

            return new Object[]{
                description,
                Double.parseDouble(value),
                Integer.parseInt(views)
            };
        } else {
            System.out.println("description file do not exists");
            throw new FileNotFoundException("description file do no exists");
        }
    }

    /* ______________________________________________________________________ */
    private void setAccountAction() {
        String themeTitle;
        String tipTitle;
        boolean isAdmin;

        this.accountButton.accountAction();
        this.setAccount(this.accountButton.getAccount());

        if (this.tipPanel instanceof TipPanel) {

            themeTitle = ((TipPanel) this.tipPanel).getTheme().getTitle();
            tipTitle = ((TipPanel) this.tipPanel).getTip().getTitle();

            this.showTip(themeTitle, tipTitle);
            this.showTip(themeTitle, tipTitle);
            System.out.println(themeTitle + ", " + tipTitle);
        }

        isAdmin = this.getAccount() instanceof AdminAccount;
        this.createButton.setVisible(isAdmin);
        this.removeButton.setVisible(isAdmin);
    }

    /* ______________________________________________________________________ */
    private void showTip(TreeSelectionEvent tse) {
        TreePath treePath = tse.getNewLeadSelectionPath();
        if (treePath != null) {

            Object[] path = treePath.getPath();
            if (path != null) {
                System.out.println(Arrays.toString(path));

                try {
                    this.showTip(
                            path[path.length - 2] + "",
                            path[path.length - 1] + ".txt"
                    );
                } catch (Exception e) {
                }
            }
        }
    }

    /* ______________________________________________________________________ */
    private void showTip(String themeTitle, String tipTitle) {

        Theme theme = this.searchTheme(themeTitle);
        Theme tip = this.searchTheme(tipTitle);

        System.out.println("Theme=" + theme);
        System.out.println("Tip=" + tip);

        if (theme != null && tip != null) {
            if (tip instanceof Tip) {
                this.remove(this.tipPanel);

                if (this.getAccount() instanceof AdminAccount) {
                    this.tipPanel = new TipAdminPanel(theme, (Tip) tip);
                } else {
                    this.tipPanel = new TipUserPanel(theme, (Tip) tip);
                }

                this.add(this.tipPanel, BorderLayout.CENTER);
                this.tipPanel.updateUI();
            }
        }
    }

    /* ______________________________________________________________________ */
    private Theme searchTheme(String nameTheme) {
        Theme theme = new Theme("Documentos", "");
        theme.setFiles(this.getThemes());
        return this.searchTheme(theme, nameTheme);
    }

    /* ______________________________________________________________________ */
    private Theme searchTheme(Theme theme, String nameTheme) {
        Theme _theme;

        for (Theme subTheme : theme.getFiles()) {
            if (subTheme.getTitle().equals(nameTheme)) {
                return subTheme;

            } else {
                _theme = this.searchTheme(subTheme, nameTheme);
                if (_theme != null) {
                    return _theme;
                }
            }
        }
        return null;
    }

    /* ______________________________________________________________________ */
    private void createTheme() {
        CreateThemeDialog createThemeDialog = new CreateThemeDialog();
        int result;
        Theme theme;

        System.out.println("Creating theme");
        result = createThemeDialog.showDialog();
        if (result != DialogPane.OK_OPTION) {
            return;
        }
        System.out.println("create process");

        theme = createThemeDialog.getTheme();
        if (theme == null) {
            return;
        }

        this.themes.add(theme);
        try {
            System.out.println("all good");
            this.createTheme(Tools.class.getResource("/docs")., theme);
            System.out.println("finish");
        } catch (IOException e) {
            System.out.println("horror");
        }
    }

    /* ______________________________________________________________________ */
    private void createTheme(String path, Theme theme) throws IOException {

        File directory;
        PlainFileManager plainFileManager;
        File imageFile;
        File descriptionFile;
        BinaryFileManager binaryFileManager;

        String image = theme.getImage();
        String title = theme.getTitle();
        String description = theme.getDescription();
        double value = theme.getValue();
        ArrayList<Theme> files = theme.getFiles();
        ArrayList<Pair<Integer, Integer>> accounts = theme.getAccounts();
        System.out.println("theme created");

        // Crear carpeta
        directory = new File(path + "/" + title);
        directory.mkdir();
        System.out.println("folder created");

        // Mover imagen
        plainFileManager = new PlainFileManager(image);
        imageFile = new File(image);

        imageFile.renameTo(new File(directory.getAbsolutePath() + "/" + imageFile.getName()));
//        plainFileManager.moveTo(directory.getAbsolutePath() + "/" + imageFile.getName());
        theme.setImage(directory.getAbsolutePath() + "/" + imageFile.getName());
        System.out.println("image moved");

        // Escribir descripcion
        descriptionFile = new File(directory.getAbsoluteFile() + "/descripcion.txt");
        descriptionFile.createNewFile();

        plainFileManager = new PlainFileManager(descriptionFile.getAbsolutePath());
        plainFileManager.write(description);
        System.out.println("description writed");

        // Crear archivos
        for (Theme file : files) {
            if (file instanceof Theme) {
                this.createTheme(path, file);
            } else if (file instanceof Tip) {
                this.createTip(path, (Tip) file);
            }
        }
        System.out.println("created files");

        // Crear archivo tema
        binaryFileManager = new BinaryFileManager(
                directory.getAbsolutePath() + "/" + title + ".dat"
        );
        binaryFileManager.write(theme);

        System.out.println("created binary");
    }

    /* ______________________________________________________________________ */
    private void createTip(String path, Tip tip) throws IOException {
        PlainFileManager plainFileManager;
        File imageFile;
        File descriptionFile;

        String image = tip.getImage();
        String title = tip.getTitle();
        String description = tip.getDescription();

        // Mover Imagen
        plainFileManager = new PlainFileManager(image);
        imageFile = new File(image);
        imageFile.renameTo(new File(path + "/" + imageFile.getName()));
//        plainFileManager.moveTo(path + "/" + imageFile.getName());

        // Escribir archivo
        descriptionFile = new File(path + "/" + tip.getTitle() + ".txt");
        descriptionFile.createNewFile();

        plainFileManager = new PlainFileManager(descriptionFile.getAbsolutePath());
        plainFileManager.write(description);

        System.out.println("created file");
    }

    /* ______________________________________________________________________ */
    private void removeTheme() {

    }

    /* GETTERS ______________________________________________________________ */
    public Account getAccount() {
        return this.account;
    }

    /* ______________________________________________________________________ */
    public ArrayList<Theme> getThemes() {
        return this.themes;
    }

    /* ______________________________________________________________________ */
    @Override
    public JButton getCloseButton() {
        return this.backButton;
    }

    /* SETTERS ______________________________________________________________ */
    public void setAccount(Account account) {
        this.account = account;
    }

    /* ______________________________________________________________________ */
    public void setThemes(ArrayList<Theme> themes) {
        this.themes = themes;
    }

    /*  MAIN ________________________________________________________________ */
    public static void main(String[] args) {
        new WorkshopsPanel(new UserAccount(
                "Alejandro",
                "413J0c",
                "passwd",
                "profile/image-31"
        )).showTestDialog();
    }
}
