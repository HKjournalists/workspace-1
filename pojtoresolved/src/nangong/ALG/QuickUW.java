package nangong.ALG;

import java.util.Arrays;
import java.util.Scanner;

/**
 * jiaquan quickly merged alg
 */
public class QuickUW {

	public static void main(String[] args) {
		Scanner cin = new Scanner(System.in);
		int N = cin.nextInt();
		int[] id = new int[N], sz = new int[N];
		for (int i = 0; i < N; i++) {
			id[i] = i;
			sz[i] = i;
		}
		while (cin.hasNext()) {
			int i, j, p = cin.nextInt(), q = cin.nextInt();
			for (i = p; i != id[i]; i = id[i])
				;
			for (j = q; j != id[j]; j = id[j])
				;
			if (i == j) continue;
			if (sz[i] < sz[j]) {
				id[i] = j;
				sz[j] += sz[i];
			} else {
				id[j] = i;
				sz[i] += sz[j];
			}
			System.out.println(p + " " + q);
			System.out.println(Arrays.toString(id));
			System.out.println(Arrays.toString(sz));
		}
	}
}
