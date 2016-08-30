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

public class OutputViewer extends JFrame {
    MainGUI mainwin;
    JTextArea output_ta;
    
    public OutputViewer (MainGUI mainwin) {
        this.mainwin = mainwin;

        JLabel output_lbl = new JLabel("Output:");
        output_ta = new JTextArea(10, 30);
        output_ta.setEditable(false);
        DefaultCaret caret = (DefaultCaret)output_ta.getCaret();
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

        JButton ok = new JButton("OK");
        JPanel ok_panel = new JPanel();
        ok_panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        ok_panel.add(ok);

        //this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        getContentPane().setLayout(
                new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
        );

        JPanel output_panel = new JPanel();
        output_panel.add(output_lbl);

        JSeparator sep = new JSeparator();
        sep.setForeground(Color.black);

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
    
    private void ok_action () {
        setVisible(false);
        dispose();
    }
    
    public void ViewOutput (String[] op) {
        int i=1;
        StringBuilder stb = new StringBuilder();
        for (String str : op) {
            if (str != null) {
                stb.append("TESTCASE " + i + ":\n" + str + "\n\n");
            }
            i++;
        }
        output_ta.setText(stb.toString());
        
        setVisible(true);
    }
}
