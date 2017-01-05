package LF.PoolGame.Math;

public abstract class QuadraticEquation
{
	public static Vector2D solve(float a, float b, float c)
	{
		float a2 = 2 * a;
		float discriminant = getDiscriminant(a, b, c);
		if(discriminant < 0) return new Vector2D(Float.NaN, Float.NaN);
		float discrSqrt = (float)Math.sqrt(getDiscriminant(a, b, c));
		return new Vector2D((-b - discrSqrt) / a2, (-b + discrSqrt) / a2);
	}
	
	public static float getDiscriminant(float a, float b, float c)
	{
		return (b * b) - (4 * a * c);
	}
}
