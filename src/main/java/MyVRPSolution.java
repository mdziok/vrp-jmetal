import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.impl.AbstractGenericSolution;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyVRPSolution extends AbstractGenericSolution<Integer, MyVRPProblem> {

    private int numberOfVehicles;
    private int numberOfCities;
    private List<Integer> listOfVehiclesRoutesLength;

    protected MyVRPSolution(MyVRPProblem problem, int numberOfCities, int numberOfVehicles, List<Integer> citiesList, List<Integer> vehiclesList) {
        super(problem);
        this.setNumberOfCities(numberOfCities);
        this.setNumberOfVehicles(numberOfVehicles);
        for (int i = 0; i < numberOfCities; i++) {
            setVariableValue(i, citiesList.get(i));
        }
        for (int i = 0; i < numberOfVehicles; i++) {
            setVariableValue(i + numberOfCities, vehiclesList.get(i));
        }
    }

    protected MyVRPSolution(MyVRPProblem problem) {
        super(problem);
        setNumberOfVehicles(problem.getNumberOfVehicles());
        setNumberOfCities(problem.getNumberOfCities());
        listOfVehiclesRoutesLength = new ArrayList<>(this.getNumberOfVehicles());

        if (this.getNumberOfVehicles() > this.getNumberOfCities()) {
            this.setNumberOfVehicles(this.getNumberOfCities());
        }

        int meanLength = this.getNumberOfCities() / this.getNumberOfVehicles();
        int sumLength = 0;
        for (int i = 0; i < getNumberOfVehicles() - 1; i++) {
            listOfVehiclesRoutesLength.add(meanLength);
            sumLength += meanLength;
        }
        listOfVehiclesRoutesLength.add(getNumberOfCities() - sumLength);


        List<Integer> randomSequence = new ArrayList<>(problem.getNumberOfCities());
        for (int j = 0; j < problem.getNumberOfCities(); j++) {
            randomSequence.add(j);
        }
        java.util.Collections.shuffle(randomSequence);

        for (int i = 0; i < problem.getNumberOfCities(); i++) {
            setVariableValue(i, randomSequence.get(i));
        }

        for (int i = 0; i < problem.getNumberOfVehicles(); i++) {
            setVariableValue(i + problem.getNumberOfCities(), listOfVehiclesRoutesLength.get(i));
        }

    }

    /**
     * Copy Constructor
     */
    public MyVRPSolution(MyVRPSolution solution) {
        super(solution.problem);
        for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
            setObjective(i, solution.getObjective(i));
        }

        for (int i = 0; i < problem.getNumberOfVariables(); i++) {
            setVariableValue(i, solution.getVariableValue(i));
        }

        attributes = new HashMap<>(solution.attributes);
        this.setNumberOfCities(solution.numberOfCities);
        this.setNumberOfVehicles(solution.getNumberOfVehicles());
        this.setListOfVehiclesRoutesLength(solution.getListOfVehiclesRoutesLength());
    }

    @Override
    public String getVariableValueString(int index) {
        return Integer.toString(getVariableValue(index));
    }

    @Override
    public Solution<Integer> copy() {
        return new MyVRPSolution(this);
    }

    private void setNumberOfVehicles(int numberOfVehicles) {
        this.numberOfVehicles = numberOfVehicles;
    }

    public int getNumberOfVehicles() {
        return this.numberOfVehicles;
    }

    public List<Integer> getListOfVehiclesRoutesLength() {
        return listOfVehiclesRoutesLength;
    }

    private void setListOfVehiclesRoutesLength(List<Integer> listOfVehiclesRoutesLength) {
        this.listOfVehiclesRoutesLength = listOfVehiclesRoutesLength;
    }

    private void setNumberOfCities(int numberOfCities) {
        this.numberOfCities = numberOfCities;
    }

    public int getNumberOfCities() {
        return numberOfCities;
    }
}
