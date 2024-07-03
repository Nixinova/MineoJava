package com.nixinova.coords;

public interface ICoord<X extends Number> {

	public PxCoord toPxCoord();

	public TxCoord toTxCoord();

	public BlockCoord toBlockCoord();

	public SubBlockCoord toSubBlockCoord();

	public X value();

}
