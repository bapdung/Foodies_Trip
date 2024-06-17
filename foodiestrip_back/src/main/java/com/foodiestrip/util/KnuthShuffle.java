package com.foodiestrip.util;

import java.util.Random;

public class KnuthShuffle {
	 public static void shuffle(int[] array) {
	        Random random = new Random();
	        for (int i = array.length - 1; i > 0; i--) {
	            int j = random.nextInt(i + 1); // 0부터 i(포함)까지의 임의의 인덱스 생성
	            // array[i]와 array[j]의 위치를 바꾼다
	            int temp = array[i];
	            array[i] = array[j];
	            array[j] = temp;
	        }
	    }
}
