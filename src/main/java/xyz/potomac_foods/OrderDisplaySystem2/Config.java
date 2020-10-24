package xyz.potomac_foods.OrderDisplaySystem2;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

/** Class that reads the config from the config file to be used
 *  in the rest of the program
 *
 * @author  Michael Amaya
 * @version 1.0
 * @since   2020-08-09
 *
 */
public class Config {
    /** The map that will hold the config, will use
     *  Map<>.getOrDefault(keyToSearch, defaultValue) in order to
     *  see if the config was retrieved correctly.
     *
     */
    private Map<String, Object> config = new HashMap<>();

    /** Constructor that initializes the config by
     *  checking the local file specific by the fileName param (below)
     *  and putting it into the global hash map
     *
     * @param fileName The name of the config file
     */
    public Config(String fileName) {
        Yaml configYaml = new Yaml();
        // String configPath = System.getProperty("user.dir") + "\\" + fileName;
        File configFile = new File(fileName);

        try {
            InputStream configInput= new FileInputStream(configFile);
            this.config = configYaml.load(configInput);

            /* Examples on how to use it.
            this.port = config.get("port").toString();
            this.payDelay = config.get("pay-delay").toString();
            config.getOrDefault("port", 33); RETURNS 33 IF PORT DOESN'T EXIST
            */

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + fileName);
        }
    }

    /** Gets the config read from the file
     *
     * @return A reference to the config Map in order to get config
     */
    public Map<String, Object> getConfig() { return this.config; }
}
