package planes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import core.Vec3;

public class FlatSurface implements Surface {
	private List<ColorPlane> pls;
	
	public FlatSurface(List<ColorPlane> pls) {
		this.pls = pls;
	}
	public FlatSurface(ColorPlane...colorPlanes){
		this(Arrays.asList(colorPlanes));
	}
	public FlatSurface(){
		this(new ArrayList<ColorPlane>());
	}
	
	public FlatSurface addPair(ColorPlane cpOn, ColorPlane cpOff){
		pls.add(cpOn);
		pls.add(cpOff);
		return this;
	}

	@Override
	public ColorPlane getAt(Vec3 ray) {
		if(pls.isEmpty()) return null;
		ColorPlane closest = pls.get(0);
		double d = closest.position.dot(closest.normal)/ray.dot(closest.normal);
		if(d<0) return null;
		Vec3 pt = ray.scale(d);
		double clDist = closest.position.sub(pt).mag2();
		for(int i=0; i<pls.size(); i+=2){
			double onDist = pls.get(i).position.sub(pt).mag2();
			double offDist = pls.get(i+1).position.sub(pt).mag2();
			if(offDist<onDist) {
				//if(Math.random()>.9999999) System.out.println("returning null "+pls.get(i)+" "+pls.get(i+1)+" "+ray);
				return null;
			}
			if(onDist<clDist){
				clDist = onDist;
				closest = pls.get(i);
			}
		}
		//if(Math.random()>.999) System.out.println(closest.color);
		return new ColorPlane(pt,closest.normal,closest.color);
		/*ColorPlane closest = getClosestTheta(ray);
		return closest.color==-1? null : closest;*/
	}

	@Override
	public ColorPlane getClosestTheta(Vec3 ray) {
		if(pls.size()<1) return null;
		ColorPlane closest = pls.get(0);
		double clTheta = closest.position.dot(ray)/ray.abs()/closest.position.abs();
		for(ColorPlane pl: pls){
			if(pl.position.dot(ray)/ray.abs()/pl.position.abs()>clTheta){
				closest = pl;
				clTheta = closest.position.dot(ray)/ray.abs()/closest.position.abs();
			}
		}
		return closest;
	}

	@Override
	public double getTheta(Vec3 ray) {
		ColorPlane closest =  getClosestTheta(ray);
		return closest.position.dot(ray)/ray.abs()/closest.position.abs();
	}
	@Override
	public boolean isGetAtNull(Vec3 ray) {
		return false;
	}
	@Override
	public void translate(Vec3 d) {
		for(ColorPlane p: pls){
			p.translate(d);
		}
	}
	@Override
	public Surface rotateX(double t) {
		for(ColorPlane p: pls){
			p.position = p.position.rotateX(t);
			p.normal = p.normal.rotateX(t);
		}
		return this;
	}
	@Override
	public Surface rotateY(double t) {
		for(ColorPlane p: pls){
			p.position = p.position.rotateY(t);
			p.normal = p.normal.rotateY(t);
		}
		return this;
	}
	
}
