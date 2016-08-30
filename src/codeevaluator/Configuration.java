/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeevaluator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.PrintStream;
import java.util.Arrays;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

class FileWrapper {
    // making it class var allowes remembering the last dir opened
    final static JFileChooser jfc = new JFileChooser();
    
    public static String readStream(String file) {
        StringBuilder sb = new StringBuilder(1024);
        InputStream is;
        try {
            is = new FileInputStream(file);
            try {
                Reader r = new InputStreamReader(is, "UTF-8");
                int c = 0;
                while ((c = r.read()) != -1) {
                    sb.append((char) c);
                }
                
                is.close();
            } catch (IOException e) {
                System.out.println("Error reading file " + file);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Could not find file " + file);
        }
    
        return sb.toString();
    }
    
    public static String[] parseTestCase (String file) {
        String raw_str = readStream(file);
        String[] tuple = new String[2];
        if ( raw_str.contains("INPUT") &&
             raw_str.contains("OUTPUT")  )
        {
            int inp_marker_start = raw_str.indexOf("INPUT");
            int inp_start = raw_str.indexOf("\n", inp_marker_start)+1;
            
            int op_marker_start = raw_str.indexOf("OUTPUT");
            int op_start = raw_str.indexOf("\n", op_marker_start)+1;
            
            String input = raw_str.substring(inp_start, op_marker_start-1);
            String input_trimmed = input.trim();
            
            String output = raw_str.substring(op_start);
            String output_trimmed = output.trim();
            
            tuple[0] = input_trimmed;
            tuple[1] = output_trimmed.replaceAll("\\r\\n", "\n");
            /* C programs generally output only a linefeed \n for new lines */
            return tuple;
        }
        
        return null;        // badly formatted file
    }
    
    public static String FileOpenDialog (Container c, boolean... exe) {
        // TODO add your handling code here:
        FileNameExtensionFilter filter;
        if (exe.length > 0)
            filter = new FileNameExtensionFilter("Executables", "exe");
        else
            filter = new FileNameExtensionFilter("Text/C", "txt", "c");
        
        jfc.setFileFilter(filter);

        int ret = jfc.showOpenDialog(c);
        if (ret == JFileChooser.APPROVE_OPTION) {
            String file_name = jfc.getSelectedFile().getAbsolutePath();
            return file_name;
        }
        return null;
    }
    
    public static String FileSaveDialog (Container c) {
        // TODO add your handling code here:
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text/C", "txt", "c");
        jfc.setFileFilter(filter);

        int ret = jfc.showSaveDialog(c);
        if (ret == JFileChooser.APPROVE_OPTION) {
            String file_name = jfc.getSelectedFile().getAbsolutePath();
            return file_name;
        }
        return null;
    }
}

class Problem {
    String name;
    int id;
    int no_test_case;
    String[] tc_files;
    long timeout;
    protected String[] tc_input_buffers;
    protected String[] tc_output_buffers;
    protected boolean[] visibility;

    public Problem(String name, int id, int no_test_case,
                    String[] tc_files, boolean[] visibility, long timeout) {
        this.name = name;
        this.id = id;
        this.no_test_case = no_test_case;
        this.tc_files = tc_files;
        this.timeout = timeout;
        this.visibility = Arrays.copyOf(visibility, visibility.length);
        
        System.out.println("I got called " + this.no_test_case);
        
        tc_input_buffers = new String[no_test_case];
        tc_output_buffers = new String[no_test_case];
        System.out.println(tc_input_buffers);
        Arrays.fill(tc_input_buffers, null);
        Arrays.fill(tc_output_buffers, null);
    }
    
    public boolean isTcVisible (int number) {
        if (number < no_test_case) {
            return visibility[number];
        }
        return false;
    }
    
    public String getTestCaseInput (int number) {
        if (number > no_test_case)
            return null;
        if (tc_input_buffers[number] == null) {
            String[] tuple = FileWrapper.parseTestCase(tc_files[number]);
            if (tuple != null) {
                tc_input_buffers[number] = tuple[0];
                tc_output_buffers[number] = tuple[1];
            }
        }
        return tc_input_buffers[number];
    }
    
    public String getTestCaseOutput (int number) {
        if (number > no_test_case)
            return null;
        if (tc_input_buffers[number] == null) {
            String[] tuple = FileWrapper.parseTestCase(tc_files[number]);
            if (tuple != null) {
                tc_input_buffers[number] = tuple[0];
                tc_output_buffers[number] = tuple[1];
            }
        }
        return tc_output_buffers[number];
    }
}
/**
 *
 * @author debd_admin
 */
public class Configuration {
    Problem[] problem_set;
    int no_of_problems;
    Settings sett;

    public Configuration(Settings sett) {
        this.sett = sett;
    }
    
    public Problem[] getProblem_set() {
        return problem_set;
    }
    
    public void init () {
        String config_file = sett.GetProp("config_path");
        String config_str = FileWrapper.readStream(config_file);
        /*
        System.out.println(config_str);
        Problem p2 = new Problem("Prob 2", 1, 5, new String[] {"file3.txt", "file4.txt"}, 2f);
        Problem p1 = new Problem("Prob 1", 1, 2, new String[] {"file1.txt", "file2.txt"}, 2f);
        System.out.println(new Gson().toJson(p1));
        */
        
        PrintStream out = System.out;
        out.println(config_str);
        if (!config_str.equals("")) {
            Gson gson = new Gson();
            this.problem_set = gson.fromJson(config_str, Problem[].class);
            this.no_of_problems = problem_set.length;
            
            System.out.println(no_of_problems);
            /*
            out.println(Arrays.deepToString(problem_set));
            System.out.println(problem_set[0].tc_input_buffers);
            System.out.println("Input: \n" + problem_set[0].getTestCaseInput(0) + "\nEnd Input");
            System.out.println("Output: \n" + problem_set[0].getTestCaseOutput(0) + "\nEnd Output");
                    */
        }
        
        //System.out.println(p.name + " " + Arrays.deepToString(p.tc_files));
    }
}
