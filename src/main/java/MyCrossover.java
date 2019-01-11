import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.ArrayList;
import java.util.List;

public class MyCrossover implements CrossoverOperator<MyVRPSolution> {

    private double crossoverProbability = 1.0;
    private BoundedRandomGenerator<Integer> cuttingPointRandomGenerator;
    private RandomGenerator<Double> crossoverRandomGenerator;

    /**
     * Constructor
     */
    public MyCrossover(double crossoverProbability) {
        this(crossoverProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
    }

    /**
     * Constructor
     */
    public MyCrossover(double crossoverProbability, RandomGenerator<Double> randomGenerator) {
        this(crossoverProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
    }

    /**
     * Constructor
     */
    public MyCrossover(double crossoverProbability, RandomGenerator<Double> crossoverRandomGenerator, BoundedRandomGenerator<Integer> cuttingPointRandomGenerator) {
        if ((crossoverProbability < 0) || (crossoverProbability > 1)) {
            throw new JMetalException("Crossover probability value invalid: " + crossoverProbability);
        }
        this.crossoverProbability = crossoverProbability;
        this.crossoverRandomGenerator = crossoverRandomGenerator;
        this.cuttingPointRandomGenerator = cuttingPointRandomGenerator;
    }

    /* Getters */
    public double getCrossoverProbability() {
        return crossoverProbability;
    }

    /* Setters */
    public void setCrossoverProbability(double crossoverProbability) {
        this.crossoverProbability = crossoverProbability;
    }

    @Override
    public int getNumberOfRequiredParents() {
        return 2;
    }

    @Override
    public int getNumberOfGeneratedChildren() {
        return 2;
    }


    @Override
    public List<MyVRPSolution> execute(List<MyVRPSolution> parents) {
        if (null == parents) {
            throw new JMetalException("Null parameter");
        } else if (parents.size() != 2) {
            throw new JMetalException("There must be two parents instead of " + parents.size());
        }

        return doCrossover2(crossoverProbability, parents);
    }

    private List<MyVRPSolution> doCrossover2(double probability, List<MyVRPSolution> parents) {
        List<MyVRPSolution> offspring = new ArrayList<>();
        offspring.add((MyVRPSolution) parents.get(0).copy());
        offspring.add((MyVRPSolution) parents.get(1).copy());

        int permutationLength = parents.get(0).getNumberOfCities();

        if (crossoverRandomGenerator.getRandomValue() < probability) {
            int cuttingPoint1;
            int cuttingPoint2;
            // STEP 1: Get two cutting points
            cuttingPoint1 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);
            cuttingPoint2 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);
            while (cuttingPoint2 == cuttingPoint1)
                cuttingPoint2 = cuttingPointRandomGenerator.getRandomValue(0, permutationLength - 1);
            if (cuttingPoint1 > cuttingPoint2) {
                int swap;
                swap = cuttingPoint1;
                cuttingPoint1 = cuttingPoint2;
                cuttingPoint2 = swap;
            }

            // STEP 2: Get the subchains to interchange
            int marked1[] = new int[permutationLength];
            int marked2[] = new int[permutationLength];
            for (int i = 0; i < permutationLength; i++)
                marked1[i] = marked2[i] = -1;

            // STEP 3: Interchange
            for (int i = cuttingPoint1; i <= cuttingPoint2; i++) {
                offspring.get(0).setVariableValue(i, parents.get(0).getVariableValue(i));
                offspring.get(1).setVariableValue(i, parents.get(1).getVariableValue(i));

                marked1[parents.get(0).getVariableValue(i)] = 1;
                marked2[parents.get(1).getVariableValue(i)] = 1;
            }

            // STEP 4: Copy order from parent
            int index1 = 0;
            int index2 = 0;

            if (cuttingPoint1 == 0) {
                index1 = index2 = cuttingPoint2 + 1;
            }
            for (int i = 0; i < permutationLength; i++) {
                if (marked1[parents.get(1).getVariableValue(i)] < 0) {
                    offspring.get(0).setVariableValue(index1, parents.get(1).getVariableValue(i));
                    index1++;
                    if (index1 == cuttingPoint1) {
                        index1 = cuttingPoint2 + 1;
                    }
                }
                if (marked2[parents.get(0).getVariableValue(i)] < 0) {
                    offspring.get(1).setVariableValue(index2, parents.get(0).getVariableValue(i));
                    index2++;
                    if (index2 == cuttingPoint1) {
                        index2 = cuttingPoint2 + 1;
                    }
                }
            }
        }
        return offspring;
    }
}
