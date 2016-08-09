package drawing;

import core.Vec3;

public class ColorPoint implements Comparable<ColorPoint>{
	public Vec3 position;
	public int color;
	private double value1;
	private double value2;
	public ColorPoint(Vec3 position, int color) {
		this.position = position;
		this.color = color;
		this.value1 = position.z/position.abs();
	}
	public ColorPoint(double x, double y, double z, int color){
		this(new Vec3(x,y,z), color);
	}
	
	public void translate(Vec3 d){
		this.position = position.add(d);
	}
	public void setPos(Vec3 pos){
		this.position = pos;
	}
	public void setValue1(double value){
		this.value1 = value;
	}
	public double getValue1(){
		return value1;
	}
	public void setValue2(double value){
		this.value2 = value;
	}
	public double getValue2(){
		return value2;
	}
	@Override
	public int compareTo(ColorPoint o) {
		if(value1<o.value1) return -1;
		if(value1==o.value1) return 0;
		return 1;
	}
	
	@Override
	public String toString() {
		return "Color "+color+" at "+position;
	}
}
