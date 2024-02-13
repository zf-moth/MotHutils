package org.zfmoth.mothutils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
import static org.zfmoth.mothutils.Constants.*;

public class GlideCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("glide")
            .requires(source -> source.hasPermissionLevel(2))
            .then(literal("add")
                .then(argument("player", GameProfileArgumentType.gameProfile())
                    .executes(context -> {
                        Collection<GameProfile> gameProfiles = GameProfileArgumentType.getProfileArgument(context, "player");
                        gameProfiles.forEach(gameProfile -> {
                            executeAdd(context.getSource(), gameProfile);
                        });
                        return 1;
                    })
                )
            )
            .then(literal("remove")
                .then(argument("player", GameProfileArgumentType.gameProfile())
                    .executes(context -> {
                        Collection<GameProfile> gameProfiles = GameProfileArgumentType.getProfileArgument(context, "player");
                        gameProfiles.forEach(gameProfile -> {
                            executeRemove(context.getSource(), gameProfile);
                        });
                        return 1;
                    })
                )
            )
            .then(literal("list")
                .executes(context -> executeList(context.getSource()))
            )
        );
    }

    private static int executeList(ServerCommandSource source) {
        StringBuilder messageBuilder = new StringBuilder("List:");

        MotHutils.Glide_Allowed.forEach((player) -> {
            Optional<GameProfile> opt = source.getServer().getUserCache().getByUuid(player);
            if(opt.isPresent()){
                GameProfile user = opt.get();
                messageBuilder.append("\n").append(user.getName());
            }
        });
        String finalMessage = messageBuilder.toString();
        source.sendFeedback(() -> Text.literal(finalMessage), true);
        return 1;
    }

    private static void executeAdd(ServerCommandSource source, GameProfile playerName) {
        if (MotHutils.Glide_Allowed.contains(playerName.getId())) {
            source.sendFeedback(() -> Text.literal("Player " + playerName.getName() + " is already allowed to glide."),true);
            return;
        }
        MotHutils.addGlide_Profile(playerName.getId());
        source.sendFeedback(() -> Text.literal("Player " + playerName.getName() + " is allowed to glide now."),true);
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(true);
        ServerPlayNetworking.send(source.getServer().getPlayerManager().getPlayer(playerName.getId()), GLIDE_SYNC_PACKET_ID, buffer);
    }

    private static void executeRemove(ServerCommandSource source, GameProfile playerName) {
        if (!MotHutils.Glide_Allowed.contains(playerName.getId())) {
            source.sendFeedback(() -> Text.literal("Player " + playerName.getName() + " is already not allowed to glide."),true);
            return;
        }
        MotHutils.removeGlide_Profile(playerName.getId());
        source.sendFeedback(() -> Text.literal("Player " + playerName.getName() + " is no longer allowed to glide."),true);
        PacketByteBuf buffer = PacketByteBufs.create();
        buffer.writeBoolean(false);
        ServerPlayNetworking.send(source.getServer().getPlayerManager().getPlayer(playerName.getId()), GLIDE_SYNC_PACKET_ID, buffer);
    }
}
