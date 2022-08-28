package com.emcikem.kcpfec;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Emcikem
 * @create 2022/8/27
 * @desc
 */
public class Matrix {

    private final int rows;

    private final int columns;

    private final byte[][] data;

    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.data = new byte[rows][];
        for (int r = 0; r < rows; r++) {
            data[r] = new byte[columns];
        }
    }

    public Matrix(byte[][] initData) {
        this.rows = initData.length;
        this.columns = initData[0].length;
        this.data = new byte[rows][];
        for (int r = 0; r < rows; r++) {
            if (initData[r].length != columns) {
                throw new IllegalArgumentException("Not all rows have the same number of columns");
            }
            this.data[r] = new byte[columns];
            System.arraycopy(initData[r], 0, this.data[r], 0, columns);
        }
    }

    /**
     * identity matrix
     */
    public static Matrix identity(int size) {
        Matrix result = new Matrix(size, size);
        for (int i = 0; i < size; i++) {
            result.set(i, i, (byte) 1);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("[");
        for (int r = 0; r < this.rows; r++) {
            result.append(r == 0 ? "" : ", ").append("[");
            for (int c = 0; c < this.columns; c++) {
                result.append(c == 0 ? "" : ", ").append(this.data[r][c] & 0xFF);
            }
            result.append("]");
        }
        return result.append("]").toString();
    }

    public String toBigString() {
        StringBuilder result = new StringBuilder();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                int value = get(r, c);
                value += value < 0 ? 256 : 0;
                result.append(String.format("%02x ", value));
            }
            result.append("\n");
        }
        return result.toString();
    }

    public int getRows() {
        return this.rows;
    }

    public int getColumns() {
        return this.columns;
    }

    public byte get(int r, int c) {
        if (r < 0 || rows <= r) {
            throw new IllegalArgumentException("Row index out of range: " + r);
        }
        if (c < 0 || columns <= c) {
            throw new IllegalArgumentException("Column index out of range: " + c);
        }
        return data[r][c];
    }

    public void set(int r, int c, byte value) {
        if (r < 0 || rows <= r) {
            throw new IllegalArgumentException("Row index out of range: " + r);
        }
        if (c < 0 || columns <= c) {
            throw new IllegalArgumentException("Column index out of range: " + c);
        }
        data[r][c] = value;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof  Matrix)) {
            return false;
        }
        for (int r = 0; r < this.rows; r++) {
            if (!Arrays.equals(data[r], ((Matrix)other).data[r])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 矩阵相乘
     */
    public Matrix times(Matrix right) {
        if (getColumns() != right.getRows()) {
            throw new IllegalArgumentException(
                    "Columns on left (" + getColumns() +") " +
                            "is different than rows on right (" + right.getRows() + ")");
        }
        Matrix result = new Matrix(getRows(), right.getColumns());
        for (int r = 0; r < getRows(); r++) {
            for (int c = 0; c < right.getColumns(); c++) {
                byte value = 0;
                for (int i = 0; i < getColumns(); i++) {
//                    value ^= Galois.multiply(get(r, i), right.get(i, c));
                }
                result.set(r, c, value);
            }
        }
        return result;
    }


    /**
     * 把两个矩阵破解起来
     */
    public Matrix augment(Matrix right) {
        if (rows != right.rows) {
            throw new IllegalArgumentException("Matrices don't have the same number of rows");
        }
        if (!isValid() || !right.isValid()) {
            throw new IllegalArgumentException("Matrices is not valid");
        }
        Matrix result = new Matrix(rows, columns + right.columns);
        for (int r = 0; r < rows; r++) {
            System.arraycopy(data[r], 0, result.data[r], 0, columns);
            System.arraycopy(right.data[r], 0, result.data[r], columns, right.columns);
        }
        return result;
    }

    public boolean isValid() {
        return rows >= 0 && columns >= 0 && data != null;
    }

    /**
     * Returns a part of this matrix.
     */
    public Matrix subMatrix(int rmin, int cmin, int rmax, int cmax) {
        if (!isValid() || rmax < rmin || cmax < cmin) {
            throw new IllegalArgumentException("inValid");
        }
        Matrix result = new Matrix(rmax - rmin, cmax - cmin);
        for (int r = rmin; r < rmax; r++) {
            System.arraycopy(data[r], cmin, result.data[r - rmin], 0, cmax - cmin);
        }
        return result;
    }

    public byte[] getRow(int row) {
        byte[] result = new byte[columns];
        for (int c = 0; c < columns; c++) {
            result[c] = get(row, c);
        }
        return result;
    }


    // 行交换
    public void swapRows(int r1, int r2) {
        if (r1 < 0 || rows <= r1 || r2 < 0 || rows <= r2) {
            throw new IllegalArgumentException("Row index out of range");
        }
        byte[] tmp = data[r1];
        data[r1] = data[r2];
        data[r2] = tmp;
    }


    /**
     * inverse of this matrix.
     * 矩阵的逆
     */
    public Matrix invert() {
        if (rows != columns) {
            throw new IllegalArgumentException("Only square matrices can be inverted");
        }
        Matrix work = augment(identity(rows));
        work.gaussianElimination();
        return work.subMatrix(0, rows, columns, columns * 2);
    }

    private void gaussianElimination() {
        // Clear out the part below the main diagonal and scale the main
        // diagonal to be 1.
        for (int r = 0; r < rows; r++) {
            // If the element on the diagonal is 0, find a row below
            // that has a non-zero and swap them.
            if (data[r][r] == (byte) 0) {
                for (int rowBelow = r + 1; rowBelow < rows; rowBelow++) {
                    if (data[rowBelow][r] != 0) {
                        swapRows(r, rowBelow);
                        break;
                    }
                }
            }
            // If we couldn't find one, the matrix is singular.
            if (data[r][r] == (byte) 0) {
                throw new IllegalArgumentException("Matrix is singular");
            }
            // Scale to 1.
            if (data[r][r] != (byte) 1) {
                byte scale = Galois.divide((byte) 1, data[r][r]);
                for (int c = 0; c < columns; c++) {
                    data[r][c] = Galois.multiply(data[r][c], scale);
                }
            }
            // Make everything below the 1 be a 0 by subtracting
            // a multiple of it.  (Subtraction and addition are
            // both exclusive or in the Galois field.)
            for (int rowBelow = r + 1; rowBelow < rows; rowBelow++) {
                if (data[rowBelow][r] != (byte) 0) {
                    byte scale = data[rowBelow][r];
                    for (int c = 0; c < columns; c++) {
                        data[rowBelow][c] ^= Galois.multiply(scale, data[r][c]);
                    }
                }
            }
        }

        // Now clear the part above the main diagonal.
        for (int d = 0; d < rows; d++) {
            for (int rowAbove = 0; rowAbove < d; rowAbove++) {
                if (data[rowAbove][d] != (byte) 0) {
                    byte scale = data[rowAbove][d];
                    for (int c = 0; c < columns; c++) {
                        data[rowAbove][c] ^= Galois.multiply(scale, data[d][c]);
                    }

                }
            }
        }
    }

}
