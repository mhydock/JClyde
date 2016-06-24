package com.them.clyde.physics;

public interface Position {

	public Position applyOffset(Position offset);
	
	public Position applyVelocity(Velocity velocity);

}
