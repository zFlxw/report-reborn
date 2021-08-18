package com.github.zflxw.reportreborn.localization;

import com.github.zflxw.reportreborn.ReportReborn;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * The Translator class is a simple message loading system. You can search for a specific key and language.
 * Since I do not use any static methods, you can create multiple instances for different directories,
 * so if you want to use this class via the API in your own plugin, go for it.
 */
public class Translator {
    private final HashMap<Language, YamlConfiguration> languageList = new HashMap<>();
    private final File directory;

    /**
     * initializes the translator for the given directory.
     * @param directory the directory where the message files are located in.
     */
    public Translator(String directory, String templatePath) {
        this(new File(directory), templatePath);
    }

    /**
     * initializes the translator for the given directory.
     * @param directory the directory where the message files are located in
     */
    public Translator(File directory, String templatePath) {
        this.directory = directory;

        this.load(templatePath);
    }

    /**
     * get a message from the message file with the default server language.
     * @param key the key to search for
     * @return the appropriate message
     */
    public String get(String key) {
        return get(Language.getByKey(ReportReborn.getInstance().getConfiguration().getString("language")), key);
    }

    /**
     * get a message from the message file with the given language.
     * @param language the language of the message
     * @param key the key to search for
     * @return the appropriate message
     */
    public String get(Language language, String key) {
        YamlConfiguration yamlConfiguration = languageList.get(language);

        if (yamlConfiguration.contains(key)) {
            return ChatColor.translateAlternateColorCodes('&', yamlConfiguration.getString(key));
        }

        return key;
    }

    /**
     * load all messages files. If there is one missing, the default one will be copied.
     * If a message file is corrupted (entries are missing), the missing entries will be added
     * @param templatePath the path where your template file is located. IMPORTANT: Your file must follow this name pattern:
     *                     <language_key>.yml. Replace the language key with "%s" in the parameter,
     *                     like on the onEnable of this plugin {@link ReportReborn#onEnable()}
     */
    private void load(String templatePath) {
        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs();
        }

        for (Language language : Language.values()) {
            File languageFile = new File(directory, language.getLanguageKey() + ".yml");

            if (!languageFile.exists()) {
                ReportReborn.getInstance().log(Level.WARNING, "No localization file for \"" + language.getLanguageName() + " (" + language.getLanguageKey() + ")\" found. Loading default file.");
                try {
                    Files.copy(ReportReborn.class.getClassLoader().getResourceAsStream(templatePath.formatted(language.getLanguageKey())), Paths.get(languageFile.toURI()));

                    languageFile = new File(directory, language.getLanguageKey() + ".yml");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                validateConfig(languageFile, language, templatePath);
            }

            languageList.put(language, YamlConfiguration.loadConfiguration(languageFile));
        }
    }

    /**
     * checks for missing entries in the message files and adds them, if they are missing
     * @param file the message file
     * @param language the language of the message file
     */
    private void validateConfig(File file, Language language, String templatePath) {
        try {
            File tmpFile = new File(ReportReborn.getInstance().getDataFolder() + "/temp", language.getLanguageKey() + "-tmpl.yml");
            FileUtils.copyInputStreamToFile(ReportReborn.class.getClassLoader().getResourceAsStream(templatePath.formatted(language.getLanguageKey())), tmpFile);

            YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
            YamlConfiguration templateConfig = YamlConfiguration.loadConfiguration(tmpFile);

            for (String key : templateConfig.getKeys(true)) {
                if (!fileConfig.contains(key)) {
                    fileConfig.set(key, templateConfig.get(key));
                }
            }

            fileConfig.save(file);
            FileUtils.deleteDirectory(new File(ReportReborn.getInstance().getDataFolder() + "/temp"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
