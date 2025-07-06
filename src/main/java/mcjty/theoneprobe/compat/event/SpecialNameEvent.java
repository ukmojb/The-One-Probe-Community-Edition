package mcjty.theoneprobe.compat.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SpecialNameEvent extends Event {

    private final Entity entity;

    private String spacialName = null;

    public SpecialNameEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public String getSpacialName() {
        return spacialName;
    }

    public void setSpacialName(String spacialName) {
        this.spacialName = spacialName;
    }
}
