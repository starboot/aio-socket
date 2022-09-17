package io.github.mxd888.socket.utils.math;

import org.ejml.simple.SimpleMatrix;

/**
 * 矩阵工具包，为后续提供仿生算法提供基础
 */
public class MatrixUtils {

    /**
     * 获取一个 rows * cols 的矩阵
     * @param rows 矩阵行
     * @param cols 矩阵列
     * @return 矩阵
     */
    public SimpleMatrix getMatrix(int rows, int cols) {
        return new SimpleMatrix(rows, cols);
    }
}
