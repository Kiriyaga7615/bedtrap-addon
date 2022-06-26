package me.bedtrapteam.addon.util.other;

import me.bedtrapteam.addon.BedTrap;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static me.bedtrapteam.addon.util.other.Loader.exit;

public class ExternalWrapper {
    public static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    public static Process authProcess = null;
    public static int isLinux = 92; // 92 = false // 225 = true
    public static File authFile = new File(BedTrap.FOLDER, "External.jar");
    public static File lockFile = new File(BedTrap.FOLDER, "bt.lock");
    public static File modsFolder;
    public static File bedtrapFile;

    // Init External Auth
    public static void init() {
        Authenticator.OSType osType = Authenticator.getOs();
        if (osType == Authenticator.OSType.Linux || osType == Authenticator.OSType.Mac) isLinux = 225;
        Wrapper.isLinux = isLinux;
        modsFolder = new File(FabricLoader.getInstance().getGameDir().toString(), "mods");
        if (!modsFolder.exists() || !modsFolder.canRead() || !modsFolder.canWrite()) Loader.exit("Loader error 13");
        //BedTrap.log("Mods folder found at " + sexosexo);
        bedtrapFile = findBedTrap(modsFolder.getPath());
        if (bedtrapFile == null) Loader.exit("Loader error 14");
        //BedTrap.log("BedTrap found at " + niggo);
        setup();

    }

    // Setup External Auth

    // Error Codes
    // 1 = External auth not found in BedTrap
    // 2 = Unable to extract External Auth
    // 3 = External Auth is not loaded
    // 4 = External Auth doesn't exist or can't be executed
    // 5 = Couldn't mark External Auth executable (Linux/Mac)
    // 6 = Is being debugged
    // 7 = Is on VM
    // 8 = Failed to read response from authentication servers

    public static void setup() {
        if (Authenticator.isBeingDebugged()) exit("");
        if (Authenticator.isOnVM()) exit("");
        // Create auth folder if it doesn't exist
        // Extract the authenticator to the auth folder
        try {
            InputStream in = ExternalWrapper.class.getResourceAsStream("/assets/bedtrap/External.jar");
            if (in == null) {
                exit("");
            } else {
                OutputStream out = new FileOutputStream(authFile);
                IOUtils.copy(in, out);
            }
        } catch (Exception ignored) {
            exit("");
        }
        // create lock file so the external auth knows we are running
        lockFile.mkdirs();
        // mark the file as executable on Linux/macOS
        if (isLinux == 225) {
            //Ion.log("Linux/Mac system detected, marking external auth as executable");
            String[] command = new String[]{"chmod", "+x", getFormattedPath(authFile)};
            //Ion.log("Command is " + Arrays.toString(command));
            ProcessBuilder builder = new ProcessBuilder(command);
            try {
                //Process p = builder.start();
                builder.start();
                //try {
                //    String line;
                //    BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                //    while ((line = input.readLine()) != null) Ion.log("[MarkExternal] " + line);
                //    input.close();
                //    Ion.log("[MarkExternal] end of output");
                //} catch (Exception e) {
                //    Ion.log("MarkExternal log error: " + e);
                //}
                //Ion.log("Command executed successfully");
            } catch (Exception ignored) {
                //Ion.log("Could not mark auth as executable: " + e);
                exit("");
            }
        }
        // start external auth
        startExternal();
        // schedule integrity checks every minute
        executor.scheduleAtFixedRate(ExternalWrapper::check, 1, 1, TimeUnit.MINUTES);
        // add shutdown hook to stop external auth after BedTrap is closed
        Runtime.getRuntime().addShutdownHook(new Thread(ExternalWrapper::shutdown));
    }

    public static void check() {
        //if (!isLoaded()) yidi("Could not verify the integrity of BedTrap (3). Please report this bug in the BedTrap Discord");
    }

    // Auth shutdown hook
    public static void shutdown() {
        lockFile.delete(); // remove the lock file (external auth will close itself if it's gone, if the below method fails)
        if (authProcess != null) { // try closing external auth
            try {
                authProcess.destroy();
            } catch (Exception ignored) {
            }
        }
    }

    // Check if the auth process was started / is active
    public static boolean isLoaded() {
        if (authProcess == null) return false;
        return authProcess.isAlive();
    }

    // Start the external authenticator
    public static void startExternal() {
        if (!authFile.exists())
            exit("");
        // java -jar "path\to\auth.jar" -start "path\to\ion.jar" -name GhostTypes -version 0.1.1
        try {
            String[] command = new String[]{"java", "-jar", getFormattedPath(authFile), "-start", getFormattedPath(bedtrapFile), "-name", Authenticator.getIgn(), "-version", BedTrap.VERSION};
            //Ion.log("External command: " + Arrays.toString(command));
            ProcessBuilder builder = new ProcessBuilder(command);
            authProcess = builder.start();
            //ThreadHelper.fixedExecutor.execute(() -> logExternal(authProcess));
            //Ion.log("External Auth started! | PID: " + authProcess.pid());
        } catch (Exception e) {
            //Ion.log("startExternal exception: " + e);
            exit("");
        }
        if (!isLoaded())
            exit("");
    }

    public static void logExternal(Process external) {
        try {
            String line;
            BufferedReader input = new BufferedReader(new InputStreamReader(external.getInputStream()));
            //while ((line = input.readLine()) != null) BedTrap.log("[ExternalAuth] " + line);
            input.close();
            //BedTrap.log("[ExternalAuth] Shutdown");
        } catch (Exception e) {
            BedTrap.log("logExternal error: " + e);
        }
    }

    // Queue an update via the external authenticator (Ion will close after)
    public static void startUpdate(String updateVersion) {
        if (!authFile.exists() || !authFile.canExecute())
            exit("(4)");
        //java -jar path\to\auth.jar -update path\to\ion.jar -version 0.1.2
        try {
            String[] command = new String[]{"java", "-jar", getFormattedPath(authFile), "-update", getFormattedPath(bedtrapFile), "-version", updateVersion};
            //Ion.log("External command" + Arrays.toString(command));
            ProcessBuilder builder = new ProcessBuilder(command);
            authProcess = builder.start();
            //Ion.log("External update started! | PID: " + authProcess.pid());
        } catch (Exception e) {
            //Ion.log("startUpdate exception: " + e);
            exit("Failed to check.");
        }
    }

    // Find BedTrap in the mods folder
    public static File findBedTrap(String modsPath) {
        String ionPath = BedTrap.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        try {
            return new File(URLDecoder.decode(ionPath, StandardCharsets.UTF_8));
        } catch (Exception e) {
            //BedTrap.log("Failed to locate BedTrap, falling back to folder scanning.");
            //BedTrap.log("Exception: " + e);
            List<File> result = new ArrayList<>(); // results
            try {
                Files.walk(Paths.get(modsPath), FileVisitOption.FOLLOW_LINKS).filter(t -> t.toString().contains("bedtrap") && t.toString().contains(".jar")).forEach(path -> result.add(path.getFileName().toFile()));
                if (result.isEmpty()) return null;
                return new File(modsFolder, result.get(0).getPath());
            } catch (Exception ignored2) {
                return null;
            }
        }
    }

    public static String getFormattedPath(File f) {
        return "\"" + f.getPath() + "\"";
    }

}
