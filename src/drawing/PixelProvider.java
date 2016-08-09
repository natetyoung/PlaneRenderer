package drawing;

public interface PixelProvider {
	public int getRGB(double x, double y);
	public void nextFrame();
}
