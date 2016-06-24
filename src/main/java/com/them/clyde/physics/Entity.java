package com.them.clyde.physics;

public interface Entity {
	
	public boolean isActive();
	public void setActive(boolean active);
	
	public Bounds getBounds();
	public void setBounds(Bounds bounds);
	
	public Scale getScale();
	public void setScale(Scale scale);
	
	public Position getPosition();
	public void setPosition(Position position);
	
	public Orientation getOrientation();
	public void setOrientation(Orientation orientation);
	
	public default boolean isColliding(Entity entity) {
		Bounds other = entity.getBounds();
		other = other.applyScale(entity.getScale());
		other = other.applyOffset(entity.getPosition());
		other = other.applyRotate(entity.getOrientation());
		
		Bounds these = getBounds();
		these = these.applyScale(getScale());
		these = these.applyOffset(getPosition());
		these = these.applyRotate(getOrientation());
		
		return these.isOverlapping(other);
	}
	public void step();
}
