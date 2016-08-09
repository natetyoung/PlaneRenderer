package planes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.print.ServiceUIFactory;

import core.Vec3;

public class ConvexSurface implements Surface {
	private List<ColorPlane> pls;
	public static int numPlane = 1;
	public static int numClosestT = 10;
	public static int numClosestA = 1;
	
	public ConvexSurface(List<ColorPlane> pls, double planeDist) {
		this.pls = pls;
	}
	public ConvexSurface(ColorPlane...colorPlanes){
		this(Arrays.asList(colorPlanes),colorPlanes[0].position.sub(colorPlanes[1].position).mag());
	}
	public ConvexSurface(double planeDist){
		this(new ArrayList<ColorPlane>(), planeDist);
	}
	public ConvexSurface(){
		this(new ArrayList<ColorPlane>(), 0);
	}
	
	@Override
	public ColorPlane getAt(Vec3 ray) {
		return getAt(new Vec3(0,0,0),ray);
		/*ray = ray.normalize();
		
		ColorPlane[] clT = new ColorPlane[numClosestT];
		double[] ctT = new double[numClosestT];
		ColorPlane[] clA = new ColorPlane[numClosestA];
		double[] ctA = new double[numClosestA];
		for(int i=0; i<ctT.length; i++){
			ctT[i] = -100;
		}
		for(int i=0; i<ctA.length; i++){
			ctA[i] = 100000;
		}
		for(ColorPlane cp: pls){
			double cpD = cp.normal1.dot(cp.position)/cp.normal1.dot(ray);//(ray.dot(cp.normal));
			if(cp.normal1.dot(ray)<0){
				int i;
				for(i=clT.length-1; i>=0; i--){
					if(cpD>ctT[i]){
						if(i>=clT.length-1) continue;
						ctT[i+1] = ctT[i];
						clT[i+1] = clT[i];
					} else {
						break;
					}
				}
				if(i<clT.length-1){
					clT[i+1] = cp;
					ctT[i+1] = cpD;
				}
			} else {
				int i;
				for(i=clA.length-1; i>=0; i--){
					if(cpD<ctA[i]){
						if(i>=clA.length-1) continue;
						ctA[i+1] = ctA[i];
						clA[i+1] = clA[i];
					} else {
						break;
					}
				}
				if(i<clA.length-1){
					clA[i+1] = cp;
					ctA[i+1] = cpD;
				}
			}
		}
		if(ctT[0]>ctA[0]) return null;

		Vec3 pos = ray.scale(ctT[0]);
		
		int intNum = pls.size()/6;
		ColorPlane[] clO = new ColorPlane[intNum];
		double[] cdO = new double[intNum];
		for(int i=0; i<cdO.length; i++){
			cdO[i] = 1000;
		}
		for(ColorPlane cp: pls){
			double cpD = cp.position.sub(pos).dot(cp.normal1)/clT[0].normal1.dot(cp.normal1);
			int i;
			for(i=clO.length-1; i>=0; i--){
				if(cpD<cdO[i] && cpD>0){
					if(i>=clO.length-1) continue;
					cdO[i+1] = cdO[i];
					clO[i+1] = clO[i];
				} else {
					break;
				}
			}
			if(i<clO.length-1){
				clO[i+1] = cp;
				cdO[i+1] = cpD;
			}
		}
		
		//pos = pos.add(clT[0].normal1.scale(cdO[1]/cdO[2]));
//		Vec3 normal = new Vec3(0,0,0);
		Vec3 normal = clO[0].normal.add(clO[1].normal);
		for(int i=2; i<clO.length-1; i++){
			normal = normal.add(clO[i].normal.scale(cdO[i-1]/cdO[i+1]/cdO[i]));
		}
		normal = normal.normalize();

		return new ColorPlane(pos, normal, clT[0].color);*/
	}
	
