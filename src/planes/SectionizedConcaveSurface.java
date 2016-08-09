package planes;

import java.util.List;

import core.Vec3;

public class SectionizedConcaveSurface implements Surface{

	private List<ColorPlane> pls;
	private List<List<Integer>> adj;
	private List<Double> r2;
	
	public SectionizedConcaveSurface(List<ColorPlane> pls, List<List<Integer>> adj, List<Double> radius2) {
		this.r2 = radius2;
		this.pls = pls;
		this.adj = adj;
	}

	@Override
	public ColorPlane getAt(Vec3 ray) {
		return getAt(Vec3.ZERO, ray);
	}

	private ColorPlane getAt(Vec3 pt, Vec3 dir) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isGetAtNull(Vec3 ray) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ColorPlane getClosestTheta(Vec3 ray) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getTheta(Vec3 ray) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void translate(Vec3 d) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Surface rotateX(double t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Surface rotateY(double t) {
		// TODO Auto-generated method stub
		return null;
	}

}
