/*
 * Copyright (C) 2016 debd92 [@] hotmail.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package codeevaluator;

import com.google.gson.Gson;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ConfigEditor extends JFrame {
    MainGUI mainwin;
    ArrayList<JButton> buttons = new ArrayList<>();
    ArrayList<JCheckBox> jcbs = new ArrayList<>();
    ArrayList<JTextField> jtfs = new ArrayList<>();
    ArrayList<JTextField> timeouts = new ArrayList<>();
    ArrayList<Problem> probs = new ArrayList<>();
    JTextField pg_tf;
    JTextField tm_tf;
    JTextField cmp_tf;
    JTextField cfg_tf;
    Settings sett;
    int prob_id = 100;
    
    int max_tc = 8;
    
    public ConfigEditor(MainGUI mainwin, Settings sett) {
        this.mainwin = mainwin;
        this.sett = sett;
        initUI();
    }
    
    private void initUI() {
        JTabbedPane jtp = new JTabbedPane();
        
        // test case setting start
        JPanel tc_setting = new JPanel();
        tc_setting.setLayout(new BorderLayout());
        
        JPanel pname_panel = new JPanel();
        
        JLabel pg_lbl = new JLabel("Problem name: ");
        pg_tf = new JTextField();
        pg_tf.setPreferredSize(new Dimension(200, 25));
        pg_tf.setMinimumSize(new Dimension(200, 25));
        
        pname_panel.add(pg_lbl);
        pname_panel.add(pg_tf);
        
        tm_tf = new JTextField();
        tm_tf.setPreferredSize(new Dimension(30, 25));
        tm_tf.setMinimumSize(new Dimension(30, 25));
        tm_tf.setMaximumSize(new Dimension(30, 25));
        
        pname_panel.add(Box.createRigidArea(new Dimension(5, 0)));
        pname_panel.add(new JLabel("Timeout(ms)"));
        pname_panel.add(tm_tf);
        
        tc_setting.add(pname_panel, BorderLayout.NORTH);
        
        JPanel tc_add_panel = new JPanel();
        tc_add_panel.setLayout(new BoxLayout(tc_add_panel, BoxLayout.Y_AXIS));
        
        Font small_font = new Font("Tahoma", Font.PLAIN, 11);
        
        for (int i=0; i<max_tc; i++) {
            JPanel tmp_panel = new JPanel();
            tmp_panel.setLayout(new BoxLayout(tmp_panel, BoxLayout.X_AXIS));
            
            JLabel tc_name = new JLabel("Testcase " + (i+1));
            
            JTextField tc_tf = new JTextField();
            tc_tf.setPreferredSize(new Dimension(180, 30));
            tc_tf.setMinimumSize(new Dimension(100, 20));
            tc_tf.setMaximumSize(new Dimension(200, 30));
            jtfs.add(tc_tf);
            
            JButton sel_btn = new JButton("...");
            buttons.add(sel_btn);
            
            JCheckBox jcb = new JCheckBox();
            jcbs.add(jcb);
            
            JLabel v = new JLabel("visible?");
            v.setFont(small_font);
            
            tmp_panel.add(tc_name);
            tmp_panel.add(Box.createRigidArea(new Dimension(5, 0)));
            tmp_panel.add(tc_tf);
            tmp_panel.add(Box.createRigidArea(new Dimension(5, 0)));
            tmp_panel.add(sel_btn);
            tmp_panel.add(Box.createRigidArea(new Dimension(5, 0)));
            tmp_panel.add(jcb);
            tmp_panel.add(v);
            //tmp_panel.add(Box.createRigidArea(new Dimension(5, 0)));
            
            tc_add_panel.add(tmp_panel);
            tmp_panel.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        for (JButton jbtn : buttons) {
            jbtn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        browse_action(e);
                    }
            });
        }
        
        tc_setting.add(tc_add_panel, BorderLayout.CENTER);
        
        JPanel btn_panel = new JPanel();
        btn_panel.setLayout(new BoxLayout(btn_panel, BoxLayout.X_AXIS));
        btn_panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton next_btn = new JButton("Next >");
        JButton save_btn = new JButton("Save");
        btn_panel.add(next_btn);
        btn_panel.add(Box.createRigidArea(new Dimension(5, 0)));
        btn_panel.add(save_btn);
        
        tc_setting.add(btn_panel, BorderLayout.SOUTH);
        
        next_btn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        next_action(e);
                    }
            });
        
        save_btn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        save_action(e);
                    }
            });
        // test case setting end
        
        // program setting start
        JPanel prog_setting = new JPanel();
        prog_setting.setLayout(new BoxLayout(prog_setting, BoxLayout.Y_AXIS));
        //prog_setting.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        JLabel cmp_lbl = new JLabel("  Choose compiler executable");
        cmp_tf = new JTextField();
        cmp_tf.setText(sett.GetProp("compiler_path"));
        cmp_tf.setPreferredSize(new Dimension(180, 20));
        cmp_tf.setMinimumSize(new Dimension(100, 20));
        cmp_tf.setMaximumSize(new Dimension(200, 20));
        JButton cmp_sel_btn = new JButton("...");
        
        JPanel comp_panel = new JPanel();
        comp_panel.setLayout(new BoxLayout(comp_panel, BoxLayout.X_AXIS));
        comp_panel.setAlignmentX(1);
        
        comp_panel.add(cmp_lbl);
        comp_panel.add(Box.createRigidArea(new Dimension(5, 0)));
        comp_panel.add(cmp_tf);
        comp_panel.add(Box.createRigidArea(new Dimension(5, 0)));
        comp_panel.add(cmp_sel_btn);
        
        prog_setting.add(comp_panel);
        prog_setting.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel cfg_lbl = new JLabel("  Choose configuration file");
        cfg_tf = new JTextField();
        cfg_tf.setText(sett.GetProp("config_path"));
        cfg_tf.setPreferredSize(new Dimension(180, 20));
        cfg_tf.setMinimumSize(new Dimension(100, 20));
        cfg_tf.setMaximumSize(new Dimension(200, 20));
        cfg_tf.setAlignmentX(Component.CENTER_ALIGNMENT);
        JButton cfg_sel_btn = new JButton("...");
        
        JPanel cfg_panel = new JPanel();
        cfg_panel.setLayout(new BoxLayout(cfg_panel, BoxLayout.X_AXIS));
        cfg_panel.setAlignmentX(1);
        
        cfg_panel.add(cfg_lbl);
        cfg_panel.add(Box.createRigidArea(new Dimension(5, 0)));
        cfg_panel.add(cfg_tf);
        cfg_panel.add(Box.createRigidArea(new Dimension(5, 0)));
        cfg_panel.add(cfg_sel_btn);
        
        prog_setting.add(cfg_panel);
        prog_setting.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JButton ok_set_btn = new JButton("OK");
        
        JPanel ok_panel = new JPanel();
        ok_panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        ok_panel.add(ok_set_btn);
        
        prog_setting.add(ok_panel);
        
        cmp_sel_btn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        set_compiler_action(e);
                    }
            });
        
        cfg_sel_btn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        set_config_action(e);
                    }
            });
        
        ok_set_btn.addActionListener(new ActionListener() {
            @Override
                    public void actionPerformed(ActionEvent e) {
                        progset_ok_action(e);
                    }
            });
        // program setting start
        
        jtp.add("Program Settings", prog_setting);
        jtp.add("Test Case Settings", tc_setting);
        
        add(jtp);
        
        pack();
        setPreferredSize(new Dimension(500, 420));
        setMinimumSize(new Dimension(420, 380));
        setResizable(false);
        setLocationRelativeTo(null);
        setTitle("Configuration");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void browse_action (ActionEvent e) {
        String fname = FileWrapper.FileOpenDialog(this);
        if (fname != null) {
            JButton sel = ((JButton) e.getSource());
            int tc_number = buttons.indexOf(sel);
            if (tc_number != -1) {
                jtfs.get(tc_number).setText(fname);
            }
        }
    }
    
    private void next_action (ActionEvent e) {
        String prob_name = pg_tf.getText().trim();
        int num_tc = 0;
        for (int i=0; i<max_tc; i++) {
            try {
                JTextField tf = jtfs.get(i);
                if (! tf.getText().equals("") ) {
                    num_tc += 1;
                }
            } catch (Exception ex) { System.out.println("OutofBuounds"); }
        }
        
        if (num_tc < 1)
            return;
        
        String[] tc_files = new String[num_tc];
        boolean[] visibles = new boolean[num_tc];
        
        for (int i=0; i<num_tc; ) {
            String t = jtfs.get(i).getText().trim();
            if (!t.equals("")) {
                tc_files[i] = t;
                visibles[i] = jcbs.get(i).isSelected();
                i++;
            }
        }
        
        String tmout = tm_tf.getText().trim();
        long timeout = 0;
        if (!tmout.equals(""))
            timeout = Integer.parseInt(tmout);
        
        Problem p = new Problem(prob_name, prob_id, num_tc, tc_files, visibles, timeout);
        probs.add(p);
        prob_id++;
        
        for (JTextField jtf : jtfs)
            jtf.setText("");
        
        for (JCheckBox jcb : jcbs)
            jcb.setSelected(false);
        
        tm_tf.setText("");
        pg_tf.setText("");
    }
    
    private void save_action (ActionEvent e) {
        if (! pg_tf.getText().trim().isEmpty() )
            next_action(e);
        
        Problem[] prob_array = probs.toArray(new Problem[probs.size()]);
        String save_name = FileWrapper.FileSaveDialog(this);
        if (save_name != null) {
            FileWriter fileWriter = null;
            try {
                Gson gson = new Gson();
                String ser = gson.toJson(prob_array, Problem[].class);
                File file = new File(save_name);
                fileWriter = new FileWriter(file);
                fileWriter.write(ser);
                fileWriter.flush();
            } catch (IOException ex) {
                Logger.getLogger(ConfigEditor.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fileWriter.close();
                } catch (IOException ex) {
                    Logger.getLogger(ConfigEditor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    private void set_compiler_action (ActionEvent e) {
        String save_name = FileWrapper.FileOpenDialog(this, true);
        if (save_name != null) {
            cmp_tf.setText(save_name);
        }
    }
    
    private void set_config_action (ActionEvent e) {
        String save_name = FileWrapper.FileOpenDialog(this);
        if (save_name != null) {
            cfg_tf.setText(save_name);
        }
    }
    
    private void progset_ok_action (ActionEvent e) {
        setVisible(false);
        String old = sett.GetProp("config_path");
        
        String comp_path = cmp_tf.getText().trim();
        String config_path = cfg_tf.getText().trim();
        
        if (!comp_path.isEmpty())
            sett.SetProp("compiler_path", comp_path);
        
        if (!config_path.isEmpty())
            sett.SetProp("config_path", config_path);
        
        sett.WriteSettingsFile();
        if (! old.equals(config_path) )
        {
            mainwin.reloadConfig();
        }
    }
    /*
    public static void main (String[] args) {
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
                ConfigEditor maingui = new ConfigEditor();
                maingui.setVisible(true);
            }
        });
    }
    */
}
