/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tivconsultancy.tivETL;

import com.tivconsultancy.opentiv.helpfunctions.strings.StringWorker;
import com.tivconsultancy.opentiv.highlevel.methods.Method;
import com.tivconsultancy.opentiv.highlevel.protocols.Prot_ReadIMGFiles;
import com.tivconsultancy.opentiv.highlevel.protocols.Prot_SystemSettings;
import com.tivconsultancy.opentiv.highlevel.protocols.Protocol;
import com.tivconsultancy.opentiv.math.specials.LookUp;
import com.tivconsultancy.opentiv.math.specials.NameObject;
import com.tivconsultancy.tivETL.controller.tivPIVETLController;
import com.tivconsultancy.tivETL.protocols.Prot_InsertPictureToSQL;
import delete.com.tivconsultancy.opentiv.devgui.main.ImagePath;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TZ ThomasZiegenhein@TIVConsultancy.com +1 480 494 7254
 */
public class tivPIVETLMethod implements Method {

    protected LookUp<Protocol> methods;
    File currentFile;

    public tivPIVETLMethod() {
        initProtocols();
    }

    private void initProtocols() {
        methods = new LookUp<>();
        methods.add(new NameObject<>("read", new Prot_ReadIMGFiles()));
        methods.add(new NameObject<>("insertPICSQL", new Prot_InsertPictureToSQL()));
//        methods.add(new NameObject<>("system", new Prot_SystemSettings()));
    }

    @Override
    public List<ImagePath> getInputImages() {
        return new ArrayList<>();
    }

    @Override
    public List<Protocol> getProtocols() {
        return methods.getValues();
    }

    @Override
    public Protocol getProtocol(String ident) {
        return methods.get(ident);
    }

    @Override
    public void readInFileForView(File f) throws Exception {
        List<String> nameOfFileSep = StringWorker.cutElements2(f.getName(), ".");
        if (nameOfFileSep.isEmpty()) {
            return;
        }

        if (nameOfFileSep.contains("png") || nameOfFileSep.contains("jpg") || nameOfFileSep.contains("jpeg") || nameOfFileSep.contains("bmp")) {
            for (Protocol p : getProtocols()) {
                try {
                    if (p instanceof Prot_ReadIMGFiles) {
                        p.run(f);
                    }
                } catch (Exception ex) {
                    throw ex;
                }
            }
        }
    }

    @Override
    public void setFiles(File[] f) {
        currentFile = f[0];
    }

    @Override
    public void run() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void runParts(String ident) throws Exception {
        if (ident.equals(tivPIVETLController.runOptions.INSPIC.toString())) {
            List<String> nameOfFileSep = StringWorker.cutElements2(currentFile.getName(), ".");
            if (nameOfFileSep == null || nameOfFileSep.isEmpty()) {
                return;
            }
            if (nameOfFileSep.contains("png") || nameOfFileSep.contains("jpg") || nameOfFileSep.contains("jpeg") || nameOfFileSep.contains("bmp")) {
                getProtocol("read").run(new Object[]{currentFile});                
                getProtocol("insertPICSQL").run(new Object[]{getProtocol("read").getResults()[0], currentFile});
            }
        }
    }

    @Override
    public Prot_SystemSettings getSystemSetting(String ident) {
        return null;
//        return (Prot_SystemSettings) methods.get("system");
    }

}
