package com.cmc.suppin.fcm.controller.dto;

import com.cmc.suppin.fcm.domain.DeviceType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeviceTokenRequestDTO {
    private String token;
    private DeviceType deviceType; // ANDROID, IOS, OTHER
}
