package mcjty.theoneprobe.mods.botaniverse;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/12/16 19:58
 */
import com.aeternal.botaniverse.Config;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkAlfheim;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkAsgard;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkMuspelheim;
import com.aeternal.botaniverse.common.entity.sparks.EntitySparkNilfheim;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import vazkii.botania.api.mana.spark.ISparkEntity;
import vazkii.botania.common.entity.EntitySpark;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author : wdcftgg
 * @create 2023/12/16 18:53
 */
public class MoreSpark implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return "botaniverse.spark";
    }


    @Override
    public void addProbeEntityInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
        if (entity instanceof ISparkEntity) {
            ISparkEntity sparkEntity = (ISparkEntity) entity;

            int speed = -1;

            if (sparkEntity instanceof EntitySparkAlfheim) speed = Config.SparkAlfheimConductivity;
            if (sparkEntity instanceof EntitySparkAsgard) speed = Config.SparkAsgardConductivity;
            if (sparkEntity instanceof EntitySparkMuspelheim) speed = Config.SparkMuspelheimConductivity;
            if (sparkEntity instanceof EntitySparkNilfheim) speed = Config.SparkNilfheimConductivity;

            if(speed != -1) {
                probeInfo.text(speed + "Mana/s");
            }

        }
    }
}

