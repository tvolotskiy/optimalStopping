package ru.ots.optimal_stopping;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.replanning.GenericStrategyManager;
import org.matsim.core.scenario.ScenarioUtils;

import java.util.ArrayList;
import java.util.List;

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

        List<Person> personList = new ArrayList<>(scenario.getPopulation().getPersons().values());

        for (Person person : personList) {
            Plan selectedPlan = person.getSelectedPlan();
            person.setSelectedPlan(null);
            for (Plan plan : new ArrayList<>(person.getPlans())) {
                person.removePlan(plan);
            }
            ArrayList<PlanElement> allPlanElements = new ArrayList<>(selectedPlan.getPlanElements());
            selectedPlan.getPlanElements().clear();
            Plan newPlan = scenario.getPopulation().getFactory().createPlan();
            newPlan.setPerson(person);
            for (PlanElement allPlanElement : allPlanElements) {
                if (allPlanElement instanceof Leg){
                    newPlan.addLeg((Leg) allPlanElement);
                } else if (allPlanElement instanceof Activity){
                    newPlan.addActivity((Activity) allPlanElement);
                }
            }
            person.addPlan(newPlan);
        }

        Controler controler = new Controler( scenario ) ;

        //controler.addControlerListener();

        controler.run();
        System.out.println("Count of reroutes "+GenericStrategyManager.rerouteCount.get());
    }
}
