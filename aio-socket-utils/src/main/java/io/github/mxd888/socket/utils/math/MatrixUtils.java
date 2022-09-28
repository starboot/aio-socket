/*
 *    Copyright 2019 The aio-socket Project
 *
 *    The aio-socket Project Licenses this file to you under the Apache License,
 *    Version 2.0 (the "License"); you may not use this file except in compliance
 *    with the License. You may obtain a copy of the License at:
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
