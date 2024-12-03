package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

public class MultiThreadedSumMatrix implements SumMatrix{
    private int threads;

    public MultiThreadedSumMatrix(final int threads) {
        this.threads = threads;
    }

    private static class Worker extends Thread {
        private final double [][] matrix;
        private final int startpos;
        private final int nelem;
        private double res;

        /**
         * Build a new worker.
         * 
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < matrix.length && i < startpos + nelem; i++) {
                for (int j = 0; j < matrix[i].length ; j ++) {
                    this.res += this.matrix[i][j];
                }
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public double getResult() {
            return this.res;
        }
    }

    @Override
    public double sum(double[][] matrix) {
        final int size = matrix.length % this.threads + matrix.length / this.threads;
        final List<Worker> listOfWorkers = new ArrayList<>(this.threads);
        for ( int start = 0; start < matrix.length; start += size) {
            listOfWorkers.add(new Worker(matrix, start, size));
        }

        for (final Worker w: listOfWorkers) {
            w.start();
        }

        double sum = 0;
        for (final Worker w: listOfWorkers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return sum;
    }
}
