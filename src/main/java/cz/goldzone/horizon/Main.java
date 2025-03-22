package cz.goldzone.horizon;

import com.google.gson.Gson;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    @Getter
    private static final Gson gson = new Gson();

    @Getter
    private static Main instance;

    @Override
    public void onEnable() {
        instance = this;
    }
}
