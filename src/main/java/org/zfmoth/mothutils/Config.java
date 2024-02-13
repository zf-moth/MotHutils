package org.zfmoth.mothutils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.GameProfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Config {
    private static Config instance;
    private static Path CONFIG_PATH;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
    private Set<UUID> glideAllowed = new HashSet<>();

    public static Config getInstance(){
        if(instance == null){
            instance = new Config();
        }
        return instance;
    }

    public static void setConfigPath(Path configPath) {
        CONFIG_PATH = configPath;
    }

    public Set<UUID> getStoredUuids() {
        return this.glideAllowed;
    }

    public void setStoredUuids(Set<UUID> storedProfile) {
        this.glideAllowed = storedProfile;
    }

    public static void writeConfig() {
        String json = GSON.toJson(Config.getInstance());
        try (BufferedWriter bf = Files.newBufferedWriter(CONFIG_PATH)) {
            if(!Files.exists(CONFIG_PATH)){
                Files.createFile(CONFIG_PATH);
            }
            bf.write(json);
        } catch (IOException e) {
            MotHutils.LOGGER.error("Error storing approved Uuids to file: " + e);
        }
    }

    public static void readConfig() {
        if(Files.exists(CONFIG_PATH)) {
            try (BufferedReader br = Files.newBufferedReader(CONFIG_PATH)) {
                instance = GSON.fromJson(br, Config.class);
            } catch (IOException e) {
                MotHutils.LOGGER.error("Error reading approved Uuids from file: " + e);
            }
        } else {
            Config.writeConfig();
            MotHutils.LOGGER.info("Config file created");
        }
    }
}