package mcjty.theoneprobe.mods.botania;

import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import vazkii.botania.common.entity.EntitySpark;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/12/16 18:53
 */
public class Spark implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return "botania.spark";
    }


    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
        if (entity instanceof EntitySpark) {
            EntitySpark sparkEntity = (EntitySpark) entity;
            probeInfo.text(20000 + "Mana/s");

            if (player.isSneaking()) {
                String upgradename = "null";
                switch (sparkEntity.getUpgrade()) {
                    case DISPERSIVE:
                        upgradename = I18n.format("top.bot.spark.dispersive");
                        break;
                    case DOMINANT:
                        upgradename = I18n.format("top.bot.spark.dominant");
                        break;
                    case RECESSIVE:
                        upgradename = I18n.format("top.bot.spark.recessive");
                        break;
                    case ISOLATED:
                        upgradename = I18n.format("top.bot.spark.isolated");
                        break;
                    case NONE:
                        break;
                }
                if(upgradename != "null") {
                    probeInfo.text(upgradename);
                }
            }
        }
    }
}
