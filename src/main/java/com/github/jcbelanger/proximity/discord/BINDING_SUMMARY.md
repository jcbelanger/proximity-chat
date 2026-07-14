# Discord Social SDK JNA Binding Summary

## File Location
`src/main/java/com/github/jcbelanger/proximity/discord/jna/DiscordSocialSDK.java`

## Statistics
- **19 Enums**: Constants and type definitions
- **24 Structures**: JNA Structure classes for Discord types
- **26 Callback Interfaces**: Function pointer callbacks
- **70+ Native Methods**: Direct C API bindings

## Enums (Constants)

### Activity Related
- `ActivityActionTypes` (Invalid, Join, JoinRequest)
- `ActivityPartyPrivacy` (Private, Public)
- `ActivityTypes` (Playing, Streaming, Listening, Watching, CustomStatus, Competing, HangStatus)
- `StatusDisplayTypes` (Name, State, Details)
- `ActivityGamePlatforms` (Desktop, Xbox, Samsung, IOS, Android, Embedded, PS4, PS5)

### Audio Related
- `AudioSystem` (Standard, Game)
- `AudioModeType` (MODE_UNINIT, MODE_VAD, MODE_PTT)

### Call/Voice Related
- `Call_Error` (None, SignalingConnectionFailed, SignalingUnexpectedClose, VoiceConnectionFailed, JoinTimeout, Forbidden)
- `Call_Status` (Disconnected, Joining, Connecting, SignalingConnected, Connected, Reconnecting, Disconnecting)

### Client Related
- `Client_Error` (None, ConnectionFailed, UnexpectedClose, ConnectionCanceled)
- `Client_Status` (Disconnected, Connecting, Connected, Ready, Reconnecting, Disconnecting, HttpWait)
- `Client_Thread` (Client, Voice, Network)

### Authentication & User Related
- `AuthorizationTokenType` (User, Bearer)
- `RelationshipType` (None, Friend, Blocked, PendingIncoming, PendingOutgoing, Implicit, Suggestion)
- `StatusType` (Online, Offline, Blocked, Idle, Dnd, Invisible, Streaming, Unknown)

### Error & Logging
- `ErrorType` (None, NetworkError, HTTPError, ClientNotReady, Disabled, ClientDestroyed, ValidationError, Aborted, AuthorizationFailed, RPCError)
- `HttpStatusCode` (None, Ok, Created, Accepted, NoContent, BadRequest, Unauthorized, Forbidden, NotFound)
- `LoggingSeverity` (Verbose, Info, Warning, Error, None)

## Structures

### Opaque Handle Types
- `Discord_Client` - Main Discord Social SDK client
- `Discord_UserHandle` - User information handle
- `Discord_UserHandle` - User information
- `Discord_LobbyHandle` - Lobby/voice channel handle
- `Discord_LobbyMemberHandle` - Lobby member handle
- `Discord_ChannelHandle` - Discord channel handle
- `Discord_MessageHandle` - Message handle
- `Discord_Call` - Voice call handle
- `Discord_ClientResult` - Result/error information
- `Discord_ClientCreateOptions` - Client creation configuration

### Activity Types
- `Discord_Activity` - Rich presence activity
- `Discord_ActivityInvite` - Activity invitation
- `Discord_ActivityAssets` - Activity images/assets
- `Discord_ActivityTimestamps` - Activity timestamps
- `Discord_ActivityParty` - Activity party information
- `Discord_ActivitySecrets` - Activity secrets
- `Discord_ActivityButton` - Activity button

### Data Types
- `Discord_AudioDevice` - Audio device info
- `Discord_String` - C string (pointer + size)
- `Discord_Properties` - Key-value properties map
- `Discord_Allocator` - Memory allocation callbacks

### Span/Array Types
- `Discord_String_Span` - String array
- `Discord_UInt64Span` - uint64_t array
- `Discord_AudioDeviceSpan` - Audio device array
- `Discord_MessageHandleSpan` - Message handle array
- `Discord_UserHandleSpan` - User handle array

## Callback Interfaces

### Voice/Call Callbacks
- `Discord_Call_OnVoiceStateChanged` - Voice state changed
- `Discord_Call_OnParticipantChanged` - Participant joined/left
- `Discord_Call_OnSpeakingStatusChanged` - Speaking status changed
- `Discord_Call_OnStatusChanged` - Call status changed

### Audio Device Callbacks
- `Discord_Client_GetInputDevicesCallback` - Get input devices
- `Discord_Client_GetOutputDevicesCallback` - Get output devices
- `Discord_Client_GetCurrentInputDeviceCallback` - Get current input device
- `Discord_Client_GetCurrentOutputDeviceCallback` - Get current output device
- `Discord_Client_SetInputDeviceCallback` - Set input device result
- `Discord_Client_SetOutputDeviceCallback` - Set output device result
- `Discord_Client_DeviceChangeCallback` - Device list changed
- `Discord_Client_NoAudioInputCallback` - No audio input detected
- `Discord_Client_EndCallCallback` - Call ended
- `Discord_Client_EndCallsCallback` - All calls ended

