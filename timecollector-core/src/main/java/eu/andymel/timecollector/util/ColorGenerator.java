package eu.andymel.timecollector.util;

import java.awt.Color;
import java.util.Arrays;

/**
 * from http://stackoverflow.com/a/12224359
 */
public class ColorGenerator {
	public static Color getColor(int i) {
	    return new Color(getRGB(i));
	}

	public static int getRGB(int index) {
	    int[] p = getPattern(index);
	    return getElement(p[0]) << 16 | getElement(p[1]) << 8 | getElement(p[2]);
	}

	public static int getElement(int index) {
	    int value = index - 1;
	    int v = 0;
	    for (int i = 0; i < 8; i++) {
	        v = v | (value & 1);
	        v <<= 1;
	        value >>= 1;
	    }
	    v >>= 1;
	    return v & 0xFF;
	}

	public static int[] getPattern(int index) {
	    int n = (int)java.lang.Math.cbrt(index);
	    index -= (n*n*n);
	    int[] p = new int[3];
	    Arrays.fill(p,n);
	    if (index == 0) {
	        return p;
	    }
	    index--;
	    int v = index % 3;
	    index = index / 3;
	    if (index < n) {
	        p[v] = index % n;
	        return p;
	    }
	    index -= n;
	    p[v      ] = index / n;
	    p[++v % 3] = index % n;
	    return p;
	}
}
