package edu.bupt.qunar.gis;

public class MathUtil{
	public static double radianByDegree(double degree){
		return (degree * 0.0174532925199433D); }

	public static double degreeByRadian(double radian) {
		return (radian / 0.0174532925199433D);
	}

	public static double limit(double num, double min, double max) {
		num = Math.max(num, min);
		num = Math.min(num, max);
		return num;
	}

	public static double loopToRange(double num, double min, double max) {
		while (num > max) {
			num -= max - min;
		}
		while (num < min) {
			num += max - min;
		}
		return num;
	}
}