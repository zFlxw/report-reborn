package com.github.zflxw.reportreborn;

import com.github.zflxw.reportreborn.utils.FileUtils;
import com.github.zflxw.reportreborn.utils.PermissionManager;
import com.github.zflxw.reportreborn.utils.commands.Command;
import com.github.zflxw.reportreborn.utils.commands.LoadCommand;
import com.github.zflxw.reportreborn.utils.listener.LoadListener;
import com.github.zflxw.reportreborn.utils.localization.Translator;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections8.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public final class ReportReborn extends JavaPlugin {
    /**
     * the namespace is used to identify the command, if there are multiple plugins with the same command.
     * You can call your command specifically by using /<namespace>:<command> (args...)
     */
    public static final String NAMESPACE = "report-reborn";

    private static ReportReborn instance;
    private PermissionManager permissionManager;
    private Translator translator;
    private FileUtils fileUtils;

    @Override
    public void onEnable() {
        instance = this;

        try {
            this.registerCommands();
            this.registerListener();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.translator = new Translator(this.getDataFolder() + "/messages");
        this.fileUtils = new FileUtils();
        this.permissionManager = new PermissionManager();
    }

    @Override
    public void onDisable() {

    }

    public static ReportReborn getInstance() { return instance; }

    public PermissionManager getPermissionManager() { return this.permissionManager; }
    public Translator getTranslator() { return this.translator; }
    public FileUtils getFileUtils() { return this.fileUtils; }

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
