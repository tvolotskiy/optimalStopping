package ru.ots.optimal_stopping;

import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.population.*;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.controler.Controler;
import org.matsim.core.controler.OutputDirectoryHierarchy;
import org.matsim.core.controler.events.IterationEndsEvent;
import org.matsim.core.controler.listener.IterationEndsListener;
import org.matsim.core.replanning.GenericStrategyManager;
import org.matsim.core.scenario.ScenarioUtils;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Runner {

    public static boolean disableOnFullPlanStack = false;
    public static void main(String[] args) throws FileNotFoundException {
        Config config ;
        if ( args.length==0 || args[0].equals("") ) {
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

        removeAgentsOnPt(scenario);
        PrintWriter printWriter = new PrintWriter(new Date().getTime()+"reroute.csv");
        controler.addControlerListener(new IterationEndsListener() {
            @Override
            public void notifyIterationEnds(IterationEndsEvent iterationEndsEvent) {
                printWriter.println(String.format("%d;%d",iterationEndsEvent.getIteration(), GenericStrategyManager.rerouteCount.get()));
            }
        });
        controler.run();
        System.out.println("Count of reroutes "+GenericStrategyManager.rerouteCount.get());
        printWriter.close();
    }

    private static void removeAgentsOnPt(Scenario scenario) {
        AgentsOnModeRemover remover = new AgentsOnModeRemover("pt", scenario.getPopulation());
        remover.clean();
    }


}
