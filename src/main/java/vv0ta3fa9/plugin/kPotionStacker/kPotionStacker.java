package vv0ta3fa9.plugin.kPotionStacker;

import org.bukkit.plugin.java.JavaPlugin;
import vv0ta3fa9.plugin.kPotionStacker.commands.CommandManager;
import vv0ta3fa9.plugin.kPotionStacker.utils.Color.Colorizer;
import vv0ta3fa9.plugin.kPotionStacker.utils.Color.impl.LegacyColorizer;

public final class kPotionStacker extends JavaPlugin {

    private Colorizer colorizer;
    private CommandManager commandManager;

    @Override
    public void onEnable() {
        try {
            colorizer = new LegacyColorizer(this);
            commandManager = new CommandManager(this);
            registerCommands();
        } catch (Exception e) {
            getLogger().severe("ОШИБКА ВКЛЮЧЕНИЯ ПЛАГИНА! Выключение плагина...");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
    }

    private void registerCommands() {
        if (getCommand("kpotionstacker") != null) {
            getCommand("kpotionstacker").setExecutor(commandManager);
        } else {
            getLogger().severe("Команда 'kpotionstacker' не найдена в plugin.yml!");
        }
    }

    public Colorizer getColorizer() {
        return colorizer;
    }
}

