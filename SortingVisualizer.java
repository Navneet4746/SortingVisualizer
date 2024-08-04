import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class SortingVisualizer extends JPanel implements ActionListener {

    private int[] array;
    private int arraySize = 100;
    private int barWidth;
    private int delay = 10; // Milliseconds
    private String currentAlgorithm = "Bubble Sort";
    private boolean sorting = false;

    public SortingVisualizer() {
        this.array = new int[arraySize];
        this.barWidth = 800 / arraySize;
        generateRandomArray();

        // Timer to repaint the panel
        Timer timer = new Timer(delay, this);
        timer.start();
    }

    private void generateRandomArray() {
        Random random = new Random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(400) + 50; // Random heights between 50 and 450
        }
        sorting = false;
    }

    public void startSorting() {
        if (sorting) return;
        sorting = true;

        new Thread(() -> {
            switch (currentAlgorithm) {
                case "Bubble Sort":
                    bubbleSort();
                    break;
                case "Insertion Sort":
                    insertionSort();
                    break;
                case "Selection Sort":
                    selectionSort();
                    break;
                case "Merge Sort":
                    mergeSort(0, array.length - 1);
                    break;
                case "Quick Sort":
                    quickSort(0, array.length - 1);
                    break;
            }
            sorting = false;
        }).start();
    }

    private void bubbleSort() {
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    swap(j, j + 1);
                    repaintAndSleep();
                }
            }
        }
    }

    private void insertionSort() {
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j = j - 1;
                repaintAndSleep();
            }
            array[j + 1] = key;
            repaintAndSleep();
        }
    }

    private void selectionSort() {
        for (int i = 0; i < array.length - 1; i++) {
            int minIdx = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[minIdx]) {
                    minIdx = j;
                }
            }
            swap(i, minIdx);
            repaintAndSleep();
        }
    }

    private void mergeSort(int left, int right) {
        if (left < right) {
            int middle = (left + right) / 2;
            mergeSort(left, middle);
            mergeSort(middle + 1, right);
            merge(left, middle, right);
        }
    }

    private void merge(int left, int middle, int right) {
        int n1 = middle - left + 1;
        int n2 = right - middle;
        int[] L = new int[n1];
        int[] R = new int[n2];

        System.arraycopy(array, left, L, 0, n1);
        System.arraycopy(array, middle + 1, R, 0, n2);

        int i = 0, j = 0, k = left;
        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                array[k] = L[i];
                i++;
            } else {
                array[k] = R[j];
                j++;
            }
            k++;
            repaintAndSleep();
        }

        while (i < n1) {
            array[k] = L[i];
            i++;
            k++;
            repaintAndSleep();
        }

        while (j < n2) {
            array[k] = R[j];
            j++;
            k++;
            repaintAndSleep();
        }
    }

    private void quickSort(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);
            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (array[j] < pivot) {
                i++;
                swap(i, j);
                repaintAndSleep();
            }
        }
        swap(i + 1, high);
        repaintAndSleep();
        return i + 1;
    }

    private void swap(int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    private void repaintAndSleep() {
        repaint();
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int i = 0; i < array.length; i++) {
            g.setColor(Color.BLUE);
            g.fillRect(i * barWidth, getHeight() - array[i], barWidth, array[i]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sorting Visualizer");
        SortingVisualizer panel = new SortingVisualizer();
        frame.add(panel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> panel.startSorting());

        JButton randomizeButton = new JButton("Randomize");
        randomizeButton.addActionListener(e -> {
            panel.generateRandomArray();
            panel.repaint();
        });

        JComboBox<String> algorithmComboBox = new JComboBox<>(new String[]{"Bubble Sort", "Insertion Sort", "Selection Sort", "Merge Sort", "Quick Sort"});
        algorithmComboBox.addActionListener(e -> panel.currentAlgorithm = (String) algorithmComboBox.getSelectedItem());

        controlPanel.add(startButton);
        controlPanel.add(randomizeButton);
        controlPanel.add(algorithmComboBox);

        frame.add(controlPanel, BorderLayout.SOUTH);

        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
