package io.github.mxd888.socket.utils.algorithm;

/**
 * 排序算法
 */
public class SortUtils {

    /**
     * 改进版冒泡排序
     * @param array 待排序数组
     */
    public static void bubbleSort(int[] array){
        for (int i = 0; i < array.length-1; i++) {
            boolean flg = false;
            for (int j = 0; j < array.length-1-i; j++) {
                if(array[j] > array[j+1]){
                    int tmp;
                    tmp = array[j];
                    array[j] = array[j+1];
                    array[j+1] = tmp;
                    flg = true;
                }
            }
            if(!flg){
                return;
            }
        }
    }
}
