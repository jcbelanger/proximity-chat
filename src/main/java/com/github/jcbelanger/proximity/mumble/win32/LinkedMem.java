package com.github.jcbelanger.proximity.mumble.win32;

import com.sun.jna.Native;
import com.sun.jna.Structure;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;


@Structure.FieldOrder({
        "uiVersion",
        "uiTick",
        "fAvatarPosition",
        "fAvatarFront",
        "fAvatarTop",
        "name",
        "fCameraPosition",
        "fCameraFront",
        "fCameraTop",
        "identity",
        "context_len",
        "context",
        "description"
})
public class LinkedMem extends Structure {
    public int uiVersion;
    public int uiTick;
    public float[] fAvatarPosition = new float[3];
    public float[] fAvatarFront = new float[3];
    public float[] fAvatarTop = new float[3];
    public byte[] name = new byte[256 * Native.WCHAR_SIZE];
    public float[] fCameraPosition = new float[3];
    public float[] fCameraFront = new float[3];
    public float[] fCameraTop = new float[3];
    public byte[] identity = new byte[256 * Native.WCHAR_SIZE];
    public int context_len;
    public byte[] context = new byte[256];
    public byte[] description = new byte[2048 * Native.WCHAR_SIZE];

    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final Charset WCHARSET = StandardCharsets.UTF_16LE;

    public LinkedMem() {
        super();
    }

    public LinkedMem(com.sun.jna.Pointer p) {
        super(p);
        read();
    }

    public String getContext() {
        return getCharArrayString(context, context_len);
    }

    public void setContext(String newContext) {
        this.context_len = setCharArrayString(newContext, context);
    }

    public String getDescription() {
        return getDwordString(description);
    }

    public void setDescription(String newDescription) {
        setDwordString(newDescription, description);
    }

    public String getIdentity() {
        return getDwordString(identity);
    }

    public void setIdentity(String newIdentity) {
        setDwordString(newIdentity, identity);
    }

    public String getName() {
        return getDwordString(name);
    }

    public void setName(String newName) {
        setDwordString(newName, name);
    }

    private String getCharArrayString(byte[] src, int len) {
        int length = Math.min(len, src.length);
        if (length <= 0) {
            return "";
        }
        return new String(src, 0, length, CHARSET);
    }

    private int setCharArrayString(String value, byte[] dst) {
        if (value == null) {
            value = "";
        }

        byte[] bytes = Native.toByteArray(value, CHARSET);
        var len = Math.min(bytes.length, dst.length);
        Arrays.fill(dst, (byte) 0);
        System.arraycopy(bytes, 0, dst, 0, len);
        return len;
    }

    private String getDwordString(byte[] src) {
        return Native.toString(src, WCHARSET);
    }

    private void setDwordString(String value, byte[] dst) {
        if (value == null) {
            value = "";
        }

        byte[] wideBytes = Native.toByteArray(value, WCHARSET);
        int maxLengthBytes = dst.length - Native.WCHAR_SIZE;
        int lengthToCopy = Math.min(wideBytes.length, maxLengthBytes);
        Arrays.fill(dst, (byte) 0);
        System.arraycopy(wideBytes, 0, dst, 0, lengthToCopy);
    }

    @Override
    public String toString() {
        return "LinkedMem{" +
                "uiVersion=" + uiVersion +
                ", uiTick=" + uiTick +
                ", fAvatarPosition=" + Arrays.toString(fAvatarPosition) +
                ", fAvatarFront=" + Arrays.toString(fAvatarFront) +
                ", fAvatarTop=" + Arrays.toString(fAvatarTop) +
                ", name='" + getName() + '\'' +
                ", fCameraPosition=" + Arrays.toString(fCameraPosition) +
                ", fCameraFront=" + Arrays.toString(fCameraFront) +
                ", fCameraTop=" + Arrays.toString(fCameraTop) +
                ", identity='" + getIdentity() + '\'' +
                ", context_len=" + context_len +
                ", context='" + getContext() + '\'' +
                ", description='" + getDescription() + '\'' +
                '}';
    }
}