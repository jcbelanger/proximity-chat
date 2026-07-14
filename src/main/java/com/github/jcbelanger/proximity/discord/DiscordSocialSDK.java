package com.github.jcbelanger.proximity.discord;

import com.sun.jna.*;

import java.util.Arrays;
import java.util.List;


/**
 * JNA bindings for the Discord Social SDK (discordpp/cdiscord.h).
 * This file provides Java-to-C interop for all Discord Social SDK functions.
 * 
 * The Discord Social SDK is a C library that provides APIs for:
 * - User authentication and tokens
 * - Rich presence / activities
 * - Messaging
 * - Voice chat and audio devices
 * - Social features (lobbies, relationships)
 * - Guild and channel management
 */
public class DiscordSocialSDK {

    // ============================================================================
    // ENUMS
    // ============================================================================

    public static class ActivityActionTypes {
        public static final int Invalid = 0;
        public static final int Join = 1;
        public static final int JoinRequest = 5;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class ActivityPartyPrivacy {
        public static final int Private = 0;
        public static final int Public = 1;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class ActivityTypes {
        public static final int Playing = 0;
        public static final int Streaming = 1;
        public static final int Listening = 2;
        public static final int Watching = 3;
        public static final int CustomStatus = 4;
        public static final int Competing = 5;
        public static final int HangStatus = 6;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class StatusDisplayTypes {
        public static final int Name = 0;
        public static final int State = 1;
        public static final int Details = 2;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class ActivityGamePlatforms {
        public static final int Desktop = 1;
        public static final int Xbox = 2;
        public static final int Samsung = 4;
        public static final int IOS = 8;
        public static final int Android = 16;
        public static final int Embedded = 32;
        public static final int PS4 = 64;
        public static final int PS5 = 128;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class ErrorType {
        public static final int None = 0;
        public static final int NetworkError = 1;
        public static final int HTTPError = 2;
        public static final int ClientNotReady = 3;
        public static final int Disabled = 4;
        public static final int ClientDestroyed = 5;
        public static final int ValidationError = 6;
        public static final int Aborted = 7;
        public static final int AuthorizationFailed = 8;
        public static final int RPCError = 9;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class HttpStatusCode {
        public static final int None = 0;
        public static final int Ok = 200;
        public static final int Created = 201;
        public static final int Accepted = 202;
        public static final int NoContent = 204;
        public static final int BadRequest = 400;
        public static final int Unauthorized = 401;
        public static final int Forbidden = 403;
        public static final int NotFound = 404;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class AudioSystem {
        public static final int Standard = 0;
        public static final int Game = 1;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class Call_Error {
        public static final int None = 0;
        public static final int SignalingConnectionFailed = 1;
        public static final int SignalingUnexpectedClose = 2;
        public static final int VoiceConnectionFailed = 3;
        public static final int JoinTimeout = 4;
        public static final int Forbidden = 5;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class AudioModeType {
        public static final int MODE_UNINIT = 0;
        public static final int MODE_VAD = 1;
        public static final int MODE_PTT = 2;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class Call_Status {
        public static final int Disconnected = 0;
        public static final int Joining = 1;
        public static final int Connecting = 2;
        public static final int SignalingConnected = 3;
        public static final int Connected = 4;
        public static final int Reconnecting = 5;
        public static final int Disconnecting = 6;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class RelationshipType {
        public static final int None = 0;
        public static final int Friend = 1;
        public static final int Blocked = 2;
        public static final int PendingIncoming = 3;
        public static final int PendingOutgoing = 4;
        public static final int Implicit = 5;
        public static final int Suggestion = 6;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class StatusType {
        public static final int Online = 0;
        public static final int Offline = 1;
        public static final int Blocked = 2;
        public static final int Idle = 3;
        public static final int Dnd = 4;
        public static final int Invisible = 5;
        public static final int Streaming = 6;
        public static final int Unknown = 7;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class Client_Error {
        public static final int None = 0;
        public static final int ConnectionFailed = 1;
        public static final int UnexpectedClose = 2;
        public static final int ConnectionCanceled = 3;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class Client_Status {
        public static final int Disconnected = 0;
        public static final int Connecting = 1;
        public static final int Connected = 2;
        public static final int Ready = 3;
        public static final int Reconnecting = 4;
        public static final int Disconnecting = 5;
        public static final int HttpWait = 6;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class Client_Thread {
        public static final int Client = 0;
        public static final int Voice = 1;
        public static final int Network = 2;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class AuthorizationTokenType {
        public static final int User = 0;
        public static final int Bearer = 1;
        public static final int forceint = 0x7FFFFFFF;
    }

    public static class LoggingSeverity {
        public static final int Verbose = 1;
        public static final int Info = 2;
        public static final int Warning = 3;
        public static final int Error = 4;
        public static final int None = 5;
        public static final int forceint = 0x7FFFFFFF;
    }

    // ============================================================================
    // STRUCTURES
    // ============================================================================

    public static class Discord_String extends Structure {
        public Pointer ptr;
        public NativeLong size;

        public Discord_String() {}

        public Discord_String(String value) {
            if (value != null) {
                byte[] bytes = value.getBytes();
                this.ptr = new Memory(bytes.length);
                this.ptr.write(0, bytes, 0, bytes.length);
                this.size = new NativeLong(bytes.length);
            }
        }

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("ptr", "size");
        }
    }

    public static class Discord_Properties extends Structure {
        public NativeLong size;
        public Pointer keys;
        public Pointer values;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("size", "keys", "values");
        }
    }

    public static class Discord_Allocator extends Structure {
        public Callback fnMalloc;
        public Callback fnFree;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("fnMalloc", "fnFree");
        }
    }

    public static class Discord_ActivityAssets extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_ActivityTimestamps extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_ActivityParty extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_ActivitySecrets extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_ActivityButton extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_ActivityInvite extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_Activity extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_ClientResult extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_UserHandle extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_LobbyHandle extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_LobbyMemberHandle extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_MessageHandle extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_ChannelHandle extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_AudioDevice extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_Client extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_ClientCreateOptions extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_Call extends Structure {
        public Pointer opaque;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("opaque");
        }
    }

    public static class Discord_String_Span extends Structure {
        public Pointer ptr;
        public NativeLong size;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("ptr", "size");
        }
    }

    public static class Discord_UInt64Span extends Structure {
        public Pointer ptr;
        public NativeLong size;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("ptr", "size");
        }
    }

    public static class Discord_AudioDeviceSpan extends Structure {
        public Pointer ptr;
        public NativeLong size;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("ptr", "size");
        }
    }

    public static class Discord_MessageHandleSpan extends Structure {
        public Pointer ptr;
        public NativeLong size;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("ptr", "size");
        }
    }

    public static class Discord_UserHandleSpan extends Structure {
        public Pointer ptr;
        public NativeLong size;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("ptr", "size");
        }
    }

    // ============================================================================
    // CALLBACK INTERFACES
    // ============================================================================

    public interface Discord_Call_OnVoiceStateChanged extends Callback {
        void invoke(long userId, Pointer userData);
    }

    public interface Discord_Call_OnParticipantChanged extends Callback {
        void invoke(long userId, boolean added, Pointer userData);
    }

    public interface Discord_Call_OnSpeakingStatusChanged extends Callback {
        void invoke(long userId, boolean isPlayingSound, Pointer userData);
    }

    public interface Discord_Call_OnStatusChanged extends Callback {
        void invoke(int status, int error, int errorDetail, Pointer userData);
    }

    public interface Discord_Client_EndCallCallback extends Callback {
        void invoke(Pointer userData);
    }

    public interface Discord_Client_EndCallsCallback extends Callback {
        void invoke(Pointer userData);
    }

    public interface Discord_Client_GetCurrentInputDeviceCallback extends Callback {
        void invoke(Discord_AudioDevice device, Pointer userData);
    }

    public interface Discord_Client_GetCurrentOutputDeviceCallback extends Callback {
        void invoke(Discord_AudioDevice device, Pointer userData);
    }

    public interface Discord_Client_GetInputDevicesCallback extends Callback {
        void invoke(Discord_AudioDeviceSpan devices, Pointer userData);
    }

    public interface Discord_Client_GetOutputDevicesCallback extends Callback {
        void invoke(Discord_AudioDeviceSpan devices, Pointer userData);
    }

    public interface Discord_Client_DeviceChangeCallback extends Callback {
        void invoke(Discord_AudioDeviceSpan inputDevices, Discord_AudioDeviceSpan outputDevices, Pointer userData);
    }

    public interface Discord_Client_SetInputDeviceCallback extends Callback {
        void invoke(Discord_ClientResult result, Pointer userData);
    }

    public interface Discord_Client_NoAudioInputCallback extends Callback {
        void invoke(boolean inputDetected, Pointer userData);
    }

    public interface Discord_Client_SetOutputDeviceCallback extends Callback {
        void invoke(Discord_ClientResult result, Pointer userData);
    }

    public interface Discord_Client_TokenExchangeCallback extends Callback {
        void invoke(Discord_ClientResult result, Discord_String accessToken, Discord_String refreshToken,
                    int tokenType, int expiresIn, Discord_String scopes, Pointer userData);
    }

    public interface Discord_Client_AuthorizationCallback extends Callback {
        void invoke(Discord_ClientResult result, Discord_String code, Discord_String redirectUri, Pointer userData);
    }

    public interface Discord_Client_FetchCurrentUserCallback extends Callback {
        void invoke(Discord_ClientResult result, long id, Discord_String name, Pointer userData);
    }

    public interface Discord_Client_LogCallback extends Callback {
        void invoke(Discord_String message, int severity, Pointer userData);
    }

    public interface Discord_Client_OnStatusChanged extends Callback {
        void invoke(int status, int error, int errorDetail, Pointer userData);
    }

    public interface Discord_Client_CreateOrJoinLobbyCallback extends Callback {
        void invoke(Discord_ClientResult result, long lobbyId, Pointer userData);
    }

    public interface Discord_Client_LeaveLobbyCallback extends Callback {
        void invoke(Discord_ClientResult result, Pointer userData);
    }

    public interface Discord_Client_SendUserMessageCallback extends Callback {
        void invoke(Discord_ClientResult result, long messageId, Pointer userData);
    }

    public interface Discord_Client_MessageCreatedCallback extends Callback {
        void invoke(long messageId, Pointer userData);
    }

    public interface Discord_Client_MessageDeletedCallback extends Callback {
        void invoke(long messageId, long channelId, Pointer userData);
    }

    public interface Discord_Client_MessageUpdatedCallback extends Callback {
        void invoke(long messageId, Pointer userData);
    }

    // ============================================================================
    // NATIVE LIBRARY INTERFACE
    // ============================================================================

    public interface DiscordNative extends Library {

        String LIBRARY_NAME = "discord_partner_sdk";

        // Memory management
        Pointer Discord_Alloc(NativeLong size);
        void Discord_Free(Pointer ptr);
        void Discord_FreeProperties(Discord_Properties props);

        // Callback management
        void Discord_SetFreeThreaded();
        void Discord_ResetCallbacks();
        void Discord_RunCallbacks();

        // Client lifecycle
        void Discord_Client_Init(Discord_Client self);
        void Discord_Client_InitWithBases(Discord_Client self, Discord_String apiBase, Discord_String webBase);
        void Discord_Client_InitWithOptions(Discord_Client self, Discord_ClientCreateOptions options);
        void Discord_Client_Drop(Discord_Client self);

        // Client info
        long Discord_Client_GetApplicationId(Discord_Client self);
        void Discord_Client_GetCurrentUser(Discord_Client self, Discord_UserHandle.ByReference returnValue);
        void Discord_Client_ErrorToString(int type, Discord_String.ByReference returnValue);
        void Discord_Client_StatusToString(int type, Discord_String.ByReference returnValue);
        void Discord_Client_ThreadToString(int type, Discord_String.ByReference returnValue);
        int Discord_Client_GetVersionMajor();
        int Discord_Client_GetVersionMinor();
        int Discord_Client_GetVersionPatch();
        void Discord_Client_GetVersionHash(Discord_String.ByReference returnValue);
        void Discord_Client_SetHttpRequestTimeout(Discord_Client self, int httpTimeoutInMilliseconds);
        boolean Discord_Client_IsAuthenticated(Discord_Client self);

        // Authentication & Tokens
        void Discord_Client_GetToken(Discord_Client self, long applicationId, Discord_String code,
                                     Discord_String codeVerifier, Discord_String redirectUri,
                                     Discord_Client_TokenExchangeCallback callback,
                                     Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_UpdateToken(Discord_Client self, int tokenType, Discord_String token,
                                        Callback callback, Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_FetchCurrentUser(Discord_Client self, int tokenType, Discord_String token,
                                             Discord_Client_FetchCurrentUserCallback callback,
                                             Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_RefreshToken(Discord_Client self, long applicationId, Discord_String refreshToken,
                                         Discord_Client_TokenExchangeCallback callback,
                                         Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_RevokeToken(Discord_Client self, long applicationId, Discord_String token,
                                        Callback callback, Callback callbackUserDataFree, Pointer callbackUserData);

        // Audio devices
        void Discord_Client_GetInputDevices(Discord_Client self, Discord_Client_GetInputDevicesCallback callback,
                                            Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_GetOutputDevices(Discord_Client self, Discord_Client_GetOutputDevicesCallback callback,
                                             Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_GetCurrentInputDevice(Discord_Client self,
                                                  Discord_Client_GetCurrentInputDeviceCallback callback,
                                                  Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_GetCurrentOutputDevice(Discord_Client self,
                                                   Discord_Client_GetCurrentOutputDeviceCallback callback,
                                                   Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_SetInputDevice(Discord_Client self, Discord_String deviceId,
                                          Discord_Client_SetInputDeviceCallback callback,
                                          Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_SetOutputDevice(Discord_Client self, Discord_String deviceId,
                                           Discord_Client_SetOutputDeviceCallback callback,
                                           Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_SetDeviceChangeCallback(Discord_Client self,
                                                   Discord_Client_DeviceChangeCallback callback,
                                                   Callback callbackUserDataFree, Pointer callbackUserData);

        // Messaging
        void Discord_Client_SendUserMessage(Discord_Client self, long recipientId, Discord_String content,
                                           Discord_Client_SendUserMessageCallback callback,
                                           Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_GetUserMessagesWithLimit(Discord_Client self, long recipientId, int limit,
                                                     Callback callback, Callback callbackUserDataFree,
                                                     Pointer callbackUserData);

        // Lobby
        void Discord_Client_CreateOrJoinLobby(Discord_Client self, long channelId,
                                              Discord_Client_CreateOrJoinLobbyCallback callback,
                                              Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_LeaveLobby(Discord_Client self, long lobbyId,
                                      Discord_Client_LeaveLobbyCallback callback,
                                      Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_SetOnStatusChangedCallback(Discord_Client self,
                                                      Discord_Client_OnStatusChanged callback,
                                                      Callback callbackUserDataFree, Pointer callbackUserData);

        void Discord_Client_AddLogCallback(Discord_Client self, Discord_Client_LogCallback callback,
                                          Callback callbackUserDataFree, Pointer callbackUserData,
                                          int minSeverity);

        // Activity / Rich Presence
        void Discord_Activity_Init(Discord_Activity self);
        void Discord_Activity_Drop(Discord_Activity self);
        void Discord_Activity_Clone(Discord_Activity self, Discord_Activity arg0);
        void Discord_Activity_SetName(Discord_Activity self, Discord_String value);
        void Discord_Activity_Name(Discord_Activity self, Discord_String.ByReference returnValue);
        void Discord_Activity_SetType(Discord_Activity self, int value);
        int Discord_Activity_Type(Discord_Activity self);

        // User operations
        void Discord_UserHandle_Clone(Discord_UserHandle self, Discord_UserHandle other);
        void Discord_UserHandle_Drop(Discord_UserHandle self);
        long Discord_UserHandle_Id(Discord_UserHandle self);
        void Discord_UserHandle_Username(Discord_UserHandle self, Discord_String.ByReference returnValue);
        void Discord_UserHandle_DisplayName(Discord_UserHandle self, Discord_String.ByReference returnValue);
        int Discord_UserHandle_Status(Discord_UserHandle self);
        boolean Discord_UserHandle_IsProvisional(Discord_UserHandle self);

        // ActivityInvite
        void Discord_ActivityInvite_Init(Discord_ActivityInvite self);
        void Discord_ActivityInvite_Drop(Discord_ActivityInvite self);
        void Discord_ActivityInvite_Clone(Discord_ActivityInvite self, Discord_ActivityInvite rhs);
        void Discord_ActivityInvite_SetSenderId(Discord_ActivityInvite self, long value);
        long Discord_ActivityInvite_SenderId(Discord_ActivityInvite self);

        // ActivityAssets
        void Discord_ActivityAssets_Init(Discord_ActivityAssets self);
        void Discord_ActivityAssets_Drop(Discord_ActivityAssets self);
        void Discord_ActivityAssets_Clone(Discord_ActivityAssets self, Discord_ActivityAssets arg0);
        void Discord_ActivityAssets_SetLargeImage(Discord_ActivityAssets self, Discord_String value);
        boolean Discord_ActivityAssets_LargeImage(Discord_ActivityAssets self, Discord_String.ByReference returnValue);

        // ActivityParty
        void Discord_ActivityParty_Init(Discord_ActivityParty self);
        void Discord_ActivityParty_Drop(Discord_ActivityParty self);
        void Discord_ActivityParty_Clone(Discord_ActivityParty self, Discord_ActivityParty arg0);
        void Discord_ActivityParty_SetId(Discord_ActivityParty self, Discord_String value);
        void Discord_ActivityParty_Id(Discord_ActivityParty self, Discord_String.ByReference returnValue);

        // ClientCreateOptions
        void Discord_ClientCreateOptions_Init(Discord_ClientCreateOptions self);
        void Discord_ClientCreateOptions_Drop(Discord_ClientCreateOptions self);
        void Discord_ClientCreateOptions_Clone(Discord_ClientCreateOptions self, Discord_ClientCreateOptions arg0);
        void Discord_ClientCreateOptions_SetWebBase(Discord_ClientCreateOptions self, Discord_String value);
        void Discord_ClientCreateOptions_WebBase(Discord_ClientCreateOptions self, Discord_String.ByReference returnValue);
        void Discord_ClientCreateOptions_SetApiBase(Discord_ClientCreateOptions self, Discord_String value);
        void Discord_ClientCreateOptions_ApiBase(Discord_ClientCreateOptions self, Discord_String.ByReference returnValue);
    }

    // ============================================================================
    // SINGLETON LIBRARY INSTANCE
    // ============================================================================

    private static DiscordNative INSTANCE;

    public static synchronized DiscordNative getInstance() {
        if (INSTANCE == null) {
            try {
                // JNA will use the library name - it's already loaded by the static initializer
                INSTANCE = Native.load(DiscordNative.LIBRARY_NAME, DiscordNative.class);
            } catch (UnsatisfiedLinkError e) {
                throw new RuntimeException("Failed to load Discord Social SDK native library. " +
                    "Ensure discord_partner_sdk is in the library path or in resources.", e);
            }
        }
        return INSTANCE;
    }

    // ============================================================================
    // HELPER UTILITY METHODS
    // ============================================================================

    /**
     * Convert a Discord_String to a Java String
     */
    public static String discordStringToJava(Discord_String str) {
        if (str == null || str.ptr == null || str.size.longValue() == 0) {
            return "";
        }
        byte[] bytes = str.ptr.getByteArray(0, (int) str.size.longValue());
        return new String(bytes);
    }

    /**
     * Create a Discord_String from a Java String
     */
    public static Discord_String javaStringToDiscord(String str) {
        return new Discord_String(str);
    }
}
