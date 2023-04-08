package mcjty.theoneprobe.network;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.config.ConfigSetup;

import java.util.HashMap;
import java.util.Map;

public class ThrowableIdentity {
    private static Map<ThrowableIdentity, Long> catchedThrowables = new HashMap<>();
    private final String identifier;

    public ThrowableIdentity(Throwable e) {
        String message = e.getMessage();
        StringBuilder builder = new StringBuilder(message == null ? "<null>" : message);
        StackTraceElement[] st = e.getStackTrace();
        for (int i = 0; i < Math.min(3, st.length); i++) {
            builder
                    .append(st[i].getClassName())
                    .append(st[i].getFileName())
                    .append(st[i].getMethodName())
                    .append(st[i].getLineNumber());
        }
        identifier = builder.toString();
    }

    public static void registerThrowable(Throwable e) {
        ThrowableIdentity identity = new ThrowableIdentity(e);
        long curtime = System.currentTimeMillis();
        if (catchedThrowables.containsKey(identity)) {
            long lasttime = catchedThrowables.get(identity);
            if (curtime < lasttime + ConfigSetup.loggingThrowableTimeout) {
                // If this exception occured less then some time ago we don't report it.
                return;
            }
        }
        catchedThrowables.put(identity, curtime);
        TheOneProbe.setup.getLogger().debug("The One Probe catched error: ", e);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThrowableIdentity that = (ThrowableIdentity) o;

        if (identifier != null ? !identifier.equals(that.identifier) : that.identifier != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }
}
