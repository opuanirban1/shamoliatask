package com.dev.aes.service;

import com.dev.aes.payloads.request.UserSystemSettingInputRequest;
import com.dev.aes.payloads.response.UserSystemSettingInputResponse;
import com.dev.aes.payloads.response.UserSystemSettingResponse;

import java.util.List;

public interface SystemSettingService {

    List<UserSystemSettingResponse> getUserSystemSettingResponse();

    UserSystemSettingInputResponse getUserSystemSettingInput(UserSystemSettingInputRequest userSystemSettingInputRequest);
}
