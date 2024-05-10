import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParallelSortAlgorithms {

    private static ExecutorService executor;

    private static final int THRESHOLD = 1000;
    private static final int MAX_DEPTH = 4;

    public static void mergeSort(int[] array, int numThreads) {
        if (numThreads <= 0) {
            throw new IllegalArgumentException("Number of threads must be greater than zero.");
        }
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        try {
            CountDownLatch latch = new CountDownLatch(1);
            sort(array, 0, array.length - 1, executor, latch);
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            shutdownAndAwaitTermination(executor);
        }
    }

    private static void sort(int[] array, int left, int right, ExecutorService executor, CountDownLatch latch) {
        if (left < right) {
            if (right - left < THRESHOLD) {
                sequentialMergeSort(array, left, right);
                latch.countDown();
            } else {
                int mid = (left + right) / 2;
                CountDownLatch subLatch = new CountDownLatch(2);

                executor.execute(() -> sort(array, left, mid, executor, subLatch));
                executor.execute(() -> sort(array, mid + 1, right, executor, subLatch));

                new Thread(() -> {
                    try {
                        subLatch.await();
                        merge(array, left, mid, right);
                        latch.countDown();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            }
        } else {
            latch.countDown();
        }
    }

    private static void sequentialMergeSort(int[] array, int left, int right) {
        if (left < right) {
            int mid = (left + right) / 2;
            sequentialMergeSort(array, left, mid);
            sequentialMergeSort(array, mid + 1, right);
            merge(array, left, mid, right);
        }
    }

    private static void merge(int[] array, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            temp[k++] = array[i] <= array[j] ? array[i++] : array[j++];
        }

        while (i <= mid) {
            temp[k++] = array[i++];
        }

        while (j <= right) {
            temp[k++] = array[j++];
        }

        System.arraycopy(temp, 0, array, left, temp.length);
    }

    public static void bubbleSort(int[] arr, int numThreads) {
        if (arr == null || arr.length < 2) return;
        int n = arr.length;
        executor = Executors.newFixedThreadPool(numThreads);
        int chunkSize = n / numThreads;

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = (i == numThreads - 1) ? n - 1 : (start + chunkSize - 1);
            executor.execute(() -> bubbleSortParallel(arr, start, end));
        }
        shutdownAndAwaitTermination(executor);
    }

    private static void bubbleSortParallel(int[] arr, int start, int end) {
        boolean swapped;
        do {
            swapped = false;
            for (int j = start; j < end; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }
        } while (swapped);
    }

    public static void selectionSort(int[] arr, int numThreads) {
        if (arr == null || arr.length < 2) return;
        executor = Executors.newFixedThreadPool(numThreads);
        int chunkSize = arr.length / numThreads;

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = (i == numThreads - 1) ? arr.length : (start + chunkSize);
            executor.execute(() -> selectionSortParallel(arr, start, end));
        }
        shutdownAndAwaitTermination(executor);
    }

    private static void selectionSortParallel(int[] arr, int start, int end) {
        for (int i = start; i < end - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < end; j++) {
                if (arr[j] < arr[minIndex]) {
                    minIndex = j;
                }
            }
            if (minIndex != i) {
                int temp = arr[minIndex];
                arr[minIndex] = arr[i];
                arr[i] = temp;
            }
        }
    }

    public static void insertionSort(int[] arr, int numThreads) {
        if (arr == null || arr.length < 2) return;
        executor = Executors.newFixedThreadPool(numThreads);
        int chunkSize = arr.length / numThreads;

        for (int i = 0; i < numThreads; i++) {
            final int start = i * chunkSize;
            final int end = (i == numThreads - 1) ? arr.length : (start + chunkSize);
            executor.execute(() -> insertionSortParallel(arr, start, end));
        }
        shutdownAndAwaitTermination(executor);
    }

    private static void insertionSortParallel(int[] arr, int start, int end) {
        for (int i = start + 1; i < end; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= start && arr[j] > key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }

    private static void shutdownAndAwaitTermination(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
