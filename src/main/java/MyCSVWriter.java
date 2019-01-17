import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardOpenOption.*;

public class MyCSVWriter {

    private Path path;

    public MyCSVWriter(String fileName) {
        this.path = Paths.get(fileName);
    }

    public void setPath(String path) {
        this.path = Paths.get(path);
    }

    public void write(String algorithmName, BaseType baseType, String dataPath, int populationSize, int maxEvaluations, double crossoverProbability, double mutationProbability, double fitness1, double fitness2, long computationTime, int minVehicles) {
        String line = String.format("%s,%s,%s,%d,%d,%f,%f,%f,%f,%d,%d\n", algorithmName, baseType, dataPath, populationSize, maxEvaluations, crossoverProbability, mutationProbability, fitness1, fitness2, computationTime, minVehicles);
        try {
            Files.write(path, line.getBytes(), APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        try {
            Files.write(path, "algorithm_name,base_type,data_path,population_size,max_evaluations,crossover,mutation,fitness1,fitness2,computation_time,vehicles\n".getBytes(), TRUNCATE_EXISTING, CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
