package net.dzikoysk.funnyguilds.guild;

import net.dzikoysk.funnyguilds.data.AbstractMutableEntity;
import net.dzikoysk.funnyguilds.shared.bukkit.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public class Region extends AbstractMutableEntity {

    private String name;
    private Guild guild;

    private World world;
    private Location center;
    private int size;
    private int enlarge;

    private Location firstCorner;
    private Location secondCorner;

    public Region(String name) {
        this.name = name;
    }

    public Region(Guild guild, Location location, int size) {
        this(guild.getName());

        this.guild = guild;
        this.world = location.getWorld();
        this.center = location;
        this.size = size;

        this.update();
    }

    public synchronized void update() {
        super.markChanged();

        if (this.center == null) {
            return;
        }

        if (this.size < 1) {
            return;
        }

        if (this.world == null) {
            this.world = Bukkit.getWorlds().get(0);
        }

        if (this.world != null) {
            int lx = this.center.getBlockX() + this.size;
            int lz = this.center.getBlockZ() + this.size;

            int px = this.center.getBlockX() - this.size;
            int pz = this.center.getBlockZ() - this.size;

            Vector l = new Vector(lx, LocationUtils.getMinHeight(this.world), lz);
            Vector p = new Vector(px, this.world.getMaxHeight(), pz);

            this.firstCorner = l.toLocation(this.world);
            this.secondCorner = p.toLocation(this.world);
        }
    }

    public boolean isIn(Location location) {
        if (location == null || this.firstCorner == null || this.secondCorner == null) {
            return false;
        }

        if (!this.center.getWorld().equals(location.getWorld())) {
            return false;
        }

        if (location.getBlockX() > this.getLowerX() && location.getBlockX() < this.getUpperX()) {
            if (location.getBlockY() > this.getLowerY() && location.getBlockY() < this.getUpperY()) {
                return location.getBlockZ() > this.getLowerZ() && location.getBlockZ() < this.getUpperZ();
            }
        }

        return false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        super.markChanged();
    }

    public Guild getGuild() {
        return this.guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
        super.markChanged();
    }

    public World getWorld() {
        return this.world;
    }

    public Location getCenter() {
        return this.center;
    }

    public Location getHeart() {
        return getCenter().getBlock().getRelative(BlockFace.DOWN).getLocation();
    }

    public void setCenter(Location location) {
        this.center = location;
        this.world = location.getWorld();
        this.update();
    }

    public int getSize() {
        return this.size;
    }

    public void setSize(int size) {
        this.size = size;
        this.update();
    }

    public int getEnlarge() {
        return this.enlarge;
    }

    public void setEnlarge(int enlarge) {
        this.enlarge = enlarge;
        super.markChanged();
    }

    public int getUpperX() {
        return compareCoordinates(true, firstCorner.getBlockX(), secondCorner.getBlockX());
    }

    public int getUpperY() {
        return compareCoordinates(true, firstCorner.getBlockY(), secondCorner.getBlockY());
    }

    public int getUpperZ() {
        return compareCoordinates(true, firstCorner.getBlockZ(), secondCorner.getBlockZ());
    }

    public int getLowerX() {
        return compareCoordinates(false, firstCorner.getBlockX(), secondCorner.getBlockX());
    }

    public int getLowerY() {
        return compareCoordinates(false, firstCorner.getBlockY(), secondCorner.getBlockY());
    }

    public int getLowerZ() {
        return compareCoordinates(false, firstCorner.getBlockZ(), secondCorner.getBlockZ());
    }

    public Location getFirstCorner() {
        return this.firstCorner;
    }

    public Location getSecondCorner() {
        return this.secondCorner;
    }

    private int compareCoordinates(boolean upper, int a, int b) {
        if (upper) {
            return Math.max(b, a);
        }
        else {
            return Math.min(a, b);
        }
    }

    @Override
    public EntityType getType() {
        return EntityType.REGION;
    }

    @Override
    public String toString() {
        return this.name;
    }

}