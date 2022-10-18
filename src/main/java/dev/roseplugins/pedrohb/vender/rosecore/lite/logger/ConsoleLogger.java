package dev.roseplugins.pedrohb.vender.rosecore.lite.logger;

import dev.roseplugins.pedrohb.vender.RoseVender;

public final class ConsoleLogger {
    private static RoseVender rosevender;

    public static void init(RoseVender rosevender) {
        ConsoleLogger.rosevender = rosevender;
    }

    public static void info(String message) {
        rosevender.getServer().getConsoleSender().sendMessage("[" + rosevender.getName() + "] " + message);
    }

    public static void criticalwarning(String message) {
        rosevender.getServer().getConsoleSender().sendMessage("§4[" + rosevender.getName() + "] " + message);
    }
}
