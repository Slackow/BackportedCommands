package com.slackow.backport.resource;

import net.fabricmc.loader.api.ModContainer;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ZipResourcePack;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ModResourcePack extends ZipResourcePack implements ResourcePack {
	private final ModContainer container;
	private final Path root;
	public ModResourcePack(String modId, ModContainer container) {
		super(container.getOrigin().getPaths().get(0).toFile());
		this.container = container;
		this.root = container.getRootPaths().get(0);

		/*
		VoxelMap depends on itself being a ZipResourcePack with a non-null ZipFile, it also expects us to be
		a ZipResourcePack

		The crash that's caused by not doing this:
		java.lang.RuntimeException: java.lang.ArithmeticException: / by zero
			at de.skyrising.litefabric.runtime.LiteFabric.onInitCompleted(LiteFabric.java:111)
			at net.minecraft.client.MinecraftClient.handler$zzn000$litefabric$onGameInitDone(MinecraftClient.java:4548)
			at net.minecraft.client.MinecraftClient.initializeGame(MinecraftClient.java:515)
			at net.minecraft.client.MinecraftClient.run(MinecraftClient.java:361)
			at net.minecraft.client.main.Main.main(Main.java:109)
			at net.fabricmc.loader.impl.game.minecraft.MinecraftGameProvider.launch(MinecraftGameProvider.java:461)
			at net.fabricmc.loader.impl.launch.knot.Knot.launch(Knot.java:74)
			at net.fabricmc.loader.launch.knot.KnotClient.main(KnotClient.java:28)
		Caused by: java.lang.ArithmeticException: / by zero
			at net.minecraft.client.texture.TextureUtil.method_7022(TextureUtil.java:149)
			at net.minecraft.client.texture.TextureUtil.method_5861(TextureUtil.java:48) // third parameter of this function is 0
			at com.mamiyaotaru.voxelmap.c.h.if(Unknown Source)
			at com.mamiyaotaru.voxelmap.u.do(Unknown Source)
			at com.mamiyaotaru.voxelmap.t.reload(Unknown Source)
			at net.minecraft.resource.ReloadableResourceManagerImpl.registerListener(ReloadableResourceManagerImpl.java:99)
			at com.mamiyaotaru.voxelmap.t.do(Unknown Source)
			at com.mamiyaotaru.voxelmap.litemod.LiteModVoxelMap.onInitCompleted(Unknown Source)
			at de.skyrising.litefabric.runtime.LiteFabric.onInitCompleted(LiteFabric.java:109)
			... 7 more

			Calling this here makes the ZipFile no longer null, fixing this crash.
		 */
		if ("voxelmap".equals(modId))
			containsFile("");
	}

	@Override
	public InputStream open(Identifier id) {
		try {
			return Files.newInputStream(getPath(id));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean contains(Identifier id) {
		return Files.exists(getPath(id));
	}

	@Override
	public Set<String> getNamespaces() {
		try (Stream<Path> stream = Files.list(getPath("assets"))) {
			return stream.filter(Files::isDirectory)
					.map(Path::getFileName)
					.map(Path::toString)
					.map(s -> s.endsWith("/") ? s.substring(0, s.length() - 1) : s)
					.collect(Collectors.toSet());
		} catch (IOException e) {
			return Collections.emptySet();
		}
	}




	@Override
	public BufferedImage getIcon() {
		try {
			return ImageIO.read(ModResourcePack.class.getResourceAsStream("/" + (new Identifier("pack.png")).getPath()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	public String getName() {
		return this.container.getMetadata().getName();
	}

	private InputStream openFile0(String file) throws IOException {
		return Files.newInputStream(getPath(file));
	}

	private Path getPath(String file) {
		return this.root.resolve(file);
	}

	private Path getPath(Identifier id) {
		return this.root.resolve("assets").resolve(id.getNamespace()).resolve(id.getPath());
	}
}
