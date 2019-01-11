import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaiii.NSGAIIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class MyRunner extends AbstractAlgorithmRunner {

    private static MyCSVWriter writer = new MyCSVWriter(".csv");

    public static void main(String[] args) throws JMetalException, IOException {
        JMetalRandom.getInstance().setSeed(100L);

        BaseType baseType = BaseType.CENTER_BASE;

        String filePath = "/tspInstances/kroA100.tsp";
        double crossoverProbability = 0.9;
        double mutationProbability = 0.2;
        int populationSize = 100;
        int maxEvaluations = 1000;
        int computationNumber = 10;


        for (String arg : args) {
            if (arg.startsWith("-s")) {
                populationSize = Integer.parseInt(arg.substring(3));
            } else if (arg.startsWith("-c")) {
                crossoverProbability = Double.parseDouble(arg.substring(3));
            } else if (arg.startsWith("-e")) {
                maxEvaluations = Integer.parseInt(arg.substring(3));
            } else if (arg.startsWith("-m")) {
                mutationProbability = Double.parseDouble(arg.substring(3));
            } else if (arg.startsWith("-N")) {
                computationNumber = Integer.parseInt(arg.substring(3));
            } else if (arg.startsWith("-f")) {
                filePath = arg.substring(3);
            } else if (arg.startsWith("-b")) {
                switch (arg.substring(3)) {
                    case "C":
                        baseType = BaseType.CENTER_BASE;
                        break;
                    case "M":
                        baseType = BaseType.MEAN_BASE;
                        break;
                    default:
                        break;
                }
            } else {
                System.out.println("Usage: \n" +
                        " -s [population size] \n" +
                        " -c [crossover probability] \n" +
                        " -m [mutation probability] \n" +
                        " -e [max number of evaluations] \n" +
                        " -f [file path to problem] \n" +
                        " -b [base type C|M] \n" +
                        " -N [repeat number]");
            }
        }

        JMetalLogger.logger.info("Program starts with parameters: \n" +
                String.format(" population size: %d \n", populationSize) +
                String.format(" crossover probability: %f \n", crossoverProbability) +
                String.format(" mutation probability: %f \n", mutationProbability) +
                String.format(" max number of evaluations: %d \n", maxEvaluations) +
                String.format(" file path to problem: %s \n", filePath) +
                String.format(" base type C|M: %s \n", baseType.toString()) +
                String.format(" repeat number: %d", computationNumber));


        Problem problem = new MyVRPProblem(baseType, filePath);
        CrossoverOperator crossover = new MyCrossover(crossoverProbability);
        MutationOperator mutation = new MyMutation(mutationProbability);
        SelectionOperator<List<MyVRPSolution>, MyVRPSolution> selection = new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<MyVRPSolution>());

        writer.setPath(String.format("%d_vrp_runner.csv", Instant.now().toEpochMilli()));
        writer.clear();

        for (int i = 0; i < computationNumber; i++) {
            Algorithm<List<MyVRPSolution>> algorithm = new SPEA2Builder<MyVRPSolution>(problem, crossover, mutation)
                    .setSelectionOperator(selection)
//                    .setCrossoverOperator(crossover)
//                    .setMutationOperator(mutation)
                    .setMaxIterations(maxEvaluations)
                    .setPopulationSize(populationSize)
                    .build();

            AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                    .execute();

            List<MyVRPSolution> population = algorithm.getResult();
            long computingTime = algorithmRunner.getComputingTime();

            double fitness1 = population.get(0).getObjective(0);
            double fitness2 = population.get(0).getObjective(1);

            new SolutionListOutput(population)
                    .setSeparator("\t")
                    .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
                    .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
                    .print();

            JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");
            JMetalLogger.logger.info(String.format("Fitness: %f, %f", fitness1, fitness2));
            writer.write(algorithm.getName(), baseType, filePath, populationSize, maxEvaluations, crossoverProbability, mutationProbability, fitness1, fitness2, computingTime);
        }
    }
}