	public ColorPlane getAt(Vec3 pt, Vec3 dir){
		dir = dir.normalize();
		
		ColorPlane[] clT = new ColorPlane[numClosestT];
		double[] ctT = new double[numClosestT];
		ColorPlane[] clA = new ColorPlane[numClosestA];
		double[] ctA = new double[numClosestA];
		for(int i=0; i<ctT.length; i++){
			ctT[i] = -100;
		}
		for(int i=0; i<ctA.length; i++){
			ctA[i] = 100000;
		}
		for(ColorPlane cp: pls){
			double cpD = cp.normal1.dot(cp.position.sub(pt))/cp.normal1.dot(dir);//(ray.dot(cp.normal));
			if(cp.normal1.dot(dir)<0){
				int i;
				for(i=clT.length-1; i>=0; i--){
					if(cpD>ctT[i]){
						if(i>=clT.length-1) continue;
						ctT[i+1] = ctT[i];
						clT[i+1] = clT[i];
					} else {
						break;
					}
				}
				if(i<clT.length-1){
					clT[i+1] = cp;
					ctT[i+1] = cpD;
				}
			} else {
				int i;
				for(i=clA.length-1; i>=0; i--){
					if(cpD<ctA[i]){
						if(i>=clA.length-1) continue;
						ctA[i+1] = ctA[i];
						clA[i+1] = clA[i];
					} else {
						break;
					}
				}
				if(i<clA.length-1){
					clA[i+1] = cp;
					ctA[i+1] = cpD;
				}
			}
		}
		if(ctT[0]>ctA[0] || clT[0] == null) return null;
		//if(ctT[0]>ctA[0]-.2) return new ColorPlane(0,0,0,0,0,0,-1);

		Vec3 pos = dir.scale(ctT[0]).add(pt);
		
		int intNum = pls.size()/6;
		ColorPlane[] clO = new ColorPlane[intNum];
		double[] cdO = new double[intNum];
		for(int i=0; i<cdO.length; i++){
			cdO[i] = 1000;
		}
		for(ColorPlane cp: pls){
			double cpD = 0;
			try{
			cpD = cp.position.sub(pos).dot(cp.normal1)/clT[0].normal1.dot(cp.normal1);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(clT[0]);
			}
			int i;
			for(i=clO.length-1; i>=0; i--){
				if(cpD<cdO[i] && cpD>0){
					if(i>=clO.length-1) continue;
					cdO[i+1] = cdO[i];
					clO[i+1] = clO[i];
				} else {
					break;
				}
			}
			if(i<clO.length-1){
				clO[i+1] = cp;
				cdO[i+1] = cpD;
			}
		}
		
		//pos = pos.add(clT[0].normal1.scale(cdO[1]/cdO[2]));
//		Vec3 normal = new Vec3(0,0,0);
		Vec3 normal = clO[0].normal.add(clO[1].normal);
		for(int i=2; i<clO.length-1; i++){
			normal = normal.add(clO[i].normal.scale(cdO[i-1]/cdO[i+1]/cdO[i]));
		}
		normal = normal.normalize();

		return new ColorPlane(pos, normal, clT[0].color);
	}
	
	@Override
	public boolean isGetAtNull(Vec3 ray) {
		/*ray = ray.normalize();
		if(dirty){
			dirty = false;
			synchronized(horizon){
			for(ColorPlane pl: pls){
				pl.setValue1(Math.abs(pl.position.dot(pl.normal)/pl.position.mag()/pl.normal.mag()));
			}
			Collections.sort(pls);
			for(int i=0; i<Math.pow(pls.size(),2/3.0)/6; i++){
				horizon.add(pls.get(i));
			}
			int hSize = horizon.size();
			for(int i=0; i<hSize; i++){
				for(int j=0; j<hSize; j++){
					if(i!=j && horizon.get(i).position.sub(horizon.get(j).position).mag2()<.2){
						horizon.add(new ColorPlane(horizon.get(i).position.add(horizon.get(j).position).scale(.5),
								horizon.get(i).normal.add(horizon.get(j).normal).scale(.5), -1));
						ColorPlane added = horizon.get(horizon.size()-1);
						if(Math.abs(added.position.dot(added.normal)/added.position.mag()/added.normal.mag())>horizon.get(i).getValue1()){
							horizon.remove(horizon.size()-1);
						}
					}
				}
			}
			System.out.println(horizon);
			for(ColorPlane cp: horizon){
				System.out.print(cp.getValue1()+" ");
			}
			}
		}
		synchronized(horizon){
			if(!horizon.isEmpty()){
			/*ColorPlane ch = horizon.get(0);
			double chDist = ch.position.dot(ray)/ray.mag()/ch.position.mag();* /
			for(ColorPlane h: horizon){
				if(h.normal.dot(ray)<=0) return false;
				/*if(h.position.dot(ray)/ray.mag()/h.position.mag()>chDist){
					chDist = h.position.dot(ray)/ray.mag()/h.position.mag();
					ch = h;
				}* /
			}
			return true;
			}
		}
		//SHOULD BE A THING NOW
		return false;*/
		//ray = ray.normalize();
		
		
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
		for(ColorPlane p: pls){
			p.translate(d);
		}
	}

