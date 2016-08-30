/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeevaluator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author debd_admin
 */
class MainGUI extends JFrame {
    boolean cmb_updating = false;
    final JFileChooser jfc = new JFileChooser();
    String[] prog_op;
    JComboBox<comboBoxElement> prob_name_cmb;
    JTextPane console;
    StyledDocument console_doc;
    SimpleAttributeSet normal_msg_style, error_msg_style;
    JPanel tc_panel;
    JLabel sol_file_lbl;
    JButton view_op_btn;
    
    Configuration config;
    //Executor executor;
    ExecutionManager exemgr;
    Settings sett;
    TestCaseViewer tcview;
    OutputViewer opview;
    
    final String icon_dir = "/icon/";
    
    public MainGUI(Configuration config, Settings sett) {
        this.config = config;
        initUI();
        
        normal_msg_style = new SimpleAttributeSet();
        StyleConstants.setForeground(normal_msg_style, Color.GREEN);
        StyleConstants.setBackground(normal_msg_style, Color.BLACK);
        
        error_msg_style = new SimpleAttributeSet();
        StyleConstants.setForeground(error_msg_style, Color.RED);
        StyleConstants.setBackground(error_msg_style, Color.BLACK);
        StyleConstants.setBold(error_msg_style, true);
        
        //executor = new Executor();
        exemgr = new ExecutionManager();
        this.sett = sett;
        tcview = new TestCaseViewer(this);
        opview = new OutputViewer(this);
        
    }
    
