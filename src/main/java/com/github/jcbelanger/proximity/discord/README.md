# Discord Social SDK JNA Bindings

This package contains JNA (Java Native Access) bindings for the Discord Social SDK C library.

## Overview

The `DiscordSocialSDK.java` file provides complete Java-to-C interop for the Discord Social SDK. It maps:

- **19 Enums**: Including `ActivityTypes`, `Client_Status`, `AuthorizationTokenType`, etc.
- **24 Structures**: Opaque handles and data structures like `Discord_Client`, `Discord_UserHandle`, `Discord_Activity`
- **26 Callback Interfaces**: Function pointer signatures for async callbacks
- **70+ Native Methods**: Directly mapped from the C API

### Library Loading

The Discord Social SDK native library (`discord_partner_sdk`) is automatically loaded when `DiscordSocialSDK` is first accessed:

1. **First attempt**: Tries to load from system library path (if Discord Social SDK is installed system-wide)
2. **Fallback**: If not found, automatically extracts and loads from `src/main/resources/`
3. **Platform support**: Automatically detects and loads the correct library for Windows, macOS, and Linux

**You don't need to do anything special** — just use `DiscordSocialSDK.getInstance()` and the library loading is handled automatically.

## Usage

### Initialization

```java
// Get singleton instance - this automatically loads the Discord Social SDK library
DiscordSocialSDK.DiscordNative lib = DiscordSocialSDK.getInstance();

// Initialize client
DiscordSocialSDK.Discord_Client client = new DiscordSocialSDK.Discord_Client();
lib.Discord_Client_Init(client);
```

The library loading is automatic on first use — it will:
1. Try to load `discord_partner_sdk` from the system library path
2. If not found, extract and load from the bundled resources in `src/main/resources/`

### Type Mappings

The JNA bindings use the following type mappings:

| C Type | Java Type |
|--------|-----------|
| `void*` | `Pointer` |
| `uint64_t` | `long` |
| `int32_t` | `int` |
| `uint8_t` | `byte` |
| `bool` | `boolean` |
| `size_t` | `NativeLong` |
| Function pointer | Callback interface |

### Working with Discord_String

The Discord Social SDK uses a custom `Discord_String` type for strings (pointer + size):

```java
// Convert C string to Java
String javaStr = DiscordSocialSDK.discordStringToJava(cStr);

// Convert Java string to C
DiscordSocialSDK.Discord_String cStr = DiscordSocialSDK.javaStringToDiscord("Hello");
```

## Structures

### Opaque Handles

Most Discord Social SDK structures are opaque and cannot be directly accessed from Java:

```java
Discord_Client client = new Discord_Client();
lib.Discord_Client_Init(client);
// Use methods like Discord_Client_GetApplicationId() to access data
```

### Span Types

Span types represent arrays with pointer + size:

```java
public class Discord_AudioDeviceSpan extends Structure {
    public Pointer ptr;      // Discord_AudioDevice*
    public NativeLong size;  // Number of elements
}
```

## Callbacks

Callbacks use userData for context passing (a common C pattern):

```java
DiscordSocialSDK.DiscordNative lib = DiscordSocialSDK.getInstance();

// Define callback
DiscordSocialSDK.Discord_Client_OnStatusChanged statusCallback = 
    (status, error, errorDetail, userData) -> {
        log.debug("Status changed: " + status);
    };

// Register callback with userData
lib.Discord_Client_SetOnStatusChangedCallback(
    client,
    statusCallback,
    null,  // userDataFree function (null = no cleanup)
    null   // userData context (can be any pointer)
);
```

### Using `callbackUserDataFree` for Cleanup

The `callbackUserDataFree` parameter is a cleanup function that Discord Social SDK calls when it's done with the callback. Use it to free resources:

```java
import com.sun.jna.*;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordCallbackManager {
    private static final DiscordSocialSDK.DiscordNative lib = DiscordSocialSDK.getInstance();
    private static final Map<Long, String> contextMap = new ConcurrentHashMap<>();
    private static long contextCounter = 0;
    
    public static void registerStatusCallback(String username, long userId) {
        long contextId = contextCounter++;
        contextMap.put(contextId, username);
        
        // 1. Define cleanup function - Discord calls this when done with callback
        Callback userDataFree = (Callback) (userData) -> {
            if (userData != null) {
                long id = userData.getLong(0);
                String user = contextMap.remove(id);
                log.debug("Cleaned up callback for: " + user);
                Native.free(Pointer.nativeValue(userData));
            }
        };
        
        // 2. Define main callback - called when status changes
        DiscordSocialSDK.Discord_Client_OnStatusChanged statusCallback = 
            (status, error, errorDetail, userDataPtr) -> {
                if (userDataPtr != null) {
                    long id = userDataPtr.getLong(0);
                    String user = contextMap.get(id);
                    log.debug("Status for " + user + ": " + status);
                }
            };
        
        // 3. Allocate userData - 8 bytes for a long
        Pointer contextPtr = new Memory(8);
        contextPtr.setLong(0, contextId);
        
        // 4. Register with cleanup function
        lib.Discord_Client_SetOnStatusChangedCallback(
            client,
            statusCallback,
            userDataFree,    // Cleanup called when done
            contextPtr       // Passed to both callbacks
        );
    }
}
```

