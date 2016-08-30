/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codeevaluator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author debd_admin
 */

class Worker extends Thread {
    Process proc;
    int exit_v;
    InputStream stderr = null;
    InputStream stdout = null;
    StringBuilder prog_err_stb;
    StringBuilder prog_op_stb;
    BufferedReader prog_op_br;
    BufferedReader prog_err_br;
    long start_time;
    long end_time;
    String tmp;
    boolean sentinel;
    
    public Worker(Process proc) throws InterruptedException {
        this.proc = proc;
        stderr = proc.getErrorStream();
        stdout = proc.getInputStream();
        prog_op_br = new BufferedReader(new InputStreamReader(stdout));
        prog_err_br = new BufferedReader(new InputStreamReader(stderr));
        prog_op_stb = new StringBuilder();
        prog_err_stb = new StringBuilder();
        sentinel = false;
    }
    
    @Override
    public void run() {
        try {
            start_time = System.currentTimeMillis();
            try {
                // stdout
                while ((tmp = prog_op_br.readLine()) != null) {
                    prog_op_stb.append(tmp + "\n");
                    ////System.out.println ("[Stdout] " + tmp);
                }
                prog_op_br.close();

                //////System.out.write(input.getBytes());
                // stderr
                //////System.out.println("\nOutput:" + prog_op_stb.toString());
                while ((tmp = prog_err_br.readLine()) != null) {
                    prog_err_stb.append(tmp + "\n");
                    ////System.out.println ("[Stderr] " + tmp);
                }
                prog_err_br.close();
                
            } catch (IOException ex) {
                exit_v = 49;
                return;
            }  
            
            ////System.out.println("worker executing");
            exit_v = proc.waitFor();
            end_time = System.currentTimeMillis();
            ////System.out.println("worker complete" + prog_op_stb.toString());
            sentinel = true;  // completed execution
            
        } catch (InterruptedException ie) {
            ////System.out.println("worker inturr");
            //Thread.currentThread().interrupt();
        }
    }
}

public class ExecutionManager {
    Process process;
    OutputStream stdin = null;
    InputStream stderr = null;
    InputStream stdout = null;
    HashMap<String, String> result = new HashMap<>();
    String prog_op, prog_stderr;
    long duration;
    Worker w;

    private void checkCorrectness (Problem p, int tc_no, String[] outputs) {
        String correct_output = p.getTestCaseOutput(tc_no);
        //System.out.println("op:'" + prog_op.trim() + "'\ncorr:'" + correct_output + "'");
        
        String op = prog_op.trim();
        if (correct_output.equals(op)) {
            ////System.out.println("Match!");
            result.put("status", "0");    // ans ok
            //System.out.println("status is 0");
        } else {
            ////System.out.println("No Match!");
            result.put("status", "1");    // ans not ok
            result.put("errmsg", "Answer Mismatch");
        }
        //System.out.println("opar size + " + outputs.length);
        outputs[tc_no] = op;
    }
    
    public HashMap<String, String> execute (Problem p, int tc_no, String sol_file, String compiler_path, String[] op) {
        result.clear();
        
        String exec_string = compiler_path + " " + '"' + sol_file + '"';
        long timeout = (long)p.timeout;
        
        try {
            Process process = Runtime.getRuntime().exec(exec_string);
            stdin = process.getOutputStream();
            stderr = process.getErrorStream();
            stdout = process.getInputStream();
            
            BufferedReader cmp_op_br = new BufferedReader(new InputStreamReader(stderr));
            String tmp;
            StringBuilder cmp_stb = new StringBuilder();
            while ((tmp = cmp_op_br.readLine()) != null) {
                cmp_stb.append(tmp + "\n");
            }
            cmp_op_br.close();
            
            try {
                int exit_v;
                exit_v = process.waitFor();
                if (exit_v != 0) {
                    ////System.out.println(cmp_stb.toString());
                    result.put("compiler_msg", cmp_stb.toString());
                    return result;
                }
            } catch (InterruptedException ex) {
                //Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
                ////System.out.println("Compilation Inturrepted");
                result.put("compiler_msg", "Compilation Inturrepted");
                return result;
            }
        } catch (IOException ex) {
            //Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
            ////System.out.println("IOException cmp2");
            result.put("compiler_msg", "IOException");
            return result;
        }
        
        // run the program
        String input = p.getTestCaseInput(tc_no);
        //exec_string = new File(sol_file).getParent() + "\\a.exe";
        exec_string = "a.exe";
        ////System.out.println(exec_string);
        try {
            process = Runtime.getRuntime().exec(exec_string);
            stdin = process.getOutputStream();
            stdin.write(input.getBytes());
            stdin.flush();
            stdin.close();
            
            w = new Worker(process);
            w.start();
            
            w.join(timeout);
            if (w.getState() != Thread.State.TERMINATED) {
                w.interrupt();  // inturrupt() is not blocking.
                                // cant rely on worker sampling end time
                w.end_time = System.currentTimeMillis();
                throw new InterruptedException();
            }
            
            prog_op = w.prog_op_stb.toString();
            prog_stderr = w.prog_err_stb.toString();
            //System.out.println("err:" + prog_stderr);
            
            duration = w.end_time - w.start_time;
            result.put("duration", String.valueOf(duration));
            result.put("exit_code", String.valueOf(w.exit_v));
            if (w.exit_v != 0) {
                if (w.exit_v == 49) {
                    result.put("status", "3");
                    result.put("errmsg", "IOException");
                    ////System.out.println("IOException while executing program");
                }
                else {
                    ////System.out.println(prog_stderr + "Err Code: " + w.exit_v);
                    result.put("status", "1"); //error
                    result.put("errmsg", prog_stderr); //may be a segfault ?
                }
                return result;
            }
            
            checkCorrectness(p, tc_no, op);
            ////System.out.println("executor complete");
            
        } catch (IOException ex) {
            //Logger.getLogger(Executor.class.getName()).log(Level.SEVERE, null, ex);
            ////System.out.println("IOException prog");
            result.put("status", "3");
            result.put("duration", "0");
            result.put("errmsg", "Segmentation fault.");
            //result.put("errmsg", "IOException while executing program");
            return result;
        } catch (InterruptedException ex) {
            ////System.out.println("executor inturr");
            duration = w.end_time - w.start_time;
            ////System.out.println(w.end_time + " " + w.start_time + " " + duration);
            result.put("duration", String.valueOf(duration));            
            result.put("status", "2");    // timeout
            result.put("errmsg", "Time Limit Exceeded");
        } finally {
            process.destroy();
        }
        
        return result;
    }
}
