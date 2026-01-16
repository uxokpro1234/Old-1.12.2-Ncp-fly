# Old-1.12.2 NcpFly

![Minecraft](https://img.shields.io/badge/Minecraft-1.12.2-green)
![Java](https://img.shields.io/badge/Java-8-orange)
![Java](https://img.shields.io/badge/NoCheatPlus-blue)

![Demo](5b5tfly.mov)



**Shoutout to the boys for sharing this code with me.**

## NcpFly is an Oyvey pasted module designed to "disable" ac in a way, that gives you ability to move(fly) at insane speeds and phase trough bocks. Looks insane from other players perspective. It has delay in client-side and server-side, the server takes somw time to move you. It was found in some Fly module. It started like: "yo lets make it fly trough blocks, because it is MOVIN". I'on remember client name. 

**NcpFly** is a splendid, sophisticated movement controller module, which hooks into the game loop, updates player motion, synchronizes with the server, and optionally renders server-side positions — a perfect example of **event-driven module, real-time movement control, and networked game logic**.
```java
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
        //System.out.println("aким аКИМ, Гупа Вилпа попуск, sanja pomog, respektuha brat, ezezezezezez");
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

```
<br><br><br>


## NcpFly Overview
- Allows the player to fly by controlling motion directly.
- Intercepts and modifies network packets related to player position.
- Adjusts flight speed, vertical glide, and direction programmatically.
- Optionally visualizes a “ghost” position or last server-corrected position in 3D space.

---

### Settings / Configuration

The module exposes several configurable values:

| Setting   | Type    | Purpose |
|-----------|--------|---------|
| `packets` | Integer | How many position packets are sent to the server each tick. |
| `speed`   | Float   | Horizontal movement speed multiplier. |
| `Upspeed` | Float   | Client-side flight speed. |
| `glide`   | Float   | Small downward motion to simulate smooth falling/gliding. |
| `nclip`   | Boolean | Whether the player ignores collisions (noClip mode). |

These allow **dynamic tweaking of movement behavior**.

---

### THE MOST important variables(i had to put it there ifykyk)

- `timer` — Tracks elapsed time to throttle packet sending and movement logic.  
- `realx, realy, realz` — Store server-corrected positions received via packets. Used for rendering a **ghost player box**.

---

### Event-driven Structure

The class is **event-driven**, reacting to several in-game events:

#### 1. `onUpdateMove(MoveEvent event)`
- Triggered when the player moves.
- If `nclip` is enabled, sets `mc.player.noClip = true`, allowing the player to ignore collisions.
- **Conceptually:** overrides default collision logic.

#### 2. `onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event)`
- Triggered every tick when the player’s movement updates.
- Key behaviors:
  - Enables client flight (`mc.player.capabilities.isFlying = true`)
  - Sets vertical motion to glide downward slowly (`motionY = -glide`)
  - Computes horizontal motion based on facing direction (`RotationUtil.directionSpeed(speed)`)
  - Sends multiple position packets to the server (`CPacketPlayer.PositionRotation`) simulating movement updates
- **Concept:** Combines motion calculation, flight control, and packet manipulation to control movement at a low level.

#### 3. `onPacketReceived(PacketEvent.Receive event)`
- Intercepts server position correction packets (`SPacketPlayerPosLook`)
- Stores server-corrected coordinates (`realx, realy, realz`)
- Optionally overrides server positions to match the client’s local position
- Sends extra client packets to synchronize positions after interception
- **Concept:** Overrides or synchronizes with server updates for smoother movement behavior.

#### 4. `onRender3D(Render3DEvent event)`
- Visualizes a bounding box at the last server-corrected position (`realx, realy, realz`)
- Computes bounding box size based on the player’s hitbox
- **Concept:** Purely visual; useful for debugging or studying client-server movement discrepancies.

---

### Movement & Packets

- **Flight:** Motion vectors (`motionX, motionY, motionZ`) computed and applied each tick.  
- **Packet Flooding:** Sends multiple position packets each tick (`packets` setting) to simulate precise motion.  
- **Timer Usage:** Throttles updates to avoid unnecessary flooding.  
- **NoClip:** Enables `mc.player.noClip` to ignore collision physics.  

---

### Lifecycle

- `onDisable()` — Resets flight and collision settings when the module is turned off.  
- `Constructor (NcpFly())` — Registers the module, its category (Movement), and default state.

---

### Concept

NcpFly demonstrates:

- **Event-driven design:** Reacts to specific game events (`MoveEvent`, `PacketEvent`, etc.).  
- **Direct motion manipulation:** Overrides velocity and position to simulate flight.  
- **Network packet handling:** Observes and modifies packets for client-server consistency.  
- **Timers & throttling:** Ensures controlled and efficient updates.  
- **Rendering & debugging:** Visualizes server-side positions for research and analysis.  


