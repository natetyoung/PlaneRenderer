package drawing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import core.Vec3;
import planes.ColorPlane;
import planes.ConcaveSurface;
import planes.ConvexSurface;
import planes.FlatSurface;
import planes.PlaneProvider;
import planes.Surface;
import src.Facet;
import src.STLCreator;

public class RenderPanel extends JPanel{
	public static final int PANEL_HEIGHT = 500;
	public static final int PANEL_WIDTH = 500;
	private int width;
	private int height;
	private PixelProvider pixels;
	private BufferedImage img;
	private AtomicBoolean calculating;
	public static int numPics = 0;
	public static long lastFrameTime = System.nanoTime();
	
	public RenderPanel(int width, int height, PixelProvider pixels) {
		super();
		this.width = width;
		this.height = height;
		this.pixels = pixels;
		this.img = new BufferedImage(width-16, height-38, BufferedImage.TYPE_INT_RGB);
		this.calculating = new AtomicBoolean(true);
		setPreferredSize(new Dimension(PANEL_WIDTH,PANEL_HEIGHT));
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PANEL_WIDTH,PANEL_HEIGHT);
	}

	@Override
	public void paintComponent(Graphics g){
		if (calculating.get()) {
			return;
		}
		else {
			super.paintComponent(g);
			synchronized(img) {
				g.drawImage(img, 0, 0, null);
			}
		}
	}
	
	public void setPixelProvider(PixelProvider p){
		pixels = p;
	}
	
	private double translateX(int x){
		return (x+0.0-width/2)/width;
	}
	private double translateY(int y){
		return -(y+0.0-height/2)/height;
	}
	
	public void calculate() {
		calculating.set(true);
		final int width = getWidth();
		final int height = getHeight();
		
		final int threadsStart = 3;
		final AtomicInteger rowIndex = new AtomicInteger();
		final AtomicInteger threadsDone = new AtomicInteger();
		for (int i = 0; i < threadsStart; i++) {
			Thread thread = new Thread() {
				public void run() {
					while (rowIndex.get() < height) {
						int y = rowIndex.getAndIncrement();
						int[] row = new int[width];
						for (int x = 0; x < width; x++) {
							row[x] = pixels.getRGB(translateX(x),translateY(y));
						}
						img.setRGB(0, y, width, 1, row, 0, width);
					}
					threadsDone.incrementAndGet();
				}
			};
			thread.setPriority(Thread.MAX_PRIORITY);
			thread.start();
		};
		calculating.set(false);
		
		while (threadsDone.get() < threadsStart);
//		try {
//			ImageIO.write(img, "png", new File("image"+numPics+".png"));
//			numPics++;
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		System.out.println("time for last frame: "+(System.nanoTime()-lastFrameTime)/1000000);
		lastFrameTime = System.nanoTime();
	}
	
	public static void main(String[] args) throws Exception {

		final JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(PANEL_WIDTH,PANEL_HEIGHT);
		final PlaneProvider<Surface> p = new PlaneProvider<Surface>();
		/*p.addPoint(new Vec3(0,0,2), Color.BLUE.getRGB());
		ColorPoint red = new ColorPoint(new Vec3(1,0,50), Color.RED.getRGB());
		ColorPoint green = new ColorPoint(new Vec3(.5,.4,50), Color.GREEN.getRGB());
		p.addPoint(red);
		p.addPoint(green);*/
		/*final List<FlatSurface> s = new ArrayList<FlatSurface>(6);
		s.add(new FlatSurface());
		s.add(new FlatSurface());
		s.add(new FlatSurface());
		s.add(new FlatSurface());
		s.add(new FlatSurface());
		s.add(new FlatSurface());
		//final List<ColorPlane> l = new CopyOnWriteArrayList<ColorPlane>();
		for(double i=0; i<=0; i+=1){
			s.get(0).addPair(new ColorPlane(-.99,-1,i, 0,-1,0, Color.BLUE.getRGB()),new ColorPlane(-1.01,-1,i, 0,-1,0,  -1));
			s.get(0).addPair(new ColorPlane(.99,-1,i, 0,-1,0, Color.BLUE.getRGB()),new ColorPlane(1.01,-1,i, 0,-1,0,  -1));
			s.get(1).addPair(new ColorPlane(-.99,1,i, 0,1,0, Color.BLUE.getRGB()),new ColorPlane(-1.01,1,i, 0,1,0,  -1));
			s.get(1).addPair(new ColorPlane(.99,1,i, 0,1,0, Color.BLUE.getRGB()),new ColorPlane(1.01,1,i, 0,1,0,  -1));
			s.get(2).addPair(new ColorPlane(-1,-.99,i, -1,0,0, Color.BLUE.getRGB()),new ColorPlane(-1,-1.01,i, -1,0,0,  -1));
			s.get(3).addPair(new ColorPlane(1,-.99,i, 1,0,0, Color.BLUE.getRGB()),new ColorPlane(1,-1.01,i, 1,0,0,  -1));
			s.get(2).addPair(new ColorPlane(-1,.99,i, -1,0,0, Color.BLUE.getRGB()),new ColorPlane(-1,1.01,i, -1,0,0,  -1));
			s.get(3).addPair(new ColorPlane(1,.99,i, 1,0,0, Color.BLUE.getRGB()),new ColorPlane(1,1.01,i, 1,0,0,  -1));
			
			s.get(4).addPair(new ColorPlane(-.99,i,-1, 0,0,-1,Color.RED.getRGB()),new ColorPlane(-1.01,i,-1, 0,0,-1,  -1));
			s.get(4).addPair(new ColorPlane(.99,i,-1, 0,0,-1,Color.RED.getRGB()),new ColorPlane(1.01,i,-1, 0,0,-1,  -1));
			s.get(5).addPair(new ColorPlane(-.99,i,1, 0,0,1,Color.RED.getRGB()),new ColorPlane(-1.01,i,1, 0,0,1,  -1));
			s.get(5).addPair(new ColorPlane(.99,i,1, 0,0,1,Color.RED.getRGB()),new ColorPlane(1.01,i,1, 0,0,1,  -1));
			s.get(2).addPair(new ColorPlane(-1,i,-.99, -1,0,0,Color.RED.getRGB()),new ColorPlane(-1,i,-1.01, -1,0,0,  -1));
			s.get(3).addPair(new ColorPlane(1,i,-.99, 1,0,0,Color.RED.getRGB()),new ColorPlane(1,i,-1.01, 1,0,0,  -1));
			s.get(2).addPair(new ColorPlane(-1,i,.99, -1,0,0,Color.RED.getRGB()),new ColorPlane(-1,i,1.01, -1,0,0,  -1));
			s.get(3).addPair(new ColorPlane(1,i,.99, 1,0,0,Color.RED.getRGB()),new ColorPlane(1,i,1.01, 1,0,0,  -1));

			s.get(0).addPair(new ColorPlane(i,-1,-.99, 0,-1,0,Color.GREEN.getRGB()),new ColorPlane(i,-1,-1.01, 0,-1,0,  -1));
			s.get(1).addPair(new ColorPlane(i,1,-.99, 0,1,0,Color.GREEN.getRGB()),new ColorPlane(i,1,-1.01, 0,1,0,  -1));
			s.get(0).addPair(new ColorPlane(i,-1,.99, 0,-1,0,Color.GREEN.getRGB()),new ColorPlane(i,-1,1.01, 0,-1,0,  -1));
			s.get(1).addPair(new ColorPlane(i,1,.99, 0,1,0,Color.GREEN.getRGB()),new ColorPlane(i,1,1.01, 0,1,0,  -1));
			s.get(4).addPair(new ColorPlane(i,-.99,-1, 0,0,-1,Color.GREEN.getRGB()),new ColorPlane(i,-1.01,-1, 0,0,-1,  -1));
			s.get(4).addPair(new ColorPlane(i,.99,-1, 0,0,-1,Color.GREEN.getRGB()),new ColorPlane(i,1.01,-1, 0,0,-1,  -1));
			s.get(5).addPair(new ColorPlane(i,-.99,1, 0,0,1,Color.GREEN.getRGB()),new ColorPlane(i,-1.01,1, 0,0,1,  -1));
			s.get(5).addPair(new ColorPlane(i,.99,1, 0,0,1,Color.GREEN.getRGB()),new ColorPlane(i,1.01,1, 0,0,1,  -1));
		}*/
		final List<Surface> s = new ArrayList<Surface>(1);
//		ConvexSurface surface = new ConvexSurface();
		/*for(double i=0; i<Math.PI*2; i+=Math.PI/16){
			for(double j=0; j<Math.PI; j+=Math.PI/16){
				Vec3 pos = new Vec3(Math.cos(i)*Math.sin(j),Math.sin(i)*Math.sin(j),Math.cos(j));
				Vec3 norm = new Vec3(Math.cos(i)*Math.sin(j),Math.sin(i)*Math.sin(j),Math.cos(j));
				surface.add(new ColorPlane(pos, norm, new Color(153, 204, 255).getRGB()));
			}
		}*/
		
		s.add(createConcave("model.stl"));
		FlatSurface sq = new FlatSurface();
		sq.addPair(new ColorPlane(0,-3,-19.99, 0,1,0,Color.GREEN.getRGB()),new ColorPlane(0,-3,-20.01, 0,1,0,  -1));
		sq.addPair(new ColorPlane(0,-3,19.99, 0,1,0,Color.GREEN.getRGB()),new ColorPlane(0,-3,20.01, 0,1,0,  -1));
		sq.addPair(new ColorPlane(-19.99,-3,0, 0,1,0,Color.GREEN.getRGB()),new ColorPlane(-20.01,-3,0, 0,1,0,  -1));
		sq.addPair(new ColorPlane(19.99,-3,0, 0,1,0,Color.GREEN.getRGB()),new ColorPlane(20.01,-3,0, 0,1,0,  -1));
		s.add(sq);
		for(Surface sf: s){
			sf.translate(new Vec3(0,.1,15));
		}
		//surface.translate(new Vec3(0,-2.5,0));
		sq.translate(new Vec3(0,-0.1,-15));
		p.setSurfaces(s);
		final RenderPanel panel = new RenderPanel(PANEL_WIDTH,PANEL_HEIGHT, p);
		jf.add(panel);
		jf.setVisible(true);
		final double theta1 = Math.PI/16;
		final double theta2 = Math.PI/16;
		//for(Surface sf: s){
			s.get(0).translate(new Vec3(0,-.1,-15));
			s.get(0).rotateX(theta2).rotateY(theta1);
			s.get(0).translate(new Vec3(0,.1,15));
		//}
		Thread thread = new Thread() {
			
			@Override
			public void run() {
				while (true) {
//					for(Surface sf: s){
					s.get(0).translate(new Vec3(0,-.1,-15));
					s.get(0).rotateX(theta2).rotateY(theta1);
					s.get(0).translate(new Vec3(0,.1,15));
//					}
					p.nextFrame();
//					p.rotateLight();
//					p.changeInterpolation();
//					System.out.println(ConvexSurface.numClosestT);
//					((ConcaveSurface) s.get(0)).rotateFirst();
					panel.calculate();
				}
			}
			
		};
		thread.setPriority(Thread.MAX_PRIORITY - 1);
		thread.start();
		
		Thread paintThread = new Thread() {
			
			public void run() {
				while (true) {
					panel.repaint();
				}
			}
		};
		paintThread.setPriority(Thread.MAX_PRIORITY);
		paintThread.start();
		
		while (true) {}
	}
	
	// Thread stuff
	/*@Override
	public void run() {
		final JFrame jf = new JFrame();
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(500, 500);
		final PointProvider p = new PointProvider();
		/*p.addPoint(new Vec3(0,0,2), Color.BLUE.getRGB());
		ColorPoint red = new ColorPoint(new Vec3(1,0,50), Color.RED.getRGB());
		ColorPoint green = new ColorPoint(new Vec3(.5,.4,50), Color.GREEN.getRGB());
		p.addPoint(red);
		p.addPoint(green);
		final ArrayList<ColorPoint> l = new ArrayList<ColorPoint>();
		for(double i=-1; i<=1; i+=.2){
			l.add(new ColorPoint(-1,-1,i,Color.BLUE.getRGB()));
			l.add(new ColorPoint(1,-1,i,Color.BLUE.getRGB()));
			l.add(new ColorPoint(-1,1,i,Color.BLUE.getRGB()));
			l.add(new ColorPoint(1,1,i,Color.BLUE.getRGB()));
			
			l.add(new ColorPoint(-1,i,-1,Color.RED.getRGB()));
			l.add(new ColorPoint(1,i,-1,Color.RED.getRGB()));
			l.add(new ColorPoint(-1,i,1,Color.RED.getRGB()));
			l.add(new ColorPoint(1,i,1,Color.RED.getRGB()));

			l.add(new ColorPoint(i,-1,-1,Color.GREEN.getRGB()));
			l.add(new ColorPoint(i,1,-1,Color.GREEN.getRGB()));
			l.add(new ColorPoint(i,-1,1,Color.GREEN.getRGB()));
			l.add(new ColorPoint(i,1,1,Color.GREEN.getRGB()));
		}
		for(ColorPoint cp: l){
			cp.translate(new Vec3(0,0,5));
		}
		p.setPts(l);
		final RenderPanel panel = new RenderPanel(500, 500, p);
		jf.add(panel);
		jf.setVisible(true);
		final double theta1 = Math.random()*Math.PI/4-Math.PI/8;
		final double theta2 = Math.random()*Math.PI/4-Math.PI/8;
		Timer t = new Timer(100,new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				for(ColorPoint cp: l){
					cp.translate(new Vec3(0,0,-5));
					cp.position = cp.position.rotateX(theta1).rotateY(theta2);
					cp.normPos();
					cp.translate(new Vec3(0,0,5));
				}
				p.nextFrame();
				//System.out.println(theta1);
				panel.repaint();
			}
			
		});
		
		t.setRepeats(true);
		t.start();
		
	}*/

	private static ConcaveSurface createConcave(String filename) throws FileNotFoundException{
		Scanner sc = new Scanner(new FileReader(filename));
		sc.useDelimiter("normal");
		List<Double> num;
		//Map<Vec3, ArrayList<ColorPlane>> m = new HashMap<Vec3, ArrayList<ColorPlane>>();
		ArrayList<ColorPlane> pls = new ArrayList<ColorPlane>();
		ArrayList<Vec3[]> vert = new ArrayList<Vec3[]>();
		ArrayList<List<Integer>> adj = new ArrayList<List<Integer>>();
		//ArrayList<Vec3> normalsUsed = new ArrayList<Vec3>();
		while(sc.hasNext()){
			num = new ArrayList<Double>(12);
			String facet = sc.next();
			String[] bits = facet.split("[^0123456789.-]");
			for(String bit: bits){
				try{
					num.add(Double.parseDouble(bit.trim()));
				} catch(Exception e){
					
				}
			}
			if(num.size()>1){
				Vec3 norm = new Vec3(num.get(0), num.get(1), num.get(2)).normalize();
				Vec3[] pos = new Vec3[]{new Vec3(num.get(3),num.get(4),num.get(5)),
						new Vec3(num.get(6),num.get(7),num.get(8)),
						new Vec3(num.get(9),num.get(10),num.get(11))};
				//System.out.println("success");
				Vec3 center = pos[0].add(pos[1]).add(pos[2]).scale(1.0/3);
				ColorPlane here = new ColorPlane(center, 
						norm.normalize(),//.add(new Vec3(Math.random()*.0002-.0001,Math.random()*.0002-.0001,Math.random()*.0002-.0001)),
						Color.ORANGE.getRGB()/*(int)(Math.random()*25565*256)*/);
//				boolean dontDoThis = false;
//				for(int i=0; i<vert.size(); i++){
//					if(pls.get(i).normal1.equals(here.normal1)){
//						Vec3[] newVert = new Vec3[vert.get(i).length+3];
//						System.arraycopy(vert.get(i), 0, newVert, 0, vert.get(i).length);
//						System.arraycopy(pos, 0, newVert, vert.get(i).length, 3);
//						vert.set(i, newVert);
//						dontDoThis = true;
//						pls.get(i).position = pls.get(i).position.add(center).scale(1.0/2);
//						System.out.println(i+" "+newVert.length);
//						break;
//					}
//				}
				pls.add(here);
				vert.add(pos);
				adj.add(new ArrayList<Integer>());
//				for(int i=0; i<pls.size()-1; i++){
//					for(int j=0; j<vert.get(i).length; j++){
//						for(int k=0; k<pos.length; k++){
//							if(vert.get(i)[j].equals(pos[k]) 
//									&& !adj.get(i).contains(adj.size()-1) 
//									&& !pls.get(i).normal1.equals(here.normal1)){
//								adj.get(i).add(adj.size()-1);
//								adj.get(adj.size()-1).add(i);
//							}
//						}
//					}
//				}
				//double asl = (pos[0].sub(pos[1]).mag()+pos[1].sub(pos[2]).mag()+pos[2].sub(pos[0]).mag())/3;
				/*for(int i=0; i<pos.length; i++){
					ArrayList<Vec3> list = m.get(pos[i]);
					if(list == null){
						list = new ArrayList<Vec3>();
						m.put(pos[i], list);
					}
					list.add(norm.normalize());
				}*/
//				if(m.get(norm)!=null){
//					m.get(norm).setValue2(m.get(norm).getValue2()+pos[0].sub(pos[1]).cross(pos[1].sub(pos[2])).mag());
//				} else if(m.get(norm)==null) {
//					ColorPlane here = new ColorPlane(center, norm, Color.ORANGE.getRGB());
//					here.setValue2(pos[0].sub(pos[1]).cross(pos[1].sub(pos[2])).mag());
//					m.put(norm, here);
//				}
//				surface.add(new ColorPlane(pos[1], norm, Color.ORANGE.getRGB()));
//				ColorPlane here = new ColorPlane(center, norm.normalize(), Color.ORANGE.getRGB());
//				if(norm.normalize().equals(new Vec3(0,1,0))) here.setValue2(10000000);
//				surface.add(here);
//				normalsUsed.add(norm.normalize());
			}
		}
		for(int i=0; i<pls.size(); i++){
			for(int j=0; j<pls.size(); j++){
				if(j!=i){
					int shared = 0;
					for(int v=0; v<vert.get(i).length; v++){
						for(int k=0; k<vert.get(j).length; k++){
							if(vert.get(i)[v].equals(vert.get(j)[k]) && !adj.get(i).contains(j)){
								/*if(pls.get(i).normal1.equals(pls.get(j).normal1)){
									adj.get(j).add(i);
									adj.get(i).add(j);
									System.out.println("normal same");
									continue;
								}*/
								shared++;
//								adj.get(j).add(i);
//								adj.get(i).add(j);
//								boolean dPos = pls.get(j).normal1.dot(pls.get(j).position.sub(vert.get(i)[0]))/pls.get(j).normal1.dot(pls.get(i).normal1)>=0;
//								boolean chopt = false;
//								for(int i1=0; i1<vert.get(i).length; i1++){
//									if(i1!=v && !vert.get(i)[i1].equals(vert.get(j)[k])){
//										Vec3 vi = vert.get(i)[i1];
//										if(pls.get(j).normal1.dot(pls.get(j).position.sub(vi))/pls.get(j).normal1.dot(pls.get(i).normal1)>=0
//												^ dPos){
//											chopt = true; break;
//										}
//									}
//								}
//								if(!chopt) adj.get(i).add(j);
//								dPos = pls.get(i).normal1.dot(pls.get(i).position.sub(vert.get(j)[0]))/pls.get(i).normal1.dot(pls.get(j).normal1)>=0;
//								chopt = false;
//								for(int i1=0; i1<vert.get(j).length; i1++){
//									if(i1!=k && !vert.get(i)[v].equals(vert.get(j)[i1])){
//										Vec3 vi = vert.get(j)[i1];
//										if(pls.get(i).normal1.dot(pls.get(i).position.sub(vi))/pls.get(i).normal1.dot(pls.get(j).normal1)>=0
//												^ dPos){
//											chopt = true; break;
//										}
//									}
//								}
//								if(!chopt) adj.get(j).add(i);

							}
						}
					}
					if(shared>=2){
						adj.get(j).add(i);
						adj.get(i).add(j);
					}
				}
			}
		}
		
		System.out.println("adj1");
//		Vec3 dir = new Vec3(Math.random(), Math.random(), Math.random());
//		Vec3 pt = new Vec3(0,0,0);
		for(int i=0; i<vert.size(); i++){
			for(int j=0; j<pls.size(); j++){
				if(j!=i){ 
					if(pls.get(i).normal1.equals(pls.get(j).normal1)){//&&vert.get(i).length<=3 && vert.get(j).length<=3){  //|| pls.get(i).normal1.equals(pls.get(j).normal1.scale(-1))
//							|| Math.abs(pls.get(i).normal1.dot(pls.get(i).position.sub(pt))/pls.get(i).normal1.dot(dir)
//							-pls.get(j).normal1.dot(pls.get(j).position.sub(pt))/pls.get(j).normal1.dot(dir))<=.0000001){//|| pls.get(i).normal1.dist2(pls.get(j).normal1)<.00001){//){//&&vert.get(i).length<=3 && vert.get(j).length<=3){
						if(Math.random()>0) System.out.println("combined "+i+" "+j);
						//JOptionPane.showMessageDialog(null, "combined "+i+" "+j);
						Vec3[] newVert = new Vec3[vert.get(i).length+vert.get(j).length];
						System.arraycopy(vert.get(i), 0, newVert, 0, vert.get(i).length);
						System.arraycopy(vert.get(j), 0, newVert, vert.get(i).length, vert.get(j).length);
						vert.set(i, newVert);
						List<Integer> newAdj = new ArrayList<Integer>();
						newAdj.addAll(adj.get(i));
						newAdj.addAll(adj.get(j));
						adj.set(i, newAdj);
						pls.get(i).position = pls.get(i).position.add(pls.get(j).position).scale(1.0/2);
						System.out.println(i+" "+newVert.length);
						for(List<Integer> l: adj){
							for(int k=0; k<l.size(); k++){
								if(l.get(k)>=j){
									l.set(k, l.get(k)-1);
								}
							}
						}
						vert.remove(j);
						pls.remove(j);
						adj.remove(j);
						j--;
					}
				}
			}
		}
		for(List<Integer> l: adj){
			for(int k=0; k<l.size(); k++){
				if(l.indexOf(l.get(k))!=k || l.lastIndexOf(l.get(k))!=k){
					l.remove(k);
					k--;
				}
			}
		}
		System.out.println("cullAdj1");
//		ArrayList<List<Integer>> newAdj = new ArrayList<List<Integer>>();
//		for(int i=0; i<adj.size(); i++){
//			newAdj.add(i, new ArrayList<Integer>());
//			newAdj.get(i).addAll(adj.get(i));
//			for(int j: adj.get(i)){
//				newAdj.get(i).addAll(adj.get(j));
//			}
//			System.out.print(i);
//		}
//		adj = newAdj;
		for(int i=0; i<adj.size(); i++){
			List<Integer> l = adj.get(i);
			for(int k=0; k<l.size(); k++){
				if(l.indexOf(l.get(k))!=k || l.lastIndexOf(l.get(k))!=k || l.get(k)==i){
					l.remove(k);
					k--;
				}
			}
		}
		System.out.println("cullAdj2");
//		for(Map.Entry<Vec3, ColorPlane> entry: m.entrySet()){
//			entry.getValue().normal = entry.getValue().normal1.scale(entry.getValue().getValue2());
//			entry.getValue().normNorm();
//			surface.add(entry.getValue());
//		}
//		System.out.println(m);
		/*for(Map.Entry<Vec3, ArrayList<Vec3>> entry: m.entrySet()){
			Vec3 norm = new Vec3(0,0,0);
			for(Vec3 n: entry.getValue()){
				norm = norm.add(n);
			}
			norm = norm.normalize();
			surface.add(new ColorPlane(entry.getKey(), norm, Color.ORANGE.getRGB()));
		}*/
		//System.out.println(m.size());
		//System.out.println(adj);
		//System.out.println(pls);
//		try {
//			STLCreator cr = new STLCreator(new File("utah2.stl"), "utah2");
//			for(Vec3[] arr: vert){
//				cr.addFacet(new Facet(convertVec3(arr[0]),convertVec3(arr[1]),convertVec3(arr[2])));
//			}
//			cr.finish();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		for(ColorPlane p: pls){
			System.out.print(p.normal1+"    ");
		}
		sc.close();
		double r2 = 0;
		for(ColorPlane cp: pls){
			if(cp.position.mag2()>r2){
				r2 = cp.position.mag2();
			}
		}
		
		return new ConcaveSurface(new Vec3(0,0,0),r2,pls, adj);
	}
}
//boolean dPos = pls.get(j).normal1.dot(pls.get(j).position.sub(vert.get(i)[0]))/pls.get(j).normal1.dot(pls.get(i).normal1)>=0;
//boolean chopt = false;
//for(int i1=0; i1<vert.get(i).length; i1++){
//	if(i1!=v && !vert.get(i)[i1].equals(vert.get(j)[k])){
//		Vec3 vi = vert.get(i)[i1];
//		if(pls.get(j).normal1.dot(pls.get(j).position.sub(vi))/pls.get(j).normal1.dot(pls.get(i).normal1)>=0
//				^ dPos){
//			chopt = true; break;
//		}
//	}
//}
//if(!chopt) adj.get(i).add(j);
//dPos = pls.get(i).normal1.dot(pls.get(i).position.sub(vert.get(j)[0]))/pls.get(i).normal1.dot(pls.get(j).normal1)>=0;
//chopt = false;
//for(int i1=0; i1<vert.get(j).length; i1++){
//	if(i1!=k && !vert.get(i)[v].equals(vert.get(j)[i1])){
//		Vec3 vi = vert.get(j)[i1];
//		if(pls.get(i).normal1.dot(pls.get(i).position.sub(vi))/pls.get(i).normal1.dot(pls.get(j).normal1)>=0
//				^ dPos){
//			chopt = true; break;
//		}
//	}
//}
//if(!chopt) adj.get(j).add(i);
