package com.example.araprojenew;

public enum powerupType{
	DOUBLESHOT,
	TRIPLESHOT,
	HEALTUP,
	INVUL;
	public static powerupType getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
	
	public static powerupType toPowerupType(int a){
		switch(a){
			case 0:return powerupType.DOUBLESHOT;
			case 1:return powerupType.TRIPLESHOT;
			case 2:return powerupType.HEALTUP;
			case 3:return powerupType.INVUL;
		}
		return powerupType.HEALTUP;
	}
}
