import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MutationTest {

    public static void main(String[] args) throws IOException {
        List<Integer> vehicles = new ArrayList<>();
        List<Integer> cities1 = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            vehicles.add(-1);
            cities1.add(i);
        }

        MyMutation mutation = new MyMutation(1);


        MyVRPSolution solution = new MyVRPSolution(new MyVRPProblem(11, 11), 11, 11, cities1, vehicles);
        MyVRPSolution mutatedSolution = mutation.execute(solution);
        System.out.println(mutatedSolution);
    }
}
