package com.github.zflxw.reportreborn;

import com.github.zflxw.reportreborn.config.Config;
import com.github.zflxw.reportreborn.database.Database;
import com.github.zflxw.reportreborn.database.DatabaseManager;
import com.github.zflxw.reportreborn.localization.Translator;
import com.github.zflxw.reportreborn.manager.PermissionManager;
import com.github.zflxw.reportreborn.manager.ReportManager;
import com.github.zflxw.reportreborn.reflections.commands.Command;
import com.github.zflxw.reportreborn.reflections.commands.LoadCommand;
import com.github.zflxw.reportreborn.reflections.listener.LoadListener;
import com.github.zflxw.reportreborn.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections8.Reflections;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.logging.Level;

public final class ReportReborn extends JavaPlugin {
    /**
     * the namespace is used to identify the command, if there are multiple plugins with the same command.
     * You can call your command specifically by using /<namespace>:<command> (args...)
     */
    public static final String NAMESPACE = "report-reborn";

    /**
     * this is just a fallback prefix, you can customize the prefix via the config file.
     */
    public static final String PREFIX = "§c[REPORT] ";

    private static ReportReborn instance;
    private PermissionManager permissionManager;
    private Translator translator;
    private FileUtils fileUtils;
    private Config config;
    private Database database;

    @Override
    public void onEnable() {
        instance = this;

        this.fileUtils = new FileUtils();
        this.translator = new Translator(this.getDataFolder() + "/messages");
        this.config = new Config(new File(this.getDataFolder(), "config.yml"));
        this.permissionManager = new PermissionManager();

        try {
            this.registerCommands();
            this.registerListener();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.database = new Database();
        this.database.connect();

        DatabaseManager.createTables();
        ReportManager.cacheAllReports();
    }

    @Override
    public void onDisable() {
        this.database.disconnect();
    }

    public void log(Level level, String message) {
        this.getLogger().log(level, ChatColor.translateAlternateColorCodes('&', message));
    }

    public static ReportReborn getInstance() { return instance; }
    public PermissionManager getPermissionManager() { return this.permissionManager; }
    public Translator getTranslator() { return this.translator; }
    public FileUtils getFileUtils() { return this.fileUtils; }
    public Config getConfiguration() { return this.config; }
    public Database getDatabase() { return this.database; }

    /**
     * registers all classes annotated with "LoadCommand"
     */
    private void registerCommands() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections("com.github.zflxw.reportreborn.commands");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(LoadCommand.class)) {
            if (!(clazz.getSuperclass() == Command.class)) {
                continue;
            }

            if (Arrays.stream(clazz.getConstructors()).anyMatch(predicate -> predicate.getParameterCount() == 0)) {
                Command command = (Command) Arrays.stream(clazz.getConstructors()).filter(predicate -> predicate.getParameterCount() == 0)
                        .findFirst().get().newInstance();

                command.register();
            }
        }
    }

    /**
     * registers all classes annotated with "LoadListener"
     */
    private void registerListener() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections("com.github.zflxw.reportreborn.listener");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(LoadListener.class)) {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            if (!(instance instanceof Listener)) {
                continue;
            }

            Bukkit.getPluginManager().registerEvents((Listener) instance, this);
        }
    }
}
