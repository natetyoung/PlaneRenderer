package drawing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import core.*;

public class PointProvider implements PixelProvider{
	private CamPlane p;
	private List<ColorPoint> pts;
	//private List<ColorPoint> pts2;

	public PointProvider(){
		p = new CamPlane();
		pts = new ArrayList<ColorPoint>();
		//pts2 = new ArrayList<ColorPoint>(pts);
	}
	
	@Override
	public int getRGB(double x, double y) {
		Vec3 pt3 = p.getPoint(x, y);
		//if(debug)System.out.println("point gotten");
		double value = pt3.atanzx();
		double value2 = pt3.atanyx();
		int[] pos = findRange1(pts,value-.05, value+.05);
		//if(debug)System.out.println("closest found");
		for(int i=pos[0]; i<pos[1]&&i<pts.size(); i++){
			ColorPoint pt = pts.get(i);
			if(pt.getValue2()>value2-.05 && pt.getValue2()<value2+.05){
				if(pt3.dot(pt.position)/pt3.abs()/pt.position.abs()>.999995){
					return pt.color;
				}
			}
		}
		return Color.WHITE.getRGB();
	}
	
	public void addPoint(Vec3 position, int color){
		pts.add(new ColorPoint(position, color));
		//pts2.add(new ColorPoint(position, color));
	}
	public void addPoint(ColorPoint p){
		pts.add(p);
		//pts2.add(p);
	}
	public void setPts(List<ColorPoint> pts) {
		this.pts = pts;
		//this.pts2 = pts;
	}

	@Override
	public void nextFrame() {
		for(ColorPoint pt: pts){
			pt.setValue1(pt.position.atanzx());
			pt.setValue2(pt.position.atanyx());
		}
		Collections.sort(pts);
		//System.out.println(pts);
	}
	
	public static int findClosest(List<ColorPoint> l, ColorPoint c){
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
	public static int[] findRange1(List<ColorPoint> l, double low, double high){
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
}