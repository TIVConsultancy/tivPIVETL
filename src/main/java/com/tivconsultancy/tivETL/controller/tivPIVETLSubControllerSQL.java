/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tivconsultancy.tivETL.controller;

import com.tivconsultancy.opentiv.datamodels.SQL.PostgreSQL;
import com.tivconsultancy.opentiv.imageproc.primitives.ImageInt;
import com.tivconsultancy.tivGUI.StaticReferences;
import com.tivconsultancy.tivGUI.startup.StartUpSubControllerSQL;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author TZ ThomasZiegenhein@TIVConsultancy.com +1 480 494 7254
 */
public class tivPIVETLSubControllerSQL extends StartUpSubControllerSQL {

    private boolean isConnected = false;
    protected String selectedExperiment = "none";
    Connection conn;

    public tivPIVETLSubControllerSQL() {

    }

    @Override
    public String connect(String user, String password, String database, String host) {
        sqlData = new PostgreSQL("jdbc:postgresql://" + host + "/" + database, user, password);
        conn = sqlData.connect();
        if (sqlData.getStatus() == null) {
            StaticReferences.getlog().log(Level.SEVERE, "SQL Status: " + sqlData.getStatus(), new Throwable());
            isConnected = false;
        } else {
            StaticReferences.getlog().log(Level.INFO, "SQL Status: " + sqlData.getStatus());
            isConnected = true;
        }
        return sqlData.getStatus();
    }

    public boolean writeIMGToSQL(String experiment, String ident, ImageInt input, String picFormat) {
        if (!isConnected) {
            return false;
        }
        if ("adminPIV".equals(StaticReferences.controller.getSQLControler(null).getUser())) {
            try (PreparedStatement pstmt = conn.prepareStatement(getupserEntryPic()); InputStream is = getISfromBuff(input.getBuffImage(), picFormat);) {
                pstmt.setString(1, experiment);
                pstmt.setString(2, ident);
                pstmt.setBinaryStream(3, is);
                pstmt.executeUpdate();
                return true;
            } catch (Exception e) {
                StaticReferences.getlog().log(Level.SEVERE, "Cannot write to database: " + input.sIdent, e);
            }
        } else {
            try (PreparedStatement pstmt = conn.prepareStatement(getinsertEntryPic()); InputStream is = getISfromBuff(input.getBuffImage(), picFormat);) {
                pstmt.setString(1, experiment);
                pstmt.setString(2, ident);
                pstmt.setBinaryStream(3, is);
                pstmt.executeUpdate();
                return true;
            } catch (Exception e) {
                StaticReferences.getlog().log(Level.SEVERE, "Cannot write to database: " + input.sIdent, e);
            }
        }

        return false;
    }
    
    public boolean writeSettingsToSQL(String experiment, String ident, String settingsString) {
        if (!isConnected) {
            return false;
        }
        try {
            if ("adminPIV".equals(StaticReferences.controller.getSQLControler(null).getUser())) {
            getDatabase(null).performStatement(getupserEntrySettings(experiment, ident, settingsString));            
        } else {
            getDatabase(null).performStatement(getinsertEntrySettings(experiment, ident, settingsString));            
        }
        } catch (Exception e) {
            StaticReferences.getlog().log(Level.SEVERE, "Cannot insert Settings", e);
        }
        
        return true;
    }

    public InputStream getISfromBuff(BufferedImage image, String picFormat) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, picFormat, os);
        InputStream is = new ByteArrayInputStream(os.toByteArray());
        return is;
    }

    public BufferedImage readIMGFromSQL(String experiment, String ident) throws SQLException {
        if (!isConnected) {
            return null;
        }
        BufferedImage img = null;
        try (PreparedStatement pstmt = conn.prepareStatement(getreadEntryPic(ident, experiment)); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                try (InputStream is = rs.getBinaryStream(1)) {
                    img = ImageIO.read(is);
                    break;
                } catch (IOException ex) {
                    Logger.getLogger(tivPIVETLSubControllerSQL.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (Exception e) {
            StaticReferences.getlog().log(Level.SEVERE, "Cannot read from database: " + ident, e);
        }
//        if (img != null) {
//            try {
//                IMG_Writer.PaintGreyPNG(img, new File("D:\\Trash\\SQLTest\\sql_post.png"));
//            } catch (IOException ex) {
//                Logger.getLogger(tivPIVETLSubControllerSQL.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        return img;
    }

    public List<String> getAvailExperiments() {
        return getColumnEntries("piv", "experiment", "ident");
    }

    public String getreadEntryPic(String ident, String experiment) {
        String sqlStatement = "SELECT picture FROM pivexp.pictures WHERE ident = '" + ident + "' AND experiment = '" + experiment + "'";
        return sqlStatement;
    }

    public String getinsertEntryPic() {
        String sqlStatement = "INSERT INTO pivexp.pictures (experiment, ident, picture)" + " VALUES(?,?,?)";
        return sqlStatement;
    }

    public String getupserEntryPic() {
        String sqlStatement = "INSERT INTO pivexp.pictures (experiment, ident, picture)" + " VALUES(?,?,?)"
                + "ON CONFLICT (experiment, ident) DO UPDATE SET "
                + "experiment = EXCLUDED.experiment, "
                + "ident = EXCLUDED.ident,"
                + "picture = EXCLUDED.picture";
        return sqlStatement;
    }
    
    public String getinsertEntrySettings(String experiment, String ident, String settingsString) {
        String sqlStatement = "INSERT INTO pivexp.pictures (experiment, ident, settingstring)" + " VALUES("+ experiment+","+ ident+","+ settingsString+")";
        return sqlStatement;
    }
    
    public String getupserEntrySettings(String experiment, String ident, String settingsString) {
        String sqlStatement = "INSERT INTO pivexp.pictures (experiment, ident, settingstring)" + " VALUES("+ experiment+","+ ident+","+ settingsString+")"
                + "ON CONFLICT (experiment, ident) DO UPDATE SET "
                + "experiment = EXCLUDED.experiment, "
                + "ident = EXCLUDED.ident,"
                + "settingstring = EXCLUDED.settingstring";
        return sqlStatement;
    }
    
    public void importCSVfile(String sDir) {
        File f = new File(sDir);
        for (File af : f.listFiles()) {
            if (af.getName().contains("Complete") && af.getName().contains(".csv")) {
                int iAffectedRows = getDatabase(null).performStatement("COPY piv.liqvelo (experiment, settings, timestampexp, posx, posy, posz, velox, veloy) FROM '" + af.toPath() + "' CSV HEADER;");
                System.out.println(iAffectedRows);
            }
        }
    }

}
