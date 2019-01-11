import org.uma.jmetal.util.JMetalException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CrossoverTest {

    public static void main(String[] args) throws JMetalException, IOException {
        MyCrossover crossover = new MyCrossover(1);
        List<Integer> vehicles = new ArrayList<>();
        List<MyVRPSolution> list = new ArrayList<>();
        List<MyVRPSolution> children;
        List<Integer> cities1 = new ArrayList<>();
        List<Integer> cities2 = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            vehicles.add(1);
            cities1.add(i);
            cities2.add(10 - i);
        }
        list.add(new MyVRPSolution(new MyVRPProblem(11, 11), 11, 11, cities1, vehicles));
        list.add(new MyVRPSolution(new MyVRPProblem(11, 11), 11, 11, cities2, vehicles));

        children = crossover.execute(list);
        System.out.println(children);
    }
}