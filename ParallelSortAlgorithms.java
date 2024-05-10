public class ParallelSortAlgorithms {

    public static void mergeSort(int[] arr, int numThreads) {
            if (arr == null || arr.length < 2) return;
            mergeSort(arr, 0, arr.length - 1, numThreads);
        }

        private static void mergeSort(int[] arr, int start, int end, int numThreads) {
            if (start < end) {
                int mid = (start + end) / 2;
                Thread leftThread = new Thread(() -> mergeSort(arr, start, mid, numThreads / 2));
                Thread rightThread = new Thread(() -> mergeSort(arr, mid + 1, end, numThreads - numThreads / 2));
                leftThread.start();
                rightThread.start();
                try {
                    leftThread.join();
                    rightThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                merge(arr, start, mid, end);
            }
        }

        private static void merge(int[] arr, int start, int mid, int end) {
            int n1 = mid - start + 1;
            int n2 = end - mid;

            int[] leftArr = new int[n1];
            int[] rightArr = new int[n2];

            for (int i = 0; i < n1; ++i)
                leftArr[i] = arr[start + i];
            for (int j = 0; j < n2; ++j)
                rightArr[j] = arr[mid + 1 + j];

            int i = 0, j = 0;
            int k = start;

            while (i < n1 && j < n2) {
                if (leftArr[i] <= rightArr[j]) {
                    arr[k] = leftArr[i];
                    i++;
                } else {
                    arr[k] = rightArr[j];
                    j++;
                }
                k++;
            }

            while (i < n1) {
                arr[k] = leftArr[i];
                i++;
                k++;
            }

            while (j < n2) {
                arr[k] = rightArr[j];
                j++;
                k++;
            }
        }


    public static void selectionSort(int[] arr) {
        if (arr == null || arr.length < 2) return;

        int n = arr.length;
        Thread[] threads = new Thread[n];

        for (int i = 0; i < n - 1; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                int minIndex = index;
                for (int j = index + 1; j < n; j++) {
                    if (arr[j] < arr[minIndex]) {
                        minIndex = j;
                    }
                }
                if (minIndex != index) {
                    int temp = arr[minIndex];
                    arr[minIndex] = arr[index];
                    arr[index] = temp;
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < n - 1; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void insertionSort(int[] arr) {
        if (arr == null || arr.length < 2) return;

        int n = arr.length;
        Thread[] threads = new Thread[n];

        for (int i = 1; i < n; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                int key = arr[index];
                int j = index - 1;

                while (j >= 0 && arr[j] > key) {
                    arr[j + 1] = arr[j];
                    j--;
                }
                arr[j + 1] = key;
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (int i = 1; i < n; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void bubbleSort(int[] arr) {
        if (arr == null || arr.length < 2) return;

        int n = arr.length;
        Thread[] threads = new Thread[n];

        for (int i = 0; i < n - 1; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < n - index - 1; j++) {
                    if (arr[j] > arr[j + 1]) {
                        int temp = arr[j];
                        arr[j] = arr[j + 1];
                        arr[j + 1] = temp;
                    }
                }
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (int i = 0; i < n - 1; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
