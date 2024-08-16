package com.nixinova.coords;

public abstract class BaseCoord<X extends Number> {

	public X x, y, z;

	public abstract Coord3 toCoord();

}
