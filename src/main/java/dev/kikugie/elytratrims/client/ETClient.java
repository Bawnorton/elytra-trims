package dev.kikugie.elytratrims.client;

import dev.kikugie.elytratrims.client.config.ETClientConfig;
import dev.kikugie.elytratrims.client.render.ETFeatureRenderer;
import dev.kikugie.elytratrims.client.resource.ETAtlasHolder;

public class ETClient {
    private static ETClientConfig config;
    private static ETAtlasHolder atlasHolder;
    private static ETFeatureRenderer renderer;

    public static void init() {

    }

    // Totally safe mhm mhm
    public static ETClientConfig getConfig() {
        if (config == null) config = ETClientConfig.load();
        return config;
    }

    public static ETAtlasHolder getAtlasHolder() {
        if (atlasHolder == null) atlasHolder = new ETAtlasHolder();
        return atlasHolder;
    }

    public static ETFeatureRenderer getRenderer() {
        if (renderer == null) renderer = new ETFeatureRenderer();
        return renderer;
    }
}