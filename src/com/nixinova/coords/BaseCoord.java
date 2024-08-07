package com.nixinova.coords;

import com.nixinova.Vector3;

public abstract class BaseCoord<X extends Number> {

	public X x, y, z;

	public abstract Coord3 toCoord();

	public abstract void add(Vector3<X> vec);

	public abstract BaseCoord<X> applyVector(Vector3<X> vec);

}
