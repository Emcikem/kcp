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

    public static Matrix identity(int size) {
        Matrix result = new Matrix(size, size);
        for (int i = 0; i < size; i++) {

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
        return null;
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


}
