package com.lw.random_additions.common.block.thaumcraft;

import com.lw.random_additions.Tags;
import com.lw.random_additions.common.tile.thaumcraft.TileInfusionInterceptor;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;

import javax.annotation.Nonnull;

@Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliserExt", modid = "thaumcraft")
public class BlockInfusionInterceptor extends BlockContainer implements IInfusionStabiliserExt {

    public BlockInfusionInterceptor() {
        super(Material.ROCK);
        this.setRegistryName(Tags.MOD_ID, "infusion_interceptor");
        this.setTranslationKey(Tags.MOD_ID + ".infusion_interceptor");
        this.setCreativeTab(CreativeTabs.MISC);
        this.setHardness(1.0F);
        this.setResistance(1.5F);
        this.setHarvestLevel("pickaxe", 0);
    }

    @Override
    public TileEntity createNewTileEntity(final World world, final int metadata) {
        return new TileInfusionInterceptor();
    }

    @Override
    public EnumBlockRenderType getRenderType(final IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean canStabaliseInfusion(final World world, final BlockPos pos) {
        return true;
    }

    @Override
    public float getStabilizationAmount(final World world, final BlockPos pos) {
        return 20.0F;
    }

    @Override
    public boolean hasSymmetryPenalty(final World world, final BlockPos pos, final BlockPos otherPos) {
        return false;
    }

    @Override
    public void onBlockAdded(@Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final IBlockState state) {
        super.onBlockAdded(world, pos, state);
        notifyMatrix(world, pos);
    }

    @Override
    public void breakBlock(@Nonnull final World world, @Nonnull final BlockPos pos, @Nonnull final IBlockState state) {
        notifyMatrix(world, pos);
        super.breakBlock(world, pos, state);
    }

    private static void notifyMatrix(final World world, final BlockPos pos) {
        if (world.isRemote) {
            return;
        }
        final TileEntity tile = world.getTileEntity(pos.up(3));
        if (tile instanceof TileInfusionMatrix) {
            ((TileInfusionMatrix) tile).checkSurroundings = true;
            tile.markDirty();
        }
    }
}
