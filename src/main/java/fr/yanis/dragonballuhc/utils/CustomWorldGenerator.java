package fr.yanis.dragonballuhc.utils;

import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;

import java.util.Random;

public class CustomWorldGenerator extends ChunkGenerator {

    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        SimplexOctaveGenerator generator = new SimplexOctaveGenerator(new Random(world.getSeed()), 8);
        ChunkData chunkData = createChunkData(world);
        int centerX = Double.valueOf(world.getSpawnLocation().getX()).intValue() >> 4;
        int centerZ = Double.valueOf(world.getSpawnLocation().getZ()).intValue() >> 4;
        int radius = 5;
        int radiusSquared = radius * radius;
        for (int X = 0; X < 16; X++)
            for (int Z = 0; Z < 16; Z++) {
                if (Math.pow(x + X - centerX, 2) + Math.pow(z + Z - centerZ, 2) < radiusSquared) {
                    biome.setBiome(X, Z, Biome.ROOFED_FOREST); // Le biome au centre
                } else {
                    biome.setBiome(X, Z, Biome.FOREST); // Le biome ailleurs
                }
            }

        return chunkData;
    }

}
