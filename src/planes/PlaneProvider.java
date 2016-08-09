package planes;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import core.CamPlane;
import core.Vec3;
import drawing.ColorPoint;
import drawing.PixelProvider;

public class PlaneProvider<E extends Surface> implements PixelProvider{
	private CamPlane p;
	private List<E> s;
	private static Vec3 hLPos = new Vec3(0,12,4);
	//private List<ColorPoint> pts2;

	public PlaneProvider(){
		p = new CamPlane();
		s = new ArrayList<E>();
		//pts2 = new ArrayList<ColorPoint>(pts);
	}
	
	/*@Override
	public int getRGB(double x, double y) {
		Vec3 pt3 = p.getPoint(x, y);
		//if(debug)System.out.println("point gotten");
		/*int[] pos = findRange1(pls,value-.05, value+.05);
		//if(debug)System.out.println("closest found");
		ColorPlane cl = pls.get(pos[0]);
		for(int i=pos[0]; i<pos[1]&&i<pls.size(); i++){
			ColorPlane pt = pls.get(i);
			if(pt.getValue2()>value2-.05 && pt.getValue2()<value2+.05){
				if(pt3.dot(pt.position)/pt3.abs()/pt.position.abs()>pt3.dot(cl.position)/pt3.abs()/cl.position.abs()){
					cl = pt;
				}
			}
		}*/
		/*for(ColorPlane pl: pls){
			pl.setValue1(pl.position.dot(pt3));
		}
		Collections.sort(pls);
		int i=0;
		ColorPlane p = pls.get(i);
		Set<Vec3> blockedNormals = new HashSet<Vec3>();
		while(p.position.dot(pt3)/pt3.abs()/p.position.abs()>.9){
			if(p.color==-1){
				blockedNormals.add(p.normal);
				i++;
				p = pls.get(i);
				continue;
			}
			if(blockedNormals.contains(p.normal)){
				i++;
				p = pls.get(i);
				continue;
			}
			return p.color;
		}/ / / / / / / / ///*
		List<ColorPlane> planes = new ArrayList<ColorPlane>();
		for(Surface sf: s){
			if(!sf.isGetAtNull(pt3)){
				ColorPlane at = sf.getAt(pt3);
				if(at!=null){
					planes.add(at);
				}
			}
		}
		
		if(planes.size()==0) return Color.WHITE.getRGB();
		ColorPlane closest = planes.get(0);
		double dist = closest.position.abs();
		for(ColorPlane pl: planes){
			if(pl.position.abs()<dist){
				closest = pl;
				dist = pl.position.abs();
			}
		}
		Color clColor = new Color(closest.color);
		Vec3 hLDist = hLPos.sub(closest.position);
		double inc = 200*hLDist.dot(closest.normal)/closest.normal.mag()/hLDist.mag()-75;
		int newRed = constrain(clColor.getRed()+inc), 
		newBlue = constrain(clColor.getBlue()+inc), newGreen = constrain(clColor.getGreen()+inc);
		return new Color(newRed, newGreen, newBlue).getRGB();
	}*/
	@Override
	public int getRGB(double x, double y) {
		Vec3 pt3 = p.getPoint(x, y);
		List<ColorPlane> planes = new ArrayList<ColorPlane>();
		
		for(Surface sf: s){
			boolean sfnull = sf.isGetAtNull(pt3);
			if(!sfnull){
				ColorPlane at = sf.getAt(pt3);
				if(at!=null){
					planes.add(at);
				}
			}
		}
		
		if(planes.size()==0) return Color.WHITE.getRGB();
		ColorPlane closest = planes.get(0);
		double dist = closest.position.abs();
		for(ColorPlane pl: planes){
			if(pl.position.abs()<dist){
				closest = pl;
				dist = pl.position.abs();
			}
		}
		Color clColor = new Color(closest.color);
		//if(closest.color == -1) return Color.BLACK.getRGB();
//		if(clColor.equals(Color.GREEN))
		boolean shadow = false;
		if(((ConcaveSurface)(s.get(0))).getAt(closest.position.add(closest.normal1.scale(.01)),hLPos.scale(2).sub(closest.position))!=null){
			shadow = true;
		}
		Vec3 hLDist = hLPos.sub(closest.position);
		Vec3 lDist = hLPos.scale(2).sub(closest.position);
		float[] hsb = Color.RGBtoHSB(clColor.getRed(), clColor.getGreen(), clColor.getBlue(), null);
		float ambient = .2f;
		float diffuse = (float) (lDist.dot(closest.normal1)/hLDist.mag())/1.75f;
		if(shadow){
			diffuse/=8;
		}
		float specular = shadow? 0: (float) Math.pow(hLDist.dot(closest.normal1)/hLDist.mag(),2)/20;
		//double inc = -80*Math.pow(Math.acos(hLDist.dot(closest.normal1)/hLDist.mag()),2)+80*Math.pow(hLDist.dot(closest.normal1)/hLDist.mag(),6);
		//if(Math.random()>.9999) System.out.println(closest);
//		int newRed = constrain(clColor.getRed()+inc), 
//		newBlue = constrain(clColor.getBlue()+inc), newGreen = constrain(clColor.getGreen()+inc);
//		return new Color(newRed, newGreen, newBlue).getRGB();
		hsb[2] = constrain2(ambient+diffuse+specular)*hsb[2];
		//hsb[1] = -constrain3(-(ambient+diffuse+specular)*1.5f)*hsb[1];
		return Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
	}
	private static int constrain(double unc){
		int newVal = (int)unc;
		if(newVal<=255 && newVal>=0) return newVal;
		if(newVal>255) return 255;
		return 0;
	}
	private static float constrain2(float unc){
		if(unc<=1 && unc>=0) return unc;
		if(unc>1) return 1;
		return 0;
	}
	private static float constrain3(float unc){
		if(unc<=0 && unc>=-1) return unc;
		if(unc>0) return 0;
		return -1;
	}
	
	public void rotateLight(){
		hLPos = hLPos.rotateZ(Math.PI/16);
	}
	public void addSurface(E surface){
		s.add(surface);
	}

	public void setSurfaces(List<E> s) {
		this.s = s;
	}

	@Override
	public void nextFrame() {
	}
	
	public static int findClosest(List<ColorPlane> l, ColorPlane c){
		int left = 0, right = l.size()-1, mid = (left+right)/2;
		while(right>left+1){
			if(c.compareTo(l.get(mid))==0){ return mid;}
			if(c.compareTo(l.get(mid))<0){
				right = mid;
			}
			else {
				left = mid;
			}
			mid = (left+right)/2;
		}
		//System.out.println(count);
		return mid;
	}
	public static int[] findRange1(List<ColorPlane> l, double low, double high){
		int[] range = new int[2];
		int left = 0, right = l.size()-1, mid = (left+right)/2;
		while(right>left+1){
			if(low<=l.get(mid).getValue1()){
				right = mid;
			}
			else {
				left = mid;
			}
			mid = (left+right)/2;
		}
		range[0] = mid;
		left = 0; right = l.size()-1; mid = (left+right)/2;
		while(right>left+1){
			if(high<l.get(mid).getValue1()){
				right = mid;
			}
			else {
				left = mid;
			}
			mid = (left+right)/2;
		}
		range[1] = mid;
		return range;
	}
	public void changeInterpolation(){
		ConcaveSurface.intPow*=2;
	}
}
