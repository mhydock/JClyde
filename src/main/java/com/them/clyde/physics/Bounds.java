package com.them.clyde.physics;

public interface Bounds {

	public Bounds applyScale(Scale scale);
	
	public Bounds applyOffset(Position position);

	public Bounds applyRotate(Orientation orientation);

	public boolean isOverlapping(Bounds other);

}
