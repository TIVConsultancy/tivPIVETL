/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tivconsultancy.tivETL.protocols;

import com.tivconsultancy.opentiv.helpfunctions.strings.StringWorker;
import com.tivconsultancy.opentiv.highlevel.protocols.NameSpaceProtocolResults1D;
import com.tivconsultancy.opentiv.highlevel.protocols.Protocol;
import com.tivconsultancy.opentiv.highlevel.protocols.UnableToRunException;
import com.tivconsultancy.opentiv.imageproc.primitives.ImageInt;
import com.tivconsultancy.opentiv.math.specials.LookUp;
import com.tivconsultancy.tivETL.controller.tivPIVETLController;
import com.tivconsultancy.tivETL.controller.tivPIVETLSubControllerSQL;
import com.tivconsultancy.tivGUI.StaticReferences;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author TZ ThomasZiegenhein@TIVConsultancy.com +1 480 494 7254
 */
public class Prot_InsertPictureToSQL extends Protocol implements Serializable {

    private static final long serialVersionUID = -5852161585940319580L;

    private String name = "PictureToSQL";
    LookUp<Double> results1D = new LookUp<>();

    public Prot_InsertPictureToSQL() {
        super();
        buildLookUp();
        initSettins();
        buildClusters();
    }

    private void buildLookUp() {
    }

    @Override
    public NameSpaceProtocolResults1D[] get1DResultsNames() {
        return new NameSpaceProtocolResults1D[0];
    }

    @Override
    public List<String> getIdentForViews() {
        return Arrays.asList(new String[]{});
    }

    @Override
    public void setImage(BufferedImage bi) {
        buildLookUp();
    }

    @Override
    public Double getOverTimesResult(NameSpaceProtocolResults1D ident) {
        return results1D.get(ident.toString());
    }

    @Override
    public void run(Object... input) throws UnableToRunException {
        if (input != null && input.length >= 2 && input[0] != null && input[1] != null && input[0] instanceof ImageInt && input[1] instanceof File) {
            tivPIVETLSubControllerSQL sql = (tivPIVETLSubControllerSQL) StaticReferences.controller.getSQLControler(null);
            ImageInt picToInsert = (ImageInt) input[0];
            File filePicture = (File) input[1];
            List<String> nameOfFileSep = StringWorker.cutElements2(filePicture.getName(), ".");
            
            sql.writeIMGToSQL(((tivPIVETLController) StaticReferences.controller).getDataPIVETL().selectedExperiment,
                              filePicture.getName(),
                              picToInsert, nameOfFileSep.get(nameOfFileSep.size()-1));
        }
    }

    @Override
    public Object[] getResults() {
        return new Object[0];
    }

    @Override
    public String getType() {
        return name;
    }

    private void initSettins() {
//        this.loSettings.add(new SettingObject("Export->SQL", "sql_activation", false, SettingObject.SettingsType.Boolean));
    }

    @Override
    public void buildClusters() {
//        SettingsCluster sqlCluster = new SettingsCluster("SQL",
//                                                         new String[]{"sql_activation", "sql_experimentident", "sql_upsert", "sql_evalsettingspiv", "sql_evalsettingsbub"}, this);
//        sqlCluster.setDescription("Handles the export to the SQL database");
//        lsClusters.add(sqlCluster);                       
    }

    @Override
    public BufferedImage getView(String identFromViewer) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
