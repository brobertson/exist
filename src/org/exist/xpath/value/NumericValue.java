package org.exist.xpath.value;

import org.exist.xpath.Constants;
import org.exist.xpath.XPathException;

public abstract class NumericValue extends ComputableValue {

	public abstract String getStringValue() throws XPathException;

	public abstract AtomicValue convertTo(int requiredType) throws XPathException;
	
	public double getDouble() throws XPathException {
		return ((DoubleValue)convertTo(Type.DECIMAL)).getValue();
	}
	
	public long getLong() throws XPathException {
		return ((IntegerValue)convertTo(Type.INTEGER)).getValue();
	}
	
	public int getInt() throws XPathException {
		return (int)((IntegerValue)convertTo(Type.INTEGER)).getValue();
	}
	
	public boolean isNaN() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.exist.xpath.value.AtomicValue#compareTo(int, org.exist.xpath.value.AtomicValue)
	 */
	public boolean compareTo(int operator, AtomicValue other)
		throws XPathException {
		if(Type.subTypeOf(other.getType(), Type.NUMBER)) {
			double otherVal = ((NumericValue)other).getDouble();
			double val = getDouble();
			switch(operator) {
				case Constants.EQ:
					return val == otherVal;
				case Constants.NEQ:
					return val != otherVal;
				case Constants.GT:
					return val > otherVal;
				case Constants.GTEQ:
					return val >= otherVal;
				case Constants.LT:
					return val < otherVal;
				case Constants.LTEQ:
					return val <= otherVal;
				default:
					throw new XPathException("Type error: cannot apply operator to numeric value");
			}
		}
		throw new XPathException("Type error: cannot compare operands: " + 
			Type.getTypeName(getType()) + " and " + 
			Type.getTypeName(other.getType()));
	}
	
	/* (non-Javadoc)
	 * @see org.exist.xpath.value.AtomicValue#compareTo(org.exist.xpath.value.AtomicValue)
	 */
	public int compareTo(AtomicValue other) throws XPathException {
		if(Type.subTypeOf(other.getType(), Type.NUMBER)) {
			double otherVal = ((NumericValue)other).getDouble();
			double val = getDouble();
			if(val == otherVal)
				return 0;
			else if(val > otherVal)
				return 1;
			else
				return -1;
		} else {
			throw new XPathException("cannot compare numeric value to non-numeric value");
		}
	}
	
	public abstract NumericValue negate() throws XPathException;
	public abstract NumericValue ceiling() throws XPathException;
	public abstract NumericValue floor() throws XPathException;
	public abstract NumericValue round() throws XPathException;
	public abstract NumericValue mod(NumericValue other) throws XPathException;
	public abstract NumericValue abs() throws XPathException;
	public abstract AtomicValue max(AtomicValue other) throws XPathException;
	public abstract AtomicValue min(AtomicValue other) throws XPathException;
}
