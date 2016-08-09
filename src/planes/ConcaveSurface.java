package planes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import core.Vec3;

public class ConcaveSurface implements Surface{
	private List<ColorPlane> pls;
	private List<List<Integer>> adj;
	private Vec3 sphC;
	private double r2;
	public static double intPow = -1;
	
	public ConcaveSurface(Vec3 sphC, double radius2, List<ColorPlane> pls, List<List<Integer>> adj) {
		this.sphC = sphC;
		this.r2 = radius2;
		this.pls = pls;
		this.adj = adj;
	}

	@Override
	public ColorPlane getAt(Vec3 ray) {
		return getAt(new Vec3(0,0,0), ray);
	}
	
	public ColorPlane getAt(Vec3 pt, Vec3 dir) {
		dir = dir.normalize();
		if(Math.pow(dir.dot(pt.sub(sphC)),2)-pt.sub(sphC).mag2()+r2<0) return null;
		//boolean debug = Math.random()>.9999;
		double[] d = new double[pls.size()];
		int[] pointed = new int[pls.size()];
		List<Integer> best = new ArrayList<Integer>();
		for(int i=0; i<pls.size(); i++){
			d[i] = pls.get(i).normal1.dot(pls.get(i).position.sub(pt))/pls.get(i).normal1.dot(dir);
		}
//		if(Math.random()>.99999){
//			for(int i=0; i<d.length; i++){
//				for(int j=0; j<d.length; j++){
//					if(i!=j && d[i]==d[j]) System.out.println("OH FFS");
//				}
//			}
//		}
		for(int i=0; i<pls.size(); i++){
			boolean bestPlane = true;
			if(d[i]<0){
				continue;
			}
			Vec3 posn = pls.get(i).position.add(pls.get(i).normal1.scale(.1));
			boolean towardsPt = pls.get(i).normal1.dot(dir)<0;//posn.dist(pt)<=pls.get(i).position.dist(pt);
			//boolean towardsPtN = posn.dist(pt)<=pls.get(i).position.dist(pt);
			for(int ind: adj.get(i)){
				//if(d[i]==d[ind]) System.out.println("same d");
				Vec3 posn2 = pls.get(ind).position.add(pls.get(ind).normal1.scale(.1));
				boolean towardsOther = posn.dist(posn2)<pls.get(i).position.dist(pls.get(ind).position);
				if(towardsPt ^ pls.get(ind).normal1.dot(dir)<0){
					if(towardsPt ^ (d[i]<d[ind]^towardsOther)){
						bestPlane = false;
						pointed[ind]++;
						break;
					}
					continue;
				}
				boolean convex = towardsOther ^ towardsPt;
				//if(Math.random()>.99999 && (convex && !towardsPt)) System.out.println(pls.get(i)+" "+pls.get(ind));
				if((convex && d[i]<=d[ind]) || (!convex && d[i]>=d[ind])){
					bestPlane = false;
					pointed[ind]++;
					break;
				}
			}
			if(bestPlane) best.add(i);
		}
//		int removed = 0;
//		for(int i=best.size()-1; i>=0; i--){
//			if(pointed[best.get(i)]<3){
//				best.remove(i); removed++; continue;
//			}
//			for(int ind: adj.get(best.get(i))){
//				if(pointed[ind]<1){
//					best.remove(i); removed++; break;
//				}
//				for(int ind2: adj.get(ind)){
//					if(pointed[ind2]<1){
//						best.remove(i); removed++; break;
//					}
//				}
//			}
//			
//		}
//		if(Math.random()>.9999) System.out.println("removed "+removed+" "+best.size()+" left");
//		if(Math.random()>.9999) System.out.println(Arrays.toString(pointed));
		if(best.size()<=0) {
			if(Math.random()>.999999) System.out.println("empty");
			return null;
		}
		/*if(best.size()%2==1 && Math.random()>.999){
			System.out.println("odd "+best.size());
			return null;
		}*/
//		for(int ind: best){
//			for(int ind2: best){
//				if(adj.get(ind).contains(ind2)){
//					return null;
//				}
//			}
//		}
		if(Math.random()>.99999) System.out.println(best.size());
		final double[] d2 = d;
		Collections.sort(best,new Comparator<Integer>(){
			@Override
			public int compare(Integer o1, Integer o2) {
				if (d2[o1]<d2[o2]) return -1;
				if (d2[o2]<d2[o1]) return 1;
				return 0;
			}
		});
		if(Math.random()>.999995){
			System.out.print("[");
			for(int ind: best){
				System.out.print(pls.get(ind)+", ");
			}
			System.out.println("]");
		}
		//if(best.size()==1) return null;
		Vec3 normal = Vec3.ZERO;//pls.get(best.get(0)).normal1.scale(Math.log(d[best.get(0)]+2));
		Vec3 pln1 = pls.get(best.get(0)).normal1;//.scale(.1);//Math.log(d[best.get(0)]+2));
//		Vec3 pln1 = pls.get(best.get(0)).normal1;
		for(int i: adj.get(best.get(0))){
//			for(int j: adj.get(i)){
//				if(j!=best.get(0)){
					normal = normal.add(pls.get(i).normal1.add(pln1).scale(Math.pow(Math.abs(d[i]-d[best.get(0)]),intPow)));
//				}
//			}
		}
		//if(Math.random()>.9999) System.out.println(normal);
		return new ColorPlane(pt.add(dir.scale(d[best.get(0)])),
				normal,pls.get(best.get(0)).color);
	}

	@Override
	public boolean isGetAtNull(Vec3 ray) {
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
		for(ColorPlane p: pls){
			p.translate(d);
		}
		sphC = sphC.add(d);
	}
	
//	public void rotateFirst(){
//		pls.get(0).normal = pls.get(0).normal.rotateX(Math.PI/32).rotateY(Math.PI/32);
//		pls.get(0).normNorm();
//	}
	
	@Override
	public Surface rotateX(double t) {
		for(ColorPlane p: pls){
			p.position = p.position.rotateX(t);
			p.normal = p.normal.rotateX(t);
			p.normPos();
			p.normNorm();
		}
		sphC = sphC.rotateX(t);
		return this;
	}

	@Override
	public Surface rotateY(double t) {
		for(ColorPlane p: pls){
			p.position = p.position.rotateY(t);
			p.normal = p.normal.rotateY(t);
			p.normPos();
			p.normNorm();
		}
		sphC = sphC.rotateY(t);
		return this;
	}
}
