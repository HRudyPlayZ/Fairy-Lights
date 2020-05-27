package me.paulf.fairylights.server.fastener.connection.type;

import com.google.common.base.MoreObjects;
import me.paulf.fairylights.server.fastener.Fastener;
import me.paulf.fairylights.server.fastener.connection.Catenary;
import me.paulf.fairylights.server.fastener.connection.FeatureType;
import me.paulf.fairylights.server.fastener.connection.collision.CollidableList;
import me.paulf.fairylights.server.fastener.connection.collision.FeatureCollisionTree;
import me.paulf.fairylights.util.AABBBuilder;
import me.paulf.fairylights.util.matrix.MatrixStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class HangingFeatureConnection<F extends HangingFeature<F>> extends Connection {
    protected static final FeatureType FEATURE = FeatureType.create("feature");

    protected F[] features = this.createFeatures(0);

    @Nullable
    protected F[] prevFeatures;

    public HangingFeatureConnection(final World world, final Fastener<?> fastener, final UUID uuid, final Fastener<?> destination, final boolean isOrigin, final CompoundNBT compound) {
        super(world, fastener, uuid, destination, isOrigin, compound);
    }

    public HangingFeatureConnection(final World world, final Fastener<?> fastener, final UUID uuid) {
        super(world, fastener, uuid);
    }

    public final F[] getFeatures() {
        return this.features;
    }

    public final F[] getPrevFeatures() {
        return MoreObjects.firstNonNull(this.prevFeatures, this.features);
    }

    @Override
    protected void updatePrev() {
        this.prevFeatures = this.features;
    }

    @Override
    protected void onCalculateCatenary(final boolean relocated) {
        this.updateFeatures(relocated);
    }

    protected void updateFeatures(final boolean relocated) {
        final Catenary catenary = this.getCatenary();
        final float spacing = this.getFeatureSpacing();
        final float totalLength = catenary.getLength();
        if (totalLength > 2.0F * Connection.MAX_LENGTH) {
            this.prevFeatures = this.features;
            this.onBeforeUpdateFeatures();
            this.features = this.createFeatures(0);
            this.onAfterUpdateFeatures();
            return;
        }
        this.prevFeatures = this.features;
        final List<F> features = new ArrayList<>();
        this.onBeforeUpdateFeatures();
        catenary.visitPoints(spacing, true, (index, x, y, z, yaw, pitch) -> {
            final F feature = this.createFeature(index, new Vec3d(x, y, z), yaw, pitch);
            if (!relocated && this.prevFeatures != null && index < this.prevFeatures.length) {
                feature.inherit(this.prevFeatures[index]);
            }
            features.add(feature);
        });
        this.features = features.toArray(this.createFeatures(features.size()));
        this.onAfterUpdateFeatures();
    }

    protected abstract F[] createFeatures(int length);

    protected abstract F createFeature(int index, Vec3d point, float yaw, final float pitch);

    protected abstract float getFeatureSpacing();

    protected void onBeforeUpdateFeatures() {}

    protected void onAfterUpdateFeatures() {}

    @Override
    public void addCollision(final CollidableList.Builder collision, final Vec3d origin) {
        super.addCollision(collision, origin);
        if (this.features.length > 0) {
            final MatrixStack matrix = new MatrixStack();
            collision.add(FeatureCollisionTree.build(FEATURE, this.features, f -> {
                final Vec3d pos = f.getPoint();
                final double x = origin.x + pos.x;
                final double y = origin.y + pos.y;
                final double z = origin.z + pos.z;
                final double w = f.getWidth() / 2;
                final double h = f.getHeight();
                matrix.push();
                if (f.parallelsCord()) {
                    matrix.rotate(f.getYaw(), 0, 1, 0);
                    matrix.rotate(f.getPitch(), 1, 0, 0);
                }
                matrix.translate(0, 0.025F, 0);
                final AABBBuilder bounds = new AABBBuilder();
                final Vec3d[] verts = {
                    new Vec3d(-w, -h, -w),
                    new Vec3d(w, -h, -w),
                    new Vec3d(w, -h, w),
                    new Vec3d(-w, -h, w),
                    new Vec3d(-w, 0, -w),
                    new Vec3d(w, 0, -w),
                    new Vec3d(w, 0, w),
                    new Vec3d(-w, 0, w)
                };
                for (final Vec3d vert : verts) {
                    bounds.include(matrix.transform(vert));
                }
                matrix.pop();
                return bounds.add(x, y, z).build();
            }));
        }
    }

}