    public static void setUIFont(FontUIResource f) {
        Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
                FontUIResource orig = (FontUIResource) value;
                Font font = new Font(f.getFontName(), orig.getStyle(), f.getSize());
                UIManager.put(key, new FontUIResource(font));
            }
        }
    }
    
    class comboBoxElement {
        Problem p;
        int[] status_arr;   // 1: ok, 0: not ok, 2: not evaled
        JScrollPane problem_panel; // associated panel of test cases
        ArrayList<JLabel> tc_label_arr = new ArrayList<>();
        ArrayList<JCheckBox> tc_chkbox_arr = new ArrayList<>();
        ArrayList<JButton> tc_btn_arr = new ArrayList<>();
        String sol_file;

        public comboBoxElement(Problem p) {
            this.p = p;
            status_arr = new int[p.no_test_case];
            Arrays.fill(status_arr, 3); // 3 stands for not evaluated
            /* 0 is ok, 1 is wrong ans, 2 is time exceeded */
            sol_file = "None selected";
            generateCheckBoxPanel(p);
        }
        
        private void generateCheckBoxPanel(Problem p) {
            JPanel scrollable_tc_pane = new JPanel();
            //scrollable_tc_pane.setLayout(new BoxLayout(scrollable_tc_pane, BoxLayout.Y_AXIS));
            scrollable_tc_pane.setLayout(new GridLayout(4, (int)Math.ceil(p.no_test_case/4)));
            //scrollable_tc_pane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            scrollable_tc_pane.setPreferredSize(new Dimension(340, 130));
            scrollable_tc_pane.setMaximumSize(new Dimension(350, 140));
            //scrollable_tc_pane.setBorder(new LineBorder(Color.gray));
            for (int i = 0; i < p.no_test_case; i++) {
                JPanel cb_panel = new JPanel();
                cb_panel.setLayout(new BoxLayout(cb_panel, BoxLayout.X_AXIS));

                JCheckBox tmp_cb = new JCheckBox("TestCase " + (i + 1));
                tmp_cb.setAlignmentX(Component.CENTER_ALIGNMENT);
                tmp_cb.setSelected(true);

                JLabel status = new JLabel("    ");
                status.setOpaque(true);
                //status.setBackground(Color.black);
                status.setAlignmentX(Component.CENTER_ALIGNMENT);
                //status.setMinimumSize(new Dimension(10, 10));
                status.setBorder(LineBorder.createBlackLineBorder());

                JButton view_btn = new JButton();
                if (p.isTcVisible(i)) {
                    view_btn.setIcon(new ImageIcon(this.getClass().getResource(icon_dir + "view.png")));
                    view_btn.setPreferredSize(new Dimension(20, 20));
                    view_btn.setMaximumSize(new Dimension(20, 20));
                }
                
                cb_panel.add(tmp_cb);
                //cb_panel.add(Box.createRigidArea(new Dimension(10, 0)));
                cb_panel.add(status);
                cb_panel.add(Box.createRigidArea(new Dimension(5, 0)));
                if (p.isTcVisible(i)) {
                    cb_panel.add(view_btn);
                    view_btn.addActionListener(new ActionListener() {
                        public void actionPerformed (ActionEvent e) {
                            show_tc_action(e);
                        }
                    });
                }
                //cb_panel.setBorder(new LineBorder(Color.GRAY));

                scrollable_tc_pane.add(cb_panel);
                //scrollable_tc_pane.add(Box.createRigidArea(new Dimension(0, 5)));
                tc_chkbox_arr.add(tmp_cb);
                tc_label_arr.add(status);
                tc_btn_arr.add(view_btn);
            }
            JScrollPane jsp = new JScrollPane(scrollable_tc_pane);
            jsp.setBorder(null);
            
            this.problem_panel = jsp;
        }

        private void show_tc_action(ActionEvent e) {
            JButton btn = (JButton) e.getSource();
            comboBoxElement selected = (comboBoxElement) prob_name_cmb.getSelectedItem();
            int tc_no = selected.tc_btn_arr.indexOf(btn);
            tcview.ViewTC(selected.p, tc_no);
        }
        
        public JScrollPane getProblem_panel() {
            return problem_panel;
        }
        
        @Override
        public String toString () {
            return p.name;
        }
    }
    
    public final void initUI () {
        
        setUIFont(new FontUIResource(new Font("Tahoma", 0, 12)));
        
        //container panel
        JPanel main_panel = new JPanel();
        main_panel.setLayout(new BoxLayout(main_panel, BoxLayout.Y_AXIS));
        add(main_panel);
        
        JPanel top_panel = new JPanel();
        top_panel.setLayout(new BoxLayout(top_panel, BoxLayout.X_AXIS));
        //top_panel.setMaximumSize(new Dimension(450, 0));
        top_panel.add(Box.createRigidArea(new Dimension(10, 0)));
        top_panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // choose problem label and cmb
        JLabel prob_name_lbl = new JLabel("Choose Problem   ");
        prob_name_lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        top_panel.add(prob_name_lbl);
        
        comboBoxElement[] cmb_elems = new comboBoxElement[config.no_of_problems];
        int i=0;
        if (config.no_of_problems > 0) {
            for (Problem p : config.getProblem_set()) {
                cmb_elems[i++] = new comboBoxElement(p);
            }
        }
        prob_name_cmb = new JComboBox<>(cmb_elems);
        prob_name_cmb.setMinimumSize(new Dimension(180, 30));
        prob_name_cmb.setMaximumSize(new Dimension(200, 30));
        prob_name_cmb.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        top_panel.add(prob_name_cmb);
        main_panel.add(Box.createRigidArea(new Dimension(0,10)));
        main_panel.add(top_panel);
        main_panel.add(Box.createRigidArea(new Dimension(0,10)));
        
        // solution file selector
        JPanel solution_panel = new JPanel();
        //solution_panel.setLayout(new BoxLayout(solution_panel, BoxLayout.X_AXIS));
        solution_panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        solution_panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        solution_panel.setPreferredSize(new Dimension(400, 40));
        solution_panel.setMinimumSize(new Dimension(400, 40));
        solution_panel.setMaximumSize(new Dimension(400, 40));
        //solution_panel.setBorder(BorderFactory.createLineBorder(Color.yellow));
        
        solution_panel.add(new JLabel("Choose solution C file: "));
        solution_panel.add(Box.createRigidArea(new Dimension(5,0)));
        
        sol_file_lbl = new JLabel("");
        sol_file_lbl.setPreferredSize(new Dimension(120, 20));
        sol_file_lbl.setMinimumSize(new Dimension(120, 20));
        sol_file_lbl.setText("None selected");
        sol_file_lbl.setBorder(BorderFactory.createLineBorder(Color.gray));
        solution_panel.add(sol_file_lbl);
        solution_panel.add(Box.createRigidArea(new Dimension(5,0)));
        
        JButton sol_select_btn = new JButton("Select");
        sol_select_btn.setPreferredSize(new Dimension(80, 30));
        solution_panel.add(sol_select_btn);
        
        main_panel.add(solution_panel);
        main_panel.add(Box.createRigidArea(new Dimension(0,10)));
        
        // test cases panel
        tc_panel = new JPanel();
        //tc_panel.setLayout();
        tc_panel.setBorder(BorderFactory.createTitledBorder("Test Cases"));
        tc_panel.setPreferredSize(new Dimension(400, 180));
        tc_panel.setMaximumSize(new Dimension(400, 160));
        if (prob_name_cmb.getItemCount() > 0)
            tc_panel.add(prob_name_cmb.getItemAt(0).getProblem_panel());
        main_panel.add(tc_panel);
        
        // add buttons
        JButton run_btn = new JButton("Run");
        view_op_btn = new JButton("View Output");
        view_op_btn.setEnabled(false);
        JPanel btn_panel = new JPanel();
        btn_panel.setLayout(new BoxLayout(btn_panel, BoxLayout.X_AXIS));
        btn_panel.setAlignmentY(1f);
        btn_panel.add(run_btn);
        btn_panel.add(Box.createRigidArea(new Dimension(5,0)));
        btn_panel.add(view_op_btn);
        
        main_panel.add(btn_panel);
        
        // add console
        JPanel console_panel = new JPanel(new BorderLayout());
        console_panel.setBorder(BorderFactory.createTitledBorder("Console"));
        console_panel.setPreferredSize(new Dimension(400, 180));
        console_panel.setMinimumSize(new Dimension(370, 180));
        console_panel.setMaximumSize(new Dimension(400, 180));
        
        console = new JTextPane();
        console_doc = console.getStyledDocument();
        
        console.setBackground(Color.black);
        console.setForeground(Color.green);
        console.setFont(new Font("Courier New", Font.PLAIN, 12));
        console.setAutoscrolls(true);
        console.setPreferredSize(new Dimension(400, 150));
        /*
        console.setText("Some Text");
                */
        
        console_panel.add(new JScrollPane(console), BorderLayout.CENTER);
        //console.setMinimumSize(new Dimension(370, 200));
        main_panel.add(console_panel);
        
        JPanel config_panel = new JPanel();
        config_panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton config_btn = new JButton();
        config_btn.setIcon(new ImageIcon(this.getClass().getResource(icon_dir + "settings.png")));
        config_panel.add(config_btn);
        JButton about_btn = new JButton("");
        about_btn.setIcon(new ImageIcon(this.getClass().getResource(icon_dir + "about.png")));
        config_panel.add(about_btn);
        
        main_panel.add(config_panel);
        
        // the combobox action listener
        prob_name_cmb.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (cmb_updating == true)
                            return;
                        comboBoxElement selectedItem = (comboBoxElement)prob_name_cmb.getSelectedItem();
                        tc_panel.removeAll();
                        tc_panel.add(
                                (selectedItem).getProblem_panel()
                        );
                        tc_panel.revalidate();
                        tc_panel.repaint();
                        // set the label shwoing file name
                        if (selectedItem.sol_file.equals("None selected"))
                            sol_file_lbl.setText(selectedItem.sol_file);
                        else {
                            sol_file_lbl.setText("..." + 
                                selectedItem.sol_file.substring(selectedItem.sol_file.length() - 15));
                        }
                        //System.out.println("fired");
                        view_op_btn.setEnabled(false);
                    }
                }
            );
        
        sol_select_btn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        fopen_action(e);
                    }
        });
        
        run_btn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        run_action(e);
                    }
        });
        
        view_op_btn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        view_action(e);
                    }
        });
        
        config_btn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        show_config_win(e);
                    }
        });
        
        about_btn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        show_about_win(e);
                    }
        });
        
        pack();
        setPreferredSize(new Dimension(850, 420));
        setMinimumSize(new Dimension(420, 380));
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle("CodeCombat Code Evaluator");
        setIconImage(new ImageIcon(this.getClass().getResource(icon_dir + "ccce.png")).getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void show_config_win (ActionEvent e) {
        ConfigEditor cedt = new ConfigEditor(this, sett);
        cedt.setVisible(true);
    }
    
    private void show_about_win (ActionEvent e) {
        String header = "<html><span>";
        String title = "<div style='text-align:center;font-size:1.2em'>CodeCombat Code Evaluator</div>"
                + "<div style='font-size:1em;text-align:right'>(v0.1)</div><br>";
        String msg = "<span style='font-size:1.2em;font-weight:bold'>The testcase evaluator for C.U. CodeCombat</span><br>";
        String contact = "<br><div style='text-align:right'>by debd92@hotmail.com</div>";
        String footer = "</span></html>";
        JOptionPane.showMessageDialog(this, header+title+msg+contact+footer, "About", JOptionPane.INFORMATION_MESSAGE, 
                new ImageIcon(this.getClass().getResource(icon_dir + "ccce_large.png")));
    }
    
    private void resetComboBox () {
        cmb_updating = true;
        prob_name_cmb.removeAllItems();
        int i=0;
        for (Problem p : config.getProblem_set()) {
            prob_name_cmb.addItem(new comboBoxElement(p));
        }
        cmb_updating = false;
    }
    
    private void reset_tc_chkbox () {
        tc_panel.add(prob_name_cmb.getItemAt(0).getProblem_panel());
    }
    
    private void resetGUIElements () {
        resetComboBox();
        reset_tc_chkbox();
        tc_panel.revalidate();
        tc_panel.repaint();
        view_op_btn.setEnabled(false);
        revalidate();
        repaint();
    }
    
    public void reloadConfig () {
        config = new Configuration(sett);
        config.init();
        resetGUIElements();
    }
    
    private void fopen_action (ActionEvent ae) {
        // TODO add your handling code here:
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text/C", "txt", "c");
        jfc.setFileFilter(filter);

        int ret = jfc.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            String file_name = jfc.getSelectedFile().getAbsolutePath();
            ((comboBoxElement) (prob_name_cmb.getSelectedItem())).sol_file
                    = file_name;
            sol_file_lbl.setText("..." + file_name.substring(file_name.length() - 15));
        }
    }
    
    private void update_console (String str, boolean... err) {
        
        try {
            if (err.length == 0) {
                console_doc.insertString(console_doc.getLength(), "\n" + str, normal_msg_style);
            } else {
                console_doc.insertString(console_doc.getLength(), "\n" + str, error_msg_style);
            }
        } catch (Exception e) {}
        
    }
    
    private void view_action (ActionEvent ae) {
        opview.ViewOutput(prog_op);
    }
    
    private void run_action (ActionEvent ae) {
        comboBoxElement selected = (comboBoxElement) (prob_name_cmb.getSelectedItem());
        HashMap<String, String> res;
        
        int i = 0, stat;
        boolean err = true, op_available = false;
        String compiler = sett.GetProp("compiler_path");
        
        if (!selected.sol_file.equals("None selected")) {
            String name = selected.p.name;
            prog_op = new String[selected.p.no_test_case];
            view_op_btn.setEnabled(false);
            
            for (JCheckBox jcb : selected.tc_chkbox_arr) {
                if (jcb.isSelected()) {
                    //res = executor.execute(selected.p, i, selected.sol_file);
                    res = exemgr.execute(selected.p, i, selected.sol_file, compiler, prog_op);
                    if (!res.containsKey("compiler_msg")) {
                        if (res.containsKey("status")) {
                            stat = (int) Integer.parseInt(res.get("status"));
                            if (stat == 0) {
                                selected.tc_label_arr.get(i).setBackground(Color.green);
                                update_console(name + " Testcase " + (i + 1) + "::: Passed   Duration: " + res.get("duration") + " ms");
                                op_available = true;
                            } else if (stat == 1) {
                                selected.tc_label_arr.get(i).setBackground(Color.red);
                                update_console(name + " Testcase " + (i + 1) + "::: Failed\nError: " + res.get("errmsg"), err);
                                op_available = true;
                            } else if (stat == 2) {
                                selected.tc_label_arr.get(i).setBackground(Color.orange);
                                update_console(name + " Testcase " + (i + 1) + "::: Failed\nError: " + res.get("errmsg") + "   Duration: " + res.get("duration") + " ms", err);
                            } else if (stat == 3) {
                                update_console(name + " Testcase " + (i + 1) + "::: Failed\nError: " + res.get("errmsg"), err);
                            }
                        }
                    } else {
                        update_console(res.get(name + " compiler_msg"), err);
                    }
                }
                i++;
            }
        }
        if (op_available == true) {
            view_op_btn.setEnabled(true);
        }
    }
}

public class CodeEvaluator {

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(MainGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                Settings sett = new Settings("CODEEVALUATOR.INI");
                sett.LoadSettingsFile();
                Configuration config = new Configuration(sett);
                config.init();
                
                MainGUI maingui = new MainGUI(config, sett);
                maingui.setVisible(true);
            }
        });
    }
}
