package com.slackow.backport.command;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


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
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static net.minecraft.block.Blocks.STONE;

public class FillCommand extends AbstractCommand {
    public FillCommand() {
    }

    public String getCommandName() {
        return "fill";
    }

    public int getPermissionLevel() {
        return 2;
    }

    public String getUsageTranslationKey(CommandSource source) {
        return "commands.fill.usage";
    }

    public static BlockPos getBlockPos(CommandSource source, String[] args, int offset) {
        int x = source.method_4086().x;
        int y = source.method_4086().y;
        int z = source.method_4086().z;
        x = MathHelper.floor(method_6332(source, x, args[offset]));
        y = MathHelper.floor(method_6333(source, y, args[offset + 1], 0, 256));
        z = MathHelper.floor(method_6332(source, z, args[offset + 2]));
        return new BlockPos(x, y, z);
    }

    public void execute(CommandSource source, String[] args) throws CommandException {
        if (args.length < 7) {
            throw new IncorrectUsageException("commands.fill.usage");
        } else {
            // method_6332(source, 0, args[0]);
            BlockPos blockPos = getBlockPos(source, args, 0);
            BlockPos blockPos2 = getBlockPos(source, args, 3);
            Block block = AbstractCommand.getBlock(source, args[6]);
            int i = 0;
            if (args.length >= 8) {
                i = getClampedInt(source, args[7], 0, 15);
            }

            BlockPos blockPos3 = new BlockPos(Math.min(blockPos.getX(), blockPos2.getX()), Math.min(blockPos.getY(), blockPos2.getY()), Math.min(blockPos.getZ(), blockPos2.getZ()));
            BlockPos blockPos4 = new BlockPos(Math.max(blockPos.getX(), blockPos2.getX()), Math.max(blockPos.getY(), blockPos2.getY()), Math.max(blockPos.getZ(), blockPos2.getZ()));
            int j = (blockPos4.getX() - blockPos3.getX() + 1) * (blockPos4.getY() - blockPos3.getY() + 1) * (blockPos4.getZ() - blockPos3.getZ() + 1);
            if (j > 32768) {
                throw new CommandException("commands.fill.tooManyBlocks", j, 32768);
            } else if (blockPos3.getY() >= 0 && blockPos4.getY() < 256) {
                World world = source.getWorld();

                NbtCompound compoundTag = new NbtCompound();
                boolean bl = false;
                if (args.length >= 10 && block.hasBlockEntity()) {
                    String string = method_4635(source, args, 9).asUnformattedString();

                    try {
                        compoundTag = (NbtCompound) StringNbtReader.method_7377(string);
                        bl = true;
                    } catch (ClassCastException var21) {
                        throw new CommandException("commands.fill.tagError", var21.getMessage());
                    }
                }

                List<BlockPos> list = Lists.newArrayList();
                j = 0;

                for(int m = blockPos3.getZ(); m <= blockPos4.getZ(); ++m) {
                    for(int n = blockPos3.getY(); n <= blockPos4.getY(); ++n) {
                        for(int o = blockPos3.getX(); o <= blockPos4.getX(); ++o) {
                            BlockPos blockPos5 = new BlockPos(o, n, m);
                            Block blockState;
                            if (args.length >= 9) {
                                if (!args[8].equals("outline") && !args[8].equals("hollow")) {
                                    if (args[8].equals("destroy")) {
                                        world.method_4715(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ(), true);
                                    } else if (args[8].equals("keep")) {
                                        if (!world.isAir(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ())) {
                                            continue;
                                        }
                                    } else if (args[8].equals("replace") && !block.hasBlockEntity()) {
                                        if (args.length > 9) {
                                            Block block2 = AbstractCommand.getBlock(source, args[9]);
                                            if (!world.getBlock(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ()).equals(block2)) {
                                                continue;
                                            }
                                        }

                                        if (args.length > 10) {
                                            int p = getInt(source, args[10]);
                                            blockState = world.getBlock(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ());
                                            if (blockState.getBlockType() != p) {
                                                continue;
                                            }
                                        }
                                    }
                                } else if (o != blockPos3.getX() && o != blockPos4.getX() && n != blockPos3.getY() && n != blockPos4.getY() && m != blockPos3.getZ() && m != blockPos4.getZ()) {
                                    if (args[8].equals("hollow")) {
                                        world.setBlock(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ(), Blocks.AIR, i, 2);
                                        list.add(blockPos5);
                                    }
                                    continue;
                                }
                            }

                            BlockEntity blockEntity = world.getBlockEntity(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ());
                            if (blockEntity != null) {
                                if (blockEntity instanceof Inventory) {
                                    //((Inventory)blockEntity).method_10897();
                                }
                                world.setBlock(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ(), STONE, i, 2);
                               // world.setBlock(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ(), BARRIER, block == Blocks.BARRIER ? 2 : 4);
                            }

                            if (world.setBlock(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ(), block, i, 2)) {
                                list.add(blockPos5);
                                ++j;
                                if (bl) {
                                    BlockEntity blockEntity2 = world.getBlockEntity(blockPos5.getX(), blockPos5.getY(), blockPos5.getZ());
                                    if (blockEntity2 != null) {
                                        // temporary, skip tile entity data insertion.
                                        if (true) continue;
                                        compoundTag.putInt("x", blockPos5.getX());
                                        compoundTag.putInt("y", blockPos5.getY());
                                        compoundTag.putInt("z", blockPos5.getZ());
                                        blockEntity2.fromNbt(compoundTag);
                                    }
                                }
                            }
                        }
                    }
                }

                Iterator iterator = list.iterator();

                while(iterator.hasNext()) {
                    BlockPos blockPos6 = (BlockPos)iterator.next();
                    Block block3 = world.getBlock(blockPos6.x, blockPos6.y, blockPos6.z);
                    world.updateNeighbors(blockPos6.x, blockPos6.y, blockPos6.z, block3);
                }

                if (j <= 0) {
                    throw new CommandException("commands.fill.failed");
                } else {
                    // source.setStat(Type.AFFECTED_BLOCKS, j);
                    run(source, this, "commands.fill.success", j);
                }
            } else {
                throw new CommandException("commands.fill.outOfWorld");
            }
        }
    }

    @Override
    public List method_3276(CommandSource source, String[] args) {
//        if (args.length > 0 && args.length <= 3) {
//            return method_10707(args, 0, source);
//        } else if (args.length > 3 && args.length <= 6) {
//            return method_10707(args, 3, pos);
//        } else if (args.length == 7) {
//            return method_10708(args, Block.REGISTRY.keySet());
//        } else if (args.length == 9) {
//            return method_2894(args, "replace", "destroy", "keep", "hollow", "outline");
//        } else {
//            return args.length == 10 && "replace".equals(args[8]) ? method_10708(args, Block.REGISTRY.keySet()) : null;
//        }
//        return super.method_3276(source, strings);
        return Collections.emptyList();
    }

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }
}
