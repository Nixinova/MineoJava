package com.nixinova.coords;

public interface ICoord<X extends Number> {
	
	public Coord toCoord();

	public PxCoord toPxCoord();

	public TxCoord toTxCoord();

	public BlockCoord toBlockCoord();

	public SubBlockCoord toSubBlockCoord();

	public X value();

}
