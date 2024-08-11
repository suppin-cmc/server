package com.cmc.suppin.fcm.service;

import com.cmc.suppin.fcm.controller.dto.FcmSendDTO;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface FcmService {

    int sendMessageTo(FcmSendDTO fcmSendDTO) throws IOException;
}
