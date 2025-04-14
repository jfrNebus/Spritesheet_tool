package com.SpriteSheeter.Enums;

public enum LayoutAxisEnum {
    X_AXIS(0), Y_AXIS(1);

    private final int axis;

    LayoutAxisEnum(int axis) {
        this.axis = axis;
    }

    public int getAxis(){
        return axis;
    }
}
