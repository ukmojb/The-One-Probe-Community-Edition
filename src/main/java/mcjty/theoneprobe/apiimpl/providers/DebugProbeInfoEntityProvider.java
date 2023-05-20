package mcjty.theoneprobe.apiimpl.providers;

import mcjty.theoneprobe.TheOneProbe;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.apiimpl.styles.LayoutStyle;
import mcjty.theoneprobe.config.Config;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import static mcjty.theoneprobe.api.TextStyleClass.INFO;
import static mcjty.theoneprobe.api.TextStyleClass.LABEL;

public class DebugProbeInfoEntityProvider implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return TheOneProbe.MODID + ":entity.debug";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
        if (mode == ProbeMode.DEBUG && Config.showDebugInfo) {
            IProbeInfo vertical = null;
            if (entity instanceof EntityLivingBase) {
                vertical = probeInfo.vertical(new LayoutStyle().borderColor(0xffff4444).spacing(2));

                EntityLivingBase entityLivingBase = (EntityLivingBase) entity;
                int totalArmorValue = entityLivingBase.getTotalArmorValue();
                int age = entityLivingBase.getIdleTime();
                float absorptionAmount = entityLivingBase.getAbsorptionAmount();
                float aiMoveSpeed = entityLivingBase.getAIMoveSpeed();
                int revengeTimer = entityLivingBase.getRevengeTimer();
                vertical
                        .text(LABEL + I18n.translateToLocal("top.Tot_armor") + ": " + INFO + totalArmorValue)
                        .text(LABEL + I18n.translateToLocal("top.Age") + ": " + INFO + age)
                        .text(LABEL + I18n.translateToLocal("top.Absorption") + ": " + INFO + absorptionAmount)
                        .text(LABEL + I18n.translateToLocal("top.AI_Move_Speed") + ": " + INFO + aiMoveSpeed)
                        .text(LABEL + I18n.translateToLocal("top.Revenge_Timer") + ": " + INFO + revengeTimer);
            }
            if (entity instanceof EntityAgeable) {
                if (vertical == null) {
                    vertical = probeInfo.vertical(new LayoutStyle().borderColor(0xffff4444).spacing(2));
                }

                EntityAgeable entityAgeable = (EntityAgeable) entity;
                int growingAge = entityAgeable.getGrowingAge();
                vertical
                        .text(LABEL + I18n.translateToLocal("top.Growing_Age") + ": " + INFO + growingAge);
            }
        }
    }
}
