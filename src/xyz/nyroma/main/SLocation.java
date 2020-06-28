package xyz.nyroma.main;

public class SLocation {
    private String world;
    private float x = 0;
    private float y = 0;
    private float z = 0;
    private float yaw = 0;
    private float pitch = 0;

    public SLocation(String world, float x, float y, float z){
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public SLocation(String world, float x, float y, float z, float yaw, float pitch){
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch =pitch;
    }

    public String getWorld() {
        return world;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getZ() {
        return z;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setPitch(int pitch) {
        this.pitch = pitch;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
