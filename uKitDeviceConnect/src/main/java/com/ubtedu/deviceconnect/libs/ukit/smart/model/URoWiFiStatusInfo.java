package com.ubtedu.deviceconnect.libs.ukit.smart.model;

import androidx.annotation.NonNull;

/**
 * @Author naOKi
 * @Date 2019/10/22
 **/
public class URoWiFiStatusInfo {

    private final URoWiFiState state;
    private final URoWiFiDisconnectReason disconnectReason;
    private final String ssid;
    private final int rssi;
    private final URoWiFiAuthMode authMode;

    public URoWiFiStatusInfo(@NonNull URoWiFiState state, @NonNull String ssid, int rssi, @NonNull URoWiFiAuthMode authMode) {
        this(state, URoWiFiDisconnectReason.REASON_UNSPECIFIED, ssid, rssi, authMode);
    }

    public URoWiFiStatusInfo(@NonNull URoWiFiState state, URoWiFiDisconnectReason disconnectReason, @NonNull String ssid, int rssi, @NonNull URoWiFiAuthMode authMode) {
        this.state = state;
        this.disconnectReason = disconnectReason == null ? URoWiFiDisconnectReason.REASON_UNSPECIFIED : disconnectReason;
        this.ssid = ssid;
        this.rssi = rssi;
        this.authMode = authMode;
    }

    public URoWiFiState getState() {
        return state;
    }

    public URoWiFiDisconnectReason getDisconnectReason() {
        return disconnectReason;
    }

    public String getSsid() {
        return ssid;
    }

    public int getRssi() {
        return rssi;
    }

    public URoWiFiAuthMode getAuthMode() {
        return authMode;
    }

    @Override
    public String toString() {
        return "URoWiFiStatusInfo{" +
                "state=" + state.getDescription() +
                ", disconnectReason=" + disconnectReason.name() +
                ", ssid='" + ssid + '\'' +
                ", rssi=" + rssi +
                ", authMode=" + authMode.name() +
                '}';
    }

    public enum URoWiFiState {
        IDLE(0, "空闲"),
        CONNECTING(1, "连接中"),
        CONNECTED(2, "已连接"),
        DISCONNECTED(3, "断开连接");

        private String description;
        private int code;

        public String getDescription() {
            return description;
        }

        public int getCode() {
            return code;
        }

        URoWiFiState(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static URoWiFiState findByCode(int code) {
            for(URoWiFiState state : URoWiFiState.values()) {
                if(state.getCode() == code) {
                    return state;
                }
            }
            return URoWiFiState.IDLE;
        }

    }

    public enum URoWiFiDisconnectReason {
        REASON_UNSPECIFIED(1),
        REASON_AUTH_EXPIRE(2),
        REASON_AUTH_LEAVE(3),
        REASON_ASSOC_EXPIRE(4),
        REASON_ASSOC_TOOMANY(5),
        REASON_NOT_AUTHED(6),
        REASON_NOT_ASSOCED(7),
        REASON_ASSOC_LEAVE(8),
        REASON_ASSOC_NOT_AUTHED(9),
        REASON_DISASSOC_PWRCAP_BAD(10),
        REASON_DISASSOC_SUPCHAN_BAD(11),
        REASON_IE_INVALID(13),
        REASON_MIC_FAILURE(14),
        REASON_4WAY_HANDSHAKE_TIMEOUT(15),
        REASON_GROUP_KEY_UPDATE_TIMEOUT(16),
        REASON_IE_IN_4WAY_DIFFERS(17),
        REASON_GROUP_CIPHER_INVALID(18),
        REASON_PAIRWISE_CIPHER_INVALID(19),
        REASON_AKMP_INVALID(20),
        REASON_UNSUPP_RSN_IE_VERSION(21),
        REASON_INVALID_RSN_IE_CAP(22),
        REASON_802_1X_AUTH_FAILED(23),
        REASON_CIPHER_SUITE_REJECTED(24),
        REASON_BEACON_TIMEOUT(200),
        REASON_NO_AP_FOUND(201),
        REASON_AUTH_FAIL(202),
        REASON_ASSOC_FAIL(203),
        REASON_HANDSHAKE_TIMEOUT(204),
        REASON_CONNECTION_FAIL(205);

        private int code;

        public int getCode() {
            return code;
        }

        URoWiFiDisconnectReason(int code) {
            this.code = code;
        }

        public static URoWiFiDisconnectReason findByCode(int code) {
            for(URoWiFiDisconnectReason reason : URoWiFiDisconnectReason.values()) {
                if(reason.getCode() == code) {
                    return reason;
                }
            }
            return URoWiFiDisconnectReason.REASON_UNSPECIFIED;
        }

    }

}
