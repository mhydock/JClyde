package com.them.clyde.physics.impl;

import com.them.clyde.physics.Bounds;
import com.them.clyde.physics.Entity;
import com.them.clyde.physics.Orientation;
import com.them.clyde.physics.Position;
import com.them.clyde.physics.Scale;
import com.them.clyde.physics.Velocity;

public class DynamicEntity implements Entity {

	private boolean active;
	private Bounds bounds;

	private Scale scale;
	private Velocity velocity;
	private Position position;
	private Orientation orientation;

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public Bounds getBounds() {
		return bounds;
	}

	@Override
	public void setBounds(Bounds bounds) {
		this.bounds = bounds;
	}
	
	@Override
	public Scale getScale() {
		return scale;
	}
	
	@Override
	public void setScale(Scale scale) {
		this.scale = scale;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void setPosition(Position position) {
		this.position = position;
	}
	
	@Override
	public Orientation getOrientation() {
		return orientation;
	}
	
	@Override
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
	}

	public Velocity getVelocity() {
		return velocity;
	}
	
	public void setVelocity(Velocity velocity) {
		this.velocity = velocity;
	}
	
	public void step() {
		setPosition(getPosition().applyVelocity(velocity));
	}
}
