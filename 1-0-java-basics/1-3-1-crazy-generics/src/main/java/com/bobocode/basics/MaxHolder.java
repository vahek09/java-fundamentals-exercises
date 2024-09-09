package com.bobocode.basics;

/**
     * {@link MaxHolder} is a container class that keeps track of the maximum value only. It works with comparable objects
     * and allows you to put new values. Every time you put a value, it is stored only if the new value is greater
     * than the current max.
     *
     * @param <T> – value type
     */
public class MaxHolder<T extends Comparable<? super T>> {
        private T max;

        public MaxHolder(T max) {
            this.max = max;
        }

        /**
         * Puts a new value to the holder. A new value is stored to the max, only if it is greater than current max value.
         *
         * @param val a new value
         */
        public void put(T val) {
            if (val != null && (getMax() == null || val.compareTo(getMax()) > 0)) {
                max = val;
            }
        }

        public T getMax() {
            return max;
        }
    }
