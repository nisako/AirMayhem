package com.example.araprojenew;

public enum powerupType{
	DOUBLESHOT,
	TRIPLESHOT,
	HEALTUP,
	INVUL;
	public static powerupType getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
}