	@Override
	public Surface rotateX(double t) {
		for(ColorPlane p: pls){
			p.position = p.position.rotateX(t);
			p.normal = p.normal.rotateX(t);
			p.normPos();
			p.normNorm();
		}
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
		return this;
	}
	
	public void add(ColorPlane p){
		pls.add(p);
		/*if(planeDist==0 && pls.size()>1){
			planeDist = pls.get(0).position.sub(pls.get(1).position).mag();
		}*/
	}
	
	@Override
	public String toString() {
		return pls.toString();
	}

}



/*@Override
public ColorPlane getAt(Vec3 ray) {
	ray = ray.normalize();
	
	ColorPlane[] clT = new ColorPlane[numClosestT];
	double[] ctT = new double[numClosestT];
	ColorPlane[] clA = new ColorPlane[numClosestA];
	double[] ctA = new double[numClosestA];
	for(int i=0; i<ctT.length; i++){
		ctT[i] = -100;
	}
	for(int i=0; i<ctA.length; i++){
		ctA[i] = 100000;
	}
	for(ColorPlane cp: pls){
		double cpD = cp.normal1.dot(cp.position)/cp.normal1.dot(ray);//(ray.dot(cp.normal));
		if(cp.normal1.dot(ray)<0){
			int i;
			for(i=clT.length-1; i>=0; i--){
				if(cpD>ctT[i]){
					if(i>=clT.length-1) continue;
					ctT[i+1] = ctT[i];
					clT[i+1] = clT[i];
				} else {
					break;
				}
			}
			if(i<clT.length-1){
				clT[i+1] = cp;
				ctT[i+1] = cpD;
			}
		} else {
			int i;
			for(i=clA.length-1; i>=0; i--){
				if(cpD<ctA[i]){
					if(i>=clA.length-1) continue;
					ctA[i+1] = ctA[i];
					clA[i+1] = clA[i];
				} else {
					break;
				}
			}
			if(i<clA.length-1){
				clA[i+1] = cp;
				ctA[i+1] = cpD;
			}
		}
	}
	/*Vec3 avg = clT[0].position.add(clA[0].position).scale(.5);
	Vec3 passByAvg = ray.scale(ray.dot(avg));
	Vec3[] posnT = new Vec3[clT.length];
	for(int i=0; i<posnT.length; i++){
		posnT[i] = clT[i].position.add(clT[i].normal1.scale(.01));
	}
	Vec3[] posnA = new Vec3[clA.length];
	for(int i=0; i<posnA.length; i++){
		posnA[i] = clA[i].position.add(clA[i].normal1.scale(.01));
	}
//	Vec3[] posnA = {clA[0].position.add(clA[0].normal1.scale(.01)),
//			clA[1].position.add(clA[1].normal1.scale(.01)),
//			clA[2].position.add(clA[2].normal1.scale(.01))};
	for(int i=0; i<(posnT.length>=3? 3 : posnT.length); i++){
		if(passByAvg.sub(clT[i].position).mag2()>passByAvg.sub(posnT[i]).mag2()){
			return null;
		}
	}
	for(int i=0; i<posnA.length; i++){
		if(passByAvg.sub(clA[i].position).mag2()>passByAvg.sub(posnA[i]).mag2()){
			return null;
		}
	}*/
/*
//	if(Math.random()>.99999){
//		System.out.println(Arrays.toString(ctT)+Arrays.toString(ctA));
//	}
	if(ctT[0]>ctA[0]) return null;
	/*boolean rn = true;
	Vec3 posn = clT[0].position1.add(clT[0].normal1.scale(.01)).normalize();
	if(ray.dot(clT[0].position1)>=ray.dot(posn)){
		rn=false;
	}
	posn = clT[1].position1.add(clT[1].normal1.scale(.01)).normalize();
	if(ray.dot(clT[1].position1)>=ray.dot(posn)){
		rn=false;
	}
	posn = clT[2].position1.add(clT[2].normal1.scale(.01)).normalize();
	if(ray.dot(clT[2].position1)>=ray.dot(posn)){
		rn=false;
	}
	if(rn) return null;*/
	//double d12 = clT[0].position1.dot(clT[1].position1);
	//if(Math.pow(Math.acos(clTDist2), 2)>Math.pow(Math.acos(clTDist), 2)+Math.pow(Math.acos(d12), 2)+TOLERANCE_CHECK) return null;
	
