package by.ts.hmxy.util;

import java.awt.Color;

public class HmxyColor extends Color{
	
	private static final long serialVersionUID = 9069259416049951312L;
	public HmxyColor(int rgb) {
		super(rgb);
	}
	
	/**
	 * 
	 * @param rgb 颜色
	 * @param a 颜色的透明度
	 */
	public HmxyColor(int rgb,int a) {
		this(rgb|((a & 0xFF) << 24),true);
	}
	
	public HmxyColor(int rgba,boolean hasA) {
		super(rgba, hasA);
	}
	
	public float getR() {
		return this.getRed()/255f;
	}
	public float getG() {
		return this.getGreen()/255f;
	}
	public float getB() {
		return this.getBlue()/255f;
	}
	public float getA() {
		return this.getAlpha()/255f;
	}
}
