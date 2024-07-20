package com.nixinova.coords;

public interface ICoord<X extends Number> {
	
	public Coord3 toCoord();

	public X value();

}
