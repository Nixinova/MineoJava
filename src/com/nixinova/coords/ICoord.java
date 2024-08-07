package com.nixinova.coords;

import com.nixinova.Vector3;

public interface ICoord<X extends Number> {

	public Coord3 toCoord();

	public X value();

	public void add(Vector3<X> vec);

	public ICoord<X> applyVector(Vector3<X> vec);

}
