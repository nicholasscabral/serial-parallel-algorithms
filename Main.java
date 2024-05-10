import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class Main {
    private static final int[] ARRAY_SIZES = {50000, 100000, 150000, 200000, 250000, 300000};
    // private static final int[] ARRAY_SIZES = {300, 400, 500, 600, 700};

    private static final int NUM_SAMPLES = 5;
    private static final int MAX_NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private static final int[] THREAD_COUNTS = {4, 6, MAX_NUM_THREADS};

    public static void main(String[] args) {
        try (FileWriter writer = new FileWriter("results.csv")) {
            StringBuilder csvHeader = new StringBuilder("Algorithm,DataSize,ExecutionType,ThreadCount,AverageTime\n");
            writer.write(csvHeader.toString());

            for (SortingAlgorithm algorithm : SortingAlgorithm.values()) {
                for (int dataSize : ARRAY_SIZES) {
                    long serialTimeSum = 0;
                    Random random = new Random();
                    for (int sample = 0; sample < NUM_SAMPLES; sample++) {
                        int[] data = generateRandomArray(dataSize, random);
                        long startTime = System.currentTimeMillis();
                        algorithm.sort(data);
                        long endTime = System.currentTimeMillis();
                        serialTimeSum += (endTime - startTime);
                    }
                    long serialTimeAvg = serialTimeSum / NUM_SAMPLES;
                    System.out.println("Algorithm: " + algorithm.name() + ", Data Size: " + dataSize + ", Execution Type: Serial, Average Time: " + serialTimeAvg + " ms");
                    writeResultToCSV(writer, algorithm.name(), dataSize, "Serial", 1, serialTimeAvg);

                    for (int numThreads : THREAD_COUNTS) {
                        long parallelTimeSum = 0;
                        for (int sample = 0; sample < NUM_SAMPLES; sample++) {
                            int[] data = generateRandomArray(dataSize, random);
                            long startTime = System.currentTimeMillis();
                            algorithm.parallelSort(data, numThreads);
                            long endTime = System.currentTimeMillis();
                            parallelTimeSum += (endTime - startTime);
                        }
                        long parallelTimeAvg = parallelTimeSum / NUM_SAMPLES;
                        System.out.println("Algorithm: " + algorithm.name() + ", Data Size: " + dataSize + ", Execution Type: Parallel, Thread Count: " + numThreads + ", Average Time: " + parallelTimeAvg + " ms");
                        writeResultToCSV(writer, algorithm.name(), dataSize, "Parallel", numThreads, parallelTimeAvg);
                    }
                }
            }
            String s = null;

            Process p = Runtime.getRuntime().exec("python3 plot-graphs.py");

            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }

            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

            System.exit(0);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    private static int[] generateRandomArray(int size, Random random) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt();
        }
        return array;
    }

    private static void writeResultToCSV(FileWriter writer, String algorithm, int dataSize, String executionType, int threadCount, long averageTime) throws IOException {
        StringBuilder csvRow = new StringBuilder(algorithm + "," + dataSize + "," + executionType + "," + threadCount + "," + averageTime + "\n");
        writer.write(csvRow.toString());
        writer.flush();
    }

    private enum SortingAlgorithm {
        MERGE_SORT {
            @Override
            public void sort(int[] arr) {
                SequentialSortAlgorithms.mergeSort(arr);
            }

            @Override
            public void parallelSort(int[] arr, int numThreads) {
                ParallelSortAlgorithms.mergeSort(arr, numThreads);
            }
        },
        SELECTION_SORT {
            @Override
            public void sort(int[] arr) {
                SequentialSortAlgorithms.selectionSort(arr);
            }

            @Override
            public void parallelSort(int[] arr, int numThreads) {
                ParallelSortAlgorithms.selectionSort(arr, numThreads);
            }
        },
        INSERTION_SORT {
            @Override
            public void sort(int[] arr) {
                SequentialSortAlgorithms.insertionSort(arr);
            }

            @Override
            public void parallelSort(int[] arr, int numThreads) {
                ParallelSortAlgorithms.insertionSort(arr, numThreads);
            }
        },
        BUBBLE_SORT {
            @Override
            public void sort(int[] arr) {
                SequentialSortAlgorithms.bubbleSort(arr);
            }

            @Override
            public void parallelSort(int[] arr, int numThreads) {
                ParallelSortAlgorithms.bubbleSort(arr, numThreads);
            }
        };

        public abstract void sort(int[] arr);

        public abstract void parallelSort(int[] arr, int numThreads);
    }
}
