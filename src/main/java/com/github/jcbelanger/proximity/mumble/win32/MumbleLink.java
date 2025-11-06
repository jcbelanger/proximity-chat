package com.github.jcbelanger.proximity.mumble.win32;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.*;
import lombok.experimental.StandardException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MumbleLink {

    @StandardException
    public static class MumbleConnectException extends Exception {}

    public static MumbleLinkConnection connect() throws MumbleConnectException {
        Kernel32.HANDLE hFileMapping = null;
        Pointer sharedMemory = null;
        LinkedMem lm = null;

        try {
            log.debug("Connecting to MumbleLink");

            if (!Platform.isWindows()) {
                String errorMessage = "Unsupported platform. Platform is not Windows.";
                log.error(errorMessage);
                throw new MumbleConnectException(errorMessage);
            }

            final String mappingName = "MumbleLink";
            final int mappingSize = new LinkedMem().size();
            final WinDef.DWORD sizeDWORD = new WinDef.DWORD(mappingSize);

            hFileMapping = Kernel32.INSTANCE.CreateFileMapping(
                    WinBase.INVALID_HANDLE_VALUE, // File handle (use INVALID_HANDLE_VALUE for shared memory backed by paging file)
                    null,
                    WinNT.PAGE_READWRITE,
                    sizeDWORD.getHigh().intValue(),
                    sizeDWORD.getLow().intValue(),
                    mappingName
            );

            if (hFileMapping == null || WinBase.INVALID_HANDLE_VALUE.equals(hFileMapping)) {
                String errorMessage = "CreateFileMapping failed: " + Kernel32.INSTANCE.GetLastError();
                log.error(errorMessage);
                throw new MumbleConnectException(errorMessage);
            }

            if (Kernel32.INSTANCE.GetLastError() == WinError.ERROR_ALREADY_EXISTS) {
                log.debug("File mapping object already exists, opening existing one.");
            } else {
                log.debug("New file mapping object created.");
            }

            final WinDef.DWORD offsetDWORD = new WinDef.DWORD(0);
            sharedMemory = Kernel32.INSTANCE.MapViewOfFile(
                    hFileMapping,
                    WinNT.SECTION_MAP_WRITE | WinNT.SECTION_MAP_READ,
                    offsetDWORD.getHigh().intValue(),
                    offsetDWORD.getLow().intValue(),
                    mappingSize
            );

            if (sharedMemory == null) {
                String errorMessage = "MapViewOfFile failed: " + Kernel32.INSTANCE.GetLastError();
                log.error(errorMessage);
                throw new MumbleConnectException(errorMessage);
            } else {
                log.debug("Shared memory object created.");
            }

            lm = new LinkedMem(sharedMemory);
            log.debug("Shared memory view obtained.");

            return new MumbleLinkConnection(hFileMapping, sharedMemory, lm);
        } catch(Exception e) {
            log.debug("Performing cleanup for exception: ", e);

            if (sharedMemory != null) {
                Kernel32.INSTANCE.UnmapViewOfFile(sharedMemory);
                log.debug("Unmapped view of file.");
            }
            if (hFileMapping != null && !WinBase.INVALID_HANDLE_VALUE.equals(hFileMapping)) {
                Kernel32.INSTANCE.CloseHandle(hFileMapping);
                log.debug("Closed file mapping handle.");
            }
            throw e;
        }
    }
}
