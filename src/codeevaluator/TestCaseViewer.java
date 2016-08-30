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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;

public class TestCaseViewer extends JFrame {
    MainGUI mainwin;
    JTextArea output_ta;
    JTextArea input_ta;

    public TestCaseViewer(MainGUI mainwin) {
        this.mainwin = mainwin;
        
        DefaultCaret caret;

        JLabel input_lbl = new JLabel("Input:");
        input_ta = new JTextArea(8, 30);
        input_ta.setEditable(false);
         caret = (DefaultCaret)input_ta.getCaret();
         caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        JLabel output_lbl = new JLabel("Expected Output:");
        output_ta = new JTextArea(8, 30);
        output_ta.setEditable(false);
        caret = (DefaultCaret)output_ta.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        JButton ok = new JButton("OK");
        JPanel ok_panel = new JPanel();
        ok_panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        ok_panel.add(ok);

        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        getContentPane().setLayout(
                new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
        );

        JPanel input_panel = new JPanel();
        input_panel.add(input_lbl);

        JPanel output_panel = new JPanel();
        output_panel.add(output_lbl);

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.black);

        add(input_panel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(new JScrollPane(input_ta));
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(sep);
        add(output_panel);
        add(Box.createRigidArea(new Dimension(0, 5)));
        add(new JScrollPane(output_ta));
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(ok_panel);

        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ok_action();
            }
        });
        
        pack();
        setPreferredSize(new Dimension(450, 420));
        setMinimumSize(new Dimension(420, 380));
        setResizable(false);
        setLocationRelativeTo(mainwin);
        
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    public void ViewTC(Problem p, int tc_no) {
        String input_str = p.getTestCaseInput(tc_no);
        String output_str = p.getTestCaseOutput(tc_no);
        
        input_ta.setText(input_str);
        output_ta.setText(output_str);
        
        setTitle("View Test Case " + (tc_no + 1));
        setVisible(true);
    }
    
    private void ok_action () {
        setVisible(false);
        dispose();
    }
}
