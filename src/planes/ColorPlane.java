package planes;

import core.Vec3;

public class ColorPlane implements Comparable<ColorPlane> {
	public Vec3 position;
	public Vec3 position1;
	public Vec3 normal;
	public Vec3 normal1;
	public int color;
	private double value1;
	private double value2;
	
	public ColorPlane(Vec3 position, Vec3 normal, int color) {
		super();
		this.position = position;
		this.normal = normal;
		this.color = color;
		this.value1 = position.z/position.abs();
		this.position1 = position.normalize();
		this.normal1 = normal.normalize();
	}

	public ColorPlane(double x, double y, double z, double x1, double y1, double z1, int color){
		this(new Vec3(x,y,z), new Vec3(x1,y1,z1), color);
	}
	
	public void translate(Vec3 d){
		this.position = position.add(d);
		this.position1 = position.normalize();
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
	public void normPos(){
		position1 = position.normalize();
	}
	public void normNorm(){
		normal1 = normal.normalize();
	}
	@Override
	public int compareTo(ColorPlane o) {
		if(value1<o.value1) return -1;
		if(value1==o.value1) return 0;
		return 1;
	}
	
	@Override
	public String toString() {
		return "Color "+color+" at "+position+" facing. "+normal;
	}
}
