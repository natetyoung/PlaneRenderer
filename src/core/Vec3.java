package core;

public class Vec3 {
	private static final double EPSILON = .00000000000001;
	public static final Vec3 ZERO = new Vec3(0,0,0);
	public double x;
	public double y;
	public double z;
	public Vec3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public String toString() {
		return "<"+x+", "+y+", "+z+">";
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof Vec3)) return false;
		Vec3 o = (Vec3) other;
		return Math.abs(x-o.x)<EPSILON&&Math.abs(y-o.y)<EPSILON&&Math.abs(z-o.z)<EPSILON;
	}
	public double dot(Vec3 o){
		return x*o.x+y*o.y+z*o.z;
	}
	public Vec3 cross(Vec3 o){
		return new Vec3(y*o.z-z*o.y,z*o.x-x*o.z,x*o.y-y*o.x);
	}
	public double abs(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	public Vec3 add(Vec3 o){
		return new Vec3(x+o.x,y+o.y,z+o.z);
	}
	public Vec3 sub(Vec3 o){
		return new Vec3(x-o.x,y-o.y,z-o.z);
	}
	public Vec3 scale(double scalar){
		return new Vec3(x*scalar, y*scalar, z*scalar);
	}
	public double mag(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	public double mag2(){
		return x*x+y*y+z*z;
	}
	public double dist(Vec3 o){
		return this.sub(o).mag();
	}
	public double dist2(Vec3 o){
		return this.sub(o).mag2();
	}
	public Vec3 normalize(){
		double mag = mag();
		return new Vec3(x/mag, y/mag, z/mag);
	}
	public Vec3 rotateX(double t){
		return new Vec3(x, y*Math.cos(t)-z*Math.sin(t), y*Math.sin(t)+z*Math.cos(t));
	}
	public Vec3 rotateY(double t){
		return new Vec3(z*Math.sin(t)+x*Math.cos(t), y, z*Math.cos(t)-x*Math.sin(t));
	}
	public Vec3 rotateZ(double t){
		return new Vec3(x*Math.cos(t)-y*Math.sin(t), x*Math.sin(t)+y*Math.cos(t), z);
	}
	public double atanzx(){return Math.atan2(z, x);}
	public double atanyx(){return Math.atan2(y, x);}
}
