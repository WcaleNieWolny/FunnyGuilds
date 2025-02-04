package net.dzikoysk.funnyguilds.nms.v1_17R1.entity;

import com.google.common.base.Preconditions;
import net.dzikoysk.funnyguilds.nms.api.entity.EntityAccessor;
import net.dzikoysk.funnyguilds.nms.api.entity.FakeEntity;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntityLiving;
import net.minecraft.world.entity.EntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class V1_17R1EntityAccessor implements EntityAccessor {

    @Override
    public FakeEntity createFakeEntity(EntityType entityType, Location location) {
        Preconditions.checkNotNull(entityType, "entity type can't be null!");
        Preconditions.checkNotNull(location, "location can't be null!");
        Preconditions.checkArgument(entityType.isSpawnable(), "entity type is not spawnable!");

        CraftWorld world = ((CraftWorld) location.getWorld());
        if (world == null) {
            throw new IllegalStateException("location's world is null!");
        }

        net.minecraft.world.entity.Entity entity = world.createEntity(location, entityType.getEntityClass());
        Packet<?> spawnEntityPacket;

        if (entity instanceof EntityLiving) {
            spawnEntityPacket = new PacketPlayOutSpawnEntityLiving((EntityLiving) entity);
        }
        else {
            spawnEntityPacket = new PacketPlayOutSpawnEntity(entity);
        }

        return new FakeEntity(entity.getId(), location, spawnEntityPacket);
    }

    @Override
    public void spawnFakeEntityFor(FakeEntity entity, Player... players) {
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().b.sendPacket((Packet<?>) entity.getSpawnPacket());
        }
    }

    @Override
    public void despawnFakeEntityFor(FakeEntity entity, Player... players) {
        PacketPlayOutEntityDestroy destroyEntityPacket = new PacketPlayOutEntityDestroy(entity.getId());

        for (Player player : players) {
            ((CraftPlayer) player).getHandle().b.sendPacket(destroyEntityPacket);
        }
    }

}
