package interfaces.showaccount;

import java.awt.BorderLayout;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JLabel;
import tools.filemanager.BinaryFileManager;
import tools.components.Dialog;
import tools.components.DialogPane;

import worldclasses.Settings;
import worldclasses.accounts.Account;

public abstract class ShowAccountDialog extends Dialog {

    /* ATTRIBUTES ___________________________________________________________ */
    private static final long serialVersionUID = -1320757065407549176L;

    private Account account;

    protected JLabel imageLabel;
    protected AccountPanel accountPanel;

    protected JButton backButton;
    protected JButton removeButton;
    protected JButton signoutButton;
    protected JButton editButton;

    /* CONSTRUCTORS _________________________________________________________ */
    public ShowAccountDialog(Account account) {

        this.account = account;

        this.initComponents();
        this.initEvents();
    }

    /* METHODS ______________________________________________________________ */
    protected void initComponents() {

        // Set up Frame --------------------------------------------------------
        this.setLayout(new BorderLayout());

        // Set up Components ---------------------------------------------------
        this.backButton = new JButton("Volver");
        this.removeButton = new JButton("Eliminar");
        this.signoutButton = new JButton("Cerrar sesion");
        this.editButton = new JButton("Editar");
    }

    /* ______________________________________________________________________ */
    protected void initEvents() {
        // Components Events ---------------------------------------------------
        this.backButton.addActionListener(ae -> {
            this.dispose();
        });

        this.removeButton.addActionListener(ae -> {
            this.removeAction();
        });

        this.signoutButton.addActionListener(ae -> {
            int option = DialogPane.showOption(
                    "Cerrar Sesion", "Desea cerral la sesion de la cuenta?"
            );
            if (option == DialogPane.YES_OPTION) {
                this.dispose();
                this.okAction();
            }
        });

        this.editButton.addActionListener(ae -> {
            this.editAction();
        });
    }

    /* ______________________________________________________________________ */
    public void removeAction() {
        int option = DialogPane.showOption(
                "Eliminar Cuenta", "Deesa eliminar definitivamente la cuenta?"
        );

        if (option == DialogPane.YES_OPTION) {
            this.dispose();
            this.okAction();
            removeAccount(this.getAccount());
        }
    }

    /* ______________________________________________________________________ */
    protected abstract void editAction();

    /* ______________________________________________________________________ */
    public void removeAccount(Account account) {
        BinaryFileManager manager = new BinaryFileManager(Settings.ACCOUNTS_PATH_FILE);
        ArrayList<Object> objects = manager.read();
        manager.clear();

        for (Object object : objects) {
            Account _account = (Account) object;
            if (!_account.getNickname().equals(account.getNickname())) {
                manager.add(_account);
            }
        }
    }

    /* GETTERS ______________________________________________________________ */
    public Account getAccount() {
        return this.account;
    }

    /* SETTERS ______________________________________________________________ */
    public void setAccount(Account account) {
        this.account = account;
    }
}
