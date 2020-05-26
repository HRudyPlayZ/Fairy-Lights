package me.paulf.fairylights.server.fastener.connection.type.letter;

import me.paulf.fairylights.server.fastener.connection.type.*;
import me.paulf.fairylights.util.styledstring.*;
import net.minecraft.util.math.*;

public final class Letter extends HangingFeature<Letter> {
    private final SymbolSet symbols;

    private final char letter;

    private final Style style;

    public Letter(final int index, final Vec3d point, final float yaw, final float pitch, final SymbolSet symbols, final char letter, final Style style) {
        super(index, point, yaw, pitch, 0.0F);
        this.symbols = symbols;
        this.letter = letter;
        this.style = style;
    }

    public char getLetter() {
        return this.letter;
    }

    public Style getStyle() {
        return this.style;
    }

    @Override
    public double getWidth() {
        return this.symbols.getWidth(this.letter) / 16F;
    }

    @Override
    public double getHeight() {
        return this.symbols.getHeight() / 16F;
    }

    @Override
    public boolean parallelsCord() {
        return true;
    }

    public void tick() {
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
        this.prevRoll = this.roll;
    }
}