//	Vec3 pos1 = ray.scale(clT[0].position.dot(clT[0].normal)/ray.dot(clT[0].normal));
//	Vec3 pos2 = ray.scale(clT[1].position.dot(clT[1].normal)/ray.dot(clT[1].normal));
//	Vec3 pos3 = ray.scale(clT[2].position.dot(clT[2].normal)/ray.dot(clT[2].normal));

/*
	Vec3 pos = ray.scale(ctT[0]);
	
	int intNum = 10;// clT[0].normal1.equals(clT[1].normal1)? 1:10;//(int)Math.min(10-9/(1+Math.exp(200*Math.acos(clT[0].normal1.dot(clT[1].normal1))-200)),clT.length);
	ColorPlane[] clO = new ColorPlane[intNum];
	double[] cdO = new double[intNum];
	for(ColorPlane cp: pls){
		if(cp.normal1.dot(clT[0].normal1)>0){
			double cpD = Math.abs(1/cp.normal1.dot(pos.sub(cp.position)));
			int i;
			for(i=clO.length-1; i>=0; i--){
				if(cpD>cdO[i]){
					if(i>=clO.length-1) continue;
					cdO[i+1] = cdO[i];
					clO[i+1] = clO[i];
				} else {
					break;
				}
			}
			if(i<clO.length-1){
				clO[i+1] = cp;
				cdO[i+1] = cpD;
			}
		}
	}
//	if(Math.random()>.9999) System.out.println(Arrays.toString(cdO));
	/*boolean flatSide = false;
	if(clT[0].normal1.sub(clT[1].normal1).mag()<.2){
		flatSide = true;
		//System.out.println(clT[0]+" "+clT[1]);
	}*/
	//pos.scale(1.0/posArr.length);
//	Vec3 pos1 = ray.scale(ray.dot(clT[0].position));
//	Vec3 pos2 = ray.scale(ray.dot(clT[1].position));
//	Vec3 pos3 = ray.scale(ray.dot(clT[2].position));
	
//	double[] scArr = new double[intNum];
//	//scArr[0] = 1/clT[0].position.sub(posArr[0]).mag();
//	
//	for(int i=1; i<scArr.length; i++){
//		scArr[i] = clT[i].normal1.dot(pos.sub(clT[i].position));
//	}
//	double sc1 = 1/clT[0].position.sub(pos1).mag2(), 
//			sc2 = 1/clT[1].position.sub(pos2).mag2(),
//			sc3 = 1/clT[2].position.sub(pos3).mag2();
	//Vec3 pos = ray.scale(clT[0].position.dot(clT[0].normal)/ray.dot(clT[0].normal));


/*
	Vec3 normal = clO[0].normal;
	for(int i=1; i<clO.length; i++){
		normal = normal.add(clO[i].normal.scale(cdO[i]));
	}
	normal = normal.normalize();
	/*Vec3 pos = new Vec3(0,0,0);
	for(int i=0; i<scArr.length; i++){
		pos = pos.add(posArr[i].scale(scArr[i]));
	}
	pos = pos.scale(1/scTotal);*/
//	Vec3 normal = clT[0].normal1.scale(sc1)
//			.add(clT[1].normal1.scale(sc2))
//			.add(clT[2].normal1.scale(sc3))
//			.scale(1/(sc1+sc2+sc3));
//	Vec3 pos = pos1.scale(sc1)
//			.add(pos2.scale(sc2))
//			.add(pos3.scale(sc3))
//			.scale(1/(sc1+sc2+sc3));
	//if(Math.random()>.99999) System.out.println(Arrays.toString(scArr));
	//if(flatSide) normal = clT[0].normal;
	
	//if(flatSide) System.out.println(new ColorPlane(pos, normal, clT[0].color));


/*
	return new ColorPlane(pos, normal, clT[0].color);
}*/
