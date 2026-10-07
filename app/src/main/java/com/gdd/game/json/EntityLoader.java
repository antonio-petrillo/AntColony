package com.gdd.game.json;

import android.content.res.AssetManager;

import com.gdd.game.ecs.factories.AntFactory;
import com.gdd.game.ecs.factories.FoodFactory;
import com.gdd.game.ecs.factories.NestFactory;
import com.gdd.game.ecs.factories.WaspFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EntityLoader {

    private EntityLoader() {}

    public static void load(AssetManager manager) throws IOException, JSONException {
        JSONObject root = new JSONObject(readAsset(manager, "data/entities.json"));
        AntFactory.init(root.getJSONObject("ant"));
        WaspFactory.init(root.getJSONObject("wasp"));
        FoodFactory.init(root.getJSONObject("food"));
        NestFactory.init(root.getJSONObject("nest"));
    }

    private static String readAsset(AssetManager manager, String path) throws IOException {
        try (InputStream is = manager.open(path);
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
            return sb.toString();
        }
    }
}
