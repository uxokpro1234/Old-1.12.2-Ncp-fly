package me.alpha432.oyvey.features.modules.movement;


import me.alpha432.oyvey.event.events.MoveEvent;
import me.alpha432.oyvey.event.events.PacketEvent;
import me.alpha432.oyvey.event.events.Render3DEvent;
import me.alpha432.oyvey.event.events.UpdateWalkingPlayerEvent;
import me.alpha432.oyvey.features.modules.Module;
import me.alpha432.oyvey.features.setting.Setting;
import me.alpha432.oyvey.util.RenderUtil;
import me.alpha432.oyvey.util.RotationUtil;
import me.alpha432.oyvey.util.Timer;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class NcpFly extends Module {

    public final Setting<Integer> packets = this.register(new Setting<Integer>("Packets", 2, 1, 50));
    public final Setting<Float> speed = this.register(new Setting<Float>("Speed", 0.2f, 0.1f, 10.0f));
    public final Setting<Float> Upspeed = this.register(new Setting<Float>("UpSpeed", 0.01f, 0.01f, 1.0f));
    public final Setting<Float> glide = this.register(new Setting<Float>("Glide", 0.001f, 0.0f, 0.1f));
    public Setting<Boolean> nclip = this.register(new Setting<Boolean>("NoClip", true));

    public Timer timer = new Timer();
    public NcpFly(){
        super("NcpFly","Disables ncp", Category.MOVEMENT,true,false,false);
    }

    @Override
    public void onDisable()  {
        mc.player.capabilities.isFlying = false;
        mc.player.noClip = false;
        //System.out.println("aким аКИМ, Гупа Вилпа попуск, пердак, Tokenlogged succes, ezezezezezez");
    }
    @SubscribeEvent
    public void onUpdateMove(MoveEvent event) {
        if (nclip.getValue()) {
            mc.player.noClip = true;

        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if(event.getStage() != 0){
            return;
        }

        mc.player.capabilities.isFlying = true;
        mc.player.motionY = -glide.getValue();

        if (timer.passedMs(2550)) {
            timer.reset();
            return;
        }
        if (timer.passedMs(2500)) {
            return;
        }

        double[] dir = RotationUtil.directionSpeed(speed.getValue());
        mc.player.capabilities.setFlySpeed(Upspeed.getValue());
        mc.player.motionZ = dir[1];
        mc.player.motionX = dir[0];

        for (int va = 0; va < packets.getValue(); va++) {
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.prevPosX, mc.player.prevPosY + 0.05, mc.player.prevPosZ, mc.player.rotationYaw, mc.player.rotationPitch, true));
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, false));
        }
    }
    double realx;
    double realy;
    double realz;


    @SubscribeEvent
    public void onPacketReceived(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            if (timer.passedMs(2500)) {
                return;
            }
            realx = ((SPacketPlayerPosLook) event.getPacket()).x;
            realz = ((SPacketPlayerPosLook) event.getPacket()).z;
            realy = ((SPacketPlayerPosLook) event.getPacket()).y;
            ((SPacketPlayerPosLook) event.getPacket()).y = mc.player.posY;
            ((SPacketPlayerPosLook) event.getPacket()).x = mc.player.posX;
            ((SPacketPlayerPosLook) event.getPacket()).z = mc.player.posZ;
            ((SPacketPlayerPosLook) event.getPacket()).yaw = mc.player.rotationYaw;
            ((SPacketPlayerPosLook) event.getPacket()).pitch = mc.player.rotationPitch;
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.prevPosX, mc.player.prevPosY + 0.05, mc.player.prevPosZ, true));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (realx != 0) {

            double sx = mc.player.boundingBox.maxX - mc.player.boundingBox.minX;
            double sy = mc.player.boundingBox.maxY - mc.player.boundingBox.minY;
            double sz = mc.player.boundingBox.maxZ - mc.player.boundingBox.minZ;

            RenderUtil.drawBoundingBox(new AxisAlignedBB(realx - sx / 2, realy, realz - sz / 2, realx + sx / 2, realy + sy, realz + sz / 2), new Color(255, 255, 255).getRGB(), new Color(255, 255, 255).getRGB());
        }
    }
}

