package com.lw.random_additions.common.tile.thaumcraft;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;

public class TileInfusionInterceptor extends TileEntity implements ITickable, IEssentiaTransport, IAspectContainer {

    private Aspect suction;

    @Override
    public void update() {
        if (this.world == null || this.world.isRemote) {
            return;
        }

        final TileEntity matrixTile = this.world.getTileEntity(this.pos.up(3));
        final TileEntity sourceTile = this.world.getTileEntity(this.pos.down());
        if (!(matrixTile instanceof TileInfusionMatrix)) {
            this.suction = null;
            return;
        }

        final TileInfusionMatrix matrix = (TileInfusionMatrix) matrixTile;
        if (matrix.getAspects() == null) {
            this.suction = null;
            return;
        }
        final Aspect[] requiredAspects = matrix.getAspects().getAspects();
        if (requiredAspects == null || requiredAspects.length == 0) {
            this.suction = null;
            return;
        }

        if (sourceTile instanceof IAspectContainer) {
            this.transferFromAspectContainer(matrix, (IAspectContainer) sourceTile, requiredAspects);
        }
        if (!(sourceTile instanceof IEssentiaTransport)) {
            this.suction = null;
            return;
        }

        final IEssentiaTransport source = (IEssentiaTransport) sourceTile;

        for (final Aspect aspect : requiredAspects) {
            final int required = matrix.getAspects().getAmount(aspect);
            if (required <= 0) {
                continue;
            }
            this.suction = aspect;
            if (!source.canOutputTo(EnumFacing.UP)
                    || !aspect.equals(source.getEssentiaType(EnumFacing.UP))
                    || source.getEssentiaAmount(EnumFacing.UP) <= 0) {
                continue;
            }

            final int requested = Math.min(required, source.getEssentiaAmount(EnumFacing.UP));
            final int taken = source.takeEssentia(aspect, requested, EnumFacing.UP);
            if (taken > 0) {
                this.consumeMatrixEssentia(matrix, aspect, taken);
            }
        }
        this.suction = null;
    }

    private void transferFromAspectContainer(final TileInfusionMatrix matrix, final IAspectContainer source,
                                             final Aspect[] requiredAspects) {
        for (final Aspect aspect : requiredAspects) {
            final int required = matrix.getAspects().getAmount(aspect);
            final int available = source.containerContains(aspect);
            final int requested = Math.min(required, available);
            if (requested <= 0 || !source.takeFromContainer(aspect, requested)) {
                continue;
            }
            if (source instanceof TileEntity) {
                ((TileEntity) source).markDirty();
            }
            this.consumeMatrixEssentia(matrix, aspect, requested);
        }
    }

    private void consumeMatrixEssentia(final TileInfusionMatrix matrix, final Aspect aspect, final int amount) {
        matrix.getAspects().remove(aspect, amount);
        matrix.markDirty();
        matrix.syncTile(false);
    }

    @Override
    public boolean isConnectable(final EnumFacing face) {
        return face == EnumFacing.DOWN;
    }

    @Override
    public boolean canInputFrom(final EnumFacing face) {
        return face == EnumFacing.DOWN;
    }

    @Override
    public boolean canOutputTo(final EnumFacing face) {
        return false;
    }

    @Override
    public void setSuction(final Aspect aspect, final int amount) {
        this.suction = aspect;
    }

    @Override
    public Aspect getSuctionType(final EnumFacing face) {
        return face == EnumFacing.DOWN ? this.suction : null;
    }

    @Override
    public int getSuctionAmount(final EnumFacing face) {
        return face == EnumFacing.DOWN && this.suction != null ? 128 : 0;
    }

    @Override
    public int takeEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return 0;
    }

    @Override
    public int addEssentia(final Aspect aspect, final int amount, final EnumFacing face) {
        return 0;
    }

    @Override
    public Aspect getEssentiaType(final EnumFacing face) {
        return null;
    }

    @Override
    public int getEssentiaAmount(final EnumFacing face) {
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public AspectList getAspects() {
        return new AspectList();
    }

    @Override
    public void setAspects(final AspectList aspects) {
    }

    @Override
    public boolean doesContainerAccept(final Aspect aspect) {
        final TileInfusionMatrix matrix = this.getMatrix();
        return matrix != null && aspect != null && matrix.getAspects().getAmount(aspect) > 0;
    }

    @Override
    public int addToContainer(final Aspect aspect, final int amount) {
        if (aspect == null || amount <= 0) {
            return amount;
        }
        final TileInfusionMatrix matrix = this.getMatrix();
        if (matrix == null) {
            return amount;
        }

        final int accepted = Math.min(amount, matrix.getAspects().getAmount(aspect));
        if (accepted > 0) {
            this.consumeMatrixEssentia(matrix, aspect, accepted);
            return amount - accepted;
        }
        return amount;
    }

    @Override
    public boolean takeFromContainer(final Aspect aspect, final int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(final AspectList aspects) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(final Aspect aspect, final int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(final AspectList aspects) {
        return false;
    }

    @Override
    public int containerContains(final Aspect aspect) {
        return 0;
    }

    private TileInfusionMatrix getMatrix() {
        if (this.world == null) {
            return null;
        }
        final TileEntity tile = this.world.getTileEntity(this.pos.up(3));
        return tile instanceof TileInfusionMatrix ? (TileInfusionMatrix) tile : null;
    }
}
