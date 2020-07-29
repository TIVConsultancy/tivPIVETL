/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tivconsultancy.tivETL;


import com.tivconsultancy.tivETL.controller.tivPIVETLController;
import com.tivconsultancy.tivGUI.MainFrame;
import com.tivconsultancy.tivGUI.StaticReferences;
import com.tivconsultancy.tivGUI.TIVScene;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Thomas Ziegenhein
 */
public class Main extends Application {


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        launch(args);
    }    

    @Override
    public void start(Stage primaryStage) throws Exception {
        showEmptyMainFrame(primaryStage);
    }

    public static void showEmptyMainFrame(Stage primaryStage) throws IOException {

        StaticReferences.setStandardIcons(new dummyClass(), "com/tivconsultancy/tivPIVETL/menuicons/");       

        List<String> icons = new ArrayList<>();
        icons.add("/com/tivconsultancy/tivPIVETL/icons/Icon128x128.png");
        icons.add("/com/tivconsultancy/tivPIVETL/icons/Icon64x64.png");
        icons.add("/com/tivconsultancy/tivPIVETL/icons/Icon32x32.png");
        icons.add("/com/tivconsultancy/tivPIVETL/icons/Icon16x16.png");
        MainFrame.setIcons(new dummyClass().getClass(), icons);
        MainFrame.setLoadPicture(new dummyClass().getClass(), "/com/tivconsultancy/tivPIVETL/icons/load.png");
        MainFrame.setLogo(new dummyClass().getClass(), "/com/tivconsultancy/tivPIVETL/logo/Logo-BlackBlueText_noBckGr_s.png");

        StaticReferences.controller = new tivPIVETLController();
        StaticReferences.controller.startNewMethod(new tivPIVETLMethod());
        MainFrame tivGUI = new MainFrame();
        TIVScene scene = new TIVScene(tivGUI);
        StaticReferences.controller.setScene(scene);
        StaticReferences.controller.setGUI(tivGUI);

        scene.getStylesheets().add(scene.getClass().getResource("/com/tivconsultancy/tivPIVETL/cssFiles/tiv.css").toExternalForm());

        primaryStage.getIcons().addAll(MainFrame.getIcon());
        primaryStage.setTitle("tivPIVETL");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
//        StaticReferences.controller.startNewSession(new File("D:\\Sync\\TIVConsultancy\\_Customers\\HZDR\\DataProject\\FolderHZDRDataManagement\\Examples-2020-3-30 20.46.28\\NabilLongColumn\\Water\\1p0"));
        StaticReferences.controller.startNewSession(null);
//        tivGUI.startNewSession();

    }
}
