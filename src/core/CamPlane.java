package core;

public class CamPlane {
	public double a;
	public double b;
	public double c;
	public CamPlane(double a, double b, double c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}
	public CamPlane(){
		this(0,0,1);
	}
	public void setCoords(double a, double b, double c){
		this.a = a;
		this.b = b;
		this.c = c;
	}
	public void setSphericalCoords(double theta, double phi){
		setCoords(Math.cos(theta)*Math.sin(phi),
				Math.sin(theta)*Math.sin(phi),
				Math.cos(phi));
	}
	public void setCoords(Vec3 coord){
		this.a = coord.x;
		this.b = coord.y;
		this.c = coord.z;
	}
	public Vec3 getCoords(){
		return new Vec3(a,b,c);
	}
	
	public Vec3 getPoint(double x, double y){
		double x1 = a; double y1 = b; double z1 = c;
		x1+=c*x+b*y; z1+=b*x+a*y; y1+= a*x+c*y; //this *might* work but is at best an approximation
		return new Vec3(x1, y1, z1);
	}
	
	public static void main(String[] args) {
		System.out.println(new CamPlane(0,0,1).getPoint(1, 0));
	}
}
