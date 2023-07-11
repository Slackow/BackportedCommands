package com.slackow.backport.command;


import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.command.AbstractCommand;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.IncorrectUsageException;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.slackow.backport.command.FillCommand.getBlockPos;

public class CloneCommand extends AbstractCommand {
    public CloneCommand() {
    }

    public String getCommandName() {
        return "clone";
    }

    public int getPermissionLevel() {
        return 2;
    }

    public String getUsageTranslationKey(CommandSource source) {
        return "commands.clone.usage";
    }

    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length < 9) {
            throw new IncorrectUsageException("commands.clone.usage");
        } else {
            BlockPos blockPos = getBlockPos(source, args, 0);
            BlockPos blockPos2 = getBlockPos(source, args, 3);
            BlockPos blockPos3 = getBlockPos(source, args, 6);
            BlockBox blockBox = new BlockBox(blockPos.x, blockPos.y, blockPos.z, blockPos2.x, blockPos2.y, blockPos2.z);
            BlockPos edge = blockPos3.add(blockBox.getBlockCountX(), blockBox.getBlockCountY(), blockBox.getBlockCountZ());
            BlockBox blockBox2 = new BlockBox(blockPos3.x, blockPos3.y, blockPos3.z, edge.x, edge.y, edge.z);
            int i = blockBox.getBlockCountX() * blockBox.getBlockCountY() * blockBox.getBlockCountZ();
            if (i > 32768) {
                throw new CommandException("commands.clone.tooManyBlocks", i, 32768);
            } else {
                boolean bl = false;
                Block block = null;
                int j = -1;
                if ((args.length < 11 || !args[10].equals("force") && !args[10].equals("move")) && blockBox.intersects(blockBox2)) {
                    throw new CommandException("commands.clone.noOverlap");
                } else {
                    if (args.length >= 11 && args[10].equals("move")) {
                        bl = true;
                    }

                    if (blockBox.minY >= 0 && blockBox.maxY < 256 && blockBox2.minY >= 0 && blockBox2.maxY < 256) {
                        World world = source.getWorld();
                        if (world.isRegionLoaded(blockBox.minX, blockBox.minY, blockBox.minZ, blockBox.maxX, blockBox.maxY, blockBox.maxZ) && world.isRegionLoaded(blockBox2.minX, blockBox2.minY, blockBox2.minZ, blockBox2.maxX, blockBox2.maxY, blockBox2.maxZ)) {
                            boolean masked = false;
                            if (args.length >= 10) {
                                if (args[9].equals("masked")) {
                                    masked = true;
                                } else if (args[9].equals("filtered")) {
                                    if (args.length < 12) {
                                        throw new IncorrectUsageException("commands.clone.usage");
                                    }

                                    block = getBlock(source, args[11]);
                                    if (args.length >= 13) {
                                        j = getClampedInt(source, args[12], 0, 15);
                                    }
                                }
                            }

                            List<BlockInfo> list = Lists.newArrayList();
                            List<BlockInfo> list2 = Lists.newArrayList();
                            List<BlockInfo> list3 = Lists.newArrayList();
                            LinkedList<BlockPos> linkedList = Lists.newLinkedList();
                            BlockPos blockPos4 = new BlockPos(blockBox2.minX - blockBox.minX, blockBox2.minY - blockBox.minY, blockBox2.minZ - blockBox.minZ);

                            for(int k = blockBox.minZ; k <= blockBox.maxZ; ++k) {
                                for(int l = blockBox.minY; l <= blockBox.maxY; ++l) {
                                    for(int m = blockBox.minX; m <= blockBox.maxX; ++m) {
                                        BlockPos blockPos5 = new BlockPos(m, l, k);
                                        BlockPos blockPos6 = blockPos5.add(blockPos4);
                                        Block block5 = world.getBlock(blockPos5.x, blockPos5.y, blockPos5.z);
                                        int blockMeta5 = world.getBlockData(blockPos5.x, blockPos5.y, blockPos5.z);
                                        if ((!masked || block5 != Blocks.AIR) && (block == null || block5 == block && (j < 0 || blockMeta5 == j))) {
                                            BlockEntity blockEntity = world.getBlockEntity(blockPos5.x, blockPos5.y, blockPos5.z);
                                            if (blockEntity != null) {
                                                // can't figure out tile entities, so temporary measure.
                                                if (true) continue;
                                                NbtCompound compoundTag = new NbtCompound();
                                                blockEntity.fromNbt(compoundTag);

                                                list2.add(new BlockInfo(blockPos6, block5, blockMeta5, compoundTag));
                                                linkedList.addLast(blockPos5);
                                            } else if (!block5.isFullBlock() && !block5.renderAsNormalBlock()) {
                                                list3.add(new BlockInfo(blockPos6, block5, blockMeta5, null));
                                                linkedList.addFirst(blockPos5);
                                            } else {
                                                list.add(new BlockInfo(blockPos6, block5, blockMeta5, null));
                                                linkedList.addLast(blockPos5);
                                            }
                                        }
                                    }
                                }
                            }

                            if (bl) {
                                Iterator iterator2;
                                BlockPos blockPos8;
                                for(iterator2 = linkedList.iterator(); iterator2.hasNext(); world.setBlock(blockPos8.x, blockPos8.y, blockPos8.z, Blocks.STONE, 0, 2)) {
                                    blockPos8 = (BlockPos)iterator2.next();
                                    BlockEntity blockEntity2 = world.getBlockEntity(blockPos8.x, blockPos8.y, blockPos8.z);
                                    if (blockEntity2 instanceof Inventory) {
                                        // ((Inventory)blockEntity2).clear();
                                    }
                                }

                                iterator2 = linkedList.iterator();

                                while(iterator2.hasNext()) {
                                    blockPos8 = (BlockPos)iterator2.next();
                                    world.setBlock(blockPos8.x, blockPos8.y, blockPos8.z, Blocks.AIR, 0, 3);
                                }
                            }

                            List<BlockInfo> list4 = Lists.newArrayList();
                            list4.addAll(list);
                            list4.addAll(list2);
                            list4.addAll(list3);
                            List<BlockInfo> list5 = Lists.reverse(list4);

                            Iterator iterator6;
                            BlockInfo blockInfo4;
                            BlockEntity blockEntity4;
                            for(iterator6 = list5.iterator(); iterator6.hasNext(); world.setBlock(blockInfo4.blockPos.x, blockInfo4.blockPos.y, blockInfo4.blockPos.z, Blocks.STONE, 0, 2)) {
                                blockInfo4 = (BlockInfo)iterator6.next();
                                blockEntity4 = world.getBlockEntity(blockInfo4.blockPos.x, blockInfo4.blockPos.y, blockInfo4.blockPos.z);
                                if (blockEntity4 instanceof Inventory) {
                                   ((Inventory)blockEntity4).setInvStack(0, null);
                                }
                            }

                            i = 0;
                            iterator6 = list4.iterator();

                            while(iterator6.hasNext()) {
                                blockInfo4 = (BlockInfo)iterator6.next();
                                if (world.setBlock(blockInfo4.blockPos.x, blockInfo4.blockPos.y, blockInfo4.blockPos.z, blockInfo4.block, blockInfo4.blockMeta, 2)) {
                                    ++i;
                                }
                            }

                            for(iterator6 = list2.iterator(); iterator6.hasNext(); world.setBlock(blockInfo4.blockPos.x, blockInfo4.blockPos.y, blockInfo4.blockPos.z, blockInfo4.block, 0, 2)) {
                                blockInfo4 = (BlockInfo)iterator6.next();
                                blockEntity4 = world.getBlockEntity(blockInfo4.blockPos.x, blockInfo4.blockPos.y, blockInfo4.blockPos.z);
                                if (blockInfo4.compound != null && blockEntity4 != null) {
                                    blockInfo4.compound.putInt("x", blockInfo4.blockPos.getX());
                                    blockInfo4.compound.putInt("y", blockInfo4.blockPos.getY());
                                    blockInfo4.compound.putInt("z", blockInfo4.blockPos.getZ());
                                    blockEntity4.fromNbt(blockInfo4.compound);
                                    System.out.println(blockInfo4.compound + " <= LMAO");
                                    blockEntity4.markDirty();
                                }
                            }

                            iterator6 = list5.iterator();

                            while(iterator6.hasNext()) {
                                blockInfo4 = (BlockInfo)iterator6.next();
                                world.method_3697(blockInfo4.blockPos.x, blockInfo4.blockPos.y, blockInfo4.blockPos.z, blockInfo4.block);
                            }
                            // temporary remove
                            //a List list6 = world.getEntitiesIn(null, Box.of(blockBox.minX, blockBox.minY, blockBox.minZ, blockBox.maxX, blockBox.maxY, blockBox.maxZ));
//                            if (list6 != null) {
//                                Iterator iterator7 = list6.iterator();
//
//                                while(iterator7.hasNext()) {
//                                    BlockEntity tickableEntry = (BlockEntity) iterator7.next();
//                                    if (blockBox.intersects(tickableEntry.x, tickableEntry.y, tickableEntry.z)) {
//                                        System.out.println("tickable: " + tickableEntry);
//                                        BlockPos blockPos9 = new BlockPos(tickableEntry.x += blockPos4.x, tickableEntry.y += blockPos4.y, tickableEntry.z += blockPos4.z);
//                                        world.method_3603(blockPos9.x, blockPos9.y, blockPos9.z, tickableEntry);
//                                    }
//                                }
//                            }

                            if (i <= 0) {
                                throw new CommandException("commands.clone.failed");
                            } else {
                                run(source, this, "commands.clone.success", i);
                            }
                        } else {
                            throw new CommandException("commands.clone.outOfWorld");
                        }
                    } else {
                        throw new CommandException("commands.clone.outOfWorld");
                    }
                }
            }
        }
    }

    @Override
    public List method_3276(CommandSource source, String[] args) {
//        if (args.length > 0 && args.length <= 3) {
//            return method_10707(args, 0, pos);
//        } else if (args.length > 3 && args.length <= 6) {
//            return method_10707(args, 3, pos);
//        } else if (args.length > 6 && args.length <= 9) {
//            return method_10707(args, 6, pos);
//        } else if (args.length == 10) {
//            return method_2894(args, "replace", "masked", "filtered");
//        } else if (args.length == 11) {
//            return method_2894(args, "normal", "force", "move");
//        } else {
//            return args.length == 12 && "filtered".equals(args[9]) ? method_10708(args, Block.REGISTRY.keySet()) : null;
//        }
        return Collections.emptyList();
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }

    static class BlockInfo {
        public final BlockPos blockPos;
        public final Block block;
        public final NbtCompound compound;
        private final int blockMeta;

        public BlockInfo(BlockPos blockPos, Block block, int blockMeta, NbtCompound compoundTag) {
            this.blockPos = blockPos;
            this.block = block;
            this.blockMeta = blockMeta;
            this.compound = compoundTag;
        }
    }
}
