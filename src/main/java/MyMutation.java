import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;


public class MyMutation implements MutationOperator<MyVRPSolution> {
    private double mutationProbability;
    private RandomGenerator<Double> mutationRandomGenerator;
    private BoundedRandomGenerator<Integer> positionRandomGenerator;

    /**
     * Constructor
     */
    public MyMutation(double mutationProbability) {
        this(mutationProbability, () -> JMetalRandom.getInstance().nextDouble(), (a, b) -> JMetalRandom.getInstance().nextInt(a, b));
    }

    /**
     * Constructor
     */
    public MyMutation(double mutationProbability, RandomGenerator<Double> randomGenerator) {
        this(mutationProbability, randomGenerator, BoundedRandomGenerator.fromDoubleToInteger(randomGenerator));
    }

    /**
     * Constructor
     */
    public MyMutation(double mutationProbability, RandomGenerator<Double> mutationRandomGenerator, BoundedRandomGenerator<Integer> positionRandomGenerator) {
        if ((mutationProbability < 0) || (mutationProbability > 1)) {
            throw new JMetalException("Mutation probability value invalid: " + mutationProbability);
        }
        this.mutationProbability = mutationProbability;
        this.mutationRandomGenerator = mutationRandomGenerator;
        this.positionRandomGenerator = positionRandomGenerator;
    }

    /* Getters */
    public double getMutationProbability() {
        return mutationProbability;
    }

    /* Setters */
    public void setMutationProbability(double mutationProbability) {
        this.mutationProbability = mutationProbability;
    }


    @Override
    public MyVRPSolution execute(MyVRPSolution solution) {
        if (null == solution) {
            throw new JMetalException("Null parameter");
        }

        doMutation(solution);
        return solution;
    }

    private void doMutation(MyVRPSolution solution) {

        int permutationLength;
        permutationLength = solution.getNumberOfCities();

        if ((permutationLength != 0) && (permutationLength != 1)) {
            if (mutationRandomGenerator.getRandomValue() < mutationProbability) {
                int pos1 = positionRandomGenerator.getRandomValue(0, permutationLength - 1);
                int pos2 = positionRandomGenerator.getRandomValue(0, permutationLength - 1);

                while (pos1 == pos2) {
                    if (pos1 == (permutationLength - 1))
                        pos2 = positionRandomGenerator.getRandomValue(0, permutationLength - 2);
                    else
                        pos2 = positionRandomGenerator.getRandomValue(pos1, permutationLength - 1);
                }

                Integer temp = solution.getVariableValue(pos1);
                solution.setVariableValue(pos1, solution.getVariableValue(pos2));
                solution.setVariableValue(pos2, temp);
            }
        }

        permutationLength = solution.getNumberOfVehicles();
        if ((permutationLength != 0) && (permutationLength != 1)) {
            if (mutationRandomGenerator.getRandomValue() < mutationProbability) {
                int pos1 = positionRandomGenerator.getRandomValue(0, permutationLength - 1);
                int pos2 = positionRandomGenerator.getRandomValue(0, permutationLength - 1);

                while (pos1 == pos2) {
                    if (pos1 == (permutationLength - 1))
                        pos2 = positionRandomGenerator.getRandomValue(0, permutationLength - 2);
                    else
                        pos2 = positionRandomGenerator.getRandomValue(pos1, permutationLength - 1);
                }

                Integer tmp1 = solution.getVariableValue(solution.getNumberOfCities() + pos1);
                Integer tmp2 = solution.getVariableValue(solution.getNumberOfCities() + pos2);

                if (tmp1 != 0 && tmp2 != 0) {
                    tmp1 += 1;
                    tmp2 -= 1;
                    solution.setVariableValue(pos1 + solution.getNumberOfCities(), tmp1);
                    solution.setVariableValue(pos2 + solution.getNumberOfCities(), tmp2);
                }

            }
        }

    }
}
