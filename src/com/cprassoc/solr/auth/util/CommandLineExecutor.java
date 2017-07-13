/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cprassoc.solr.auth.util;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;

/**
 *
 * @author kevin
 */
public class CommandLineExecutor {

    public static int exec(String cmd) {
      int exitValue =  -1;
        try {
            CommandLine cmdLine = CommandLine.parse(cmd);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setExitValue(1);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
            executor.setWatchdog(watchdog);
             exitValue = executor.execute(cmdLine);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exitValue;
    }
    
    public static int execChain(ArrayList<String> cmds){
        int exitValue = -1;
        try{
        for(int i=0; i<cmds.size(); i++){
            exitValue = exec(cmds.get(i));
            if(exitValue > 0){
                throw new IOException();
            }
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        return exitValue;
    }
}
