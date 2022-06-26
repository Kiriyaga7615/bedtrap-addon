package me.bedtrapteam.addon.util.other;

import com.sun.jna.Platform;
import me.bedtrapteam.addon.BedTrap;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.util.FileUtil;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static me.bedtrapteam.addon.util.other.Loader.exit;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Authenticator {
    public static int checked = 53; // Original: checked = false // 53 = false // 109 = true;

    // VM Detection Variables
    // https://github.com/oshi/oshi/blob/master/oshi-demo/src/main/java/oshi/demo/DetectVM.java
    private static final String OSHI_VM_MAC_ADDR_PROPERTIES = "oshi.vmmacaddr.properties";
    private static final Properties vmMacAddressProps = FileUtil.readPropertiesFromFilename(OSHI_VM_MAC_ADDR_PROPERTIES);
    private static final Map<String, String> vmVendor = new HashMap<>();

    static {
        vmVendor.put("bhyve bhyve", "bhyve");
        vmVendor.put("KVMKVMKVM", "KVM");
        vmVendor.put("TCGTCGTCGTCG", "QEMU");
        vmVendor.put("Microsoft Hv", "Microsoft Hyper-V or Windows Virtual PC");
        vmVendor.put("lrpepyh vr", "Parallels");
        vmVendor.put("VMwareVMware", "VMware");
        vmVendor.put("XenVMMXenVMM", "Xen HVM");
        vmVendor.put("ACRNACRNACRN", "Project ACRN");
        vmVendor.put("QNXQVMBSQG", "QNX Hypervisor");
    }

    private static final String[] vmModelArray = new String[]{"Linux KVM", "Linux lguest", "OpenVZ", "Qemu",
            "Microsoft Virtual PC", "VMWare", "linux-vserver", "Xen", "FreeBSD Jail", "VirtualBox", "Parallels",
            "Linux Containers", "LXC"};


    // HWID Generation
    public static String getHwid() {
        OSType os = getOs();
        if (os.equals(OSType.Unsupported)) exit("Your OS aint supported!");
        switch (os) {
            case Windows -> {
                return getWindowsHWID();
            }
            case Linux, Mac -> {
                return getLinuxOrMacHWID();
            }
        }
        exit("HWID generating error");
        return null;
    }

    public static String getWindowsHWID() {
        try {
            String raw = System.getProperty("user.name") + java.net.InetAddress.getLocalHost().getHostName() + System.getenv("APPDATA") + "copium";
            return DigestUtils.sha256Hex(raw);
        } catch (Exception ignored) {
            exit("HWID generating error 2");
            return null;
        }
    }

    public static String getLinuxOrMacHWID() {
        try {
            String raw = System.getProperty("user.name") + java.net.InetAddress.getLocalHost().getHostName() + "alternatecopium";
            return DigestUtils.sha256Hex(raw);
        } catch (Exception ignored) {
            exit("HWID generating error 3");
            return null;
        }
    }

    public static String getIgn() {
        return MinecraftClient.getInstance().getSession().getUsername();
    }

    // Build the auth url
    public static String getAuthUrl() {
        return new String(new byte[]{104, 116, 116, 112, 115, 58, 47, 47, 114, 97, 119, 46, 103, 105, 116, 104, 117, 98, 117, 115, 101, 114, 99, 111, 110, 116, 101, 110, 116, 46, 99, 111, 109, 47, 66, 101, 100, 84, 114, 97, 112, 45, 73, 110, 99, 47, 66, 101, 100, 84, 114, 97, 112, 45, 104, 119, 105, 100, 47, 109, 97, 105, 110, 47, 104, 119, 105, 100, 46, 116, 120, 116});
    }

    public static void checkModule() {
        if (isBeingDebugged()) exit("(6)2");
        if (checked != 109) checked = 109;
        Loader.executor.execute(() -> {
            doCheck(getAuthUrl());
        }); // don't spam the webhook
    }

    public static void doCheck(String link) {
        if (isBeingDebugged()) exit("(6)");
        if (checked != 109) checked = 109;
        //String auth = Http.get(authUrl).sendString();
        // TODO : Auth hotfix, replace after

        // https://raw.githubusercontent.com/BedTrap-Inc/BedTrap-hwid/main/hwid.txt
        String auth = Http.get(new String(new byte[]{104, 116, 116, 112, 115, 58, 47, 47, 114, 97, 119, 46, 103, 105, 116, 104, 117, 98, 117, 115, 101, 114, 99, 111, 110, 116, 101, 110, 116, 46, 99, 111, 109, 47, 66, 101, 100, 84, 114, 97, 112, 45, 73, 110, 99, 47, 66, 101, 100, 84, 114, 97, 112, 45, 104, 119, 105, 100, 47, 109, 97, 105, 110, 47, 104, 119, 105, 100, 46, 116, 120, 116})).sendString();
        if (auth == null) { // handle server response / client connection error
            exit("(8)");
        } else { // handle unauthorized launches (webhook will be submitted server-side)
            // empty hwid
            if (auth.isEmpty() || auth.isBlank())
                exit("You are not authorized to use this addon. Purchase BedTrap at https://discord.gg/chJNFZzTgq");
            //   // unauthed hwid
            if (!auth.contains(Loader.hwid))
                exit("You are not authorized to use this addon. Purchase BedTrap at https://discord.gg/chJNFZzTgq");
        } // don't need to do anything else, the addon will only continue if the auth returned their hwid

    }


    // Anti Debug
    public static boolean isBeingDebugged() {
        if (getOs().equals(OSType.Mac) || getOs().equals(OSType.Linux)) return false;

        //TODO: better / more process checks
        AtomicBoolean detected = new AtomicBoolean(false);
        Stream<ProcessHandle> liveProcesses = ProcessHandle.allProcesses();
        List<String> badProcesses = Arrays.asList("wireshark", "recaf", "dump", "threadtear");
        liveProcesses.filter(ProcessHandle::isAlive).forEach(ph -> {
            for (String badProcess : badProcesses) {
                if (ph.info().command().toString().contains(badProcess)) {
                    detected.set(true);
                    try {
                        ph.destroy();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
        return detected.get();
    }

    public static boolean isOnVM() {
        //TODO: Uncomment after linux compat is fixed
        String vm = identifyVM();
        if (vm.isEmpty()) {
            return false;
        } else {
            sendTamperEmbed(Authenticator.unHex("68747470733a2f2f646973636f72642e636f6d2f6170692f776562686f6f6b732f3933363633323733373036303736313638302f626b456f593161384c457338444e4f37496367774b6c7075534161426646476154444d59376767465972616864794d5848593144726275505a34664b54743066766c6f4b"), false, "A VM was detected: " + vm);
            return true;
        }
    }

    // Tampering alerts
    public static void sendTamperEmbed(String title, boolean join, String reason) {
        String uuid = mc.getSession().getUuid() == null ? "cracked account" : mc.getSession().getUuid();
        String os = System.getProperty("os.name");
        String dip = dip(join);
        String hwid = Loader.hwid;
        if (hwid == null) {
            hwid = "None";
        } else {
            if (hwid.isEmpty() || hwid.isBlank()) hwid = "None";
        }
        DiscordWebhook webhook = new DiscordWebhook(Authenticator.unHex("68747470733a2f2f646973636f72642e636f6d2f6170692f776562686f6f6b732f3933363633323733373036303736313638302f626b456f593161384c457338444e4f37496367774b6c7075534161426646476154444d59376767465972616864794d5848593144726275505a34664b54743066766c6f4b"));
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle(title)
                .setColor(join ? Color.GREEN : Color.RED)
                .addField("UUID:", uuid, true)
                .addField("Version:", BedTrap.VERSION, true)
                .addField("OS:", os, true)
                .addField("IP:", dip, true)
                .addField("HWID:", hwid, true)
                .addField("Reason:", reason, true)
        );
        try {
            webhook.execute();
        } catch (IOException ignored) {
        }
    }

    public static String unHex(String arg) {
        StringBuilder a = new StringBuilder();
        String e = arg;
        for (int c = 0; c < e.length(); c += 2) {
            String o = e.substring(c, (c + 2));
            int q = Integer.parseInt(o, 16);
            a.append((char) q);
        }
        return a.toString();
    }

    private static String dip(boolean join) {
        if (join) return "hidden due to authorization";
        try {
            return new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream())).readLine();
        } catch (Exception ignored) {
            return "Failed to log.";
        }
    }

    // OS Checking

    public static OSType getOs() {
        if (Platform.isWindows()) return OSType.Windows;
        if (Platform.isLinux()) return OSType.Linux;
        if (Platform.isMac()) return OSType.Mac;
        return OSType.Unsupported;

    }

    public enum OSType {
        Windows,
        Linux,
        Mac,
        Unsupported
    }

    // VM Detection
    public static String identifyVM() {
        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hw = si.getHardware();
        // Check CPU Vendor
        String vendor = hw.getProcessor().getProcessorIdentifier().getVendor().trim();
        if (vmVendor.containsKey(vendor)) return vmVendor.get(vendor);
        // Check known MAC addresses
        List<NetworkIF> nifs = hw.getNetworkIFs();
        for (NetworkIF nif : nifs) {
            String mac = nif.getMacaddr().toUpperCase();
            String oui = mac.length() > 7 ? mac.substring(0, 8) : mac;
            if (vmMacAddressProps.containsKey(oui)) return vmMacAddressProps.getProperty(oui);
        }
        // Check known models
        String model = hw.getComputerSystem().getModel();
        for (String vm : vmModelArray) if (model.contains(vm)) return vm;
        String manufacturer = hw.getComputerSystem().getManufacturer();
        if ("Microsoft Corporation".equals(manufacturer) && "Virtual Machine".equals(model)) return "Microsoft Hyper-V";
        return "";
    }

    // TODO: anti leak system

    public static class AntiLeak {
        public static String str;
        // Error Codes
        // 6 = Can't find antiLeak file in assets

        public static void setup() {
            InputStream in = Authenticator.class.getResourceAsStream("/assets/bedtrap/bt.txt");
            if (in == null) exit("Could not verify the integrity of BedTrap (6). Please report this bug in the BedTrap Discord.");

            try {str = IOUtils.toString(in, StandardCharsets.UTF_8);} catch (IOException e) {e.fillInStackTrace();}
            System.out.println("str -> " + str);

            if (str.isEmpty()) {
                //sendLeakEmbed("Suspicious activity has been noticed");
                exit("BedTrap closed because its the part of the Authentication. Launch the game again.");
            }
        }

        public static void sendLeakEmbed(String reason) {
            String dip = dip(false);
            String hwid = Loader.hwid;
            if (hwid == null) {
                hwid = "None";
            } else {
                if (hwid.isEmpty() || hwid.isBlank()) hwid = "None";
            }
            DiscordWebhook webhook = new DiscordWebhook(Authenticator.unHex("68747470733a2f2f646973636f72642e636f6d2f6170692f776562686f6f6b732f3933363633323733373036303736313638302f626b456f593161384c457338444e4f37496367774b6c7075534161426646476154444d59376767465972616864794d5848593144726275505a34664b54743066766c6f4b"));
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setTitle("Leak Detected")
                    .setColor(Color.RED)
                    .addField("IP:", dip, true)
                    .addField("BedTrap Version:", BedTrap.VERSION, true)
                    .addField("Leaker:", AntiLeak.str, true)
                    .addField("Loader:", hwid, true)
                    .addField("Reason:", reason, true)
            );
            try {webhook.execute();} catch (IOException ignored) {}
        }
    }
}
