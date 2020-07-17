package controller;

import javax.swing.*;
import java.awt.event.*;
import java.io.File;
import java.awt.*;
import java.util.*;


public class DirectoryChooser extends JPanel
{

  public static File main(String s[]) {
	String choosertitle = "Folder";
    JFrame frame = new JFrame("");
    JFileChooser chooser = new JFileChooser(); 
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle(choosertitle);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    //
    // disable the "All files" option.
    //
    chooser.setAcceptAllFileFilterUsed(false);
    //    
    if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) { 
      System.out.println("getCurrentDirectory(): " 
         +  chooser.getCurrentDirectory());
      System.out.println("getSelectedFile() : " 
         +  chooser.getSelectedFile());
      }
    else {
      System.out.println("No Selection ");
      }
    File file = chooser.getSelectedFile();
    file.mkdir();
    return file;
    }
}