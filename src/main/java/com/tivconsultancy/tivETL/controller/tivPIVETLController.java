/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tivconsultancy.tivETL.controller;

import com.tivconsultancy.opentiv.datamodels.Result1D;
import com.tivconsultancy.opentiv.datamodels.Results1DPlotAble;
import com.tivconsultancy.opentiv.datamodels.overtime.Database;
import com.tivconsultancy.opentiv.highlevel.methods.Method;
import com.tivconsultancy.tivETL.data.DataPIVETL;
import com.tivconsultancy.tivETL.tivPIVETLMethod;
import com.tivconsultancy.tivGUI.Dialogs.Data.processes.DialogProcessing;
import com.tivconsultancy.tivGUI.StaticReferences;
import com.tivconsultancy.tivGUI.controller.BasicController;
import com.tivconsultancy.tivGUI.controller.ControllerUI;
import com.tivconsultancy.tivGUI.startup.StartUpSubControllerImageTools;
import com.tivconsultancy.tivGUI.startup.StartUpSubControllerLog;
import com.tivconsultancy.tivGUI.startup.StartUpSubControllerPlots;
import com.tivconsultancy.tivGUI.startup.StartUpSubControllerViews;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javafx.application.Platform;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author TZ ThomasZiegenhein@TIVConsultancy.com +1 480 494 7254
 */
public class tivPIVETLController extends BasicController {

    protected File mainFolder;
    protected List<File> ReadInFile;
    Map<String, Dialog> openDialogBoxes = new HashMap<>();
    protected DataPIVETL dataPIVETL;

    public tivPIVETLController() {
        ReadInFile = new ArrayList<>();
        initData();
    }
    
    private void initData(){
        dataPIVETL = new DataPIVETL();
    }

    protected void initSubControllers() {
        subViews = new StartUpSubControllerViews(this);
        subPlots = new StartUpSubControllerPlots();
        subMenu = new tivPIVETLSubControllerMenu();
        subLog = new StartUpSubControllerLog();
        subSQL = new tivPIVETLSubControllerSQL();
        subImageTools = new StartUpSubControllerImageTools();
    }
    
    public DataPIVETL getDataPIVETL(){
        return dataPIVETL;
    }

    @Override
    public void clickOnImage(int i, int j, MouseEvent evt, String ident) {
        /**
         * Not needed in implementation
         */
    }

    @Override
    public void buttonPressed(KeyEvent evt, String ident) {
        /**
         * Not needed in implementation
         */
    }

    @Override
    public Dialog getDialog(Enum ident) {
        return openDialogBoxes.get(ident.toString());
    }

    @Override
    public void setDialog(Enum ident, Dialog dialogBox) {
        openDialogBoxes.put(ident.toString(), dialogBox);
    }

    @Override
    public void storeTempData() {
        /**
         * Not needed in implementation
         */
    }

    @Override
    public void startNewMethod(Method newMethod) {
        currentMethod = newMethod;
        ReadInFile = new ArrayList<>();
        initSubControllers();
        createHints(currentMethod);
    }

    @Override
    public void startNewIndexStep() {
        /**
         * Not needed in implementation
         */
    }

    @Override
    public void setSelectedFile(File fOld, File f) {
        this.selectedFile = f;
        this.getCurrentMethod().setFiles(new File[]{f});
        try {
            getCurrentMethod().readInFileForView(f);
        } catch (Exception ex) {
            StaticReferences.getlog().log(Level.SEVERE, "Cannot read file", ex);
        }
        subViews.update();
    }

    @Override
    public Database getDataBase() {
        return null;
    }

    @Override
    public List<File> getInputFiles(String name) {
        return ReadInFile;
    }

    @Override
    public Results1DPlotAble get1DResults() {
        return new Result1D(0);
    }

    @Override
    public void loadSession(File f) {
        startNewSession(f);
    }

    @Override
    public void startNewSession(File inputFolder) {
        if (getCurrentMethod() == null) {
            startNewMethod(new tivPIVETLMethod());
        }
        if (inputFolder != null && inputFolder.exists()) {
            mainFolder = inputFolder;
            ReadInFile = new ArrayList<>();
            for (File f : mainFolder.listFiles()) {
                if (f.isDirectory()) {
                    continue;
                }
                ReadInFile.add(f);
            }
            Collections.sort(ReadInFile);
        }
        mainFrame.startNewSession();
    }

    @Override
    public void runCurrentStep(String... options) {
        StaticReferences.getlog().log(Level.SEVERE, "No processing implemented");
    }

    @Override
    public void run(String... options) {
        if (options != null && options.length >= 1 && options[0] != null && options[0].toString().equals(runOptions.INSPIC.toString())) {
            blockUIForProceess();
            Dialog dialogProgress = new DialogProcessing();
            StaticReferences.controller.setDialog(ControllerUI.DialogNames_Default.PROCESS, dialogProgress);
            dialogProgress.show();
            Thread running = new Thread() {
                @Override
                public void run() {
                    timeline:
                    for (File f : ReadInFile) {
                        try {
                            StaticReferences.getlog().log(Level.SEVERE, "Starting for: " + f);
                            setSelectedFile(null, f);
                            try {
                                getCurrentMethod().runParts(runOptions.INSPIC.toString());
                                subViews.update();
                            } catch (Exception ex) {
                                StaticReferences.getlog().log(Level.SEVERE, "Unable to finish step: " + f + " : " + ex.getMessage(), ex);
                            }
                        } catch (Exception e) {
                            StaticReferences.getlog().log(Level.SEVERE, "Unable to select file and start new timestep : " + e.getMessage(), e);
                        }
                    }
                    releaseUIAfterProceess();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            StaticReferences.controller.getDialog(ControllerUI.DialogNames_Default.PROCESS).close();
                        }
                    });
                }
            };
            setRunningThread(running);
            try {
                running.start();
            } catch (Exception e) {
                StaticReferences.getlog().log(Level.SEVERE, "Thread stopped : " + e.getMessage(), e);
                releaseUIAfterProceess();
            }
        }
    }

    @Override
    public void blockUIForProceess() {
        mainFrame.deactivateImageTree();
    }

    @Override
    public void releaseUIAfterProceess() {
        mainFrame.activateImageTree();
    }

    public static enum runOptions {
        INSPIC
    }
    
    public static enum DialogNames{
        PICSQL
    }

}