### Complete Example: Tracking Audio Devices

```java
public class AudioDeviceManager {
    private static final DiscordSocialSDK.DiscordNative lib = DiscordSocialSDK.getInstance();
    
    static class DeviceListenerContext {
        String listenerId;
        long startTime = System.currentTimeMillis();
    }
    
    public static void listenForDeviceChanges(String listenerId) {
        DeviceListenerContext ctx = new DeviceListenerContext();
        ctx.listenerId = listenerId;
        
        // Cleanup function
        Callback cleanup = (Callback) (userData) -> {
            if (userData != null) {
                long duration = System.currentTimeMillis() - ctx.startTime;
                log.debug("Device listener " + ctx.listenerId + 
                         " cleaned up after " + duration + "ms");
                Native.free(Pointer.nativeValue(userData));
            }
        };
        
        // Main device change callback
        DiscordSocialSDK.Discord_Client_DeviceChangeCallback deviceCallback = 
            (inputDevices, outputDevices, userData) -> {
                log.debug(ctx.listenerId + " - Devices changed:");
                log.debug("  Input: " + inputDevices.size);
                log.debug("  Output: " + outputDevices.size);
            };
        
        // Allocate context pointer
        Pointer ctx_ptr = new Memory(8);
        ctx_ptr.setLong(0, System.identityHashCode(ctx));
        
        // Register with cleanup
        lib.Discord_Client_SetDeviceChangeCallback(
            client,
            deviceCallback,
            cleanup,
            ctx_ptr
        );
    }
}
```

## Common Patterns

### Client Lifecycle

```java
// Create and initialize
Discord_Client client = new Discord_Client();
lib.Discord_Client_Init(client);

// Get info
long appId = lib.Discord_Client_GetApplicationId(client);

// Cleanup
lib.Discord_Client_Drop(client);
```

### Async Operations with Callbacks

```java
lib.Discord_Client_GetInputDevices(
    client,
    (devices, userData) -> {
        log.debug("Devices: " + devices.size);
    },
    null,
    null
);
```

### Memory Management

The Discord Social SDK manages memory through:
- `Discord_Alloc()` / `Discord_Free()` - Direct allocation
- `Discord_FreeProperties()` - Specialized cleanup
- Callback `userDataFree` function - Custom cleanup for callback context

```java
// Allocate memory
Pointer mem = lib.Discord_Alloc(1024);

// Use memory...

// Free when done
lib.Discord_Free(mem);
```

## Adding More Bindings

To add new functions from the Discord Social SDK:

1. Find the C function in `discordpp.h` or `cdiscord.h`
2. Add the method signature to `DiscordNative` interface
3. Map parameter types using the type mapping table
4. For callbacks, add an interface extending `Callback`

Example:

```java
// C code:
// DISCORD_API void Discord_Client_GetUserGuilds(
//     Discord_Client* self,
//     Discord_Client_GetUserGuildsCallback callback,
//     Discord_FreeFn callbackUserDataFree,
//     void* callbackUserData);

// Java binding:
public interface Discord_Client_GetUserGuildsCallback extends Callback {
    void invoke(Discord_ClientResult result, Discord_GuildMinimalSpan guilds, Pointer userData);
}

// In DiscordNative:
void Discord_Client_GetUserGuilds(
    Discord_Client self,
    Discord_Client_GetUserGuildsCallback callback,
    Callback callbackUserDataFree,
    Pointer callbackUserData
);
```

## Enums

All Discord Social SDK enums are available as static int constants:

```java
int status = DiscordSocialSDK.Client_Status.Ready;
int errorType = DiscordSocialSDK.ErrorType.NetworkError;
int activityType = DiscordSocialSDK.ActivityTypes.Playing;
```

## Notes

- The library is loaded by name "discord_sdk" via `Native.load()`
- Ensure the Discord Social SDK native library is in the library path
- All callback userData context must be cleaned up by the userDataFree callback
- Opaque handle structures should not be modified directly; use API methods instead

## References

- Discord Social SDK C Header: `src/main/cpp/discord_social_sdk/include/cdiscord.h`
- Discord Social SDK C++ Header: `src/main/cpp/discord_social_sdk/include/discordpp.h`
- JNA Documentation: https://github.com/java-native-access/jna
