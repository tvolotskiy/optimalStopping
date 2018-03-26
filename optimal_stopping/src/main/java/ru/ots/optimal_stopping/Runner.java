package ru.ots.optimal_stopping;

import org.matsim.api.core.v01.Scenario;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.replanning.GenericStrategyManager;
import org.matsim.core.scenario.ScenarioUtils;

public class Runner {

    public static boolean disableOnFullPlanStack = true;
    public static void main(String[] args) {
        Config config ;
        if ( args.length==0 || args[0]=="" ) {
            config = ConfigUtils.loadConfig( "input/config.xml" ) ;
            //config.controler().setLastIteration(1);
            config.controler().setOverwriteFileSetting( OutputDirectoryHierarchy.OverwriteFileSetting.deleteDirectoryIfExists );
        } else {
            config = ConfigUtils.loadConfig(args[0]) ;
        }


        Scenario scenario = ScenarioUtils.loadScenario(config) ;

        Controler controler = new Controler( scenario ) ;

        //controler.addControlerListener();

        controler.run();
        System.out.println("Count of reroutes "+GenericStrategyManager.rerouteCount.get());
    }
}
