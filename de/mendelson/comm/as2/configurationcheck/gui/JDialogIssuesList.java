///$Header: /as2/de/mendelson/comm/as2/configurationcheck/gui/JDialogIssuesList.java 3     24.02.20 15:20 Heller $
package de.mendelson.comm.as2.configurationcheck.gui;

/*
 * Copyright (C) mendelson-e-commerce GmbH Berlin Germany
 *
 * This software is subject to the license agreement set forth in the license.
 * Please read and agree to all terms before using this software.
 * Other product and brand names are trademarks of their respective owners.
 */
import de.mendelson.comm.as2.client.ModuleStarter;
import de.mendelson.comm.as2.clientserver.message.ConfigurationCheckRequest;
import de.mendelson.comm.as2.clientserver.message.ConfigurationCheckResponse;
import de.mendelson.comm.as2.configurationcheck.ConfigurationIssue;
import de.mendelson.comm.as2.configurationcheck.ResourceBundleConfigurationIssue;
import de.mendelson.util.MecResourceBundle;
import de.mendelson.util.clientserver.BaseClient;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * List of functions, useful for auto complete
 *
 * @author S.Heller
 * @version $Revision: 3 $
 */
public class JDialogIssuesList extends JDialog implements FocusListener, MouseMotionListener {

    private KeyEventDispatcherIssuesList keyEventDispatcher = new KeyEventDispatcherIssuesList();
    private BaseClient baseClient = null;
    private Point origin;
    private MecResourceBundle rb;
    private ModuleStarter moduleStarter;
    private final List<ConfigurationIssue> issues = Collections.synchronizedList(new ArrayList<ConfigurationIssue>());
    private JFrame parent;

    public JDialogIssuesList(JFrame parent, BaseClient baseClient, Point origin, ModuleStarter moduleStarter) {
        super(parent, false);
        this.parent = parent;
        this.moduleStarter = moduleStarter;
        //load resource bundle
        try {
            this.rb = (MecResourceBundle) ResourceBundle.getBundle(
                    ResourceBundleConfigurationIssue.class.getName());
        } catch (MissingResourceException e) {
            throw new RuntimeException("Oops..resource bundle " + e.getClassName() + " not found.");
        }
        this.baseClient = baseClient;
        this.origin = origin;
        initComponents();
        this.jList.addFocusListener(this);
        this.addFocusListener(this);
        this.jScrollPaneList.addFocusListener(this);
        this.populateList();
        this.setBounds();
        //on any parent frame move, hide etc this should vanish
        this.parent.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                JDialogIssuesList.this.setVisible(false);
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                JDialogIssuesList.this.setVisible(false);
            }

            @Override
            public void componentShown(ComponentEvent e) {
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                JDialogIssuesList.this.setVisible(false);
            }

        });
    }

    public int getRowHeight() {
        synchronized (this.issues) {
            if (this.issues.isEmpty()) {
                return (0);
            }
        }
        int height = (int) this.jList.getCellBounds(0, 0).getHeight();
        return (height);
    }

    private void setBounds() {
        synchronized (this.issues) {
            if (this.issues.isEmpty()) {
                this.setBounds(0, 0, 0, 0);
                return;
            }
        }
        //compute max width
        String longestEntry = "";
        int entryCount = this.jList.getModel().getSize();
        for (int i = 0; i < entryCount; i++) {
            String foundEntry = this.jList.getModel().getElementAt(i).toString();
            if (foundEntry.length() > longestEntry.length()) {
                longestEntry = foundEntry;
            }
        }
        int width = this.computeStringWidth(longestEntry)
                + this.getInsets().left
                + this.getInsets().right + 30;
        int height = entryCount * this.getRowHeight() + 5;
        this.setBounds(this.origin.x, this.origin.y - height, width, height);
    }

    /**
     * Compute the width of the content up to the actual cursor position not
     * been found on the OS
     */
    private int computeStringWidth(String text) {
        Graphics2D g = (Graphics2D) this.jLabelInternalUse.getGraphics();
        FontMetrics metrics = g.getFontMetrics(this.jLabelInternalUse.getFont());
        return ((int) Math.ceil(metrics.getStringBounds(text, g).getWidth()));
    }

    private void populateList() {
        if (this.baseClient != null) {
            ConfigurationCheckResponse response = (ConfigurationCheckResponse) this.baseClient.sendSync(new ConfigurationCheckRequest());
            List<String> listData = new ArrayList<String>();
            List<ConfigurationIssue> responseIssues = response.getIssues();
            for (ConfigurationIssue issue : responseIssues) {
                StringBuilder entry = new StringBuilder();
                entry.append(this.rb.getResourceString(String.valueOf(issue.getIssueId())));
                if (issue.getDetails() != null) {
                    entry.append(" (").append(issue.getDetails()).append(")");
                }
                listData.add(entry.toString());
            }
            this.jList.setListData(listData.toArray());
            synchronized (this.issues) {
                this.issues.clear();
                this.issues.addAll(responseIssues);
            }
        }
    }

    @Override
    public void setVisible(boolean flag) {
        if (flag) {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this.keyEventDispatcher);
            this.jList.addMouseMotionListener(this);
        } else {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this.keyEventDispatcher);
            this.jList.removeMouseMotionListener(this);
        }
        super.setVisible(flag);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        int index = this.jList.locationToIndex(evt.getPoint());
        this.jList.getSelectionModel().setSelectionInterval(index, index);
    }

    private void displayIssueDetailsDialog() {
        int index = this.jList.getSelectedIndex();
        final List<ConfigurationIssue> issueList;
        synchronized (this.issues) {
            if (index >= 0 && index < this.issues.size()) {
                issueList = Collections.unmodifiableList(new ArrayList<ConfigurationIssue>(this.issues));
            } else {
                issueList = null;
            }
        }
        if (issueList != null) {
            JDialogConfigurationIssueDetails dialog
                    = new JDialogConfigurationIssueDetails(this.parent, this.moduleStarter, issueList,
                            index);
            dialog.setVisible(true);
        }
    }

    private class KeyEventDispatcherIssuesList implements KeyEventDispatcher {

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    displayIssueDetailsDialog();
                    setVisible(false);
                }
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    setVisible(false);
                }
                if (e.getKeyCode() != KeyEvent.VK_UP && e.getKeyCode() != KeyEvent.VK_DOWN) {
                    setVisible(false);
                }
            }
            return false;
        }
    }

    @Override
    public void focusGained(FocusEvent e) {
    }

    @Override
    public void focusLost(FocusEvent e) {
        this.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLabelInternalUse = new javax.swing.JLabel();
        jScrollPaneList = new javax.swing.JScrollPane();
        jList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());
        getContentPane().add(jLabelInternalUse, new java.awt.GridBagConstraints());

        jScrollPaneList.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jScrollPaneList.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPaneList.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        jList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListMouseClicked(evt);
            }
        });
        jScrollPaneList.setViewportView(jList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jScrollPaneList, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void jListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListMouseClicked
        displayIssueDetailsDialog();
    }//GEN-LAST:event_jListMouseClicked
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabelInternalUse;
    private javax.swing.JList jList;
    private javax.swing.JScrollPane jScrollPaneList;
    // End of variables declaration//GEN-END:variables
}
