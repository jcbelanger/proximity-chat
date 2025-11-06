package com.github.jcbelanger.proximity.mumble.win32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;


@Slf4j
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MumbleLinkConnection implements AutoCloseable {

    private Kernel32.HANDLE hFileMapping = null;
    private Pointer sharedMemory = null;
    private LinkedMem lm = null;

    public void update(int x, int y, int z) {
        lm.fAvatarPosition[0] = x;
        lm.fAvatarPosition[1] = y;
        lm.fAvatarPosition[2] = z;
    }

    public void update(Consumer<LinkedMem> step) {
        if(lm.uiVersion != 2) {
            lm.setName("RuneLite");
            lm.setDescription("Provides position changes from RuneLite through Link plugin.");
            lm.uiVersion = 2;
        }
        lm.uiTick++;
        step.accept(lm);
        lm.write();
    }

    @Override
    public void close() throws Exception {
        log.debug("Closing mumble link connection");

        lm = null;

        if (sharedMemory != null) {
            Kernel32.INSTANCE.UnmapViewOfFile(sharedMemory);
            sharedMemory = null;
            log.debug("Unmapped view of file.");
        }

        if (hFileMapping != null && !WinBase.INVALID_HANDLE_VALUE.equals(hFileMapping)) {
            Kernel32.INSTANCE.CloseHandle(hFileMapping);
            hFileMapping = null;
            log.debug("Closed file mapping handle.");
        }
    }
}
