package org.zfmoth.mothutils.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.zfmoth.mothutils.Constants.*;

public class MotHutilsClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("MotHutils");
    public static boolean GlideAllowed = false;
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(GLIDE_SYNC_PACKET_ID, (client, handler, buffer, sender) -> {
            boolean enabled = buffer.readBoolean();
            client.execute(()->GlideAllowed = enabled);
            LOGGER.info(String.valueOf(enabled));
        });
    }
}