### Authentication Callbacks
- `Discord_Client_AuthorizationCallback` - OAuth authorization result
- `Discord_Client_FetchCurrentUserCallback` - Fetch current user result
- `Discord_Client_TokenExchangeCallback` - Token exchange result

### Status & Logging
- `Discord_Client_OnStatusChanged` - Client status changed
- `Discord_Client_LogCallback` - Log message

### Messaging Callbacks
- `Discord_Client_SendUserMessageCallback` - Message sent
- `Discord_Client_MessageCreatedCallback` - Message created
- `Discord_Client_MessageDeletedCallback` - Message deleted
- `Discord_Client_MessageUpdatedCallback` - Message updated

### Lobby Callbacks
- `Discord_Client_CreateOrJoinLobbyCallback` - Lobby created/joined
- `Discord_Client_LeaveLobbyCallback` - Left lobby

## Key Native Methods

### Memory Management
- `Discord_Alloc(size)` - Allocate memory
- `Discord_Free(ptr)` - Free memory
- `Discord_FreeProperties(props)` - Free properties

### Callback Management
- `Discord_SetFreeThreaded()` - Set free-threaded mode
- `Discord_ResetCallbacks()` - Reset all callbacks
- `Discord_RunCallbacks()` - Process pending callbacks

### Client Lifecycle
- `Discord_Client_Init(client)` - Initialize client
- `Discord_Client_InitWithBases(client, apiBase, webBase)` - Initialize with custom endpoints
- `Discord_Client_InitWithOptions(client, options)` - Initialize with options
- `Discord_Client_Drop(client)` - Destroy client

### Client Info
- `Discord_Client_GetApplicationId(client)` - Get application ID
- `Discord_Client_GetCurrentUser(client, user)` - Get current user
- `Discord_Client_GetVersionMajor/Minor/Patch()` - Get SDK version
- `Discord_Client_IsAuthenticated(client)` - Check if authenticated

### Authentication
- `Discord_Client_GetToken(...)` - OAuth token exchange
- `Discord_Client_RefreshToken(...)` - Refresh token
- `Discord_Client_UpdateToken(...)` - Update token
- `Discord_Client_RevokeToken(...)` - Revoke token
- `Discord_Client_FetchCurrentUser(...)` - Fetch current user

### Audio Devices
- `Discord_Client_GetInputDevices(...)` - List input devices
- `Discord_Client_GetOutputDevices(...)` - List output devices
- `Discord_Client_GetCurrentInputDevice(...)` - Get active input device
- `Discord_Client_GetCurrentOutputDevice(...)` - Get active output device
- `Discord_Client_SetInputDevice(...)` - Set input device
- `Discord_Client_SetOutputDevice(...)` - Set output device

### Messaging
- `Discord_Client_SendUserMessage(...)` - Send DM
- `Discord_Client_GetUserMessagesWithLimit(...)` - Get user messages

### Lobbies
- `Discord_Client_CreateOrJoinLobby(...)` - Create/join lobby
- `Discord_Client_LeaveLobby(...)` - Leave lobby

### Activity/Rich Presence
- `Discord_Activity_Init/Drop/Clone(...)` - Activity lifecycle
- `Discord_Activity_SetName/SetType(...)` - Set activity properties
- `Discord_ActivityParty_SetId(...)` - Set party ID
- `Discord_ActivityAssets_SetLargeImage(...)` - Set activity image

### Logging
- `Discord_Client_AddLogCallback(...)` - Register log callback

## Usage Patterns

### Initialize Discord Client
```java
DiscordSocialSDK.Discord_Client client = new DiscordSocialSDK.Discord_Client();
DiscordSocialSDK.getInstance().Discord_Client_Init(client);
```

### Register Status Callback
```java
DiscordSocialSDK.getInstance().Discord_Client_SetOnStatusChangedCallback(
    client,
    (status, error, errorDetail, userData) -> {
        // Handle status change
    },
    null,
    null
);
```

### Set Rich Presence
```java
DiscordSocialSDK.Discord_Activity activity = new DiscordSocialSDK.Discord_Activity();
DiscordSocialSDK.getInstance().Discord_Activity_Init(activity);
DiscordSocialSDK.getInstance().Discord_Activity_SetName(activity, new DiscordSocialSDK.Discord_String("Playing Game"));
```

### Get Audio Devices
```java
DiscordSocialSDK.getInstance().Discord_Client_GetInputDevices(
    client,
    (devices, userData) -> {
        // Use devices.size and devices.ptr
    },
    null,
    null
);
```

## Helper Methods

- `discordStringToJava(Discord_String)` - Convert C string to Java String
- `javaStringToDiscord(String)` - Convert Java String to Discord_String
- `getInstance()` - Get singleton Discord library instance
