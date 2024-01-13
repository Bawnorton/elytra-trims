package dev.kikugie.elytratrims.client;

/*? if fabric {*/
import net.fabricmc.api.ClientModInitializer;
public class ETClientWrapper implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ETClient.init();
    }
}
/*?} else {*//*
import dev.kikugie.elytratrims.client.config.ConfigScreenProvider;
import dev.kikugie.elytratrims.common.ETReference;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
@Mod.EventBusSubscriber(modid = ETReference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ETClientWrapper {
   public static void init(FMLClientSetupEvent event) {
       ETClient.init();
       var screenProvider = ConfigScreenProvider.getScreen();
       if (screenProvider != null) ModLoadingContext.get().registerExtensionPoint(
               ConfigScreenFactory.class,
               () -> new ConfigScreenFactory(($, screen) -> screenProvider.apply(screen)));
   }
}
*//*?}*/