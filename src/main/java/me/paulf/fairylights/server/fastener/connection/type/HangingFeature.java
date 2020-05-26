package me.paulf.fairylights.server.fastener.connection.type;

import me.paulf.fairylights.server.fastener.*;
import me.paulf.fairylights.server.fastener.connection.*;
import me.paulf.fairylights.util.*;
import net.minecraft.util.math.*;

public abstract class HangingFeature<F extends HangingFeature<F>> implements Feature {
    protected final int index;

    protected final Vec3d point;

    protected float yaw, pitch, roll;

    protected float prevYaw, prevPitch, prevRoll;

    public HangingFeature(final int index, final Vec3d point, final float yaw, final float pitch, final float roll) {
        this.index = index;
        this.point = point;
        this.prevYaw = this.yaw = yaw;
        this.prevPitch = this.pitch = pitch;
        this.prevRoll = this.roll = roll;
    }

    @Override
    public final int getId() {
        return this.index;
    }

    public final Vec3d getPoint() {
        return this.point;
    }

    public final float getYaw() {
        return this.yaw;
    }

    public final float getPitch() {
        return this.pitch;
    }

    public final float getRoll() {
        return this.roll;
    }

    public final float getYaw(final float t) {
        return (float) Mth.lerpAngle(this.prevYaw, this.yaw, t);
    }

    public final float getPitch(final float t) {
        return (float) Mth.lerpAngle(this.prevPitch, this.pitch, t);
    }

    public final float getRoll(final float t) {
        return Mth.lerpAngle(this.prevRoll, this.roll, t);
    }

    public final Vec3d getAbsolutePoint(final Fastener<?> fastener) {
        return this.point.add(fastener.getConnectionPoint());
    }

    public void inherit(final F feature) {
        this.prevYaw = feature.yaw;
        this.prevPitch = feature.pitch;
        this.prevRoll = feature.roll;
    }

    public abstract double getWidth();

    public abstract double getHeight();

    public abstract boolean parallelsCord();
}
