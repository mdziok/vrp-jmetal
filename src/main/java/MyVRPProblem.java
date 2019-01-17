import org.uma.jmetal.problem.impl.AbstractGenericProblem;
import org.uma.jmetal.util.JMetalException;

import java.io.*;

public class MyVRPProblem extends AbstractGenericProblem<MyVRPSolution>{
    private int numberOfCities;
    private int numberOfVehicles;
    private double[][] distMatrix;
    private BaseType baseType;

    private int maxDriverPath = 5000;

    public MyVRPProblem(int numberOfCities, int numberOfVehicles){
        setNumberOfVariables(numberOfCities + numberOfVehicles);
        setNumberOfObjectives(2);
        setName("VRP Problem");
    }


    public MyVRPProblem(BaseType baseType, String fileName) throws  IOException {
        this.baseType = baseType;
        distMatrix = readProblem(fileName);
        numberOfVehicles = 10;

        setNumberOfVariables(numberOfCities + numberOfVehicles);
        setNumberOfObjectives(2);
        setName("VRP Problem");

    }

    public MyVRPProblem(String fileName) throws IOException {
        this(BaseType.CENTER_BASE, fileName);
    }

    @Override
    public void evaluate(MyVRPSolution solution) {
        //TODO znaleźć bazę
        //TODO dla każdego auta liczyć odległość

        int cityN = 0; //pointer on current city
        int fitness1 = 0;       //sum of distances
        int fitness2 = 0;       //number of vehicles

        int driverPathLength ;

        for (int i = getNumberOfCities(); i < getNumberOfVariables(); i++){  // iteration over vehicles
            driverPathLength = 0;
            if (solution.getVariableValue(i) > 0) { // if vehicle visit some cities...
//                fitness2++; // fitness2 - number of vehicles
                driverPathLength += distMatrix[solution.getVariableValue(cityN)][getNumberOfCities()]; // from base to first city
                for (int j = 0; j < solution.getVariableValue(i)-1; j++) {
                    int x ;
                    int y ;
                    x = solution.getVariableValue(cityN) ;
                    y = solution.getVariableValue(cityN+1) ;

                    driverPathLength += distMatrix[x][y];
                    cityN++;
                }
                driverPathLength += distMatrix[solution.getVariableValue(cityN)][getNumberOfCities()]; // from last city to base
                cityN++;
            }

            fitness1 += driverPathLength;
            if (driverPathLength > maxDriverPath) {
                fitness2 += (driverPathLength - maxDriverPath);
            }
        }

        solution.setObjective(0, fitness1);
        solution.setObjective(1, fitness2);
    }

    @Override
    public MyVRPSolution createSolution() {
        return new MyVRPSolution(this);
    }

    public int getNumberOfVehicles() {
        return numberOfVehicles;
    }

    private void setNumberOfVehicles(int numberOfVehicles) {
        this.numberOfVehicles = numberOfVehicles;
    }

    public int getNumberOfCities() {
        return numberOfCities;
    }

    private double [][] readProblem(String file) throws IOException {
        double [][] matrix;

        InputStream in = getClass().getResourceAsStream(file);
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);

        StreamTokenizer token = new StreamTokenizer(br);
        try {
            boolean found ;
            found = false ;

            token.nextToken();
            while(!found) {
                if ((token.sval != null) && ((token.sval.compareTo("DIMENSION") == 0)))
                    found = true ;
                else
                    token.nextToken() ;
            }

            token.nextToken() ;
            token.nextToken() ;

            numberOfCities =  (int)token.nval ;

            matrix = new double[numberOfCities+1][numberOfCities+1] ;

            // Find the string SECTION
            found = false ;
            token.nextToken();
            while(!found) {
                if ((token.sval != null) &&
                        ((token.sval.compareTo("SECTION") == 0)))
                    found = true ;
                else
                    token.nextToken() ;
            }

            double [] c = new double[2*(numberOfCities+1)] ;

            double sum_x = 0;
            double sum_y = 0;

            double max_x = 0;
            double min_x = Integer.MAX_VALUE;
            double min_y = Integer.MAX_VALUE;
            double max_y = 0;

            for (int i = 0; i < numberOfCities; i++) {
                token.nextToken() ;
                int j = (int)token.nval ;

                token.nextToken() ;
                c[2*(j-1)] = token.nval ;
                token.nextToken() ;
                c[2*(j-1)+1] = token.nval ;

                sum_x += c[2*(j-1)];
                if (c[2*(j-1)] > max_x){
                    max_x = c[2*(j-1)];
                }
                if (c[2*(j-1)]  < min_x){
                    min_x = c[2*(j-1)];
                }

                sum_y += c[2*(j-1)+1];
                if (c[2*(j-1)+1] > max_y){
                    max_y = c[2*(j-1)+1];
                }
                if (c[2*(j-1)+1] < min_y){
                    min_y = c[2*(j-1)+1];
                }
            } // for

            double base_x = 0, base_y = 0;

            switch (baseType){
                case MEAN_BASE:
                    base_x = sum_x/numberOfCities;
                    base_y = sum_y/numberOfCities;
                    break;
                case CENTER_BASE:
                    base_x = (max_x - min_x)/2;
                    base_y = (max_y - min_y)/2;
                    break;
            }
            c[2*numberOfCities] = base_x;
            c[2*numberOfCities+1] = base_y;

            double dist ;
            for (int k = 0; k < numberOfCities + 1; k++) {
                matrix[k][k] = 0;
                for (int j = k + 1; j < numberOfCities + 1; j++) {
                    dist = Math.sqrt(Math.pow((c[k*2]-c[j*2]),2.0) +
                            Math.pow((c[k*2+1]-c[j*2+1]), 2));
                    dist = (int)(dist + .5);
                    matrix[k][j] = dist;
                    matrix[j][k] = dist;
                }
            }
        } catch (Exception e) {
            throw new JMetalException("TSP.readProblem(): error when reading data file " + e);
        }
        return matrix;
    }
}
