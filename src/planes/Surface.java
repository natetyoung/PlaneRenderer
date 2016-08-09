package planes;

import core.Vec3;

public interface Surface {
	/**
	 * Returns the <code>ColorPlane</code> interpolated at the given ray
	 * @param ray A <code>Vec3</code> along the desired ray
	 * @return The <code>ColorPlane</code> interpolated, or <b><code>null</code></b>
	 *  if the ray should not intersect this <code>Surface</code>
	 */
	public ColorPlane getAt(Vec3 ray);
	/**
	 * Returns whether <code>getAt</code> will necessarily return null; sometimes used for speed. <br>
	 * If this method would not be significantly faster than <code>getAt</code>, it should always return false.
	 * @param ray A <code>Vec3</code> along the desired ray
	 * @return Whether <code>getAt</code> for the given ray would necessarily return null.
	 * Some implementations always return false for speed.
	 */
	public boolean isGetAtNull(Vec3 ray);
	public ColorPlane getClosestTheta(Vec3 ray);
	public double getTheta(Vec3 ray);
	
	
	public void translate(Vec3 d);
	public Surface rotateX(double t);
	public Surface rotateY(double t);
}
