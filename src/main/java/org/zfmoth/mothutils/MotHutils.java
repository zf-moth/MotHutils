package org.zfmoth.mothutils;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.EntityElytraEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zfmoth.mothutils.client.MotHutilsClient;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.zfmoth.mothutils.Constants.GLIDE_SYNC_PACKET_ID;

public class MotHutils implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("MotHutils");
    public static Set<UUID> Glide_Allowed = new HashSet<>();

    @Override
    public void onInitialize() {
        Config.getInstance();
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> GlideCommand.register(dispatcher)));

        ServerPlayConnectionEvents.JOIN.register(((handler, sender, server) -> {
            server.sendMessage(Text.of("Player " + handler.getPlayer().getDisplayName() + " just joined"));
        }));

        EntityElytraEvents.CUSTOM.register(((entity, tickElytra) -> {
            if(entity.getWorld().isClient()){
                return MotHutilsClient.GlideAllowed;
            }
            if(entity instanceof PlayerEntity && Glide_Allowed.contains(entity.getUuid())){
                return true;
            }
            return false;
        }));

        ServerLifecycleEvents.SERVER_STARTING.register((server -> {
            Path path = server.getSavePath(WorldSavePath.ROOT).resolve("mothutils").normalize();
            Config.setConfigPath(path);
            Config.readConfig();
            Glide_Allowed = Config.getInstance().getStoredUuids();
        }));

        ServerPlayConnectionEvents.JOIN.register((handler,sender,server)->{
            boolean enabled = Glide_Allowed.contains(handler.getPlayer().getUuid());
            PacketByteBuf buffer = PacketByteBufs.create();
            buffer.writeBoolean(enabled);
            sender.sendPacket(sender.createPacket(GLIDE_SYNC_PACKET_ID,buffer));
        });
    }

    public static void addGlide_Profile(UUID player){
        Glide_Allowed.add(player);
        Config.getInstance().setStoredUuids(Glide_Allowed);
        Config.writeConfig();
    }

    public static void removeGlide_Profile(UUID player){
        Glide_Allowed.remove(player);
        Config.getInstance().setStoredUuids(Glide_Allowed);
        Config.writeConfig();
    }
}

