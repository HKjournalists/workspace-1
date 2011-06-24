package edu.bupt.utils;

/**
 * BigDecimal平方根的实现
 * 手动开平方
 * 1．将被开方数的整数部分从个位起向左每隔两位划为一段，用撇号分开，分成几段，表示所求平方根是几位数；小数部分从最高位向后两位一段隔开，段数以需要的精度+1为准。
 * 2．根据左边第一段里的数，求得平方根的最高位上的数。（在右边例题中，比5小的平方数是4，所以平方根的最高位为2。）
 * 3．从第一段的数减去最高位上数的平方，在它们的差的右边写上第二段数组成第一个余数。
 * 4．把第二步求得的最高位的数乘以20去试除第一个余数，所得的最大整数作为试商。（右例中的试商即为[152/(2×20)]＝[3.8]＝3。）
 * 5．用第二步求得的的最高位数的20倍加上这个试商再乘以试商。如果所得的积小于或等于余数，试商就是平方根的第二位数；如果所得的积大于余数，就把试商减小再试，得到的第一个小于余数的试商作为平方根的第二个数。（即3为平方根的第二位。）
 * 6．用同样的方法，继续求平方根的其他各位上的数。用上一个余数减去上法中所求的积（即152－129＝23），与第三段数组成新的余数（即2325）。这时再求试商，要用前面所得到的平方根的前两位数（即23）乘以20去试除新的余数（2325），所得的最大整数为新的试商。（2325/(23×20)的整数部分为5。）
 * 7．对新试商的检验如前法。（右例中最后的余数为0，刚好开尽，则235为所求的平方根。）
 */

import java.math.BigDecimal;
import java.math.BigInteger;

public class BigSquareRoot {

	final static BigInteger HUNDRED = BigInteger.valueOf(100);

	public static BigDecimal sqrt(BigDecimal number, int scale, int roundingMode) {
		if (number.compareTo(BigDecimal.ZERO) < 0)
			throw new ArithmeticException("sqrt with negative");
		BigInteger integer = number.toBigInteger();
		StringBuffer sb = new StringBuffer();
		String strInt = integer.toString();
		int lenInt = strInt.length();
		if (lenInt % 2 != 0) {
			strInt = '0' + strInt;
			lenInt++;
		}
		BigInteger res = BigInteger.ZERO;
		BigInteger rem = BigInteger.ZERO;
		for (int i = 0; i < lenInt / 2; i++) {
			res = res.multiply(BigInteger.TEN);
			rem = rem.multiply(HUNDRED);

			BigInteger temp = new BigInteger(strInt.substring(i * 2, i * 2 + 2));
			rem = rem.add(temp);

			BigInteger j = BigInteger.TEN;
			while (j.compareTo(BigInteger.ZERO) > 0) {
				j = j.subtract(BigInteger.ONE);
				if (((res.add(j)).multiply(j)).compareTo(rem) <= 0) {
					break;
				}
			}

			res = res.add(j);
			rem = rem.subtract(res.multiply(j));
			res = res.add(j);
			sb.append(j);
		}
		sb.append('.');
		BigDecimal fraction = number.subtract(number.setScale(0,
				BigDecimal.ROUND_DOWN));
		int fracLen = (fraction.scale() + 1) / 2;
		fraction = fraction.movePointRight(fracLen * 2);
		String strFrac = fraction.toPlainString();
		for (int i = 0; i <= scale; i++) {
			res = res.multiply(BigInteger.TEN);
			rem = rem.multiply(HUNDRED);

			if (i < fracLen) {
				BigInteger temp = new BigInteger(strFrac.substring(i * 2,
						i * 2 + 2));
				rem = rem.add(temp);
			}

			BigInteger j = BigInteger.TEN;
			while (j.compareTo(BigInteger.ZERO) > 0) {
				j = j.subtract(BigInteger.ONE);
				if (((res.add(j)).multiply(j)).compareTo(rem) <= 0) {
					break;
				}
			}
			res = res.add(j);
			rem = rem.subtract(res.multiply(j));
			res = res.add(j);
			sb.append(j);
		}
		return new BigDecimal(sb.toString()).setScale(scale, roundingMode);
	}

	public static BigDecimal sqrt(BigDecimal number, int scale) {
		return sqrt(number, scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal sqrt(BigDecimal number) {
		int scale = number.scale() * 2;
		if (scale < 50)
			scale = 50;
		return sqrt(number, scale, BigDecimal.ROUND_HALF_UP);
	}

	public static void main(String args[]) {
		BigDecimal num = new BigDecimal("6510354513.6564897413514568413");
		long time = System.nanoTime();
		BigDecimal root = sqrt(num, 1000);
		time = System.nanoTime() - time;
		System.out.println(root);
		System.out.println(root.pow(2));
		System.out.println(time);
	}

}
